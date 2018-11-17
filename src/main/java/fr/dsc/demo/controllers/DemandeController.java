package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.DemandeDao;
import fr.dsc.demo.dao.FichierDao;
import fr.dsc.demo.dao.MessageDao;
import fr.dsc.demo.dao.UtilisateurDao;
import fr.dsc.demo.models.Demande;
import fr.dsc.demo.models.Fichier;
import fr.dsc.demo.models.Message;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.utilities.Util;
import fr.dsc.demo.utilities.XmlHelper;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Controller
public class DemandeController {

    @Autowired
    UtilisateurDao utilisateurDao;
    @Autowired
    MessageDao messageDao;
    @Autowired
    DemandeDao demandeDao;

    @Autowired
    FichierDao fichierDao;

    @Autowired
    XmlHelper xmlHelper;

    @RequestMapping(value = "/nouvelle-demande", method = RequestMethod.GET)
    public String renderCreateDemandePage(Model model, HttpServletRequest request) {
        Fichier fichier = new Fichier();
        model.addAttribute("fichier", fichier);
        model.addAttribute("msg", new Message());
//            System.out.println(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(""));
        return "nouvelle-demande";
    }

    @RequestMapping(value = "/nouvelle-demande", method = RequestMethod.POST)
    public String createDemande(@ModelAttribute(value = "fichier") Fichier fichier, @ModelAttribute(value = "msg") Message msg, HttpServletRequest request, Model model) {
        if (demandeDao.existsByEmail(fichier.getRecepteur().getEmail()) == 0) {
            Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
            if (fichier.getRecepteur().getEmail().equals(u.getEmail())) {

            } else {
                XmlHelper xmlHelper = new XmlHelper();

                Utilisateur recepteur = new Utilisateur();
                Date now = new Date();
                String email = fichier.getRecepteur().getEmail();
                if (utilisateurDao.existsByEmail(email) == 0) {
                    recepteur.setNom(email);
                    recepteur.setPrenom(email);
                    recepteur.setEmail(email);
                    recepteur.setMdp(Util.generateUniqueToken());
                    utilisateurDao.save(recepteur);
                } else {
                    recepteur = utilisateurDao.findByEmail(email);
                }
                fichier.setRecepteur(recepteur);
                fichier.setEmetteur(u);
                msg.setDateEnvoie(now);
                msg.setType(Message.DEMANDE);
                msg.getDemande().setDateDebut(now);
                List<Message> messages = new ArrayList<>();
                messages.add(msg);
                fichier.setMessages(messages);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, 20);
                msg.setDateExpiration(calendar.getTime());
                demandeDao.save(msg.getDemande());
                fichier.setDateCreation(now);
                fichierDao.save(fichier);

                msg.setFichier(fichier);
                messageDao.save(msg);

                String xml = xmlHelper.creerDemandeXml(fichier);
                String path = "demande" + fichier.getMessages().get(0).getDemande().getId() + ".xml";
                xmlHelper.creerFichierXml(path, xml);
            }

        } else {

        }
        fichier = new Fichier();
        model.addAttribute("fichier", fichier);
        return "nouvelle-demande";
    }

    @GetMapping("/mes-demandes/envoye")
    public String propositionEnvoye(Model model, HttpServletRequest request) {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Fichier> demandes = fichierDao.getFichiersEnvoye(u.getEmail(), Message.DEMANDE);
        System.out.println("size of list: " + demandes.size());
        model.addAttribute("demandes", demandes);
        model.addAttribute("dmdRecus", false);
        return "demandes";
    }

    @GetMapping("/mes-demandes/recus")
    public String propositionRecus(Model model, HttpServletRequest request) {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Fichier> demandes = fichierDao.getFichiersRecus(u.getEmail(), Message.DEMANDE);
        System.out.println("size of list: " + demandes.size());
        model.addAttribute("demandes", demandes);
        model.addAttribute("dmdRecus", true);
        return "demandes";
    }


    @GetMapping("/process")
    public String processDemandes() {
        try {

            ClassPathResource cpr = new ClassPathResource("/static/imports/");
            ClassPathResource cprXsd = new ClassPathResource("/static/XSD_MTBC.xsd");
            final File folder = cpr.getFile();
            System.out.println(cpr.getPath());
            System.out.println(cpr.getFile().getPath());
            if (folder.isDirectory()) {
                for (final File fileEntry : folder.listFiles()) {
                    System.out.println("file name: " + fileEntry.getName());
                    if ((int) fileEntry.getTotalSpace() <= Message.MAX_SIZE) {
                        if (fichierDao.existsByFicId(xmlHelper.getFicId(fileEntry.getPath())) == 0) {
                            if (xmlHelper.validateAgainstXSD(new DataInputStream(new FileInputStream(fileEntry)), new DataInputStream(new FileInputStream(cprXsd.getFile())))) {
                                System.out.println("Valiiiiid");
                                if (xmlHelper.checkNumAuthAndCheckSum(fileEntry.getPath())) {
                                    System.out.println("test passed");
                                    xmlHelper.processFile(fileEntry.getPath());
                                } else {
                                    System.out.println("test not passed ...");
                                }
                            } else
                                System.out.println("not valid :(");
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "process";
    }

    @RequestMapping(value = "/demande/{action}", method = RequestMethod.POST)
    public @ResponseBody
    String processDemande(@RequestBody String dmdId, @PathVariable(value = "action") String action) {
        if ("accepte".equals(action) || "refuse".equals(action)) {
            JSONArray jsonArray = new JSONArray(dmdId);
            dmdId = jsonArray.getString(0);
            Optional<Demande> dmdOpt = demandeDao.findById(Integer.parseInt(dmdId));
            if (dmdOpt.isPresent()) {
                Demande demande = dmdOpt.get();
                if (action.equals("accepte"))
                    demande.setAuth(Demande.ACCEPTE);
                else
                    demande.setAuth(Demande.REFUSE);
                demande.setNumAuth(UUID.randomUUID().toString());
                demandeDao.save(demande);
                Fichier fichier = fichierDao.getByDemande(demande.getId());
                fichier.setFicId(Util.generateUniqueToken());

                String xml = xmlHelper.creerAuth(fichier);
                System.out.println(xml);
                xmlHelper.creerFichierXml("Auth_" + demande.getAuth() + ".xml", xml);
                return "[\"ok\"]";

            } else
                return "[\"ko\"]";
        }
        return "[\"ko\"]";

    }
}

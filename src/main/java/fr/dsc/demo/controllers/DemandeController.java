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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
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

        return "nouvelle-demande";
    }
    @PostMapping("/check-utilisateur")
    public @ResponseBody
    String checkUtilisateur(@RequestBody String email,HttpServletRequest request){
        if(utilisateurDao.existsByEmail(email).intValue() == 0)
            return "[\"0\"]";
            if(!demandeDao.getNumAuthByEmail(((Utilisateur) request.getSession().getAttribute("utilisateur")).getEmail(),utilisateurDao.findByEmail(email).getEmail()).isEmpty())
            return "[\"-1\"]";


        return "[\"1\"]";

    }
    @PostMapping("/nouvelle-demande")
    public String createDemande(@ModelAttribute(value = "fichier") Fichier fichier, @ModelAttribute(value = "msg") Message msg, HttpServletRequest request, Model model) {
        String alertMsg = "",alertType = "";
        if (demandeDao.existsByEmail(fichier.getRecepteur().getEmail()) == 0) {
            Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
            if (fichier.getRecepteur().getEmail().equals(u.getEmail())) {

            } else {
                XmlHelper xmlHelper = new XmlHelper();

                Utilisateur recepteur = fichier.getRecepteur();
                Date now = new Date();
                if (utilisateurDao.existsByEmail(recepteur.getEmail()) == 0) {
                    recepteur.setMdp(Util.generateUniqueToken());
                    utilisateurDao.save(recepteur);
                } else {
                    recepteur = utilisateurDao.findByEmail(recepteur.getEmail());
                }
                fichier.setRecepteur(recepteur);
                fichier.setEmetteur(u);
                msg.setDateEnvoie(now);
                msg.setType(Message.DEMANDE);
                msg.getDemande().setDateDebut(now);
                List<Message> messages = new ArrayList<>();
                msg.setDureeValid(Integer.toString(u.getDureeExpirationMsg()));
                messages.add(msg);
                fichier.setMessages(messages);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, u.getDureeExpirationMsg());
                msg.setDateExpiration(calendar.getTime());
                demandeDao.save(msg.getDemande());
                fichier.setDateCreation(now);
                fichierDao.save(fichier);

                msg.setFichier(fichier);
                messageDao.save(msg);

                String xml = xmlHelper.creerDemandeXml(fichier);
                String path = "demande" + fichier.getMessages().get(0).getDemande().getId() + ".xml";
                xmlHelper.creerFichierXml(path, xml);
                alertMsg = "vous trouverez les demandes dans la rubrique: <a href='/mes-demandes/envoye'> demandes envoyées</a>";
                alertType = "success";
            }

        } else {
            alertMsg = "L'utilisateur a déjà accepte une demande et vous pouvez désormais effectuer des trocs avec lui";
            alertType="error";
        }
        model.addAttribute("fichier", new Fichier());
        model.addAttribute("msg", new Message());
        model.addAttribute("showAlert", true);
        model.addAttribute("alertMsg", alertMsg);
        model.addAttribute("alertType", alertType);
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

    @GetMapping("/generate-xml-demande/{fic}")
    public void genererFichier(@PathVariable(value = "fic") String ficId, Model model, HttpServletResponse response){
        try {
            Optional<Fichier> optionalFichier = fichierDao.findById(Integer.parseInt(ficId));
            if (optionalFichier.isPresent()) {
                Fichier fichier = optionalFichier.get();
                String xmlString = xmlHelper.creerDemandeXml(fichier);
                xmlHelper.creerFichierXml("download_" + fichier.getId()+".xml", xmlString);
                ClassPathResource cpr = new ClassPathResource("/static/exports/download_" + fichier.getId()+".xml");
                File file = cpr.getFile();
                String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                if (mimeType == null) {
                    System.out.println("mimetype is not detectable, will take default");
                    mimeType = "application/octet-stream";
                }

                System.out.println("mimetype : " + mimeType);

                response.setContentType(mimeType);

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
                response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));


                /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
                //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

                response.setContentLength((int) file.length());

                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

                //Copy bytes from source to destination(outputstream in this example), closes both streams.
                FileCopyUtils.copy(inputStream, response.getOutputStream());

                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

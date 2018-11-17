package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.*;
import fr.dsc.demo.models.*;
import fr.dsc.demo.utilities.Util;
import fr.dsc.demo.utilities.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class TrocController {

    @Autowired
    UtilisateurDao utilisateurDao;

    @Autowired
    ObjetDao objetDao;

    @Autowired
    TrocDao trocDao;

    @Autowired
    MessageDao messageDao;

    @Autowired
    DemandeDao demandeDao;

    @Autowired
    FichierDao fichierDao;

    @Autowired
    XmlHelper xmlHelper;

    @GetMapping("/nouvelle-proposition")
    public String renderCreatePropositionPage(Model model) {
        model.addAttribute("contreProp", false);

        model.addAttribute("uAuth", utilisateurDao.getUtilisateursAuth());
        return "nouvelle-proposition";
    }

    @RequestMapping(value = "/nouvelle-proposition", method = RequestMethod.POST)
    public @ResponseBody
    String createProposition(@RequestBody String propoString, HttpServletRequest request) {
        System.out.println(propoString);


        JSONObject propo = new JSONObject(propoString);
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        System.out.println(propo.get("destinataires").toString());
        JSONArray jsonArrayRecep = new JSONArray(propo.get("destinataires").toString());


        Date now = new Date();
        XmlHelper xmlHelper = new XmlHelper();
        JSONArray jsonArrayProps = new JSONArray(propo.get("props").toString());

        for (int k = 0; k < jsonArrayRecep.length(); k++) {
            Fichier fichier = new Fichier();
            fichier.setRecepteur(utilisateurDao.findByEmail(jsonArrayRecep.getString(k)));
            fichier.setEmetteur(u);
            fichierDao.save(fichier);

            String numAuth = demandeDao.getNumAuthByEmail(u.getEmail(), jsonArrayRecep.getString(k));
            List<Message> messageList = new ArrayList<>();
            String xml = "";
            for (int j = 0; j < jsonArrayProps.length(); j++) {
                Troc troc = new Troc();

                System.out.println("props here");
                String jsonString = jsonArrayProps.get(j).toString();
                JSONObject prop = new JSONObject(jsonString);
                System.out.println(jsonString);
                System.out.println(prop);

                String type = prop.get("typePropo").toString();
                System.out.println(type);
                JSONArray jsonArray = new JSONArray(prop.get("objets").toString());
                List<Objet> objetList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    System.out.println(jsonArray.get(i).toString());
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    Objet objet = new Objet();
                    objet.setNom(jsonObject.get("nom").toString());
                    objet.setType(jsonObject.get("type").toString());
                    objet.setValeur(jsonObject.get("valeur").toString());
                    objet.setOffre(troc);
                    objetList.add(objet);
                }
                troc.setType(type);
                if (type.equals(Troc.DEMANDE))
                    troc.setDemandes(objetList);
                else if (type.equals(Troc.OFFRE))
                    troc.setOffres(objetList);
                trocDao.save(troc);
                //creation message
                Message message = new Message();
                message.setDateEnvoie(now);
                message.setStatus(true);
                message.setDureeValid("20");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, 20);
                message.setDateExpiration(calendar.getTime());
                message.setType(Message.PROPOSITION);
                message.setTroc(troc);
                message.setFichier(fichier);
                messageDao.save(message);
                objetDao.saveAll(objetList);
                messageList.add(message);

            }
            fichier.setMessages(messageList);
            fichierDao.save(fichier);

            xml = xmlHelper.creerPropXml(fichier, numAuth);
            xmlHelper.creerFichierXml("proposition" + fichier.getRecepteur().getNom() + "_" + fichier.getFicId() + ".xml", xml);


        }
        return "[\"ok\"]";
    }

    @GetMapping("/mes-proposition/envoye")
    public String propositionEnvoye(Model model, HttpServletRequest request) {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Fichier> fichiers = fichierDao.getFichiersEnvoye(u.getEmail(), Message.PROPOSITION);
        System.out.println("size of list: " + fichiers.size());
        model.addAttribute("props", fichiers);
        model.addAttribute("propRecus", false);

        return "props";
    }

    @GetMapping("/mes-proposition/recus")
    public String propositionRecus(Model model, HttpServletRequest request) {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Fichier> fichiers = fichierDao.getFichiersRecus(u.getEmail(), Message.PROPOSITION);
        System.out.println("size of list: " + fichiers.size());
        model.addAttribute("props", fichiers);
        model.addAttribute("propRecus", true);

        return "props";
    }

    @RequestMapping(value = "/prop/{action}", method = RequestMethod.POST)
    public @ResponseBody
    String processProp(@RequestBody String propId, @PathVariable(value = "action") String action) {
        JSONArray jsonArray = new JSONArray(propId);
        propId = jsonArray.getString(0);
        switch (action) {
            case "accepte":
                System.out.println("Im in accepte");
                Optional<Fichier> fichierOpt = fichierDao.findById(Integer.parseInt(propId));
                if (fichierOpt.isPresent()) {
                    System.out.println("file found");
                    Fichier fichier = fichierOpt.get();
                    fichier.setStatusProp(Demande.ACCEPTE);
                    fichierDao.save(fichier);
                    String xml = xmlHelper.creerAccep(fichier);
                    xmlHelper.creerFichierXml("Acceptation_fichier_" + fichier.getFicId() + ".xml", xml);
                }
                break;
            case "refuse":
                break;
            case "forward":
                break;
        }
        return "[\"ok\"]";

    }

    @GetMapping("/proposition/repondre/{ficId}")
    public String contreProp(@PathVariable(value = "ficId") String ficId, Model model) {
        Optional<Fichier> fichierOpt = fichierDao.findById(Integer.parseInt(ficId));
        if (fichierOpt.isPresent()) {
            Fichier fichier = fichierOpt.get();
            model.addAttribute("fic", fichier);
            model.addAttribute("contreProp", true);
        }

        return "contre-proposition";
    }

    @RequestMapping(value = "/contre-proposition", method = RequestMethod.POST)
    public @ResponseBody
    String createContreProposition(@RequestBody String propoString, HttpServletRequest request) {
        System.out.println(propoString);
        JSONArray cps = new JSONArray(propoString);
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < cps.length(); i++) {
            JSONObject contreProp = new JSONObject(cps.get(i).toString());
            System.out.println("ContreProp: " + contreProp);
            Troc trocSrc = trocDao.findById(contreProp.getInt("contrePropSrc")).get();
            Message msgSrc = messageDao.findById(contreProp.getInt("msg")).get();
            Troc troc = new Troc();
            troc.setTitre(contreProp.getString("contrePropTitre"));
            troc.setParent(trocSrc);

            JSONArray jsonArray = new JSONArray(contreProp.get("contrePropObjets").toString());
            List<Objet> objetList = new ArrayList<>();
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(j).toString());
                Objet objet = new Objet();
                objet.setNom(jsonObject.get("nom").toString());
                objet.setType(jsonObject.get("type").toString());
                objet.setValeur(jsonObject.get("valeur").toString());
                objet.setOffre(troc);
                objetList.add(objet);
            }
            if (contreProp.getString("typePropo").equals(Troc.DEMANDE)) {
                troc.setDemandes(objetList);
                troc.setType(Troc.DEMANDE);
            } else if (contreProp.getString("typePropo").equals(Troc.OFFRE)) {
                troc.setOffres(objetList);
                troc.setType(Troc.OFFRE);
            }
            List<Troc> trocs = trocSrc.getContreProps();
            trocs.add(troc);
            trocSrc.setStatus("2");
            trocSrc.setContreProps(trocs);
            trocDao.save(troc);
            objetDao.saveAll(objetList);
            trocDao.save(trocSrc);
            msgSrc.setTroc(troc);
            messages.add(msgSrc);



        }
        Fichier fichier = messages.get(0).getFichier();
        fichier.setFicId(Util.generateUniqueToken());
        fichier.setMessages(messages);
        String xml = xmlHelper.creerContreProp(fichier,demandeDao.getNumAuthByEmail(fichier.getRecepteur().getEmail(),fichier.getEmetteur().getEmail()));
        xmlHelper.creerFichierXml("contreProp_"+fichier.getFicId()+".xml",xml);
        return "[\"ok\"]";
    }

}

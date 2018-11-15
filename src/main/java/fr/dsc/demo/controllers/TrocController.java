package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.*;
import fr.dsc.demo.models.Message;
import fr.dsc.demo.models.Objet;
import fr.dsc.demo.models.Troc;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.utilities.Util;
import fr.dsc.demo.utilities.XmlHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @RequestMapping(value = "/nouvelle-proposition", method = RequestMethod.GET)
    public String renderCreatePropositionPage(Model model) {

        Message message = new Message();
        message.setType(Message.PROPOSITION);
        model.addAttribute("msg", message);
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
        List<Troc> trocs = new ArrayList<>();
        for (int k = 0; k < jsonArrayRecep.length(); k++) {
            Message message = new Message();
            message.setRecepteur(utilisateurDao.findByEmail(jsonArrayRecep.getString(k)));
            message.setEmetteur(u);
            message.setMsgId(Util.generateUniqueToken());
            message.setDateEnvoie(now);
            message.setStatus(true);
            message.setDureeValid("20");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 20);
            message.setDateExpiration(calendar.getTime());
            message.setType(Message.PROPOSITION);
            messageDao.save(message);
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
                trocs.add(troc);
                troc.setMessage(message);
                trocDao.save(troc);
                objetDao.saveAll(objetList);


            }
            message.setTroc(trocs);
            messageDao.save(message);
            String numAuth = demandeDao.getNumAuthByEmail(u.getEmail(), jsonArrayRecep.getString(k));
            String xml = xmlHelper.creerPropXml(message,numAuth);
            xmlHelper.creerFichierXml("proposition" + message.getId() + message.getRecepteur().getNom() + ".xml", xml);

        }
        return "[\"ok\"]";
    }

    @GetMapping("/mes-proposition/envoye")
    public String propositionEnvoye(Model model, HttpServletRequest request) {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Message> messages = messageDao.getMessagesEnvoye(u.getEmail(), Message.PROPOSITION);
        System.out.println("size of list: " + messages.size());
        model.addAttribute("props", messages);
        return "sent-prop";
    }


}

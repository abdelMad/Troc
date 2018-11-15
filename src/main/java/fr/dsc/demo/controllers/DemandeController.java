package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.DemandeDao;
import fr.dsc.demo.dao.MessageDao;
import fr.dsc.demo.dao.UtilisateurDao;
import fr.dsc.demo.models.Demande;
import fr.dsc.demo.models.Message;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.utilities.Util;
import fr.dsc.demo.utilities.XmlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class DemandeController {

    @Autowired
    UtilisateurDao utilisateurDao;
    @Autowired
    MessageDao messageDao;
    @Autowired
    DemandeDao demandeDao;

    @RequestMapping(value = "/nouvelle-demande", method = RequestMethod.GET)
    public String renderCreateDemandePage(Model model, HttpServletRequest request) {
        Message message = new Message();
        model.addAttribute("msg", message);
        message.setType(Message.DEMANDE);
        System.out.println();
//            System.out.println(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(""));
        return "nouvelle-demande";
    }

    @RequestMapping(value = "/nouvelle-demande", method = RequestMethod.POST)
    public String createDemande(@ModelAttribute(value = "msg") Message message, HttpServletRequest request,Model model) {
        if (demandeDao.existsByEmail(message.getRecepteur().getEmail()) == 0) {
            Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
            if (message.getRecepteur().getEmail().equals(u.getEmail())) {

            } else {
                XmlHelper xmlHelper = new XmlHelper();

                Utilisateur recepteur = new Utilisateur();
                Date now = new Date();
                String email = message.getRecepteur().getEmail();
                if (utilisateurDao.existsByEmail(email) == 0) {
                    recepteur.setNom(email);
                    recepteur.setPrenom(email);
                    recepteur.setEmail(email);
                    recepteur.setMdp(Util.generateUniqueToken());
                    utilisateurDao.save(recepteur);
                } else {
                    recepteur = utilisateurDao.findByEmail(email);
                }
                message.setRecepteur(recepteur);
                message.setEmetteur(u);
                message.setMsgId(Util.generateUniqueToken());
                message.setDateEnvoie(now);
                message.setStatus(true);
                message.setType(Message.DEMANDE);
                message.getDemande().setDateDebut(now);
                System.out.println(message.getDemande().getDateFin());
                message.setDureeValid("20");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, 20);
                message.setDateExpiration(calendar.getTime());
                demandeDao.save(message.getDemande());
                messageDao.save(message);

                String xml = xmlHelper.creerDemandeXml(message);
                String path = "demande" + message.getDemande().getId() + ".xml";
                xmlHelper.creerFichierXml(path, xml);
            }

        } else {

        }
        message = new Message();
        model.addAttribute("msg", message);
        return "nouvelle-demande";
    }

    @GetMapping("/mes-demandes/envoye")
    public String propositionEnvoye(Model model,HttpServletRequest request){
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        List<Message> demandes = messageDao.getMessagesEnvoye(u.getEmail(),Message.DEMANDE);
        System.out.println("size of list: "+demandes.size());
        model.addAttribute("demandes",demandes);
        return "sent-demandes";
    }



    @GetMapping("/process")
    public String processDemandes(){
        try {
            XmlHelper xmlHelper = new XmlHelper();

            ClassPathResource cpr = new ClassPathResource("/static/imports/");
            ClassPathResource cprXsd = new ClassPathResource("/static/XSD_MTBC.xsd");
            final File folder = cpr.getFile();
            System.out.println(cpr.getPath());
            System.out.println(cpr.getFile().getPath());
            if(folder.isDirectory()) {
                for (final File fileEntry : folder.listFiles()) {
                    if((int)fileEntry.getTotalSpace() <= Message.MAX_SIZE){
                        if (xmlHelper.validateAgainstXSD(new DataInputStream(new FileInputStream(fileEntry)), new DataInputStream(new FileInputStream(cprXsd.getFile())))) {
                            System.out.println("Valiiiiid");
                            if (xmlHelper.checkNumAuthAndCheckSum(fileEntry.getPath())) {
                                System.out.println("test passed");

                            }else{
                                System.out.println("test not passe ...");
                            }
                        }
                        else
                            System.out.println("not valid :(");
                        System.out.println(fileEntry.getName());
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "process";
    }
}

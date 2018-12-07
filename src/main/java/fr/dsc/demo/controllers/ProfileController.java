package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.FichierDao;
import fr.dsc.demo.dao.UtilisateurDao;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UtilisateurDao utilisateurDao;

    @Autowired
    private FichierDao fichierDao;

    @GetMapping("/profil")
    public String profileRender(Model model, HttpServletRequest request) {

        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        u.setLoginStatus("");
        model.addAttribute("u", u);
        model.addAttribute("sentFiles", fichierDao.countSentFiles(u.getId()));
        model.addAttribute("receivedFiles", fichierDao.countReceivedFiles(u.getId()));
        return "profile";
    }

    @PostMapping("/profil/edit")
    public String modifierProfile(@ModelAttribute("u") Utilisateur u, HttpServletRequest request) {

        Utilisateur currU = (Utilisateur) request.getSession().getAttribute("utilisateur");
        if (utilisateurDao.findByEmailAndMdp(currU.getEmail(), Util.hashString(u.getMdp())) != null) {
            System.out.println(u.getNom());
            System.out.println(u.getPrenom());
            System.out.println(u.getEmail());
            u.setId(currU.getId());
            if (u.getLoginStatus() != "")
                u.setMdp(Util.hashString(u.getLoginStatus()));

            if (u.getEmail().equals(""))
                u.setEmail(currU.getEmail());
            if (u.getNom().equals(""))
                u.setEmail(currU.getNom());

            if (u.getPrenom().equals(""))
                u.setEmail(currU.getPrenom());

            if (u.getPrenom().equals(""))
                u.setEmail(currU.getPrenom());

            utilisateurDao.save(u);
            request.getSession().setAttribute("utilisateur", u);
        } else {
            System.out.println("mal9it walo");
        }

        return "redirect:/profil";
    }

    @PostMapping("/profil/configure")
    public String configureProfile(@ModelAttribute("u") Utilisateur u, HttpServletRequest request) {
        System.out.println(u.getDureeExpirationMsg());
        if (u.getDureeExpirationMsg() > 0 && u.getDureeExpirationMsg() <= 180) {
            Utilisateur utCourant = (Utilisateur) request.getSession().getAttribute("utilisateur");
            utCourant.setDureeExpirationMsg(u.getDureeExpirationMsg());
            utilisateurDao.save(utCourant);
        }

        return "redirect:/profil";
    }
}

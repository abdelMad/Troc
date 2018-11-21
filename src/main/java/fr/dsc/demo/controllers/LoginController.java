package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.UtilisateurDao;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class LoginController {
    @Autowired
    private UtilisateurDao utilisateurDao;
    private String renderedPage = "default";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String LoginPageRender(Model model) {
        renderedPage = "login";
        Utilisateur utilisateur = new Utilisateur();
        model.addAttribute("utilisateur", utilisateur);
        return renderedPage;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String ProcessLogin(@ModelAttribute(value = "utilisateur") Utilisateur utilisateur, HttpServletRequest request) {
        renderedPage = "redirect:";
        Utilisateur loggedInUser = utilisateurDao.findByEmailAndMdp(utilisateur.getEmail(), Util.hashString(utilisateur.getMdp()));
        if (loggedInUser != null) {
            request.getSession().setAttribute("utilisateur", loggedInUser);
        } else {
            renderedPage = "login";
        }
        return renderedPage;
    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String RegisterPageRender(Model model) {
        renderedPage = "register";
        Utilisateur utilisateur = new Utilisateur();
        model.addAttribute("utilisateur", utilisateur);
        return renderedPage;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String ProcessRegister(@ModelAttribute(value = "utilisateur") Utilisateur utilisateur, HttpServletRequest request, Model model) {
        renderedPage = "redirect:";
        if (utilisateur.getEmail().isEmpty() || utilisateur.getMdp().isEmpty() || utilisateur.getNom().isEmpty()
                || utilisateur.getPrenom().isEmpty() || utilisateur.getLoginStatus().isEmpty()) {
            renderedPage = "register";
            model.addAttribute("msgError", "tout les champs sont obligatoirs");
        }
        if (utilisateurDao.existsByEmail(utilisateur.getEmail()) == 0) {
            if (utilisateur.getMdp().equals(utilisateur.getLoginStatus())) {
                Date now = new Date();
                utilisateur.setMdp(Util.hashString(utilisateur.getMdp()));
                utilisateur.setLoginStatus(now.toString());
                utilisateur.setRegisterDate(now);
                utilisateurDao.save(utilisateur);
                request.getSession().setAttribute("utilisateur", utilisateur);
            } else {
                renderedPage = "register";
                model.addAttribute("msgError", "Les mots de passes entre ne sont pas identiques");
            }
        } else {
            renderedPage = "register";
            model.addAttribute("msgError", "Adresse email existe deja");
        }
        return renderedPage;
    }

    @GetMapping("/logout")
    public String deconnexion(HttpServletRequest request) {
        request.getSession().removeAttribute("utilisateur");
        return "redirect:/login";
    }





}

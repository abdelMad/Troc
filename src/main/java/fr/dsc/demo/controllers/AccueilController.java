package fr.dsc.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccueilController {

    @RequestMapping("/")
    public String renderAccueil(){
        return "acceuil";
    }
}

package fr.dsc.demo.controllers;

import fr.dsc.demo.models.Utilisateur;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MessageController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    @RequestMapping("/greeting")
    public Utilisateur greeting(@RequestParam(value="name", defaultValue="World") String name) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        return utilisateur;
    }

}

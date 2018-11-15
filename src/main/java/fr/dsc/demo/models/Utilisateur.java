package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String email;
    private String mdp;
    private String nom;
    private String prenom;
    private String loginStatus;
    private Date registerDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String image;
    @OneToMany(mappedBy = "emetteur")
    private List<Message> messagesEnvoyes;
    @OneToMany(mappedBy = "recepteur")
    private List<Message> messagesRecus;


}

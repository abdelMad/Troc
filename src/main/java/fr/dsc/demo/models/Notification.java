package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
public class Notification {
    @Id @GeneratedValue
    private int id;

    private String message;

    private String url;

    private boolean status;
    @ManyToOne
    @JoinColumn(name = "utilisateur")
    private Utilisateur utilisateur;

    public Notification(){
        status = false;
    }
}

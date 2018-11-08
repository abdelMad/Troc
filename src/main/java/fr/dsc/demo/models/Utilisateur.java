package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
@Data
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String email;
    private String mdp;
    private String nom;
    private String prenom;
    private String loginStatus;
    private boolean emailChecked;
    private Date registerDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String image;
    @OneToMany(mappedBy = "emetteur")
    private Set<Demande> demandesEmis;
    @OneToMany(mappedBy = "recepteur")
    private Set<Demande> demandesRecus;
    @OneToMany(mappedBy = "emetteur")
    private Set<Message> messagesEnvoyes;
    @OneToMany(mappedBy = "recepteur")
    private Set<Message> messagesRecus;
    @OneToMany(mappedBy = "emetteur")
    private Set<Troc> trocsEnvoyes;
    @OneToMany(mappedBy = "recepteur")
    private Set<Troc> trocsRecus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public boolean isEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Demande> getDemandesEmis() {
        return demandesEmis;
    }

    public void setDemandesEmis(Set<Demande> demandesEmis) {
        this.demandesEmis = demandesEmis;
    }

    public Set<Demande> getDemandesRecus() {
        return demandesRecus;
    }

    public void setDemandesRecus(Set<Demande> demandesRecus) {
        this.demandesRecus = demandesRecus;
    }

    public Set<Message> getMessagesEnvoyes() {
        return messagesEnvoyes;
    }

    public void setMessagesEnvoyes(Set<Message> messagesEnvoyes) {
        this.messagesEnvoyes = messagesEnvoyes;
    }

    public Set<Message> getMessagesRecus() {
        return messagesRecus;
    }

    public void setMessagesRecus(Set<Message> messagesRecus) {
        this.messagesRecus = messagesRecus;
    }

    public Set<Troc> getTrocsEnvoyes() {
        return trocsEnvoyes;
    }

    public void setTrocsEnvoyes(Set<Troc> trocsEnvoyes) {
        this.trocsEnvoyes = trocsEnvoyes;
    }

    public Set<Troc> getTrocsRecus() {
        return trocsRecus;
    }

    public void setTrocsRecus(Set<Troc> trocsRecus) {
        this.trocsRecus = trocsRecus;
    }
}

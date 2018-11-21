package fr.dsc.demo.models;

import fr.dsc.demo.utilities.Util;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Message {
    @Transient
    public static final String DEMANDE = "Dmd";
    @Transient
    public static final String PROPOSITION = "Prop";
    @Transient
    public static final String AUTORISATION = "Auth";
    @Transient
    public static final String ACCEPTATION = "Accep";
    @Transient
    public static final int MAX_SIZE = 40000;

    @Id
    @GeneratedValue
    private int id;
    private String msgId;
    @Column(columnDefinition = "TEXT")
    private String text;
    @OneToOne(fetch = FetchType.EAGER)
    private Troc troc;
    @OneToOne(fetch = FetchType.EAGER)
    private Demande demande;

    @ManyToOne
    @JoinColumn(name = "fichier")
    private Fichier fichier;
    private Date dateEnvoie;
    private String dureeValid;
    private Date dateExpiration;
    // valide ou perime
    private boolean status;

    //parmis les 4 en haut
    private String type;

    public Message() {
        status = true;
        msgId = "m"+UUID.randomUUID().toString();
        dureeValid = "20";
    }
}

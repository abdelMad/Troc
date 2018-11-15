package fr.dsc.demo.models;

import fr.dsc.demo.utilities.Util;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    @Column(unique = true)
    private String msgId;
    @Column(columnDefinition = "TEXT")
    private String text;
    @ManyToOne
    @JoinColumn(name = "emetteur")
    private Utilisateur emetteur;
    @ManyToOne
    @JoinColumn(name = "recepteur")
    private Utilisateur recepteur;
    @OneToMany(mappedBy = "message")
    private List<Troc> troc;
    private String titreProp;
    @OneToOne
    private Demande demande;
    private Date dateEnvoie;
    private String dureeValid;
    private Date dateExpiration;
    // valide ou perime
    private boolean status = true;

    //demande troc
    private String type;

    public Message() {
        msgId = Util.generateUniqueToken();
    }
}

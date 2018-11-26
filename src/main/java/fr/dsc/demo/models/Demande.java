package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Demande {
    @Transient
    public static final String ACCEPTE = "1";
    @Transient
    public static final String REFUSE = "-1";
    @Id @GeneratedValue
    private int id;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String  auth = "0";
    private String numAuth;


}

package fr.dsc.demo.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class FichierTraite {
    @Id
    @GeneratedValue
    private int id;
    private String ficId;
}

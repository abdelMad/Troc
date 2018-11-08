package fr.dsc.demo.dao;

import fr.dsc.demo.models.Demande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDao extends JpaRepository<Demande,Integer> {
}

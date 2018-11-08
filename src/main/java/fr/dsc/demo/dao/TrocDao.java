package fr.dsc.demo.dao;

import fr.dsc.demo.models.Troc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrocDao extends JpaRepository<Troc,Integer> {
}

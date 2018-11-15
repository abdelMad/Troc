package fr.dsc.demo.dao;

import fr.dsc.demo.models.Troc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface TrocDao extends JpaRepository<Troc,Integer> {
}

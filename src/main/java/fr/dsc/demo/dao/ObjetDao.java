package fr.dsc.demo.dao;

import fr.dsc.demo.models.Objet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface ObjetDao extends CrudRepository<Objet,Integer> {
}

package fr.dsc.demo.dao;

import fr.dsc.demo.models.FichierTraite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface FichierTraiteDao extends CrudRepository<FichierTraite, Integer> {
}

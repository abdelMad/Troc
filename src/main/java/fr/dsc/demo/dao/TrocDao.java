package fr.dsc.demo.dao;

import fr.dsc.demo.models.Troc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface TrocDao extends JpaRepository<Troc,Integer> {
//    @Query("SELECT DISTINCT * FROM troc t JOIN message m ON t.id=m.troc_id JOIN fichier f ON f.id=m.fichier WHERE t.id NOT IN (SELECT parent_id FROM troc WHERE parent_id is NOT NULL)")
//    List<Troc>


    @Query("SELECT (t) FROM Troc t JOIN t.parent p WHERE p.id=:propoId")
    Troc getContreProposByPropo(@Param("propoId") int propoId);
}

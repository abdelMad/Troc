package fr.dsc.demo.dao;

import fr.dsc.demo.models.Demande;
import fr.dsc.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
public interface DemandeDao extends JpaRepository<Demande,Integer> {

    @Query("SELECT DISTINCT count(d) FROM Message m JOIN Fichier f ON m.fichier=f JOIN Demande d  ON m.demande=d JOIN Utilisateur u ON f.recepteur=u WHERE  u.email=(:email)")
    Long existsByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT d.numAuth FROM Message m JOIN Fichier f ON m.fichier=f JOIN Demande d  ON m.demande=d JOIN Utilisateur r ON f.recepteur=r JOIN Utilisateur em ON f.emetteur = em WHERE (em.email=(:emetteur) AND r.email=(:recepteur)) OR (r.email=(:emetteur) AND em.email=(:recepteur))")
    String getNumAuthByEmail(@Param("emetteur") String emetteur,@Param("recepteur") String recepteur);

    @Query("SELECT DISTINCT (d) FROM Message m JOIN Demande d ON m.demande=d WHERE  m.msgId=:msgId")
    Demande findByMsgId(@Param("msgId") String msgId);
}

package fr.dsc.demo.dao;

import fr.dsc.demo.models.Fichier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface FichierDao extends CrudRepository<Fichier, Integer> {
    @Query("SELECT DISTINCT (f) FROM Fichier f JOIN Message m ON m.fichier=f JOIN Utilisateur u ON f.emetteur=u WHERE u.email=:email AND m.type=:type")
    List<Fichier> getFichiersEnvoye(@Param("email") String email, @Param("type") String type);

    @Query("SELECT DISTINCT (f) FROM Fichier f JOIN Message m ON m.fichier=f JOIN Utilisateur u ON f.recepteur=u WHERE u.email=:email AND m.type=:type")
    List<Fichier> getFichiersRecus(@Param("email") String email, @Param("type") String type);

    @Query("SELECT count(f) FROM Fichier f WHERE f.ficId=:ficId")
    Long existsByFicId(@Param("ficId") String ficId);

    @Query("SELECT DISTINCT (f) FROM Fichier f JOIN Message m ON m.fichier=f JOIN Utilisateur u ON f.emetteur=u JOIN Demande d ON m.demande =d WHERE d.id=:dmd")
    Fichier getByDemande(@Param("dmd") int dmd);

    @Query("SELECT COUNT (f) FROM Fichier f JOIN Utilisateur u ON f.emetteur=u WHERE u.id=:id")
    Integer countSentFiles(@Param("id") int id);

    @Query("SELECT COUNT (f) FROM Fichier f JOIN Utilisateur u ON f.recepteur=u WHERE u.id=:id")
    Integer countReceivedFiles(@Param("id") int id);

//    @Query("SELECT DISTINCT (f) FROM Fichier f JOIN Message m ON m.fichier=f JOIN Utilisateur u ON f.emetteur=u JOIN Demande d ON m.demande =d " +
//            "JOIN Troc t ON m.troc=t JOIN objet od ON od.demande=t JOIN Objet op ON op.offre=t WHERE t.id=:troc")
//    Fichier getByTroc(@Param("troc") int troc);


}

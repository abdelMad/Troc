package fr.dsc.demo.dao;

import fr.dsc.demo.models.Demande;
import fr.dsc.demo.models.Utilisateur;
import fr.dsc.demo.models.Fichier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface UtilisateurDao extends CrudRepository<Utilisateur, Integer> {
    @Query("SELECT count (u) FROM Utilisateur u WHERE u.email = (:email)")
    Long existsByEmail(@Param("email") String email);

    @Query("SELECT  u FROM Utilisateur u WHERE u.email = (:email)")
    Utilisateur findByEmail(@Param("email") String email);

    @Query("SELECT  u FROM Utilisateur u WHERE u.email = (:email) AND u.mdp = (:mdp)")
    Utilisateur findByEmailAndMdp(@Param("email") String email, @Param("mdp") String mdp);
    @Query(value = "SELECT * FROM utilisateur u JOIN fichier fe ON fe.emetteur = u.id JOIN message me ON fe.id=me.fichier JOIN demande de ON me.demande_id=de.id WHERE u.email<>:email AND de.auth='"+Demande.ACCEPTE+"'" +
            " UNION " +
            " SELECT * FROM utilisateur u JOIN fichier fr ON fr.recepteur=u.id JOIN message mr ON fr.id=mr.fichier JOIN demande d ON mr.demande_id=d.id WHERE u.email<>:email  AND d.auth='"+Demande.ACCEPTE+"'",nativeQuery = true)
    List<Utilisateur> getUtilisateursAuth(@Param("email") String email);
}

package fr.dsc.demo.dao;

import fr.dsc.demo.models.Demande;
import fr.dsc.demo.models.Utilisateur;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface UtilisateurDao extends CrudRepository<Utilisateur,Integer> {
    @Query("SELECT count (u) FROM Utilisateur u WHERE u.email = (:email)")
    Long existsByEmail(@Param("email") String email);

    @Query("SELECT  u FROM Utilisateur u WHERE u.email = (:email)")
    Utilisateur findByEmail(@Param("email") String email);

    @Query("SELECT  u FROM Utilisateur u WHERE u.email = (:email) AND u.mdp = (:mdp)")
    Utilisateur findByEmailAndMdp(@Param("email") String email,@Param("mdp") String mdp);

    @Query("SELECT u FROM Utilisateur u JOIN Fichier f ON f.recepteur=u JOIN Message m ON m.fichier=f JOIN Demande d ON d=m.demande WHERE d.auth='"+Demande.ACCEPTE+"'")
    List<Utilisateur> getUtilisateursAuth();
}

package fr.dsc.demo.dao;

import fr.dsc.demo.models.Utilisateur;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UtilisateurDao extends CrudRepository<Utilisateur,Integer> {
    @Query("SELECT count (u) FROM Utilisateur u WHERE u.email = (:email)")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT  u FROM Utilisateur u WHERE u.email = (:email) AND u.mdp = (:mdp)")
    Utilisateur findByEmailAndMdp(@Param("email") String email,@Param("mdp") String mdp);
}

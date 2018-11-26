package fr.dsc.demo.dao;

import fr.dsc.demo.models.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface NotifDao extends CrudRepository<Notification, Integer> {

    @Query("SELECT (n) FROM Notification n JOIN Utilisateur u ON n.utilisateur=u WHERE u.email=:email")
    List<Notification> findAllByUtilisateur(@Param("email") String email);
}

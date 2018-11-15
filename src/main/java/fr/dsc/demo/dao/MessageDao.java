package fr.dsc.demo.dao;

import fr.dsc.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface MessageDao extends JpaRepository<Message, Integer> {
    @Query("SELECT DISTINCT (m) FROM Message m JOIN Utilisateur u ON m.recepteur=u WHERE u.email<>:email AND m.type=:type")
    List<Message> getMessagesEnvoye(@Param("email") String email,@Param("type") String type);



}

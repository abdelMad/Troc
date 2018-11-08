package fr.dsc.demo.dao;

import fr.dsc.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageDao extends JpaRepository<Message,Integer> {
}

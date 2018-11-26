package fr.dsc.demo.dao;

import fr.dsc.demo.models.Message;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(rollbackFor = Exception.class)
public interface MessageDao extends JpaRepository<Message, Integer> {

@Query("SELECT (m) FROM Message m WHERE m.msgId=:msgId ORDER BY m.id DESC ")
 List<Message> findByMsgId(@Param("msgId") String msgId, Pageable pageable);

    default List<Message> findByMsgId(String msgId) {
        return findByMsgId(msgId, new PageRequest(0,1));
    }
}

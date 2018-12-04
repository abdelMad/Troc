package fr.dsc.demo.controllers;

import fr.dsc.demo.dao.NotifDao;
import fr.dsc.demo.models.Notification;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    private NotifDao notifDao;

    @PostMapping("/set-notification-status")
    public @ResponseBody
    String setNotificationStatus(@RequestBody String jsonString) {
        JSONArray jsonArray = new JSONArray(jsonString);
        Optional<Notification> notificationOptional = notifDao.findById(Integer.parseInt(jsonArray.get(0).toString()));
        if(notificationOptional.isPresent()){
            Notification notification = notificationOptional.get();
            notification.setStatus(true);
            notifDao.save(notification);
        }else
            return "[\"notifNotFound\"]";

        return "[\"ok\"]";
    }
}

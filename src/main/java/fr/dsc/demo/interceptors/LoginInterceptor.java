package fr.dsc.demo.interceptors;

import fr.dsc.demo.dao.NotifDao;
import fr.dsc.demo.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private NotifDao notifDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Utilisateur u = (Utilisateur) request.getSession().getAttribute("utilisateur");
        if (u == null) {
            System.out.println("Im in interceptor");
            response.sendRedirect("/login");
            return false;
        }
        System.out.println(notifDao == null);
        request.getSession().setAttribute("notifs", notifDao.findAllByUtilisateur(u.getEmail()));
        request.getSession().setAttribute("notifsCount", notifDao.countUnreadNotifs(u.getEmail()));
        System.out.println("Im in interceptor");
        return true;
    }
}

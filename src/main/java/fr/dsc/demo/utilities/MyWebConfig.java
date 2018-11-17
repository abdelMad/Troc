package fr.dsc.demo.utilities;

import fr.dsc.demo.interceptors.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors (InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(new String[] {"/","/nouvelle-demande","/nouvelle-proposition","/mes-proposition/envoye","/mes-demandes/envoye","/mes-demandes/recus","/mes-proposition/recus"});
    }
}

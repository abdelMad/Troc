package fr.dsc.demo.utilities;

import fr.dsc.demo.interceptors.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors (InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor()).addPathPatterns(new String[] {"/","/nouvelle-demande","/nouvelle-proposition","/mes-proposition/envoye","/mes-demandes/envoye","/mes-demandes/recus","/mes-proposition/recus","/profil"});
    }
}

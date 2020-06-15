package br.com.scampini.itau.sre.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@Configuration
public class TwitterAutoConfiguration {

    @Bean
    public Twitter getConnection() {
         return TwitterFactory.getSingleton();
    }

}

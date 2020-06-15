package br.com.scampini.itau.sre.api.twitter.statistics;

//imports as static

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statistics;

    @GetMapping(path="/users/most-followed", produces = MediaType.APPLICATION_JSON_VALUE)
    private String getMostFollowedUsers(){
        Gson json = new Gson();
        return json.toJson(statistics.getMostFollowedUsers());
    }

    @GetMapping(path="/tweets/total-by-hour-of-day", produces = MediaType.APPLICATION_JSON_VALUE)
    private String getTotalTweetsByDateTime(){
        Gson json = new Gson();
        return json.toJson(statistics.getTotalTweetsByDateTime());
    }

    @GetMapping(path="/tweets/total-by-language", produces = MediaType.APPLICATION_JSON_VALUE)
    private String getTotalTweetsByLanguage(){
        Gson json = new Gson();
        return json.toJson(statistics.getTotalTweetByUserLanguage());
    }

}

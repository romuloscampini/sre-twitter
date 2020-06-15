package br.com.scampini.itau.sre.api.twitter.collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweet/collector")
public class TweetCollectorController {

    @Autowired
    private TweetCollectorService tweetCollector;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> fetchTweetsFromHashTag(@RequestParam("hashtag") String hashtag) throws Exception {
        return new ResponseEntity<>(tweetCollector.fetchTweetsFromHashTag(hashtag), HttpStatus.CREATED);
    }
}

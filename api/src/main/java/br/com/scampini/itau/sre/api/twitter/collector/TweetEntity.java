package br.com.scampini.itau.sre.api.twitter.collector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Document(collection = "tweets")
public class TweetEntity {

    @Id
    private Long id;
    private String text;
    private String hashtag;
    private Date published;
    private String twitterUserName;
    private String twitterUserLanguage;
    private String twitterUserCountry;
    private int twitterUserQtyFollowers;


}

package br.com.scampini.itau.sre.api.twitter.collector.impl;

import br.com.scampini.itau.sre.api.twitter.collector.TweetCollectorService;
import br.com.scampini.itau.sre.api.twitter.collector.TweetEntity;
import br.com.scampini.itau.sre.api.twitter.collector.TweetRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetCollectorServiceImpl implements TweetCollectorService {

    private static final Logger LOG = LoggerFactory.getLogger(TweetCollectorServiceImpl.class);

    @Autowired
    private TweetRepository tweetRepository;

    private static final String SERVICE_TYPE = "[COLLECTOR] ";

    @Autowired
    Twitter twitter;

    @Override
    public String getTweetsFromHashtag(String hashtag) throws Exception{
        return null;
    }

    @Override
    public String fetchTweetsFromHashTag(String hashtag) throws Exception{
        LOG.info(SERVICE_TYPE+"Searching Tweets from Twitter...");
        Gson json = new Gson();
        if(null != hashtag && hashtag.contains("#"))
            hashtag = hashtag.replaceAll("#", "");
        Query query = new Query("%23" + hashtag);
        query.setCount(100);
        query.setResultType(Query.ResultType.recent);
        LOG.debug(SERVICE_TYPE + "Parameters to search tweets from twitter:" + query.toString());
        QueryResult result = twitter.search(query);
        LOG.debug(SERVICE_TYPE + "Fetching tweets...");
        List<Status> tweetsFromTwitter = result.getTweets();
        LOG.debug(SERVICE_TYPE + "Tweets received: " + tweetsFromTwitter.size());

        LOG.debug(SERVICE_TYPE + "Filtering reponse to save only tweets with hashtag specified...");
        List<TweetEntity> tweetsFiltered = filterTweets(hashtag, tweetsFromTwitter);
        LOG.debug(SERVICE_TYPE + "Tweets to store: " + tweetsFiltered.size() + " / " + tweetsFromTwitter.size());

        LOG.debug(SERVICE_TYPE + "Saving tweets filtered...");
        tweetsFiltered.forEach(tweetRepository::save);

        return json.toJson(tweetsFiltered, List.class);
    }

    /**
     * Filter tweets from result.getTweets with hashtag specified on request and map result entity
     * to Tweet model to save only necessary information.
     */
    private List<TweetEntity> filterTweets(String hashtag, List<Status> tweetsFromTwitter) {
        return tweetsFromTwitter.stream()
                    .filter(item -> {
                        ArrayList<HashtagEntity> hashtagEntities = new ArrayList<>(Arrays.asList(item.getHashtagEntities()));
                        List<HashtagEntity> hashtagFiltered = hashtagEntities.stream()
                                .filter(entity -> hashtag.equalsIgnoreCase(entity.getText()))
                                .collect(Collectors.toList());
                        return hashtagFiltered.size() > 0;
                    }).collect(Collectors.toList())
                    .stream()
                    .map(temp -> {
                        return TweetEntity.builder()
                                .id(temp.getId())
                                .text(temp.getText())
                                .hashtag(hashtag)
                                .published(temp.getCreatedAt())
                                .twitterUserName(temp.getUser().getScreenName())
                                .twitterUserLanguage(temp.getLang())
                                .twitterUserCountry(temp.getPlace() != null ? temp.getPlace().getCountry() : "Unavailable")
                                .twitterUserQtyFollowers(temp.getUser().getFollowersCount())
                                .build();
                    }).collect(Collectors.toList());
    }

    @Override
    public String getHashtagsImported() throws Exception{
        return null;
    }
}

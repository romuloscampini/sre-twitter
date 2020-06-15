package br.com.scampini.itau.sre.api.twitter.collector;

public interface TweetCollectorService {

    String getTweetsFromHashtag(String hashtag) throws Exception;
    String fetchTweetsFromHashTag(String hashtag) throws Exception;

    String getHashtagsImported() throws Exception;
}

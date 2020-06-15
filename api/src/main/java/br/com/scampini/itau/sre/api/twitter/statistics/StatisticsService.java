package br.com.scampini.itau.sre.api.twitter.statistics;

import br.com.scampini.itau.sre.api.twitter.statistics.mapper.TweetByDateTimeMapper;
import br.com.scampini.itau.sre.api.twitter.statistics.mapper.TweetByHashtagLanguageMapper;
import br.com.scampini.itau.sre.api.twitter.statistics.mapper.UserStatisticsMapper;

import java.util.List;

public interface StatisticsService {

    List<UserStatisticsMapper> getMostFollowedUsers();
    List<TweetByDateTimeMapper> getTotalTweetsByDateTime();
    List<TweetByHashtagLanguageMapper> getTotalTweetByUserLanguage();
}

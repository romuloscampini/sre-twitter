package br.com.scampini.itau.sre.api.twitter.statistics.impl;

import br.com.scampini.itau.sre.api.twitter.collector.TweetEntity;
import br.com.scampini.itau.sre.api.twitter.statistics.StatisticsService;
import br.com.scampini.itau.sre.api.twitter.statistics.mapper.TweetByDateTimeMapper;
import br.com.scampini.itau.sre.api.twitter.statistics.mapper.TweetByHashtagLanguageMapper;
import br.com.scampini.itau.sre.api.twitter.statistics.mapper.UserStatisticsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<UserStatisticsMapper> getMostFollowedUsers() {
        LOG.debug("Fetching users most-followed from all tweets collected...");
        Aggregation groupUsers = newAggregation(
                group("twitterUserName").first("twitterUserQtyFollowers").as("twitterUserQtyFollowers"),
                project("twitterUserQtyFollowers").and("twitterUserName").previousOperation(),
                sort(Sort.Direction.DESC, "twitterUserQtyFollowers"),
                limit(5));
        AggregationResults<UserStatisticsMapper> groupResults
                = mongoTemplate.aggregate(groupUsers, TweetEntity.class, UserStatisticsMapper.class);
        return groupResults.getMappedResults();
    }

    @Override
    public List<TweetByDateTimeMapper> getTotalTweetsByDateTime() {
        Aggregation groupByDateTime = newAggregation(
                project("id", "published")
                        .andExpression("published").dateAsFormattedString("%Y-%m-%d-%H").as("published"),
                group(fields().and("published"))
                        .sum("id").as("count")
                        .count().as("count"),
                sort(Sort.Direction.DESC,"published"));

        AggregationResults<TweetByDateTimeMapper> groupResults
                = mongoTemplate.aggregate(groupByDateTime, TweetEntity.class, TweetByDateTimeMapper.class);

        return groupResults.getMappedResults();
    }

    @Override
    public List<TweetByHashtagLanguageMapper> getTotalTweetByUserLanguage() {
        Aggregation groupByHashtagAndLanguage = newAggregation(
                group("hashtag", "twitterUserLanguage")
//                project("hashtag", "twitterUserLanguage").asArray("id"),
                        .sum("id").as("count")
                        .count().as("count"));

        AggregationResults<TweetByHashtagLanguageMapper> groupResults
                = mongoTemplate.aggregate(groupByHashtagAndLanguage, TweetEntity.class, TweetByHashtagLanguageMapper.class);

        return groupResults.getMappedResults();

    }
}

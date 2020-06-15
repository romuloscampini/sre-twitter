package br.com.scampini.itau.sre.api.twitter.collector;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetRepository extends MongoRepository<TweetEntity, String> {
}

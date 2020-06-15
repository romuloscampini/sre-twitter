package br.com.scampini.itau.sre.api.twitter.statistics.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TweetByDateTimeMapper {

    private String id;
    private Double count;
}

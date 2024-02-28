package com.ppp.domain.diary.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.ppp.domain.diary.dto.DiaryPopularTermsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
public class DiarySearchQuerydslRepository {
    private final ElasticsearchClient elasticsearchClient;

    public DiaryPopularTermsDto findMostUsedTermsByPetId(Long petId) {
        try {
            return new DiaryPopularTermsDto(elasticsearchClient.search(new SearchRequest.Builder()
                    .size(0)
                    .query(petIdEq(petId))
                    .aggregations("title_terms", fieldTermsAggregation("title"))
                    .aggregations("content_terms", fieldTermsAggregation("content"))
                    .build(), Void.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Query petIdEq(Long petId) {
        return new MatchQuery.Builder()
                .field("petId")
                .query(petId)
                .build()._toQuery();
    }

    private Aggregation fieldTermsAggregation(String fieldName) {
        return new TermsAggregation.Builder().field(fieldName)
                .minDocCount(3)
                .size(5)
                .build()._toAggregation();
    }
}

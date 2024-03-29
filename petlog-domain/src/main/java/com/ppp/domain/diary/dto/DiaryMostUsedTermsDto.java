package com.ppp.domain.diary.dto;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class DiaryMostUsedTermsDto {
    private final Set<String> mostUsedTerms;

    public DiaryMostUsedTermsDto(SearchResponse<Void> searchResponse) {
        List<StringTermsBucket> buckets = searchResponse.aggregations().get("title_terms").sterms().buckets().array();
        buckets.addAll(searchResponse.aggregations().get("content_terms").sterms().buckets().array());
        mostUsedTerms = buckets.stream().map(bucket -> bucket.key().stringValue())
                .collect(Collectors.toSet());
    }
}

package com.ppp.common.client;

import com.ppp.domain.common.BaseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ElasticSearchClient<T extends BaseDocument> {
    private final ElasticsearchOperations elasticsearchOperations;

    public void save(T document) {
        elasticsearchOperations.save(document);
    }

    public void update(T document) {
        elasticsearchOperations.update(document);
    }

    public void delete(T document) {
        elasticsearchOperations.delete(document);
    }
}

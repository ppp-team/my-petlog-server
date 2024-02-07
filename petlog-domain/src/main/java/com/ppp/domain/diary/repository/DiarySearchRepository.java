package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.DiaryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, String> {
}

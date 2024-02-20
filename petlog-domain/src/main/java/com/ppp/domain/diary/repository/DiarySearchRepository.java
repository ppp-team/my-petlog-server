package com.ppp.domain.diary.repository;

import com.ppp.domain.diary.DiaryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiarySearchRepository extends ElasticsearchRepository<DiaryDocument, String> {
    @Query("{\"bool\" : { \"must\" : [ { \"bool\" : { \"should\" : [ {\"wildcard\" : { \"title\" : { \"value\" : \"*?0*\"}}}, {\"match_phrase\" : { \"content\" : { \"query\" : \"?0\", \"slop\" : 1}}}] } } ], \"filter\": [ {\"term\": {\"petId\": ?1}} ] }}")
    Page<DiaryDocument> findByTitleContainsOrContentContainsAndPetIdOrderByDateDesc(String keyword, Long petId, PageRequest request);

    List<DiaryDocument> findByUser_Id(String userId);
}

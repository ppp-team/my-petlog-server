package com.ppp.domain.video.repository;

import com.ppp.domain.video.TempVideo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempVideoRedisRepository extends CrudRepository<TempVideo, String> {
}

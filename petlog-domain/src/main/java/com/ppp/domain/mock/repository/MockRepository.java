package com.ppp.domain.mock.repository;

import com.ppp.domain.mock.Mock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MockRepository extends JpaRepository<Mock, Long> {

}

package com.ll.clearpath.domain.tourlist.tourlist.repository;

import com.ll.clearpath.domain.tourlist.tourlist.entity.KeywordList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordListRepository extends JpaRepository<KeywordList, Long> {
}

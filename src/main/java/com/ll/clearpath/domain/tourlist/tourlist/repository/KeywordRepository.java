package com.ll.clearpath.domain.tourlist.tourlist.repository;

import com.ll.clearpath.domain.tourlist.tourlist.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByContent(String tagContent);
}

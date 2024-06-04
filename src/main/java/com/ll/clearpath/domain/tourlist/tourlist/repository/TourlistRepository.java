package com.ll.clearpath.domain.tourlist.tourlist.repository;

import com.ll.clearpath.domain.tourlist.tourlist.entity.Tourlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourlistRepository extends JpaRepository<Tourlist, Long> {

    @Query("SELECT DISTINCT t FROM Tourlist t " +
            "LEFT JOIN t.keywordLists kl " +
            "LEFT JOIN kl.keyword k " +
            "WHERE (:category = 'all' AND (:search = '' OR t.title LIKE %:search% OR k.content LIKE %:search%)) " +
            "OR (:category = 'title' AND (:search = '' OR t.title LIKE %:search%)) " +
            "OR (:category = 'keyword' AND (:search = '' OR k.content LIKE %:search%))")
    List<Tourlist> searchTours(
            @Param("category") String category,
            @Param("search") String search);

    List<Tourlist> findTop1By();

    Optional<Tourlist> findByTitle(String title);
}

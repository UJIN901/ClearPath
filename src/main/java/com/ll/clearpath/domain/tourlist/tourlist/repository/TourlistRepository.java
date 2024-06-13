package com.ll.clearpath.domain.tourlist.tourlist.repository;

import com.ll.clearpath.domain.tourlist.tourlist.entity.Tourlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourlistRepository extends JpaRepository<Tourlist, Long>, JpaSpecificationExecutor<Tourlist> {
    Optional<Tourlist> findByTitle(String title);
}

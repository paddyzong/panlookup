package com.cardlookup.panlookup.repository;

import com.cardlookup.panlookup.entity.CardRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRangeRepository extends JpaRepository<CardRange, Long> {

    @Query("""
           SELECT c
           FROM CardRange c
           WHERE :pan BETWEEN c.startBin AND c.endBin
           """)
    Optional<CardRange> findByPan(@Param("pan") long pan);

}

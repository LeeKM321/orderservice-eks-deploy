package com.playdata.orderingservice.ordering.repository;

import com.playdata.orderingservice.ordering.entity.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {

//    @Query("SELECT o FROM Ordering o WHERE o.userId = ?1")
    List<Ordering> findByUserId(Long userId);

}


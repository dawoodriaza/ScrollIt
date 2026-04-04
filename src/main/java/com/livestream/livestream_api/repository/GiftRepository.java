package com.livestream.livestream_api.repository;

import com.livestream.livestream_api.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByActiveTrue();
    boolean existsByGiftName(String giftName);
}

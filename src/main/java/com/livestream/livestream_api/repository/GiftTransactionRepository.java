package com.livestream.livestream_api.repository;

import com.livestream.livestream_api.model.GiftTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftTransactionRepository extends JpaRepository<GiftTransaction, Long> {
    Page<GiftTransaction> findBySender_UserId(Long senderId, Pageable pageable);
    Page<GiftTransaction> findByStream_StreamId(Long streamId, Pageable pageable);

    @Query("SELECT SUM(gt.coinsSpent) FROM GiftTransaction gt WHERE gt.stream.streamId = :streamId")
    Integer sumCoinsSpentByStream(@Param("streamId") Long streamId);
}

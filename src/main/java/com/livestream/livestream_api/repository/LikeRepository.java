package com.livestream.livestream_api.repository;


import com.livestream.livestream_api.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUser_UserIdAndStream_StreamId(Long userId, Long streamId);
    List<Like> findByStream_StreamId(Long streamId);
    boolean existsByUser_UserIdAndStream_StreamId(Long userId, Long streamId);
    long countByStream_StreamId(Long streamId);
}
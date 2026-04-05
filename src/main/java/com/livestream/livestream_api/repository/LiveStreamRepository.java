package com.livestream.livestream_api.repository;



import com.livestream.livestream_api.model.LiveStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStreamRepository extends JpaRepository<LiveStream, Long> {

    List<LiveStream> findByStatus(LiveStream.StreamStatus status);

    Page<LiveStream> findByStatus(LiveStream.StreamStatus status, Pageable pageable);

    List<LiveStream> findByHost_UserId(Long hostId);

    @Query("SELECT ls FROM LiveStream ls WHERE ls.title LIKE %:keyword%")
    Page<LiveStream> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}
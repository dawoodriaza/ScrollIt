package com.livestream.livestream_api.repository;
import com.livestream.livestream_api.model.StreamViewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamViewerRepository extends JpaRepository<StreamViewer, Long> {
    List<StreamViewer> findByStream_StreamId(Long streamId);
    Optional<StreamViewer> findByUser_UserIdAndStream_StreamIdAndStatus(
            Long userId, Long streamId, StreamViewer.ViewerStatus status);
    long countByStream_StreamIdAndStatus(Long streamId, StreamViewer.ViewerStatus status);
}
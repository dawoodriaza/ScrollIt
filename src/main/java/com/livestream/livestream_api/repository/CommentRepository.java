package com.livestream.livestream_api.repository;


import com.livestream.livestream_api.model.Comment;

import com.livestream.livestream_api.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByStream_StreamIdAndDeletedFalse(Long streamId, Pageable pageable);
    List<Comment> findByUser_UserIdAndDeletedFalse(Long userId);
}

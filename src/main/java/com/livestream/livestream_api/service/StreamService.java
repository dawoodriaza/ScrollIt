package com.livestream.livestream_api.service;


import com.livestream.livestream_api.config.StreamConcurrencyManager;
import com.livestream.livestream_api.dto.request.CommentRequest;
import com.livestream.livestream_api.dto.request.GiftRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.*;
import com.livestream.livestream_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamService {

    private final LiveStreamRepository      streamRepository;
    private final UserRepository            userRepository;
    private final LikeRepository            likeRepository;
    private final CommentRepository         commentRepository;
    private final GiftRepository            giftRepository;
    private final GiftTransactionRepository transactionRepository;
    private final StreamConcurrencyManager  concurrency;

    @Transactional
    public ApiResponse.MessageResponse toggleLike(String email, Long streamId) {
        User user = findUserByEmail(email);
        LiveStream stream = findStream(streamId);
        if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
            throw new BadRequestException("You can only like a live stream.");
        boolean alreadyLiked = likeRepository.existsByUser_UserIdAndStream_StreamId(user.getUserId(), streamId);
        if (alreadyLiked) {
            likeRepository.delete(likeRepository.findByUser_UserIdAndStream_StreamId(user.getUserId(), streamId).orElseThrow());
            int newCount = concurrency.decrementLikeCount(streamId);
            stream.setLikeCount(newCount);
            streamRepository.save(stream);

            return new ApiResponse.MessageResponse("Like removed. Total likes: " + newCount);
        } else {
            likeRepository.save(Like.builder().user(user).stream(stream).build());
            int newCount = concurrency.incrementLikeCount(streamId);
            stream.setLikeCount(newCount);
            streamRepository.save(stream);

            return new ApiResponse.MessageResponse("Stream liked! Total likes: " + newCount);
        }
    }

    public long getLikeCount(Long streamId) {
        return concurrency.getLikeCount(streamId);
    }

    @Transactional
    public ApiResponse.CommentSummary postComment(String email, Long streamId, CommentRequest.Create req) {
        User user = findUserByEmail(email);
        ReadWriteLock rwLock = concurrency.getStreamMetaLock(streamId);
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            LiveStream stream = findStream(streamId);
            if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
                throw new BadRequestException("You can only comment on a live stream.");
            Comment comment = Comment.builder().user(user).stream(stream).message(req.getMessage()).build();
            Comment saved = commentRepository.save(comment);

            return ApiResponse.CommentSummary.from(saved);
        } finally { readLock.unlock(); }
    }

    public Page<ApiResponse.CommentSummary> getStreamComments(Long streamId, Pageable pageable) {
        return commentRepository.findByStream_StreamIdAndDeletedFalse(streamId, pageable)
                .map(ApiResponse.CommentSummary::from);
    }

    @Transactional
    public ApiResponse.CommentSummary updateComment(String email, Long commentId, CommentRequest.Update req) {
        Comment comment = findComment(commentId);
        assertCommentOwner(comment, email);
        comment.setMessage(req.getMessage());
        return ApiResponse.CommentSummary.from(commentRepository.save(comment));
    }

    @Transactional
    public ApiResponse.MessageResponse deleteComment(String email, Long commentId) {
        Comment comment = findComment(commentId);
        assertCommentOwner(comment, email);
        comment.setDeleted(true);
        commentRepository.save(comment);

        return new ApiResponse.MessageResponse("Comment deleted.");
    }

    @Transactional
    public ApiResponse.TransactionSummary sendGift(String email, Long streamId, GiftRequest.Send req) {
        User sender = findUserByEmail(email);
        Semaphore giftSemaphore = concurrency.getGiftSemaphore(streamId);
        try {
            giftSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Gift sending interrupted. Please try again.");
        }
        ReentrantLock userCoinLock = concurrency.getUserCoinLock(sender.getUserId());
        userCoinLock.lock();
        try {
            sender = userRepository.findById(sender.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            LiveStream stream = findStream(streamId);
            if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
                throw new BadRequestException("You can only send gifts to a live stream.");
            if (stream.getHost().getUserId().equals(sender.getUserId()))
                throw new BadRequestException("You cannot send gifts to your own stream.");
            Gift gift = giftRepository.findById(req.getGiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gift", req.getGiftId()));
            if (!gift.isActive()) throw new BadRequestException("This gift is no longer available.");
            if (sender.getCoinBalance() < gift.getCoinValue())
                throw new InsufficientCoinsException(
                        "Insufficient coins. You need " + gift.getCoinValue() + " but have " + sender.getCoinBalance() + ".");
            sender.setCoinBalance(sender.getCoinBalance() - gift.getCoinValue());
            userRepository.save(sender);
            User host = userRepository.findById(stream.getHost().getUserId()).orElseThrow();
            host.setCoinBalance(host.getCoinBalance() + gift.getCoinValue());
            userRepository.save(host);
            GiftTransaction tx = GiftTransaction.builder().sender(sender).stream(stream).gift(gift)
                    .coinsSpent(gift.getCoinValue()).build();
            GiftTransaction saved = transactionRepository.save(tx);

            return ApiResponse.TransactionSummary.from(saved);
        } finally {
            userCoinLock.unlock();
            giftSemaphore.release();
        }
    }

    public Page<ApiResponse.TransactionSummary> getStreamTransactions(Long streamId, Pageable pageable) {
        return transactionRepository.findByStream_StreamId(streamId, pageable).map(ApiResponse.TransactionSummary::from);
    }

    public Page<ApiResponse.TransactionSummary> getMyTransactions(String email, Pageable pageable) {
        User user = findUserByEmail(email);
        return transactionRepository.findBySender_UserId(user.getUserId(), pageable).map(ApiResponse.TransactionSummary::from);
    }

    private LiveStream findStream(Long id) {
        return streamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stream", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment", id));
    }

    private void assertCommentOwner(Comment comment, String email) {
        if (!comment.getUser().getEmail().equals(email))
            throw new UnauthorizedException("You can only modify your own comments.");
    }
}
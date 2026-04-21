package com.livestream.livestream_api.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class StreamConcurrencyManager {

    @Getter
    private final ConcurrentHashMap<Long, AtomicInteger> viewerCounts   = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> likeCounts      = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReentrantLock> userCoinLocks   = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ReadWriteLock> streamMetaLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Semaphore>     giftSemaphores  = new ConcurrentHashMap<>();

    private static final int MAX_CONCURRENT_GIFT_SENDERS = 10;

    public int incrementViewerCount(Long streamId) {
        return viewerCounts.computeIfAbsent(streamId, id -> new AtomicInteger(0)).incrementAndGet();
    }

    public int decrementViewerCount(Long streamId) {
        return viewerCounts.computeIfAbsent(streamId, id -> new AtomicInteger(0))
                .updateAndGet(c -> Math.max(0, c - 1));
    }

    public int getViewerCount(Long streamId) {
        return viewerCounts.getOrDefault(streamId, new AtomicInteger(0)).get();
    }

    public void resetViewerCount(Long streamId) {
        viewerCounts.put(streamId, new AtomicInteger(0));
    }

    public int incrementLikeCount(Long streamId) {
        return likeCounts.computeIfAbsent(streamId, id -> new AtomicInteger(0)).incrementAndGet();
    }

    public int decrementLikeCount(Long streamId) {
        return likeCounts.computeIfAbsent(streamId, id -> new AtomicInteger(0))
                .updateAndGet(c -> Math.max(0, c - 1));
    }

    public int getLikeCount(Long streamId) {
        return likeCounts.getOrDefault(streamId, new AtomicInteger(0)).get();
    }

    public void initLikeCount(Long streamId, int initialValue) {
        likeCounts.put(streamId, new AtomicInteger(initialValue));
    }

    public ReentrantLock getUserCoinLock(Long userId) {
        return userCoinLocks.computeIfAbsent(userId, id -> new ReentrantLock(true));
    }

    public ReadWriteLock getStreamMetaLock(Long streamId) {
        return streamMetaLocks.computeIfAbsent(streamId, id -> new ReentrantReadWriteLock(true));
    }

    public Semaphore getGiftSemaphore(Long streamId) {
        return giftSemaphores.computeIfAbsent(streamId,
                id -> new Semaphore(MAX_CONCURRENT_GIFT_SENDERS, true));
    }


    public void cleanupStream(Long streamId) {
        viewerCounts.remove(streamId);
        likeCounts.remove(streamId);
        streamMetaLocks.remove(streamId);
        giftSemaphores.remove(streamId);
        log.info("Cleaned up concurrency state for stream {}", streamId);
    }
}
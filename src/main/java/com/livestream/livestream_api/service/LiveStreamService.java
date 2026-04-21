package com.livestream.livestream_api.service;

import com.livestream.livestream_api.config.StreamConcurrencyManager;
import com.livestream.livestream_api.dto.request.LiveStreamRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.*;
import com.livestream.livestream_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveStreamService {

    private final LiveStreamRepository     streamRepository;
    private final UserRepository           userRepository;
    private final StreamViewerRepository   viewerRepository;
    private final FileStorageService       fileStorageService;
    private final StreamConcurrencyManager concurrency;


    public Page<ApiResponse.StreamSummary> getAllLiveStreams(Pageable pageable) {
        return streamRepository.findByStatus(LiveStream.StreamStatus.LIVE, pageable)
                .map(stream -> {
                    ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
                    summary.setViewerCount(concurrency.getViewerCount(stream.getStreamId()));
                    summary.setLikeCount(concurrency.getLikeCount(stream.getStreamId()));
                    return summary;
                });
    }

    public Page<ApiResponse.StreamSummary> getAllStreams(Pageable pageable) {
        return streamRepository.findAll(pageable)
                .map(stream -> {
                    ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
                    summary.setViewerCount(concurrency.getViewerCount(stream.getStreamId()));
                    summary.setLikeCount(concurrency.getLikeCount(stream.getStreamId()));
                    return summary;
                });
    }

    public Page<ApiResponse.StreamSummary> getliveStreams(Pageable pageable) {
        return streamRepository.findByStatus(LiveStream.StreamStatus.LIVE, pageable)
                .map(stream -> {
                    ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
                    summary.setViewerCount(concurrency.getViewerCount(stream.getStreamId()));
                    summary.setLikeCount(concurrency.getLikeCount(stream.getStreamId()));
                    return summary;
                });
    }

    public Page<ApiResponse.StreamSummary> getScheduledStreams(Pageable pageable) {
        return streamRepository.findByStatus(LiveStream.StreamStatus.SCHEDULED, pageable)
                .map(ApiResponse.StreamSummary::from);
    }

    public Page<ApiResponse.StreamSummary> getALLEndedStreams(Pageable pageable) {
        return streamRepository.findByStatus(LiveStream.StreamStatus.ENDED, pageable)
                .map(ApiResponse.StreamSummary::from);
    }

    public Page<ApiResponse.StreamSummary> searchStreams(String keyword, Pageable pageable) {
        return streamRepository.searchByTitle(keyword, pageable)
                .map(stream -> {
                    ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
                    summary.setViewerCount(concurrency.getViewerCount(stream.getStreamId()));
                    summary.setLikeCount(concurrency.getLikeCount(stream.getStreamId()));
                    return summary;
                });
    }





//    public Page<ApiResponse.StreamSummary> getAllLiveStreams(Pageable pageable) {
//        return streamRepository.findAll(pageable)
//                .map(ApiResponse.StreamSummary::from);
//    }

//    public ApiResponse.StreamSummary getStreamById(Long id) {
//        LiveStream stream = findStream(id);
//        ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
//        summary.setViewerCount(concurrency.getViewerCount(id));
//        summary.setLikeCount(concurrency.getLikeCount(id));
//        return summary;
//    }


    public ApiResponse.StreamSummary getStreamById(Long id) {
        LiveStream stream = findStream(id);
        ApiResponse.StreamSummary summary = ApiResponse.StreamSummary.from(stream);
        if (stream.getStatus() == LiveStream.StreamStatus.LIVE) {
            summary.setViewerCount(concurrency.getViewerCount(id));
            summary.setLikeCount(concurrency.getLikeCount(id));
        }
        return summary;
    }

    @Transactional
    public List<ApiResponse.StreamSummary> getMyStreams(String email) {
        User user = findUserByEmail(email);
        return streamRepository.findByHost_UserId(user.getUserId())
                .stream().map(ApiResponse.StreamSummary::from).toList();
    }

    @Transactional
    public ApiResponse.StreamSummary createStream(String email, LiveStreamRequest.Create req, MultipartFile thumbnail) {
        User host = findUserByEmail(email);
        LiveStream stream = LiveStream.builder().title(req.getTitle()).description(req.getDescription())
                .host(host).status(LiveStream.StreamStatus.SCHEDULED).build();
        if (thumbnail != null && !thumbnail.isEmpty())
            stream.setThumbnailUrl(fileStorageService.storeFile(thumbnail));
        LiveStream saved = streamRepository.save(stream);
        concurrency.resetViewerCount(saved.getStreamId());
        concurrency.initLikeCount(saved.getStreamId(), 0);

        return ApiResponse.StreamSummary.from(saved);
    }

    @Transactional
    public ApiResponse.StreamSummary updateStream(String email, Long streamId, LiveStreamRequest.Update req) {
        ReadWriteLock rwLock = concurrency.getStreamMetaLock(streamId);
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            LiveStream stream = findStream(streamId);
            assertHost(stream, email);
            if (req.getTitle() != null) stream.setTitle(req.getTitle());
            if (req.getDescription() != null) stream.setDescription(req.getDescription());
            return ApiResponse.StreamSummary.from(streamRepository.save(stream));
        } finally { writeLock.unlock(); }
    }

    @Transactional
    public ApiResponse.StreamSummary startStream(String email, Long streamId) {
        ReadWriteLock rwLock = concurrency.getStreamMetaLock(streamId);
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            LiveStream stream = findStream(streamId);
            assertHost(stream, email);
            if (stream.getStatus() != LiveStream.StreamStatus.SCHEDULED)
                throw new BadRequestException("Stream is not in SCHEDULED status.");
            stream.setStatus(LiveStream.StreamStatus.LIVE);
            concurrency.resetViewerCount(streamId);
            concurrency.initLikeCount(streamId, 0);
            LiveStream saved = streamRepository.save(stream);

            return ApiResponse.StreamSummary.from(saved);
        } finally { writeLock.unlock(); }
    }
    @Transactional
    public ApiResponse.StreamSummary uploadThumbnail(String email, Long streamId, MultipartFile file) {
        LiveStream stream = findStream(streamId);
        assertHost(stream, email);

        if (stream.getThumbnailUrl() != null)
            fileStorageService.deleteFile(stream.getThumbnailUrl());

        stream.setThumbnailUrl(fileStorageService.storeFile(file));
        return ApiResponse.StreamSummary.from(streamRepository.save(stream));
    }



    @Transactional
    public ApiResponse.StreamSummary endStream(String email, Long streamId) {
        ReadWriteLock rwLock = concurrency.getStreamMetaLock(streamId);
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            LiveStream stream = findStream(streamId);
            assertHost(stream, email);
            if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
                throw new BadRequestException("Stream is not currently LIVE.");
            int finalViewers = concurrency.getViewerCount(streamId);
            int finalLikes   = concurrency.getLikeCount(streamId);
            stream.setViewerCount(finalViewers);
            stream.setLikeCount(finalLikes);
            stream.setStatus(LiveStream.StreamStatus.ENDED);
            stream.setEndedAt(LocalDateTime.now());
            LiveStream saved = streamRepository.save(stream);

            concurrency.cleanupStream(streamId);
            return ApiResponse.StreamSummary.from(saved);
        } finally { writeLock.unlock(); }
    }

    @Transactional
    public ApiResponse.MessageResponse deleteStream(String email, Long streamId) {
        LiveStream stream = findStream(streamId);
        assertHost(stream, email);
        if (stream.getThumbnailUrl() != null) fileStorageService.deleteFile(stream.getThumbnailUrl());

        streamRepository.delete(stream);
        concurrency.cleanupStream(streamId);
        return new ApiResponse.MessageResponse("Stream deleted successfully.");
    }


    @Transactional
    public ApiResponse.ViewerSummary joinStream(String email, Long streamId) {
        User user = findUserByEmail(email);
        LiveStream stream = findStream(streamId);
        if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
            throw new BadRequestException("Stream is not currently live.");
        if (viewerRepository.findByUser_UserIdAndStream_StreamIdAndStatus(
                user.getUserId(), streamId, StreamViewer.ViewerStatus.WATCHING).isPresent())
            throw new BadRequestException("You are already watching this stream.");
        StreamViewer viewer = StreamViewer.builder().user(user).stream(stream)
                .status(StreamViewer.ViewerStatus.WATCHING).build();
        viewerRepository.save(viewer);
        int count = concurrency.incrementViewerCount(streamId);

        return ApiResponse.ViewerSummary.from(viewer);
    }


    @Transactional
    public ApiResponse.ViewerSummary joinStreamAsGuest(Long streamId, String guestName) {
        LiveStream stream = findStream(streamId);
        if (stream.getStatus() != LiveStream.StreamStatus.LIVE)
            throw new BadRequestException("Stream is not currently live.");

        String name = (guestName != null && !guestName.isBlank()) ? guestName : "Guest";

        StreamViewer viewer = StreamViewer.builder()
                .user(null)
                .guestName(name)
                .stream(stream)
                .status(StreamViewer.ViewerStatus.WATCHING)
                .build();
        viewerRepository.save(viewer);

        int count = concurrency.incrementViewerCount(streamId);
        log.info("[VIEWER] Guest '{}' joined stream [{}] | Live viewers: {}", name, streamId, count);
        return ApiResponse.ViewerSummary.from(viewer);
    }


    @Transactional
    public ApiResponse.MessageResponse leaveStreamAsGuest(Long streamId, Long viewerId) {
        StreamViewer viewer = viewerRepository.findById(viewerId)
                .orElseThrow(() -> new BadRequestException("Viewer not found."));
        viewer.setStatus(StreamViewer.ViewerStatus.LEFT);
        viewer.setLeaveTime(LocalDateTime.now());
        viewerRepository.save(viewer);
        int count = concurrency.decrementViewerCount(streamId);
        log.info("[VIEWER] Guest left stream [{}] | Live viewers: {}", streamId, count);
        return new ApiResponse.MessageResponse("You have left the stream.");
    }


    @Transactional
    public ApiResponse.MessageResponse leaveStream(String email, Long streamId) {
        User user = findUserByEmail(email);
        StreamViewer viewer = viewerRepository.findByUser_UserIdAndStream_StreamIdAndStatus(
                        user.getUserId(), streamId, StreamViewer.ViewerStatus.WATCHING)
                .orElseThrow(() -> new BadRequestException("You are not watching this stream."));
        viewer.setStatus(StreamViewer.ViewerStatus.LEFT);
        viewer.setLeaveTime(LocalDateTime.now());
        viewerRepository.save(viewer);
        int count = concurrency.decrementViewerCount(streamId);

        return new ApiResponse.MessageResponse("You have left the stream.");
    }

    public List<ApiResponse.ViewerSummary> getStreamViewers(Long streamId) {
        return viewerRepository.findByStream_StreamId(streamId)
                .stream().map(ApiResponse.ViewerSummary::from).toList();
    }

    private LiveStream findStream(Long id) {
        return streamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stream", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertHost(LiveStream stream, String email) {
        if (!stream.getHost().getEmail().equals(email))
            throw new UnauthorizedException("You are not the host of this stream.");
    }
}
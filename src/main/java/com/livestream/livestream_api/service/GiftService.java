package com.livestream.livestream_api.service;

import com.livestream.livestream_api.dto.request.GiftRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.Gift;
import com.livestream.livestream_api.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftRepository giftRepository;

    public List<ApiResponse.GiftSummary> getAllActiveGifts() {
        return giftRepository.findByActiveTrue().stream().map(ApiResponse.GiftSummary::from).toList();
    }

    public List<ApiResponse.GiftSummary> getAllGifts() {
        return giftRepository.findAll().stream().map(ApiResponse.GiftSummary::from).toList();
    }

    public ApiResponse.GiftSummary getGiftById(Long id) {
        return ApiResponse.GiftSummary.from(findGift(id));
    }

    @Transactional
    public ApiResponse.GiftSummary createGift(GiftRequest.Create req) {
        if (giftRepository.existsByGiftName(req.getGiftName()))
            throw new DuplicateResourceException("Gift '" + req.getGiftName() + "' already exists.");
        return ApiResponse.GiftSummary.from(giftRepository.save(
                Gift.builder().giftName(req.getGiftName()).coinValue(req.getCoinValue())
                        .iconUrl(req.getIconUrl()).active(true).build()));
    }

    @Transactional
    public ApiResponse.GiftSummary updateGift(Long id, GiftRequest.Update req) {
        Gift gift = findGift(id);
        if (req.getGiftName() != null && !req.getGiftName().equals(gift.getGiftName())) {
            if (giftRepository.existsByGiftName(req.getGiftName()))
                throw new DuplicateResourceException("Gift name already exists.");
            gift.setGiftName(req.getGiftName());
        }
        if (req.getCoinValue() > 0) gift.setCoinValue(req.getCoinValue());
        if (req.getIconUrl() != null) gift.setIconUrl(req.getIconUrl());
        if (req.getActive() != null) gift.setActive(req.getActive());
        return ApiResponse.GiftSummary.from(giftRepository.save(gift));
    }

    @Transactional
    public ApiResponse.MessageResponse deleteGift(Long id) {
        Gift gift = findGift(id);
        gift.setActive(false);
        giftRepository.save(gift);
        return new ApiResponse.MessageResponse("Gift deactivated successfully.");
    }

    private Gift findGift(Long id) {
        return giftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gift", id));
    }
}
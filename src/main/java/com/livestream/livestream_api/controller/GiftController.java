package com.livestream.livestream_api.controller;


import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.service.*;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService   giftService;


    @GetMapping
    public ResponseEntity<List<ApiResponse.GiftSummary>> getActiveGifts() {
        return ResponseEntity.ok(giftService.getAllActiveGifts());
    }






}
package com.livestream.livestream_api.controller;



import com.livestream.livestream_api.service.Userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final Userservice userService;

    @GetMapping("/me")




}
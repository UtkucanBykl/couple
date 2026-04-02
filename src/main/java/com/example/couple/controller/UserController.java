package com.example.couple.controller;


import com.example.couple.dto.request.UserCreateRequest;
import com.example.couple.dto.request.UserLoginRequest;
import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.dto.response.UserLoginResponse;
import com.example.couple.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController{
    private final UserService userService;
    @PostMapping
    public ResponseEntity<UserCreateResponse> createUser(@Valid @RequestBody UserCreateRequest request){
        UserCreateResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse response = userService.loginUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
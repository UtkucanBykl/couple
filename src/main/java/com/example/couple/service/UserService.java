package com.example.couple.service;
import java.security.SecureRandom;
import java.util.Optional;

import com.example.couple.dto.request.UserLoginRequest;
import com.example.couple.dto.response.UserCreateResponse;
import com.example.couple.dto.response.UserLoginResponse;
import com.example.couple.exception.BadRequestException;
import com.example.couple.mapper.UserMapper;
import com.example.couple.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.couple.dto.request.UserCreateRequest;
import com.example.couple.entity.User;
import com.example.couple.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private static final SecureRandom random = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;

    }
    public String createFriendCode() {
        while (true) {
            StringBuilder sb = new StringBuilder(6);
            String chars = "0123456789";

            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(chars.length());
                sb.append(chars.charAt(index));
            }

            String code = sb.toString();

            if (!userRepository.existsByFriendCode(code)) {
                return code;
            }
        }
    }

    public void validate(UserCreateRequest userCreateRequest){
        boolean emailExists = userRepository.existsByEmail(userCreateRequest.getEmail());
        boolean usernameExists = userRepository.existsByUsername(userCreateRequest.getUsername());
        if (emailExists | usernameExists){
            throw new BadRequestException("Böyle bir hesap var.");
        }
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest userCreateRequest){
        this.validate(userCreateRequest);
        String friendCode = this.createFriendCode();
        User user = new User();
        user.setEmail(userCreateRequest.getEmail());
        user.setUsername(userCreateRequest.getUsername());
        user.setFriendCode(friendCode);
        user.setPasswordHash(this.hashPassword(userCreateRequest.getPassword()));
        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateToken(savedUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
        return userMapper.toCreateResponse(savedUser, accessToken, refreshToken);
    }

    @Transactional()
    public UserLoginResponse loginUser(UserLoginRequest userLoginRequest){
        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
        if(optionalUser.isEmpty()){
            throw new BadRequestException("Kullanıcı adı yok");
        }
        User user = optionalUser.get();
        boolean isPasswordValid = checkPassword(userLoginRequest.getPassword(),user.getPasswordHash());
        if(!isPasswordValid){
            throw new BadRequestException("Parola yanlış");
        }
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        return userMapper.toLoginResponse(user, accessToken, refreshToken);
    }

}

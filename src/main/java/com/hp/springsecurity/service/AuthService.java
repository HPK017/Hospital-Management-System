package com.hp.springsecurity.service;

import com.hp.springsecurity.dto.JwtResponse;
import com.hp.springsecurity.dto.LoginRequestDto;
import com.hp.springsecurity.dto.LoginResponseDto;
import com.hp.springsecurity.dto.SignUpResponseDto;
import com.hp.springsecurity.entity.RefreshToken;
import com.hp.springsecurity.entity.User;
import com.hp.springsecurity.repository.UserRepository;
import com.hp.springsecurity.security.AuthUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String refreshToken = authUtil.createRefreshToken(user);
        String accessToken = authUtil.generateAccessToken(user);

//        return JwtResponse.builder()
//                .accessToken(token)
//                .token(refreshToken.getToken()).build();

       return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    public SignUpResponseDto signup(LoginRequestDto signupRequestDto) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if (user != null) throw new IllegalArgumentException("User already exists");

        user = userRepository.save(User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build()
        );

        return new SignUpResponseDto(user.getId(), user.getUsername());
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        Long userId = authUtil.generateUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId);

        String accessToken = authUtil.generateAccessToken(user);
        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

}

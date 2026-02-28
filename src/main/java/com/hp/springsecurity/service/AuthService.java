package com.hp.springsecurity.service;

import com.hp.springsecurity.dto.*;
import com.hp.springsecurity.entity.Patient;
import com.hp.springsecurity.entity.RefreshToken;
import com.hp.springsecurity.entity.User;
import com.hp.springsecurity.repository.PatientRepository;
import com.hp.springsecurity.repository.UserRepository;
import com.hp.springsecurity.security.AuthUtil;
import com.hp.springsecurity.type.AuthProviderType;
import com.hp.springsecurity.type.RoleType;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final PatientRepository patientRepository;

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

    public User signUpInternal(SignUpRequestDto signupRequestDto, AuthProviderType authProviderType, String providerId) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if (user != null) throw new IllegalArgumentException("User already exists");

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .providerId(providerId)
                .providerType(authProviderType)
                .roles(signupRequestDto.getRoles()) //Role.Patient
                .build();

        if(authProviderType == AuthProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }

        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getUsername())
                .user(user)
                .build();

        patientRepository.save(patient);


        return user;
    }

    public SignUpResponseDto signup(SignUpRequestDto signupRequestDto) {
        User user = signUpInternal(signupRequestDto, AuthProviderType.EMAIL, null);

        return new SignUpResponseDto(user.getId(), user.getUsername());
    }



//    public SignUpResponseDto signup(LoginRequestDto signupRequestDto) {
//        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);
//
//        if (user != null) throw new IllegalArgumentException("User already exists");
//
//        user = userRepository.save(User.builder()
//                .username(signupRequestDto.getUsername())
//                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
//                .build()
//        );
//
//        return new SignUpResponseDto(user.getId(), user.getUsername());
//    }
//
    public LoginResponseDto refreshToken(String refreshToken) {
        Long userId = authUtil.generateUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId);

        String accessToken = authUtil.generateAccessToken(user);
        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    public ResponseEntity<LoginResponseDto> handleOauth2loginRequest(OAuth2User oAuth2User, String registrationId) {

        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuthwUser(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User emailUser = userRepository.findByUsername(email).orElse(null);

        if(user == null && emailUser == null) {
            //signup flow
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signUpInternal(new SignUpRequestDto(username, null,name, Set.of(RoleType.PATIENT)), providerType, providerId);
        } else if (user != null) {
            if(email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setUsername(email);
                userRepository.save(user);
            }
        } else {
            throw  new BadCredentialsException("This email is already registered with provider " + emailUser.getProviderType());
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(user.getId(), authUtil.generateAccessToken(user), authUtil.createRefreshToken(user));
        return ResponseEntity.ok(loginResponseDto);

        //fetch providerType and providerId
        //save the providertype and providerId info with user
        // if the user has an account directly login
        //otherwise, first signUp and then Login
    }
}

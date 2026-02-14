package com.hp.springsecurity.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    String jwt;
    Long userId;
}

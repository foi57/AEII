package org.demo.artExaminationInformationInquiry.api.controller;

import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.service.impl.UsersServiceImpl;
import org.demo.artExaminationInformationInquiry.config.SecurityConfig.JwtTokenUtil;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersServiceImpl usersService;

    public AuthController(JwtTokenUtil jwtTokenUtil, UsersServiceImpl usersService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersService = usersService;
    }
    Logger logger=org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        logger.info("refreshToken: {}", refreshToken);
        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的refresh token");
        }

        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        Users user = usersService.selectUsersByName(username);

        String newAccessToken = jwtTokenUtil.generateToken(username);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(username);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(response);
    }
}
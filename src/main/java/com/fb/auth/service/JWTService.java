package com.fb.auth.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface JWTService {
    Map<String, String> generate(String username);
}

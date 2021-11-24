package com.scalelable.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

@Component
public class JWTUtils {
    @Value("${jwt.signature}")
    private  String signature;

    public  String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(signature));
    }

    public  DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(signature)).build().verify(token);
    }
}
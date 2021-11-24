package com.scalelable.demo.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalelable.demo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class PersonInterceptor implements HandlerInterceptor {
    private final JWTUtils jwtUtils;

    @Autowired
    public PersonInterceptor(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        Map<String, Object> map = new HashMap<>();
        try {
            DecodedJWT verify = jwtUtils.verify(token);
            request.setAttribute("companyBucket", verify.getClaim("companyBucket").asString());
            request.setAttribute("project", verify.getClaim("project").asString());
            request.setAttribute("projectId", verify.getClaim("projectId").asString());
            return true;
        } catch (Exception e) {
            map.put("state", false);
            map.put("msg", e.getMessage());

            String json = new ObjectMapper().writeValueAsString(map);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(json);
            return false;
        }
    }
}

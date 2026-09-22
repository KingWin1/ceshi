package com.community.backend.config;

import com.community.common.utils.JwtUtil;
import com.community.common.utils.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器，验证请求头中的 Token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头中获取 Token
        String token = request.getHeader("Authorization");
        
        // 如果请求头中没有，尝试从参数中获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        // 验证 Token
        if (token == null || token.isEmpty()) {
            sendError(response, "401", "未登录，请先登录");
            return false;
        }

        // 去除 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 解析 Token
            Claims claims = JwtUtil.parseToken(token);
            if (claims == null) {
                sendError(response, "401", "Token 无效，请重新登录");
                return false;
            }
            // 将用户信息存入请求属性中，供后续使用
            request.setAttribute("adminId", claims.getSubject());
            return true;
        } catch (Exception e) {
            sendError(response, "401", "Token 已过期，请重新登录");
            return false;
        }
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, String code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Result<?> result = Result.error(code, msg);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}

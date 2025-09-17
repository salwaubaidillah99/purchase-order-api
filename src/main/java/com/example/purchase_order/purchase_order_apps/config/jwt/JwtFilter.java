package com.example.purchase_order.purchase_order_apps.config.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil; // ADDED

    public JwtFilter(JwtUtil jwtUtil) { // ADDED
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (path.equals("/api/users/register") || path.equals("/api/users/login")
                || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String auth = req.getHeader("Authorization");
        System.out.println("[DBG] AUTH RAW: " + auth); // DEBUG

        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Missing token\",\"data\":null}");
            res.getWriter().flush();
            return;
        }

        String token = auth.substring(7).trim(); // CHANGED: trim
        System.out.println("[DBG] TOKEN LEN: " + token.length()); // DEBUG
        if (token.isEmpty()) { // ADDED
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Empty token\",\"data\":null}");
            res.getWriter().flush();
            return;
        }

        // DEBUG: lihat header & payload JWT, tanpa verifikasi
        try {
            String[] parts = token.split("\\.");
            String hdr = new String(java.util.Base64.getUrlDecoder().decode(parts[0]));
            String pld = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            System.out.println("[DBG] JWT HDR: " + hdr);
            System.out.println("[DBG] JWT PAYLOAD: " + pld);
        } catch (Exception ignored) {
            System.out.println("[DBG] Cannot decode JWT parts");
        }

        try {
            Long userId = jwtUtil.parseUserId(token);
            String firstName = jwtUtil.parseClaim(token, "firstName");
            String lastName  = jwtUtil.parseClaim(token, "lastName");
            System.out.println("[DBG] PARSED userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName); // DEBUG
            String audit = (firstName != null && lastName != null)
                    ? (firstName + "." + lastName).toLowerCase()
                    : null;

            req.setAttribute("authUserId", userId);
            req.setAttribute("authAuditName", audit);
            chain.doFilter(request, response);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Token expired\",\"data\":null}");
            res.getWriter().flush();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Invalid signature\",\"data\":null}");
            res.getWriter().flush();
        } catch (io.jsonwebtoken.MalformedJwtException e) { // ADDED
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Malformed token\",\"data\":null}");
            res.getWriter().flush();
        } catch (io.jsonwebtoken.UnsupportedJwtException e) { // ADDED
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Unsupported token\",\"data\":null}");
            res.getWriter().flush();
        } catch (IllegalArgumentException e) { // ADDED
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Empty or invalid token value\",\"data\":null}");
            res.getWriter().flush();
        } catch (Exception e) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"status\":401,\"message\":\"Invalid token\",\"data\":null}");
            res.getWriter().flush();
        }
    }
}

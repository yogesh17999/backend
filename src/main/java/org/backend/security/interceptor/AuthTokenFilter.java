package org.backend.security.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.backend.entity.User;
import org.backend.security.context.RequestContext;
import org.backend.service.UserService;
import org.backend.serviceImpl.UserDetailsImpl;
import org.backend.security.helper.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsImpl userDetailsImpl;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if(jwt == null){
                filterChain.doFilter(request, response);
            }
            else if (jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUserNameFromJwtToken(jwt);

                log.info("Incoming user from {}", request.getLocalAddr());
                log.info("Incoming user from {}",request.getHeaderNames());

                UserDetails userDetails = userDetailsImpl.loadUserByUsername(username);

                jwtUtil.setRequestContextDetails(userDetails,request);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Unauthorized");
                log.info("Unauthorized: invalid token");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            log.error("Cannot set user authentication: {}", e);
        }

    }

    private String parseJwt(HttpServletRequest request) {
         String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth)) {
            return  headerAuth.replaceAll("Bearer ", "");
        }

        return null;
    }
}
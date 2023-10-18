package com.lithan.merrymeals.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lithan.merrymeals.security.service.UserDetailsServiceImpl;
import org.springframework.util.StringUtils;

public class AuthTokenFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    try {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            List<String> roles = jwtUtils.getRolesFromJwtToken(jwt); // Get roles from JWT
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            // Create authentication object with roles
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    } catch (Exception e) {
        logger.error("Failed: {}", e.getMessage());
    }
    filterChain.doFilter(request, response);
}

    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth)
            && headerAuth.startsWith("Bearer ")){
                return headerAuth.substring(7, headerAuth.length());
            }
            return null;
    }
    
}

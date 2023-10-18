package com.lithan.merrymeals.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;



import com.lithan.merrymeals.security.jwt.AuthEntryPointJwt;
import com.lithan.merrymeals.security.jwt.AuthTokenFilter;
import com.lithan.merrymeals.security.oauth.CustomOAuth2User;
import com.lithan.merrymeals.security.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unAuthorizedHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.cors()
        .and().csrf().disable().exceptionHandling()
        .authenticationEntryPoint(unAuthorizedHandler)
        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().authorizeRequests()
        .antMatchers("/auth/**", "/paypal/**", "/pay").permitAll()
        .antMatchers("/api/**").hasAnyAuthority("ADMIN", "PARTNER", "CAREGIVER", "USER", "PENDING", "PENDING1", "PENDING2") 
        .anyRequest().authenticated();
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Bean
    AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authentication) throws Exception{
        return authentication.getAuthenticationManager();
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}


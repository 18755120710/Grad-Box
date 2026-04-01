package com.material.manaement.config;

import com.material.manaement.security.JwtAuthenticationTokenFilter;
import com.material.manaement.security.RestAuthenticationEntryPoint;
import com.material.manaement.security.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 开启跨域支持
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/*.html")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**/*.html")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**/*.css")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/**/*.js")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-resources/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/projects/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/projects")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/categories/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/categories")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS)).permitAll()
                        .anyRequest().authenticated());

        // 禁用页面缓存
        httpSecurity.headers(header -> header.cacheControl(cache -> cache.disable()));
        // 添加JWT过滤器
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加自定义未授权和未登录结果返回
        httpSecurity.exceptionHandling(exception -> exception
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                Arrays.asList("http://localhost:3095", "http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

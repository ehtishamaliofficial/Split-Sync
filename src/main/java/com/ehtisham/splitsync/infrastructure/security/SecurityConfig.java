package com.ehtisham.splitsync.infrastructure.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Value("${app.jwt.public-key}")
    private Resource publicKeyResource;

    @Value("${app.jwt.private-key}")
    private Resource privateKeyResource;

    /**
     * BCrypt password encoder (strength 12)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/api/v1/auth/register",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/index.html",
            "/swagger-ui/index.html"
    };

    /**
     * Main security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())));
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(successHandler));

        return http.build();
    }

    /**
     * JWT Decoder (verifies tokens using public key)
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            RSAPublicKey publicKey = loadPublicKey(publicKeyResource);
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key for JWT decoder", e);
        }

    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        RSAPublicKey publicKey = loadPublicKey(publicKeyResource);
        RSAPrivateKey privateKey = loadPrivateKey(privateKeyResource);

        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    // --- Utility methods for loading keys from PEM ---

    private RSAPublicKey loadPublicKey(Resource resource) throws Exception {
        byte[] decoded = readPemBytes(resource);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    private RSAPrivateKey loadPrivateKey(Resource resource) throws Exception {
        byte[] decoded = readPemBytes(resource);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private byte[] readPemBytes(Resource resource) throws Exception {
        String content;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            content = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());
        }
        return Base64.getDecoder().decode(content);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}

package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private String jwtSecret = "testSecretKeyForJwtTokenGenerationAndValidation";
    private int jwtExpirationMs = 86400000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void generateJwtToken_shouldReturnValidToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserNameFromJwtToken_shouldReturnUsername() {
        String username = "test@test.com";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String result = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals(username, result);
    }

    @Test
    void validateJwtToken_shouldReturnTrue_whenTokenValid() {
        String token = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenInvalid() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenExpired() {
        String expiredToken = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenHasInvalidSignature() {
        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(tokenWithWrongSignature);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenMalformed() {
        String malformedToken = "this.is.not.a.valid.jwt";

        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenEmpty() {
        String emptyToken = "";

        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenUnsupported() {
        // Create a token without signature (unsupported)
        String unsupportedToken = Jwts.builder()
                .setSubject("test@test.com")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(unsupportedToken);

        assertFalse(isValid);
    }
}

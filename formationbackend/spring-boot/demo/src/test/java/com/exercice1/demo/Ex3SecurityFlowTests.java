package com.exercice1.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.exercice1.DemoApplication;
import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;
import com.exercice1.security.security.EmailService;
import com.exercice1.security.security.JwtService;
import com.exercice1.security.service.LoginAttemptService;
import com.exercice1.security.service.RefreshTokenService;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class Ex3SecurityFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    void refreshToken_shouldReturnNewAccessToken() throws Exception {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .build();

        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .token("refresh-123")
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenService.verifyRefreshToken("refresh-123")).thenReturn(token);
        when(jwtService.generateToken(any(), any(Boolean.class))).thenReturn("new-access-token");
        when(jwtService.getExpirationTime(false)).thenReturn(86400000L);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "refreshToken": "refresh-123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-123"))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void resendVerification_shouldReturnOkForUnverifiedUser() throws Exception {
        User user = User.builder()
                .id(2L)
                .username("bob")
                .email("bob@example.com")
                .enabled(false)
                .verificationToken("old-token")
                .verificationTokenExpiry(LocalDateTime.now().plusHours(12))
                .build();

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        mockMvc.perform(post("/api/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "bob@example.com"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void verifyEmail_shouldReturnBadRequestWhenTokenExpired() throws Exception {
        User user = User.builder()
                .id(3L)
                .username("carol")
                .email("carol@example.com")
                .enabled(false)
                .verificationToken("expired-token")
                .verificationTokenExpiry(LocalDateTime.now().minusHours(1))
                .build();

        when(userRepository.findByVerificationToken("expired-token")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/verify/expired-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnTooManyRequestsWhenBlocked() throws Exception {
        when(loginAttemptService.isBlocked("locked-user")).thenReturn(true);
        when(loginAttemptService.getRemainingBlockTime("locked-user")).thenReturn(59L);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "locked-user",
                          "password": "Password123",
                          "rememberMe": false
                        }
                        """))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void login_shouldReturnBadRequestWhenEmailNotVerified() throws Exception {
        User user = User.builder()
                .id(4L)
                .username("david")
                .email("david@example.com")
                .password("encoded")
                .roles(Set.of("ROLE_USER"))
                .enabled(false)
                .build();

        when(loginAttemptService.isBlocked("david")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("david")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "david",
                          "password": "Password123",
                          "rememberMe": false
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "alice", roles = { "USER" })
    void changePassword_shouldRevokeRefreshTokensAndReturnOk() throws Exception {
        User user = User.builder()
                .id(5L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded-old")
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword123", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-new");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(refreshTokenService).revokeUserRefreshToken(any(User.class));
        doNothing().when(emailService).sendPasswordChangedEmail(anyString(), anyString());

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "oldPassword": "OldPassword123",
                          "newPassword": "NewPassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }
}

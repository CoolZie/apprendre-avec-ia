# 🚀 Exercice 3 : Fonctionnalités avancées JWT

## 🎯 Objectifs

Implémenter des fonctionnalités avancées de sécurité :
- ✅ Refresh tokens pour renouveler l'authentification
- ✅ Logout avec blacklist de tokens
- ✅ Change password sécurisé
- ✅ Email verification (simulation)
- ✅ Rate limiting contre brute force

---

## 📋 Cahier des Charges

### Fonctionnalités Requises

#### 1. Refresh Token
- Token d'accès : 1 heure
- Refresh token : 7 jours
- Endpoint `/api/auth/refresh` pour renouveler

#### 2. Logout
- Invalider les tokens actifs
- Blacklist en DB ou Redis (simulation avec Map)

#### 3. Change Password
- Vérifier l'ancien mot de passe
- Valider le nouveau (min 8 chars, complexity)
- Invalider tous les tokens existants

#### 4. Email Verification (Simulation)
- Générer un token de vérification
- Endpoint `/api/auth/verify/{token}`
- User pas activé tant que non vérifié

#### 5. Rate Limiting
- Max 5 tentatives de login par heure
- Bloquer temporairement après échecs répétés

### Endpoints à créer

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/api/auth/refresh` | Renouveler access token |
| POST | `/api/auth/logout` | Se déconnecter (blacklist token) |
| POST | `/api/auth/change-password` | Changer mot de passe |
| POST | `/api/auth/verify/{token}` | Vérifier email |
| POST | `/api/auth/resend-verification` | Renvoyer email de vérification |

---

## 📐 Architecture

```
src/main/java/com/formation/security/
├── model/
│   ├── User.java                    # +enabled, +verificationToken
│   ├── RefreshToken.java            # Nouveau
│   └── TokenBlacklist.java          # Nouveau
├── repository/
│   ├── RefreshTokenRepository.java  # Nouveau
│   └── TokenBlacklistRepository.java # Nouveau
├── service/
│   ├── RefreshTokenService.java     # Nouveau
│   ├── TokenBlacklistService.java   # Nouveau
│   ├── EmailService.java            # Nouveau (simulation)
│   └── LoginAttemptService.java     # Nouveau
├── controller/
│   └── AuthController.java          # Enrichi
├── dto/
│   ├── RefreshTokenRequest.java     # Nouveau
│   ├── ChangePasswordRequest.java   # Nouveau
│   └── TokenResponse.java           # Nouveau
└── security/
    └── JwtAuthenticationFilter.java # +blacklist check
```

---

## 🛠️ Instructions

### Étape 1 : Modifier User pour email verification

**Fichier** : `model/User.java`

```java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles")
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    // ✅ NOUVEAU : Email verification
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;  // Pas activé par défaut
    
    private String verificationToken;
    
    private LocalDateTime verificationTokenExpiry;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Étape 2 : Créer l'entité RefreshToken

**Fichier** : `model/RefreshToken.java`

```java
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
```

### Étape 3 : Créer l'entité TokenBlacklist

**Fichier** : `model/TokenBlacklist.java`

```java
@Entity
@Table(name = "token_blacklist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @CreationTimestamp
    private LocalDateTime blacklistedAt;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
```

### Étape 4 : Créer les Repositories

**RefreshTokenRepository.java** :
```java
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
```

**TokenBlacklistRepository.java** :
```java
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);
    
    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
```

### Étape 5 : Créer RefreshTokenService

**Fichier** : `service/RefreshTokenService.java`

```java
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    public RefreshToken createRefreshToken(User user) {
        // Supprimer les anciens refresh tokens de l'utilisateur
        refreshTokenRepository.deleteByUser(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
        
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token expired");
        }
        
        return refreshToken;
    }
    
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
    
    @Scheduled(cron = "0 0 * * * *")  // Toutes les heures
    @Transactional
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
```

### Étape 6 : Créer TokenBlacklistService

**Fichier** : `service/TokenBlacklistService.java`

```java
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final TokenBlacklistRepository blacklistRepository;
    private final JwtService jwtService;
    
    public void blacklistToken(String token) {
        // Extraire l'expiration du token
        Date expiration = jwtService.extractExpiration(token);
        
        TokenBlacklist blacklisted = TokenBlacklist.builder()
            .token(token)
            .expiryDate(expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime())
            .build();
        
        blacklistRepository.save(blacklisted);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }
    
    @Scheduled(cron = "0 0 * * * *")  // Toutes les heures
    @Transactional
    public void cleanExpiredTokens() {
        blacklistRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
```

### Étape 7 : Créer LoginAttemptService (Rate Limiting)

**Fichier** : `service/LoginAttemptService.java`

```java
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockCache = new ConcurrentHashMap<>();
    
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }
    
    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            blockCache.put(username, LocalDateTime.now().plusHours(1));
        }
    }
    
    public boolean isBlocked(String username) {
        if (!blockCache.containsKey(username)) {
            return false;
        }
        
        LocalDateTime blockUntil = blockCache.get(username);
        if (LocalDateTime.now().isAfter(blockUntil)) {
            blockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        
        return true;
    }
    
    public int getRemainingAttempts(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
}
```

### Étape 8 : Créer EmailService (Simulation)

**Fichier** : `service/EmailService.java`

```java
@Service
@Slf4j
public class EmailService {
    
    public void sendVerificationEmail(String to, String token) {
        // ✅ SIMULATION : Afficher dans les logs
        String verificationLink = "http://localhost:8080/api/auth/verify/" + token;
        
        log.info("=== EMAIL VERIFICATION ===");
        log.info("To: {}", to);
        log.info("Subject: Verify your email");
        log.info("Link: {}", verificationLink);
        log.info("=========================");
        
        // En production : Utiliser JavaMailSender ou un service externe (SendGrid, Mailgun)
    }
    
    public void sendPasswordChangedEmail(String to) {
        log.info("=== PASSWORD CHANGED ===");
        log.info("To: {}", to);
        log.info("Subject: Your password has been changed");
        log.info("========================");
    }
}
```

### Étape 9 : Créer les DTOs

**RefreshTokenRequest.java** :
```java
@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
```

**ChangePasswordRequest.java** :
```java
@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain uppercase, lowercase and digit"
    )
    private String newPassword;
}
```

**TokenResponse.java** :
```java
@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String email;
}
```

### Étape 10 : Modifier AuthService

**Fichier** : `service/AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService blacklistService;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Générer token de vérification
        String verificationToken = UUID.randomUUID().toString();
        
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of("ROLE_USER"))
            .enabled(false)  // Pas activé par défaut
            .verificationToken(verificationToken)
            .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
            .build();
        
        userRepository.save(user);
        
        // Envoyer email de vérification
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        
        // Générer tokens
        UserDetails userDetails = toUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return new TokenResponse(
            accessToken,
            refreshToken.getToken(),
            user.getUsername(),
            user.getEmail()
        );
    }
    
    public TokenResponse login(LoginRequest request) {
        // Vérifier si bloqué
        if (loginAttemptService.isBlocked(request.getUsername())) {
            throw new AccountBlockedException("Account temporarily blocked due to too many failed attempts");
        }
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            loginAttemptService.loginSucceeded(request.getUsername());
            
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(request.getUsername());
            int remaining = loginAttemptService.getRemainingAttempts(request.getUsername());
            throw new BadCredentialsException(
                "Invalid credentials. Remaining attempts: " + remaining
            );
        }
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        
        // Vérifier si email vérifié
        if (!user.isEnabled()) {
            throw new AccountNotEnabledException("Please verify your email first");
        }
        
        UserDetails userDetails = toUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return new TokenResponse(
            accessToken,
            refreshToken.getToken(),
            user.getUsername(),
            user.getEmail()
        );
    }
    
    public TokenResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenStr);
        User user = refreshToken.getUser();
        
        UserDetails userDetails = toUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);
        
        return new TokenResponse(
            newAccessToken,
            refreshToken.getToken(),
            user.getUsername(),
            user.getEmail()
        );
    }
    
    @Transactional
    public void logout(String accessToken) {
        // Blacklister le token
        blacklistService.blacklistToken(accessToken);
        
        // Supprimer refresh tokens
        String username = jwtService.extractUsername(accessToken);
        User user = userRepository.findByUsername(username).orElseThrow();
        refreshTokenService.deleteByUser(user);
    }
    
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Incorrect old password");
        }
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Invalider tous les refresh tokens
        refreshTokenService.deleteByUser(user);
        
        // Envoyer email de confirmation
        emailService.sendPasswordChangedEmail(user.getEmail());
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));
        
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Verification token expired");
        }
        
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.isEnabled()) {
            throw new IllegalStateException("Email already verified");
        }
        
        // Générer nouveau token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }
    
    private UserDetails toUserDetails(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .disabled(!user.isEnabled())
            .build();
    }
}
```

### Étape 11 : Enrichir AuthController

**Fichier** : `controller/AuthController.java`

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);  // Retirer "Bearer "
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/verify/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok("Verification email sent");
    }
}
```

### Étape 12 : Modifier JwtAuthenticationFilter pour vérifier blacklist

**Fichier** : `security/JwtAuthenticationFilter.java`

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;  // ✅ NOUVEAU
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        
        // ✅ Vérifier si le token est blacklisté
        if (blacklistService.isBlacklisted(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is blacklisted");
            return;
        }
        
        final String username = jwtService.extractUsername(jwt);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Étape 13 : Modifier JwtService pour exposer extractExpiration

**Fichier** : `security/JwtService.java`

```java
// Rendre public pour TokenBlacklistService
public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}
```

### Étape 14 : Modifier application.properties

```properties
# JWT Configuration
jwt.secret=bXlTdXBlclNlY3JldEtleVdpdGhNb3JlVGhhbjI1NkJpdHNGb3JTaGEyNTY=
jwt.expiration=3600000
jwt.refresh-expiration=604800000

# Scheduler (pour nettoyage auto)
spring.task.scheduling.enabled=true
```

### Étape 15 : Activer le Scheduling

**Fichier** : `Application.java`

```java
@SpringBootApplication
@EnableScheduling  // ✅ Activer les @Scheduled tasks
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}
```

### Étape 16 : Créer les Exceptions manquantes

**InvalidTokenException.java** :
```java
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
```

**AccountBlockedException.java** :
```java
public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String message) {
        super(message);
    }
}
```

**AccountNotEnabledException.java** :
```java
public class AccountNotEnabledException extends RuntimeException {
    public AccountNotEnabledException(String message) {
        super(message);
    }
}
```

**InvalidPasswordException.java** :
```java
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
```

### Étape 17 : Enrichir GlobalExceptionHandler

```java
@ExceptionHandler(InvalidTokenException.class)
public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(ex.getMessage()));
}

@ExceptionHandler(AccountBlockedException.class)
public ResponseEntity<ErrorResponse> handleAccountBlocked(AccountBlockedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage()));
}

@ExceptionHandler(AccountNotEnabledException.class)
public ResponseEntity<ErrorResponse> handleAccountNotEnabled(AccountNotEnabledException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse(ex.getMessage()));
}

@ExceptionHandler(InvalidPasswordException.class)
public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage()));
}
```

---

## 🧪 Tests

### Scenario 1 : Refresh Token

```bash
# 1. Login
POST /api/auth/login
{"username": "alice", "password": "Password123"}
# → {"accessToken": "...", "refreshToken": "abc-123-xyz"}

# 2. Attendre expiration access token (ou changer jwt.expiration=5000)

# 3. Utiliser refresh token
POST /api/auth/refresh
{"refreshToken": "abc-123-xyz"}
# → Nouveau {"accessToken": "...", "refreshToken": "abc-123-xyz"}
```

### Scenario 2 : Logout

```bash
# 1. Login
POST /api/auth/login
# → Token

# 2. Utiliser le token
GET /api/products
Authorization: Bearer <token>
# → 200 OK

# 3. Logout
POST /api/auth/logout
Authorization: Bearer <token>

# 4. Essayer avec le même token
GET /api/products
Authorization: Bearer <token>
# → 401 Unauthorized "Token is blacklisted"
```

### Scenario 3 : Change Password

```bash
POST /api/auth/change-password
Authorization: Bearer <token>
{
  "oldPassword": "Password123",
  "newPassword": "NewPassword456"
}
# → 200 OK

# Tous les refresh tokens invalides
POST /api/auth/refresh
{"refreshToken": "old-token"}
# → 401 "Refresh token not found"
```

### Scenario 4 : Email Verification

```bash
# 1. Register
POST /api/auth/register
{"username": "bob", "email": "bob@example.com", "password": "Password123"}
# → Logs : lien de vérification

# 2. Login avant vérification
POST /api/auth/login
{"username": "bob", "password": "Password123"}
# → 403 "Please verify your email first"

# 3. Vérifier email
POST /api/auth/verify/abc-123-xyz-token

# 4. Login après vérification
POST /api/auth/login
# → 200 OK
```

### Scenario 5 : Rate Limiting

```bash
# Essayer 6 fois avec mauvais password
POST /api/auth/login (x6)
{"username": "alice", "password": "wrong"}

# Réponse 1-4 : "Invalid credentials. Remaining attempts: 4, 3, 2, 1"
# Réponse 5 : "Invalid credentials. Remaining attempts: 0"
# Réponse 6+ : "Account temporarily blocked" (pendant 1h)
```

---

## ✅ Critères de Validation

### Fonctionnalités (60 points)

- [ ] **Refresh Tokens** (15 pts) : Génération, validation, renouvellement
- [ ] **Logout avec blacklist** (15 pts) : Token invalidé après logout
- [ ] **Change Password** (10 pts) : Vérification ancien + invalidation tokens
- [ ] **Email Verification** (10 pts) : Token expirable, activation compte
- [ ] **Rate Limiting** (10 pts) : Blocage après 5 échecs

### Sécurité (25 points)

- [ ] **Tokens expirables** (10 pts) : Access 1h, Refresh 7j
- [ ] **Blacklist fonctionnelle** (10 pts) : Vérifiée dans filter
- [ ] **Nettoyage auto** (5 pts) : @Scheduled pour expired tokens

### Tests (10 points)

- [ ] **Tous les scénarios** (10 pts)

### Bonus (5 points)

- [ ] **Redis pour blacklist** (3 pts) : Au lieu de DB
- [ ] **Login history** (2 pts) : Tracker les connexions

**Score total : /100**

---

**Bon courage ! 💪**  
*Temps estimé : 5-6 heures*  
*Difficulté : ⭐⭐⭐⭐⭐*

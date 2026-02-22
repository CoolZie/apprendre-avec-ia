# 🚀 Exercice 3 : Fonctionnalités avancées Spring Security

## 🎯 Objectifs

Implémenter des fonctionnalités avancées de sécurité :
- ✅ **Refresh tokens** pour renouveler l'authentification (voir guide séparé)
- ✅ **Logout** avec révocation de tokens
- ✅ **Change password** sécurisé
- ✅ **Email verification** (simulation en logs)
- ✅ **Rate limiting** contre brute force

> **Note importante** : Pour le **Refresh Token** et **Remember Me**, consulte le guide complet :  
> 📘 **`formationbackend/spring-security/GUIDE_REFRESH_TOKEN_REMEMBER_ME.md`**
>
> Cet exercice se concentre sur les autres fonctionnalités avancées.

---

## 📋 Cahier des Charges

### Fonctionnalités Requises

#### 1. Refresh Token & Remember Me
> ✅ **Voir le guide dédié** : `GUIDE_REFRESH_TOKEN_REMEMBER_ME.md`
> 
> Le guide contient :
> - Explications conceptuelles complètes
> - Architecture technique avec diagrammes
> - Implémentation étape par étape (11 étapes)
> - Tests et validation
> 
> Fais d'abord le guide refresh token avant de continuer cet exercice.

#### 2. Logout sécurisé
- Révoquer le refresh token en DB
- Optionnel : Blacklist des access tokens
- Endpoint `/api/auth/logout`

#### 3. Change Password
- Vérifier l'ancien mot de passe
- Valider le nouveau (min 8 chars, uppercase/lowercase/digit)
- Invalider tous les refresh tokens existants
- Envoyer email de notification

#### 4. Email Verification (Simulation)
- Générer un token de vérification à l'inscription
- Endpoint `/api/auth/verify/{token}`
- User pas actif tant que non vérifié
- Bloquer login si email non vérifié

#### 5. Rate Limiting
- Max 5 tentatives de login par utilisateur
- Bloquer temporairement (1h) après échecs répétés
- Afficher tentatives restantes

### Endpoints à créer/modifier

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/api/auth/refresh` | ✅ Voir GUIDE_REFRESH_TOKEN_REMEMBER_ME.md |
| POST | `/api/auth/logout` | Se déconnecter (révoque refresh token) |
| POST | `/api/auth/change-password` | Changer mot de passe |
| GET | `/api/auth/verify/{token}` | Vérifier email |
| POST | `/api/auth/resend-verification` | Renvoyer email de vérification |

---

## 📐 Architecture

```
src/main/java/com/exercice1/security/
├── model/
│   ├── User.java                    # +enabled, +verificationToken
│   └── RefreshToken.java            # ✅ Voir guide refresh token
├── repository/
│   ├── RefreshTokenRepository.java  # ✅ Voir guide refresh token
│   └── UserRepository.java
├── service/
│   ├── RefreshTokenService.java     # ✅ Voir guide refresh token
│   ├── EmailService.java            # Nouveau (simulation logs)
│   └── LoginAttemptService.java     # Nouveau (rate limiting)
├── controller/
│   └── AuthController.java          # Enrichi
├── dto/
│   ├── RefreshTokenRequest.java     # ✅ Voir guide refresh token
│   ├── ChangePasswordRequest.java   # Nouveau
│   └── VerificationRequest.java     # Nouveau
└── exception/
    └── AccountBlockedException.java # Nouveau
```

---

## 🛠️ Instructions

### Pré-requis : Implémenter Refresh Token

> ⚠️ **IMPORTANT** : Avant de commencer cet exercice, suis le guide :
> 
> **`formationbackend/spring-security/GUIDE_REFRESH_TOKEN_REMEMBER_ME.md`**
>
> Une fois terminé, reviens ici pour ajouter les fonctionnalités suivantes.

---

### Étape 1 : Modifier User pour email verification

**Fichier** : `security/model/User.java`

**Ajouter les champs** :
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
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
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

**💡 Explication** :
- `enabled` : L'utilisateur ne peut se connecter qu'après validation email
- `verificationToken` : UUID aléatoire envoyé dans l'email
- `verificationTokenExpiry` : Le token expire après 24h

---

### Étape 2 : Créer le service EmailService (Simulation)

**Fichier** : `security/service/EmailService.java` (NOUVEAU)

```java
package com.exercice1.security.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    
    /**
     * Simule l'envoi d'un email de vérification
     * En production : utiliser JavaMailSender ou SendGrid/Mailgun
     */
    public void sendVerificationEmail(String to, String token) {
        String verificationLink = "http://localhost:8080/api/auth/verify/" + token;
        
        log.info("╔════════════════════════════════════════╗");
        log.info("║     EMAIL DE VÉRIFICATION (SIMULATION)      ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║ To: {}", to);
        log.info("║ Subject: Vérifiez votre adresse email");
        log.info("║ ");
        log.info("║ Bonjour,");
        log.info("║ ");
        log.info("║ Cliquez sur le lien pour vérifier votre email :");
        log.info("║ {}", verificationLink);
        log.info("║ ");
        log.info("║ Ce lien expire dans 24 heures.");
        log.info("╚════════════════════════════════════════╝");
    }
    
    /**
     * Simule l'envoi d'un email de notification de changement de password
     */
    public void sendPasswordChangedEmail(String to, String username) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║   MOT DE PASSE MODIFIÉ (SIMULATION)    ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║ To: {}", to);
        log.info("║ Subject: Votre mot de passe a été modifié");
        log.info("║ ");
        log.info("║ Bonjour {},", username);
        log.info("║ ");
        log.info("║ Votre mot de passe a été modifié avec succès.");
        log.info("║ ");
        log.info("║ Si vous n'êtes pas à l'origine de ce changement,");
        log.info("║ contactez immédiatement le support.");
        log.info("╚════════════════════════════════════════╝");
    }
}
```

**💡 Explication** :
- Simule l'envoi d'emails en affichant dans les logs
- En production, utiliser `JavaMailSender` ou API externe

---

### Étape 3 : Créer le service LoginAttemptService (Rate Limiting)

**Fichier** : `security/service/LoginAttemptService.java` (NOUVEAU)

```java
package com.exercice1.security.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_HOURS = 1;
    
    // Map : username -> nombre de tentatives
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    
    // Map : username -> date de fin de blocage
    private final Map<String, LocalDateTime> blockCache = new ConcurrentHashMap<>();
    
    /**
     * Appelé après un login réussi
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }
    
    /**
     * Appelé après un échec de login
     */
    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            blockCache.put(username, LocalDateTime.now().plusHours(BLOCK_DURATION_HOURS));
        }
    }
    
    /**
     * Vérifie si un utilisateur est bloqué
     */
    public boolean isBlocked(String username) {
        if (!blockCache.containsKey(username)) {
            return false;
        }
        
        LocalDateTime blockUntil = blockCache.get(username);
        if (LocalDateTime.now().isAfter(blockUntil)) {
            // Le blocage est expiré
            blockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        
        return true;
    }
    
    /**
     * Nombre de tentatives restantes avant blocage
     */
    public int getRemainingAttempts(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
    
    /**
     * Temps restant de blocage (en minutes)
     */
    public long getRemainingBlockTime(String username) {
        if (!blockCache.containsKey(username)) {
            return 0;
        }
        
        LocalDateTime blockUntil = blockCache.get(username);
        long minutes = java.time.Duration.between(LocalDateTime.now(), blockUntil).toMinutes();
        return Math.max(0, minutes);
    }
}
```

**💡 Explication** :
- `ConcurrentHashMap` : Thread-safe pour accès concurrent
- Après 5 échecs → blocage 1h
- Login réussi → reset compteur

---

### Étape 4 : Créer les DTOs

**ChangePasswordRequest.java** (NOUVEAU) :
```java
package com.exercice1.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "Old password is required")
    private String oldPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String newPassword;
}
```

**VerificationRequest.java** (NOUVEAU) :
```java
package com.exercice1.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
```

---

### Étape 5 : Créer l'exception AccountBlockedException

**Fichier** : `security/exception/AccountBlockedException.java` (NOUVEAU)

```java
package com.exercice1.security.exception;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String message) {
        super(message);
    }
}
```

**Ajouter le handler dans SecurityExceptionHandler.java** :
```java
@ExceptionHandler(AccountBlockedException.class)
public ResponseEntity<ErrorResponse> handleAccountBlocked(AccountBlockedException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(new ErrorResponse(ex.getMessage()));
}
```

---

### Étape 6 : Modifier AuthController

**Fichier** : `security/controller/AuthController.java`

**Ajouter les injections** :
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;  // ✅ Déjà ajouté (guide refresh)
    private final EmailService emailService;                // ✅ NOUVEAU
    private final LoginAttemptService loginAttemptService;  // ✅ NOUVEAU
    
    // ... reste du code
}
```

**Modifier la méthode `register()`** pour ajouter email verification :

```java
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
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
        .enabled(false)  // ← Pas activé par défaut
        .verificationToken(verificationToken)
        .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
        .build();
    
    userRepository.save(user);
    
    // Envoyer email de vérification
    emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    
    // Générer tokens (même si non vérifié, pour tester)
    String accessToken = jwtService.generateToken(toUserDetails(user), false);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, false);
    
    return ResponseEntity.ok(AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .username(user.getUsername())
        .email(user.getEmail())
        .tokenType("Bearer")
        .expiresIn(jwtService.getExpirationTime(false))
        .build());
}
```

**Modifier la méthode `login()`** pour ajouter rate limiting et vérification email :

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    // 1. Vérifier si le compte est bloqué
    if (loginAttemptService.isBlocked(request.getUsername())) {
        long remainingMinutes = loginAttemptService.getRemainingBlockTime(request.getUsername());
        throw new AccountBlockedException(
            "Account temporarily blocked due to too many failed login attempts. " +
            "Try again in " + remainingMinutes + " minutes."
        );
    }
    
    try {
        // 2. Authentifier
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();
        
        // 3. Vérifier si l'email est vérifié
        if (!user.isEnabled()) {
            throw new InvalidDataException(
                "Email not verified. Please check your inbox and verify your email address."
            );
        }
        
        // 4. Login réussi → reset tentatives
        loginAttemptService.loginSucceeded(request.getUsername());
        
        // 5. Générer tokens
        boolean rememberMe = request.isRememberMe();
        String accessToken = jwtService.generateToken(toUserDetails(user), rememberMe);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, rememberMe);
        
        return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .username(user.getUsername())
            .email(user.getEmail())
            .tokenType("Bearer")
            .expiresIn(jwtService.getExpirationTime(rememberMe))
            .build());
            
    } catch (BadCredentialsException ex) {
        // 6. Login échoué → incrémenter tentatives
        loginAttemptService.loginFailed(request.getUsername());
        int remaining = loginAttemptService.getRemainingAttempts(request.getUsername());
        
        throw new InvalidDataException(
            "Invalid username or password. Remaining attempts: " + remaining
        );
    }
}
```

**Ajouter l'endpoint `/verify/{token}`** :

```java
/**
 * Vérifier l'email avec le token reçu par email
 */
@GetMapping("/verify/{token}")
public ResponseEntity<String> verifyEmail(@PathVariable String token) {
    User user = userRepository.findByVerificationToken(token)
        .orElseThrow(() -> new InvalidDataException("Invalid verification token"));
    
    // Vérifier si le token n'est pas expiré
    if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new InvalidDataException("Verification token has expired");
    }
    
    // Activer l'utilisateur
    user.setEnabled(true);
    user.setVerificationToken(null);
    user.setVerificationTokenExpiry(null);
    userRepository.save(user);
    
    return ResponseEntity.ok("Email verified successfully. You can now login.");
}
```

**Ajouter l'endpoint `/resend-verification`** :

```java
/**
 * Renvoyer un email de vérification
 */
@PostMapping("/resend-verification")
public ResponseEntity<String> resendVerification(@Valid @RequestBody VerificationRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new InvalidDataException("User not found"));
    
    if (user.isEnabled()) {
        throw new InvalidDataException("Email already verified");
    }
    
    // Générer nouveau token
    String newToken = UUID.randomUUID().toString();
    user.setVerificationToken(newToken);
    user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
    userRepository.save(user);
    
    // Renvoyer email
    emailService.sendVerificationEmail(user.getEmail(), newToken);
    
    return ResponseEntity.ok("Verification email sent. Please check your inbox.");
}
```

**Ajouter l'endpoint `/change-password`** :

```java
/**
 * Changer le mot de passe de l'utilisateur connecté
 */
@PostMapping("/change-password")
public ResponseEntity<String> changePassword(
    @AuthenticationPrincipal UserDetails userDetails,
    @Valid @RequestBody ChangePasswordRequest request) {
    
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new InvalidDataException("User not found"));
    
    // Vérifier l'ancien mot de passe
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
        throw new InvalidDataException("Old password is incorrect");
    }
    
    // Vérifier que le nouveau est différent
    if (request.getOldPassword().equals(request.getNewPassword())) {
        throw new InvalidDataException("New password must be different from old password");
    }
    
    // Changer le mot de passe
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    
    // Invalider tous les refresh tokens de l'utilisateur
    refreshTokenService.revokeUserTokens(user);
    
    // Envoyer email de notification
    emailService.sendPasswordChangedEmail(user.getEmail(), user.getUsername());
    
    return ResponseEntity.ok("Password changed successfully. Please login again.");
}
```

---

### Étape 7 : Ajouter findByEmail et findByVerificationToken dans UserRepository

**Fichier** : `security/repository/UserRepository.java`

**Ajouter** :
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);  // ✅ NOUVEAU
    Optional<User> findByVerificationToken(String token);  // ✅ NOUVEAU
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);  // ✅ NOUVEAU
}
```

---

## ✅ Tests et Validation

### Test 1 : Inscription avec email verification

**1. S'inscrire** :
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "Password123"
}
```

**Résultat** :
- ✅ 200 OK
- ✅ Token access et refresh générés
- ✅ Email de vérification affiché dans les logs
- ✅ `user.enabled = false` en DB

**2. Vérifier les logs** :
```
╔════════════════════════════════════════╗
║     EMAIL DE VÉRIFICATION (SIMULATION)      ║
╠════════════════════════════════════════╣
║ To: alice@example.com
║ Link: http://localhost:8080/api/auth/verify/<uuid>
╚════════════════════════════════════════╝
```

**3. Tenter de se connecter AVANT vérification** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "Password123"
}
```

**Résultat** :
- ❌ 400 Bad Request
- Message : "Email not verified. Please check your inbox..."

**4. Vérifier l'email** :
```bash
GET http://localhost:8080/api/auth/verify/<uuid-from-logs>
```

**Résultat** :
- ✅ 200 OK
- Message : "Email verified successfully. You can now login."
- `user.enabled = true` en DB

**5. Se connecter APRÈS vérification** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "Password123"
}
```

**Résultat** :
- ✅ 200 OK
- Access token et refresh token générés

---

### Test 2 : Rate Limiting (Brute Force)

**1. Tentative avec mauvais password (5 fois)** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "WrongPassword"
}
```

**Résultats** :
- Tentative 1 : "Invalid username or password. Remaining attempts: 4"
- Tentative 2 : "... Remaining attempts: 3"
- Tentative 3 : "... Remaining attempts: 2"
- Tentative 4 : "... Remaining attempts: 1"
- Tentative 5 : "... Remaining attempts: 0"

**2. Tentative 6 (bloqué)** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "Password123"
}
```

**Résultat** :
- ❌ 429 Too Many Requests
- Message : "Account temporarily blocked due to too many failed login attempts. Try again in 60 minutes."

**3. Login réussi débloque** :
- Attendre 1h OU
- En dev, modifier le code pour réduire la durée de blocage à 1 minute
- Login réussi → reset compteur

---

### Test 3 : Changement de password

**1. Se connecter** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "Password123"
}
```

**2. Changer le password** :
```bash
POST http://localhost:8080/api/auth/change-password
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "oldPassword": "Password123",
  "newPassword": "NewPassword456"
}
```

**Résultat** :
- ✅ 200 OK
- Message : "Password changed successfully. Please login again."
- Email de notification dans les logs
- Refresh tokens révoqués (vérifier en DB)

**3. Vérifier que le refresh token est invalidé** :
```bash
POST http://localhost:8080/api/auth/refresh
{
  "refreshToken": "<old_refresh_token>"
}
```

**Résultat** :
- ❌ 400 Bad Request
- Message : "Invalid refresh token" ou "Refresh token has been revoked"

**4. Se reconnecter avec nouveau password** :
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "alice",
  "password": "NewPassword456"
}
```

**Résultat** :
- ✅ 200 OK
- Nouveaux tokens générés

---

### Test 4 : Renvoyer email de vérification

**1. S'inscrire** :
```bash
POST http://localhost:8080/api/auth/register
{
  "username": "bob",
  "email": "bob@example.com",
  "password": "Password123"
}
```

**2. Ne pas vérifier l'email immédiatement**

**3. Demander un renvoi** :
```bash
POST http://localhost:8080/api/auth/resend-verification
Content-Type: application/json

{
  "email": "bob@example.com"
}
```

**Résultat** :
- ✅ 200 OK
- Message : "Verification email sent..."
- Nouveau token généré en DB
- Email affiché dans les logs

---

## 📝 Récapitulatif

### Ce que tu as appris

1. **Email Verification** :
   - Génération de token UUID
   - Expiration de token (24h)
   - Activation compte après vérification
   - Blocage login si non vérifié

2. **Rate Limiting** :
   - Compteur de tentatives par utilisateur
   - Blocage temporaire (1h)
   - Reset après login réussi
   - Protection contre brute force

3. **Change Password** :
   - Vérification ancien password
   - Validation nouveau (complexité)
   - Révocation tokens après changement
   - Notification email

4. **Logout sécurisé** :
   - Révocation refresh token (voir guide)
   - Optionnel : blacklist access tokens

5. **EmailService** :
   - Simulation en logs (dev)
   - Production : JavaMailSender ou API externe

---

## 🎯 Critères de Réussite

- [ ] Refresh Token implémenté (GUIDE_REFRESH_TOKEN_REMEMBER_ME.md)
- [ ] Email verification fonctionnel
- [ ] Token de vérification expire après 24h
- [ ] Login bloqué si email non vérifié
- [ ] Rate limiting : 5 tentatives max, blocage 1h
- [ ] Change password avec validations
- [ ] Refresh tokens révoqués après changement password
- [ ] EmailService affiche dans les logs
- [ ] Resend verification fonctionne
- [ ] Tests réussis pour tous les scénarios

---

## 💡 Conseils

1. **Commence par le guide Refresh Token** avant cet exercice
2. **Teste email verification en premier** (plus simple)
3. **Rate limiting** : Réduis le blocage à 1 minute en dev
4. **Vérifie en DB** : tables user_roles, refresh_tokens
5. **Utilise Postman** : Collections pour USER/MODERATOR/ADMIN
6. **Logs** : Active DEBUG pour Spring Security si besoin
7. **Production** : Remplace EmailService par vrai service email

---

## 🚀 Pour aller plus loin (Optionnel)

Si tu veux améliorer encore :

1. **Vraie Blacklist** : Redis pour les access tokens révoqués
2. **2FA** : Two-Factor Authentication avec TOTP
3. **OAuth2** : Login avec Google/GitHub
4. **Audit Log** : Tracer toutes les actions sécurité
5. **IP Tracking** : Rate limit par IP + username
6. **Password History** : Empêcher réutilisation anciens passwords

---

**Bon courage ! 🔐🚀**

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

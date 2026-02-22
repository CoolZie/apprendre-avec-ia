# 🔄 Guide Complet : Refresh Token & Remember Me

## 📚 Table des matières

1. [Concepts fondamentaux](#concepts)
2. [Pourquoi utiliser un Refresh Token ?](#pourquoi)
3. [Architecture technique](#architecture)
4. [Remember Me - Explication](#remember-me)
5. [Plan d'implémentation étape par étape](#implementation)
6. [Tests et validation](#tests)

---

## 🎯 Concepts fondamentaux {#concepts}

### Access Token (JWT actuel)

**Ce que tu as déjà :**
- Token JWT généré au login
- Durée de vie : 24 heures
- Contient : username, roles, expiration
- Envoyé dans chaque requête : `Authorization: Bearer <token>`

**Problème :**
- Si le token expire, l'utilisateur est déconnecté brutalement
- L'utilisateur doit se reconnecter (username + password)
- Risque de sécurité si on augmente la durée (si volé, valide longtemps)

### Refresh Token (nouveau)

**C'est quoi ?**
- Un second token distinct de l'access token
- Durée de vie plus longue (7 jours, 30 jours)
- NE contient PAS de données sensibles (juste un UUID aléatoire)
- Stocké en base de données (peut être révoqué)
- Utilisé UNIQUEMENT pour obtenir un nouveau access token

**Avantages :**
- ✅ Access token court (15 min) = plus sécurisé
- ✅ Refresh token long = pas de déconnexion brutale
- ✅ Peut être révoqué (logout, changement password)
- ✅ Traçabilité (on sait qui a des tokens actifs)

---

## 🤔 Pourquoi utiliser un Refresh Token ? {#pourquoi}

### Scénario sans Refresh Token (ton système actuel)

```
1. User login → Access Token (24h)
2. User utilise l'app pendant 23h59
3. Token expire
4. User fait une action → 401 Unauthorized
5. User doit se reconnecter (mauvaise UX)
```

**Solutions simplistes (mauvaises) :**
- ❌ Augmenter l'expiration à 30 jours → risque si le token est volé
- ❌ Redemander password toutes les 24h → mauvaise UX

### Scénario avec Refresh Token (meilleur)

```
1. User login → Access Token (15 min) + Refresh Token (7 jours)
2. Frontend stocke les 2 tokens
3. User utilise l'app
4. Après 15 min, Access Token expire
5. Frontend détecte 401
6. Frontend envoie Refresh Token au backend
7. Backend vérifie en DB → génère nouveau Access Token
8. User continue sans interruption
```

**Avantages :**
- ✅ Token actif court (si volé, expire vite)
- ✅ UX fluide (pas de déconnexion)
- ✅ Contrôle (on peut révoquer le Refresh Token)

---

## 🏗️ Architecture technique {#architecture}

### Flux complet

```
┌─────────────────────────────────────────────────────────────────┐
│                      1. LOGIN                                    │
│                                                                  │
│  Client                           Backend                        │
│    │                                 │                           │
│    │  POST /api/auth/login           │                           │
│    │  { username, password }         │                           │
│    │─────────────────────────────────>                           │
│    │                                 │                           │
│    │                                 │ 1. Vérifier credentials   │
│    │                                 │ 2. Générer Access Token   │
│    │                                 │ 3. Créer Refresh Token    │
│    │                                 │ 4. Sauvegarder en DB      │
│    │                                 │                           │
│    │  {                              │                           │
│    │    accessToken: "eyJhbGc...",   │                           │
│    │    refreshToken: "uuid...",     │                           │
│    │    expiresIn: 900000            │                           │
│    │  }                              │                           │
│    <─────────────────────────────────│                           │
│    │                                 │                           │
│    │ Stocke en localStorage          │                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   2. UTILISATION NORMALE                         │
│                                                                  │
│  Client                           Backend                        │
│    │                                 │                           │
│    │  GET /api/products              │                           │
│    │  Authorization: Bearer <access> │                           │
│    │─────────────────────────────────>                           │
│    │                                 │ Vérifie Access Token      │
│    │                                 │ ✅ Valide                 │
│    │  { products: [...] }            │                           │
│    <─────────────────────────────────│                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│              3. ACCESS TOKEN EXPIRÉ (après 15 min)               │
│                                                                  │
│  Client                           Backend                        │
│    │                                 │                           │
│    │  GET /api/products              │                           │
│    │  Authorization: Bearer <expired>│                           │
│    │─────────────────────────────────>                           │
│    │                                 │ Vérifie Access Token      │
│    │                                 │ ❌ Expiré                 │
│    │  401 Unauthorized               │                           │
│    <─────────────────────────────────│                           │
│    │                                 │                           │
│    │ Frontend intercepte 401         │                           │
│    │                                 │                           │
│    │  POST /api/auth/refresh         │                           │
│    │  { refreshToken: "uuid..." }    │                           │
│    │─────────────────────────────────>                           │
│    │                                 │ 1. Chercher en DB         │
│    │                                 │ 2. Vérifier validité      │
│    │                                 │ 3. Générer nouvel Access  │
│    │                                 │                           │
│    │  {                              │                           │
│    │    accessToken: "eyJnew...",    │                           │
│    │    refreshToken: "uuid...",     │                           │
│    │    expiresIn: 900000            │                           │
│    │  }                              │                           │
│    <─────────────────────────────────│                           │
│    │                                 │                           │
│    │ Relance requête initiale        │                           │
│    │ avec nouveau token              │                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      4. LOGOUT                                   │
│                                                                  │
│  Client                           Backend                        │
│    │                                 │                           │
│    │  POST /api/auth/logout          │                           │
│    │  { refreshToken: "uuid..." }    │                           │
│    │─────────────────────────────────>                           │
│    │                                 │ 1. Chercher en DB         │
│    │                                 │ 2. Marquer revoked=true   │
│    │                                 │                           │
│    │  "Logged out successfully"      │                           │
│    <─────────────────────────────────│                           │
│    │                                 │                           │
│    │ Supprime tokens en localStorage │                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Structure en base de données

**Table : `refresh_tokens`**
```sql
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,      -- UUID aléatoire
    expiry_date TIMESTAMP NOT NULL,          -- Date d'expiration
    revoked BOOLEAN DEFAULT FALSE,           -- Révoqué (logout)
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Pourquoi stocker en DB ?**
- ✅ Permet de révoquer (logout)
- ✅ Permet de lister les sessions actives
- ✅ Permet de déconnecter sur tous les appareils
- ✅ Permet de supprimer les tokens expirés

---

## 💭 Remember Me - Explication {#remember-me}

### C'est quoi ?

**Remember Me = "Se souvenir de moi"**
- Checkbox sur le formulaire de login
- Si cochée → tokens plus longs
- Si non cochée → tokens courts

### Durées recommandées

| Type | Sans Remember Me | Avec Remember Me |
|------|-----------------|------------------|
| **Access Token** | 15 minutes | 7 jours |
| **Refresh Token** | 7 jours | 30 jours |

### Cas d'usage

**Sans Remember Me (usage normal) :**
- Ordinateur partagé (bureau, cybercafé)
- Ordinateur public
- Sécurité maximale

**Avec Remember Me :**
- Ordinateur personnel
- Téléphone personnel
- Confort d'utilisation

### Exemple UX

```
┌──────────────────────────────────┐
│         Login                    │
│                                  │
│  Username: [john_doe____]        │
│  Password: [••••••••____]        │
│                                  │
│  ☑ Remember me on this device   │ ← Cette checkbox
│                                  │
│  [      Login      ]             │
│                                  │
└──────────────────────────────────┘
```

**Si cochée :**
- Access Token valide 7 jours
- Refresh Token valide 30 jours
- L'utilisateur peut fermer le navigateur et revenir 1 semaine plus tard sans se reconnecter

**Si non cochée :**
- Access Token valide 15 min
- Refresh Token valide 7 jours
- Plus sécurisé mais nécessite refresh plus souvent

---

## 🛠️ Plan d'implémentation étape par étape {#implementation}

### Vue d'ensemble

**Fichiers à créer :**
1. `RefreshToken.java` (model)
2. `RefreshTokenRepository.java` (repository)
3. `RefreshTokenService.java` (service)
4. `RefreshTokenRequest.java` (dto)
5. `InvalidDataException.java` (exception)

**Fichiers à modifier :**
6. `LoginRequest.java` (ajout rememberMe)
7. `AuthResponse.java` (ajout refreshToken)
8. `JwtService.java` (ajout remember me)
9. `AuthController.java` (endpoints refresh/logout)
10. `SecurityExceptionHandler.java` (gestion erreurs)
11. `application.properties` (config durées)

---

### ÉTAPE 1 : Créer l'entité RefreshToken

**📁 Fichier : `model/RefreshToken.java`**

```java
package com.exercice1.security.model;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.*;

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
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                  // ← L'utilisateur propriétaire
    
    private String token;                // ← UUID aléatoire
    
    private Instant expiryDate;          // ← Date d'expiration
    
    private boolean revoked;             // ← Si révoqué (logout)
}
```

**💡 Explications :**
- `@ManyToOne` : Un user peut avoir plusieurs refresh tokens (multi-appareils)
- `token` : UUID généré aléatoirement (pas un JWT)
- `expiryDate` : Instant (timestamp UTC)
- `revoked` : Permet de désactiver sans supprimer

**🎯 À retenir :**
- Ne stocke PAS de données sensibles
- Le token est juste un identifiant aléatoire
- Permet de chercher en DB et vérifier validité

---

### ÉTAPE 2 : Créer le repository

**📁 Fichier : `repository/RefreshTokenRepository.java`**

```java
package com.exercice1.security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    // Trouver un token par sa valeur
    Optional<RefreshToken> findByToken(String token);
    
    // Supprimer tous les tokens d'un utilisateur (logout all devices)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(User user);
    
    // Nettoyer les tokens expirés (tâche périodique)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
```

**💡 Explications :**
- `findByToken()` : Chercher quand l'utilisateur envoie son refresh token
- `deleteByUser()` : Logout sur tous les appareils
- `deleteExpiredTokens()` : Nettoyage automatique (optionnel)

**🎯 À retenir :**
- @Modifying pour les requêtes DELETE/UPDATE
- CURRENT_TIMESTAMP pour comparer avec la date actuelle

---

### ÉTAPE 3 : Créer le service RefreshToken

**📁 Fichier : `service/RefreshTokenService.java`**

```java
package com.exercice1.security.service;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.exercice1.security.exception.InvalidDataException;
import com.exercice1.security.model.RefreshToken;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${jwt.refresh.expiration:604800000}") // 7 jours par défaut
    private long refreshTokenExpiration;
    
    @Value("${jwt.refresh.expiration.rememberMe:2592000000}") // 30 jours
    private long refreshTokenExpirationRememberMe;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    /**
     * Créer un nouveau refresh token pour un utilisateur
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, boolean rememberMe) {
        // 1. Supprimer les anciens tokens (1 seul token actif par user)
        refreshTokenRepository.deleteByUser(user);
        
        // 2. Calculer la durée selon rememberMe
        long expirationTime = rememberMe 
            ? refreshTokenExpirationRememberMe 
            : refreshTokenExpiration;
        
        // 3. Créer le token
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())  // ← UUID aléatoire
            .expiryDate(Instant.now().plusMillis(expirationTime))
            .revoked(false)
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Vérifier et récupérer un refresh token valide
     */
    public RefreshToken verifyRefreshToken(String token) {
        // 1. Chercher en DB
        RefreshToken refreshToken = refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new InvalidDataException("Invalid refresh token"));
        
        // 2. Vérifier si révoqué
        if (refreshToken.isRevoked()) {
            throw new InvalidDataException("Refresh token has been revoked");
        }
        
        // 3. Vérifier si expiré
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidDataException("Refresh token has expired");
        }
        
        return refreshToken;
    }
    
    /**
     * Révoquer un refresh token (logout)
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
}
```

**💡 Explications étape par étape :**

**createRefreshToken() :**
1. Supprime les anciens tokens du user (politique 1 token/user)
2. Choisit la durée selon `rememberMe`
3. Génère un UUID aléatoire (pas un JWT)
4. Calcule `expiryDate = maintenant + durée`
5. Sauvegarde en DB

**verifyRefreshToken() :**
1. Cherche le token en DB
2. Vérifie s'il a été révoqué (logout)
3. Vérifie s'il est expiré (compare dates)
4. Si expiré → supprime de la DB
5. Sinon → retourne le token valide

**revokeRefreshToken() :**
1. Trouve le token
2. Met `revoked = true`
3. Sauvegarde (soft delete)

**🎯 À retenir :**
- On ne supprime pas immédiatement (on marque revoked)
- UUID.randomUUID() génère une chaîne unique
- @Transactional garantit l'intégrité

---

### ÉTAPE 4 : Créer le DTO RefreshTokenRequest

**📁 Fichier : `dto/RefreshTokenRequest.java`**

```java
package com.exercice1.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
```

**💡 Explication :**
- DTO simple pour recevoir le refresh token
- Utilisé par `/refresh` et `/logout`

---

### ÉTAPE 5 : Créer l'exception InvalidDataException

**📁 Fichier : `exception/InvalidDataException.java`**

```java
package com.exercice1.security.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
```

**💡 Explication :**
- Exception pour données invalides (token expiré, révoqué)
- RuntimeException → pas besoin de try/catch

---

### ÉTAPE 6 : Modifier LoginRequest (ajout Remember Me)

**📁 Fichier : `dto/LoginRequest.java`**

```java
package com.exercice1.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * Si true, l'access token durera 7 jours au lieu de 15 min
     * et le refresh token durera 30 jours au lieu de 7 jours
     */
    private boolean rememberMe = false;  // ← Nouveau champ
}
```

**💡 Explication :**
- Valeur par défaut `false` (sécurité)
- Frontend enverra `true` si checkbox cochée

---

### ÉTAPE 7 : Modifier AuthResponse (ajout refreshToken)

**📁 Fichier : `dto/AuthResponse.java`**

```java
package com.exercice1.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;       // ← Renommé (avant "token")
    private String refreshToken;      // ← Nouveau
    private String username;
    private String email;
    private String tokenType;         // ← "Bearer"
    private long expiresIn;           // ← Durée en millisecondes
    
    // Constructeur pour compatibilité (optionnel)
    public AuthResponse(String token, String username, String email) {
        this.accessToken = token;
        this.username = username;
        this.email = email;
        this.tokenType = "Bearer";
    }
}
```

**💡 Explications :**
- `accessToken` : Le JWT (avant nommé "token")
- `refreshToken` : Le UUID du refresh token
- `tokenType` : Toujours "Bearer" (standard OAuth2)
- `expiresIn` : Durée en ms (frontend calcule expiration)

**Exemple de réponse :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "a3f2b1c9-8d7e-4f6a-b5c3-d9e8f7a6b5c4",
  "username": "john_doe",
  "email": "john@example.com",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

---

### ÉTAPE 8 : Modifier JwtService (support Remember Me)

**📁 Fichier : `security/JwtService.java`**

**Ajouter :**
```java
@Value("${jwt.expiration.rememberMe:604800000}") // 7 jours
private long expirationRememberMe;
```

**Modifier generateToken() :**
```java
// Méthode 1 : Sans remember me (compatibilité)
public String generateToken(UserDetails userDetails) {
    return generateToken(userDetails, false);
}

// Méthode 2 : Avec remember me (surcharge)
public String generateToken(UserDetails userDetails, boolean rememberMe) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList()));
    
    // Choisir la durée selon rememberMe
    long expirationTime = rememberMe ? expirationRememberMe : expiration;
    
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSigningKey())
        .compact();
}

// Méthode utilitaire pour obtenir la durée
public long getExpirationTime(boolean rememberMe) {
    return rememberMe ? expirationRememberMe : expiration;
}
```

**💡 Explications :**
- Surcharge : 2 méthodes avec même nom, paramètres différents
- Si `rememberMe=true` → expiration longue
- Si `rememberMe=false` → expiration courte

---

### ÉTAPE 9 : Modifier AuthController (endpoints refresh/logout)

**📁 Fichier : `controller/AuthController.java`**

**Ajouter l'injection :**
```java
private final RefreshTokenService refreshTokenService;
```

**Modifier register() :**
```java
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    // ... (validation et création user)
    
    // Générer les tokens (pas de remember me au register)
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

**Modifier login() :**
```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    // Authentifier
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
    
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow();
    
    // Générer avec support remember me
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
}
```

**Ajouter l'endpoint /refresh :**
```java
/**
 * Rafraîchir l'access token avec un refresh token valide
 */
@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    // 1. Vérifier le refresh token
    RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
    User user = refreshToken.getUser();
    
    // 2. Générer un nouveau access token
    String accessToken = jwtService.generateToken(toUserDetails(user), false);
    
    // 3. Retourner les tokens (même refresh token)
    return ResponseEntity.ok(AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())  // ← Même refresh token
        .username(user.getUsername())
        .email(user.getEmail())
        .tokenType("Bearer")
        .expiresIn(jwtService.getExpirationTime(false))
        .build());
}
```

**Ajouter l'endpoint /logout :**
```java
/**
 * Déconnexion - révoque le refresh token
 */
@PostMapping("/logout")
public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
    refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    return ResponseEntity.ok("Logged out successfully");
}
```

**💡 Explications :**

**register() :**
- Crée access token + refresh token
- Pas de remember me (toujours false)

**login() :**
- Récupère `rememberMe` du request
- Passe à `generateToken()` et `createRefreshToken()`

**refresh() :**
- Vérifie le refresh token en DB
- Génère un NOUVEAU access token
- Retourne le MÊME refresh token (on ne le change pas)

**logout() :**
- Marque le refresh token comme révoqué
- L'access token expire naturellement

---

### ÉTAPE 10 : Modifier SecurityExceptionHandler

**📁 Fichier : `exception/SecurityExceptionHandler.java`**

**Ajouter :**
```java
@ExceptionHandler(InvalidDataException.class)
public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage()));
}
```

**💡 Explication :**
- Gère les erreurs de refresh token invalide/expiré
- HTTP 400 (Bad Request)

---

### ÉTAPE 11 : Modifier application.properties

**📁 Fichier : `application.properties`**

**Ajouter :**
```properties
# JWT Configuration
jwt.secret=bXlTdXBlclNlY3JldEtleVdpdGhNb3JlVGhhbjI1NkJpdHNGb3JTaGEyNTY=

# Access token expiration (15 minutes)
jwt.expiration=900000

# Access token expiration with remember me (7 days)
jwt.expiration.rememberMe=604800000

# Refresh token expiration (7 days)
jwt.refresh.expiration=604800000

# Refresh token expiration with remember me (30 days)
jwt.refresh.expiration.rememberMe=2592000000
```

**💡 Conversions :**
- 15 minutes = 15 × 60 × 1000 = 900 000 ms
- 7 jours = 7 × 24 × 60 × 60 × 1000 = 604 800 000 ms
- 30 jours = 30 × 24 × 60 × 60 × 1000 = 2 592 000 000 ms

---

## ✅ Tests et validation {#tests}

### Test 1 : Login sans Remember Me

**Requête :**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "rememberMe": false
}
```

**Réponse attendue :**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "uuid-...",
  "username": "john_doe",
  "email": "john@example.com",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

**Vérifications :**
- ✅ Access token expire dans 15 min
- ✅ Refresh token créé en DB avec expiration 7 jours
- ✅ `revoked = false`

---

### Test 2 : Login avec Remember Me

**Requête :**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "rememberMe": true
}
```

**Réponse attendue :**
```json
{
  "expiresIn": 604800000
}
```

**Vérifications :**
- ✅ Access token expire dans 7 jours
- ✅ Refresh token expire dans 30 jours

---

### Test 3 : Utiliser l'Access Token

**Requête :**
```bash
GET http://localhost:8080/api/auth/me
Authorization: Bearer eyJhbGc...
```

**Résultat :**
- ✅ 200 OK si le token est valide
- ✅ 401 Unauthorized si expiré

---

### Test 4 : Refresh Token

**Attendre 15 minutes (access token expiré) ou modifier l'expiration à 10 secondes pour tester.**

**Requête :**
```bash
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "uuid-from-login"
}
```

**Réponse attendue :**
```json
{
  "accessToken": "eyJnew...",
  "refreshToken": "uuid-same",
  "username": "john_doe",
  "email": "john@example.com",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

**Vérifications :**
- ✅ Nouveau access token généré
- ✅ Même refresh token retourné
- ✅ Peut utiliser le nouvel access token

---

### Test 5 : Logout

**Requête :**
```bash
POST http://localhost:8080/api/auth/logout
Content-Type: application/json

{
  "refreshToken": "uuid-from-login"
}
```

**Réponse :**
```
"Logged out successfully"
```

**Vérifications en DB :**
```sql
SELECT * FROM refresh_tokens WHERE token = 'uuid...';
-- revoked = true
```

**Tester refresh après logout :**
```bash
POST http://localhost:8080/api/auth/refresh
{
  "refreshToken": "uuid-same"
}
```

**Résultat attendu :**
```json
{
  "message": "Refresh token has been revoked"
}
```
- ✅ HTTP 400

---

### Test 6 : Vérifier en base H2

**Accéder à la console H2 :**
```
http://localhost:8080/h2-console
```

**Requête SQL :**
```sql
SELECT 
    rt.id,
    rt.token,
    rt.expiry_date,
    rt.revoked,
    u.username
FROM refresh_tokens rt
JOIN users u ON rt.user_id = u.id;
```

**Vérifications :**
- ✅ Token présent après login
- ✅ `expiryDate` correct (7 ou 30 jours selon rememberMe)
- ✅ `revoked = true` après logout

---

## 📝 Récapitulatif des concepts

### Questions pour vérifier ta compréhension

1. **Pourquoi un refresh token en plus de l'access token ?**
   - → Access token court = sécurisé
   - → Refresh token long = confort UX
   - → Peut être révoqué contrairement au JWT

2. **Pourquoi stocker le refresh token en DB ?**
   - → Permet de révoquer (logout)
   - → Traçabilité des sessions
   - → Nettoyage des tokens expirés

3. **Quelle est la différence entre access token et refresh token ?**
   - → Access : JWT signé, contient données, pas en DB
   - → Refresh : UUID aléatoire, en DB, pas de données

4. **Remember Me modifie quoi ?**
   - → Durée des tokens (plus longue)
   - → Ne change pas le comportement

5. **Que se passe-t-il si le refresh token expire ?**
   - → L'utilisateur doit se reconnecter
   - → Impossible de générer un nouveau access token

6. **Peut-on utiliser le refresh token pour accéder aux ressources ?**
   - → NON ! Seulement pour `/refresh`
   - → Les ressources utilisent l'access token

---

## 🎯 Checklist d'implémentation

Avant de commencer, imprime cette checklist :

- [ ] **Étape 1** : Créer RefreshToken.java (model)
- [ ] **Étape 2** : Créer RefreshTokenRepository.java
- [ ] **Étape 3** : Créer RefreshTokenService.java
- [ ] **Étape 4** : Créer RefreshTokenRequest.java (dto)
- [ ] **Étape 5** : Créer InvalidDataException.java
- [ ] **Étape 6** : Modifier LoginRequest.java (ajout rememberMe)
- [ ] **Étape 7** : Modifier AuthResponse.java (ajout refreshToken)
- [ ] **Étape 8** : Modifier JwtService.java (surcharge)
- [ ] **Étape 9** : Modifier AuthController.java (3 endpoints)
- [ ] **Étape 10** : Modifier SecurityExceptionHandler.java
- [ ] **Étape 11** : Modifier application.properties
- [ ] **Test 1** : Login sans remember me
- [ ] **Test 2** : Login avec remember me
- [ ] **Test 3** : Utiliser access token
- [ ] **Test 4** : Refresh token
- [ ] **Test 5** : Logout
- [ ] **Test 6** : Vérifier en DB H2

---

## 💪 Conseils pour réussir

1. **Fais étape par étape**
   - Ne passe pas à l'étape suivante tant que la précédente n'est pas comprise

2. **Compile après chaque étape**
   - Vérifie qu'il n'y a pas d'erreur avant de continuer

3. **Teste immédiatement**
   - Après avoir modifié AuthController, teste login
   - Après /refresh, teste refresh

4. **Utilise la console H2**
   - Vérifie que les tokens sont bien créés
   - Regarde les dates d'expiration

5. **Pose des questions si bloqué**
   - "Je ne comprends pas pourquoi on stocke en DB"
   - "Comment savoir si c'est le bon UUID ?"

6. **Compare avec l'exemple**
   - Si ça ne marche pas, compare ton code avec le guide

---

## 🚀 Prêt ?

**Tu peux maintenant implémenter le refresh token et remember me !**

**Commence par l'Étape 1 et avance progressivement.**

Si tu as des questions ou blocages, demande-moi :
- "Explique-moi l'étape X"
- "Pourquoi on fait Y ?"
- "Mon code de l'étape Z ne compile pas"

Bon courage ! 💪🔐

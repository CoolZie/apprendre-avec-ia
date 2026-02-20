# 🔐 Exercice 1 : Authentification JWT de base

## 🎯 Objectifs

Créer un système d'authentification sécurisé avec JWT :
- ✅ Configurer Spring Security pour API REST
- ✅ Implémenter l'enregistrement et la connexion utilisateurs
- ✅ Générer et valider des tokens JWT
- ✅ Protéger les endpoints avec authentification
- ✅ Encoder les mots de passe avec BCrypt

---

## 📋 Cahier des Charges

### Fonctionnalités Requises

#### 1. Gestion Utilisateurs
- **Enregistrement** : Créer un compte avec username, email, password
- **Connexion** : Authentifier un utilisateur et retourner un JWT
- **Profil** : Récupérer les informations de l'utilisateur connecté

#### 2. Sécurisation
- **Mots de passe** : Toujours encodés avec BCrypt (strength 12)
- **JWT** : Token valide 24 heures
- **Validation** : Username unique, email valide, password min 8 caractères

#### 3. Endpoints à créer

| Méthode | Route | Protection | Description |
|---------|-------|------------|-------------|
| POST | `/api/auth/register` | Publique | Créer un compte |
| POST | `/api/auth/login` | Publique | Se connecter |
| GET | `/api/auth/me` | JWT | Profil utilisateur connecté |
| GET | `/api/products` | JWT | Liste produits (protégée) |

---

## 📐 Architecture

```
src/main/java/com/formation/security/
├── config/
│   ├── SecurityConfig.java          # Configuration Spring Security
│   └── ApplicationConfig.java       # Beans (AuthManager, PasswordEncoder)
├── model/
│   └── User.java                    # Entité utilisateur
├── repository/
│   └── UserRepository.java          # JPA Repository
├── security/
│   ├── JwtService.java              # Génération/validation JWT
│   └── JwtAuthenticationFilter.java # Filtre pour vérifier JWT
├── service/
│   ├── UserDetailsServiceImpl.java  # Chargement utilisateur
│   └── AuthService.java             # Logique métier auth
├── controller/
│   └── AuthController.java          # API auth
├── dto/
│   ├── RegisterRequest.java         # DTO inscription
│   ├── LoginRequest.java            # DTO connexion
│   ├── AuthResponse.java            # DTO réponse avec JWT
│   └── UserResponse.java            # DTO utilisateur
└── exception/
    ├── DuplicateResourceException.java
    └── InvalidCredentialsException.java
```

---

## 🛠️ Instructions

### Étape 1 : Ajouter les dépendances

Dans `pom.xml` :

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT (JJWT) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Étape 2 : Configuration application.properties

```properties
# Application
spring.application.name=security-app
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:securitydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (optionnel, pour debug)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
jwt.secret=bXlTdXBlclNlY3JldEtleVdpdGhNb3JlVGhhbjI1NkJpdHNGb3JTaGEyNTY=
jwt.expiration=86400000
```

### Étape 3 : Créer l'entité User

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
    
    @Column(nullable = false)
    private String role = "ROLE_USER";
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

### Étape 4 : Créer le Repository

**Fichier** : `repository/UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### Étape 5 : Créer les DTOs

**RegisterRequest.java** :
```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

**LoginRequest.java** :
```java
@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**AuthResponse.java** :
```java
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
}
```

**UserResponse.java** :
```java
@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
```

### Étape 6 : Implémenter JwtService

**Fichier** : `security/JwtService.java`

Créer une classe service avec les méthodes suivantes :

**Méthodes à implémenter** :
- `String generateToken(UserDetails userDetails)` : Générer un JWT
- `String extractUsername(String token)` : Extraire le username du token
- `boolean isTokenValid(String token, UserDetails userDetails)` : Valider le token
- `Claims extractAllClaims(String token)` : Parser le token
- `boolean isTokenExpired(String token)` : Vérifier l'expiration

**Indices** :
```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(UserDetails userDetails) {
        // TODO: Utiliser Jwts.builder()
        //   - subject: username
        //   - issuedAt: maintenant
        //   - expiration: maintenant + expiration
        //   - signWith: getSigningKey()
    }
    
    // TODO: Implémenter les autres méthodes
}
```

### Étape 7 : Créer JwtAuthenticationFilter

**Fichier** : `security/JwtAuthenticationFilter.java`

Créer un filtre qui :
1. Extrait le token du header `Authorization: Bearer <token>`
2. Valide le token
3. Met l'utilisateur dans le `SecurityContext`

**Structure** :
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraire le header Authorization
        final String authHeader = request.getHeader("Authorization");
        
        // 2. Vérifier si header existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. Extraire le token (substring(7) pour retirer "Bearer ")
        
        // 4. Extraire le username du token
        
        // 5. Si username existe et pas déjà authentifié :
        //    - Charger UserDetails
        //    - Valider le token
        //    - Créer UsernamePasswordAuthenticationToken
        //    - Le mettre dans SecurityContextHolder
        
        // 6. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
```

### Étape 8 : Implémenter UserDetailsService

**Fichier** : `service/UserDetailsServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRole())
            .build();
    }
}
```

### Étape 9 : Créer AuthService

**Fichier** : `service/AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        // TODO:
        // 1. Vérifier si username existe déjà (throw DuplicateResourceException)
        // 2. Vérifier si email existe déjà (throw DuplicateResourceException)
        // 3. Créer User avec password encodé (passwordEncoder.encode())
        // 4. Sauvegarder en DB
        // 5. Générer JWT
        // 6. Retourner AuthResponse
    }
    
    public AuthResponse login(LoginRequest request) {
        // TODO:
        // 1. Authentifier avec authenticationManager.authenticate()
        // 2. Récupérer l'utilisateur depuis la DB
        // 3. Générer JWT
        // 4. Retourner AuthResponse
    }
    
    public UserResponse getCurrentUser(String username) {
        // TODO: Récupérer user depuis DB et mapper vers UserResponse
    }
}
```

### Étape 10 : Créer AuthController

**Fichier** : `controller/AuthController.java`

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
    }
}
```

### Étape 11 : Configurer Spring Security

**Fichier** : `config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Pour H2 Console
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        return http.build();
    }
}
```

**Fichier** : `config/ApplicationConfig.java`

```java
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    
    private final UserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
```

### Étape 12 : Créer les Exceptions

**DuplicateResourceException.java** :
```java
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
```

**InvalidCredentialsException.java** :
```java
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
```

**GlobalExceptionHandler.java** :
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Invalid username or password"));
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}

@Data
@AllArgsConstructor
class ErrorResponse {
    private String message;
}
```

---

## 🧪 Tests

### Tester avec Postman/cURL

#### 1. Inscription

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "Password123"
}

# Réponse attendue :
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "alice",
  "email": "alice@example.com"
}
```

#### 2. Connexion

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "Password123"
}

# Réponse : même format que register
```

#### 3. Profil utilisateur (avec JWT)

```bash
GET http://localhost:8080/api/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

# Réponse attendue :
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "createdAt": "2024-02-18T10:30:00"
}
```

#### 4. Test sans token (doit échouer)

```bash
GET http://localhost:8080/api/auth/me

# Réponse attendue : 403 Forbidden
```

### Cas d'erreur à tester

✅ **Username déjà existant** :
```bash
POST /api/auth/register
{
  "username": "alice",  # Déjà pris
  "email": "alice2@example.com",
  "password": "Password123"
}
# → 409 Conflict : "Username already exists"
```

✅ **Email invalide** :
```bash
POST /api/auth/register
{
  "username": "bob",
  "email": "invalid-email",
  "password": "Password123"
}
# → 400 Bad Request : "Invalid email format"
```

✅ **Mot de passe trop court** :
```bash
POST /api/auth/register
{
  "username": "charlie",
  "email": "charlie@example.com",
  "password": "abc"
}
# → 400 Bad Request : "Password must be at least 8 characters"
```

✅ **Mauvais credentials** :
```bash
POST /api/auth/login
{
  "username": "alice",
  "password": "WrongPassword"
}
# → 401 Unauthorized : "Invalid username or password"
```

✅ **Token expiré** :
- Modifier `jwt.expiration=5000` (5 secondes)
- Attendre 10 secondes après login
- Appeler `/api/auth/me`
- → 403 Forbidden

---

## ✅ Critères de Validation

### Fonctionnalités (60 points)

- [ ] **Inscription** (15 pts) : Créer un utilisateur avec validation
- [ ] **Connexion** (15 pts) : Authentification et génération JWT
- [ ] **Profil utilisateur** (10 pts) : Récupérer user connecté
- [ ] **Protection endpoints** (10 pts) : JWT requis pour `/api/auth/me`
- [ ] **Password encoding** (10 pts) : BCrypt avec strength 12

### Sécurité (25 points)

- [ ] **Mots de passe chiffrés** (10 pts) : Jamais en clair en DB
- [ ] **JWT valide** (10 pts) : Token bien signé et validé
- [ ] **Stateless** (5 pts) : Pas de session serveur

### Validation (10 points)

- [ ] **Username unique** (3 pts)
- [ ] **Email unique** (3 pts)
- [ ] **Contraintes respectées** (4 pts) : @NotBlank, @Email, @Size

### Gestion d'erreurs (5 points)

- [ ] **Exceptions customs** (3 pts) : DuplicateResourceException
- [ ] **Messages clairs** (2 pts) : Réponses HTTP appropriées

### Bonus (5 points)

- [ ] **Tests unitaires** (3 pts) : JwtService, AuthService
- [ ] **Documentation** (2 pts) : Javadoc sur méthodes principales

**Score total : /100**

---

## 💡 Conseils

### Débugger JWT

```java
// Afficher le token généré
System.out.println("Generated JWT: " + token);

// Décoder sur https://jwt.io pour voir le payload

// Vérifier la clé secrète
System.out.println("Secret length: " + secret.length());  // Doit être ≥ 256 bits
```

### Logs utiles

```properties
# Dans application.properties
logging.level.org.springframework.security=DEBUG
logging.level.com.formation.security=DEBUG
```

### Erreurs courantes

❌ **"Unable to process Jwt"** → Clé secrète trop courte ou invalide  
❌ **"Cannot invoke toString on null object"** → Token mal extrait du header  
❌ **"Access Denied"** → Filtre pas ajouté ou mal configuré  
❌ **"There is no PasswordEncoder mapped"** → PasswordEncoder bean manquant  

### Aide JwtService

```java
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
}

private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
}

public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}
```

---

## 🚀 Pour aller plus loin (Optionnel)

### Refresh Token

Ajouter un endpoint `/api/auth/refresh` qui :
- Reçoit un refresh token (validité 7 jours)
- Retourne un nouveau access token (validité 24h)

### Remember Me

- Stocker un cookie `rememberMe` côté client
- Générer un token de longue durée (30 jours)

### Multi-rôles

Modifier `User.java` pour supporter plusieurs rôles :
```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "user_roles")
@Column(name = "role")
private Set<String> roles = new HashSet<>();
```

---

**Bon courage ! 💪**  
*Temps estimé : 3-4 heures*  
*Difficulté : ⭐⭐⭐☆☆*

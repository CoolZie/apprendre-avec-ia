# 🔐 Module 4 : Spring Security - Sécurisation des APIs

## 📚 Table des matières

- [Introduction](#introduction)
- [Architecture Spring Security](#architecture)
- [Configuration de base](#configuration-de-base)
- [Authentification](#authentification)
- [Autorisation](#autorisation)
- [JWT (JSON Web Tokens)](#jwt)
- [Password Encoding](#password-encoding)
- [Roles et Permissions](#roles-et-permissions)
- [CORS et CSRF](#cors-et-csrf)
- [Bonnes pratiques](#bonnes-pratiques)

---

## 🎯 Objectifs du Module

À la fin de ce module, vous serez capable de :
- ✅ Sécuriser une API REST avec Spring Security
- ✅ Implémenter l'authentification par JWT
- ✅ Gérer les rôles et permissions
- ✅ Encoder les mots de passe avec BCrypt
- ✅ Configurer CORS et protéger contre CSRF
- ✅ Créer un système d'authentification complet

---

## 🔰 Introduction

### Qu'est-ce que Spring Security ?

**Spring Security** est le framework de sécurité standard pour les applications Spring. Il fournit :

- 🔑 **Authentification** : Vérifier l'identité de l'utilisateur
- 🛡️ **Autorisation** : Contrôler l'accès aux ressources
- 🔒 **Protection** : CSRF, XSS, Session Fixation
- 🎫 **Gestion de sessions** : Cookies, JWT, OAuth2
- 🔐 **Chiffrement** : Mots de passe, tokens, données sensibles

### Concepts clés

```
┌─────────────────────────────────────────────┐
│          APPLICATION SPRING BOOT            │
├─────────────────────────────────────────────┤
│  ┌──────────────────────────────────────┐  │
│  │      SPRING SECURITY FILTERS         │  │
│  │  (Authentication, Authorization)     │  │
│  └───────────────┬──────────────────────┘  │
│                  ↓                          │
│  ┌──────────────────────────────────────┐  │
│  │    Security Context (Session/JWT)    │  │
│  └───────────────┬──────────────────────┘  │
│                  ↓                          │
│  ┌──────────────────────────────────────┐  │
│  │     Controllers (APIs protégées)     │  │
│  └──────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

### Authentification vs Autorisation

| Authentification | Autorisation |
|------------------|--------------|
| **"Qui êtes-vous ?"** | **"Qu'avez-vous le droit de faire ?"** |
| Login/password, JWT, OAuth2 | Roles, Permissions, ACL |
| Vérifier l'identité | Contrôler l'accès |
| Exemple : `john@gmail.com` + password | Exemple : ROLE_ADMIN peut DELETE |

---

## 🏗️ Architecture Spring Security

### Les Filtres (Filters)

Spring Security fonctionne avec une **chaîne de filtres** qui interceptent chaque requête HTTP :

```java
HTTP Request
    ↓
┌─────────────────────────────────┐
│  SecurityFilterChain            │
├─────────────────────────────────┤
│  1. CorsFilter                  │  ← CORS
│  2. CsrfFilter                  │  ← Protection CSRF
│  3. UsernamePasswordAuthFilter  │  ← Login classique
│  4. JwtAuthenticationFilter     │  ← JWT Token
│  5. AuthorizationFilter         │  ← Vérification droits
│  ...                            │
└─────────────────────────────────┘
    ↓
Controller (si autorisé)
```

### Les composants principaux

#### 1. **SecurityContext**
Stocke les informations de l'utilisateur connecté

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
```

#### 2. **UserDetailsService**
Charge les informations utilisateur depuis la base de données

```java
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Chercher user dans la DB
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles())
            .build();
    }
}
```

#### 3. **PasswordEncoder**
Chiffre et vérifie les mots de passe

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## ⚙️ Configuration de Base

### 1. Dépendances Maven

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
    </dependency>
</dependencies>
```

### 2. Configuration Minimale

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Désactiver CSRF pour API REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Routes publiques
                .anyRequest().authenticated()                 // Tout le reste authentifié
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Sans session (JWT)
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 🔑 Authentification

### Authentification par JWT (Recommandée pour API REST)

#### Architecture JWT

```
Client                    Server
  │                         │
  │  POST /api/auth/login   │
  │  {username, password}   │
  │────────────────────────>│
  │                         │ 1. Vérifier credentials
  │                         │ 2. Générer JWT
  │                         │
  │  {token: "eyJhbGc..."}  │
  │<────────────────────────│
  │                         │
  │  GET /api/products      │
  │  Header: Bearer eyJh... │
  │────────────────────────>│
  │                         │ 3. Valider JWT
  │                         │ 4. Extraire user
  │                         │
  │  {products: [...]}      │
  │<────────────────────────│
```

#### 1. Entité User

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
    private String password;  // Toujours stocké chiffré !
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    private boolean enabled = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

#### 2. JwtService (Génération et validation de tokens)

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;  // Clé secrète (dans application.properties)
    
    @Value("${jwt.expiration}")
    private long expiration;  // Durée validité (ex: 86400000 = 24h)
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Générer un token JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }
    
    // Extraire le username du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Vérifier si le token est valide
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
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
}
```

#### 3. JwtAuthenticationFilter

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
        
        // 1. Extraire le token du header Authorization
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);
        
        // 2. Si username existe et utilisateur pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 3. Valider le token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 4. Mettre l'utilisateur dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### 4. AuthController (Login/Register)

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Créer le nouvel utilisateur
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of("ROLE_USER"))  // Rôle par défaut
            .build();
        
        userRepository.save(user);
        
        // Générer le token JWT
        String token = jwtService.generateToken(toUserDetails(user));
        
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();
        
        // Générer le token JWT
        String token = jwtService.generateToken(toUserDetails(user));
        
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }
    
    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }
}
```

---

## 🛡️ Autorisation

### Sécuriser les endpoints par rôle

#### 1. Configuration dans SecurityConfig

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Routes publiques
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/public/**").permitAll()
            
            // Routes pour utilisateurs connectés
            .requestMatchers("/api/products").hasAnyRole("USER", "ADMIN")
            
            // Routes admin uniquement
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
            
            // Tout le reste nécessite authentification
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

#### 2. Annotations @PreAuthorize (plus flexible)

```java
@Configuration
@EnableMethodSecurity  // Activer les annotations de sécurité
public class SecurityConfig {
    // ...
}
```

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    // Accessible à tous les utilisateurs connectés
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    
    // Nécessite le rôle ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
    
    // Nécessite ADMIN ou que l'utilisateur soit le propriétaire
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @productService.isOwner(principal, #id)")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }
    
    // Logique complexe
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and #request.stock <= 100)")
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
```

### Récupérer l'utilisateur connecté

```java
@GetMapping("/me")
public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    // ...
}

// Ou manuellement :
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

---

## 🔐 Password Encoding (BCrypt)

### Pourquoi BCrypt ?

BCrypt est **lent volontairement** pour ralentir les attaques par force brute.

**❌ JAMAIS stocker les mots de passe en clair !**

```java
// ❌ INTERDIT : Mot de passe en clair
user.setPassword("password123");

// ✅ CORRECT : Mot de passe chiffré
String encodedPassword = passwordEncoder.encode("password123");
user.setPassword(encodedPassword);
```

### Configuration

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // Strength 12 (recommandé)
}
```

### Utilisation

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    // Créer un utilisateur
    public User createUser(RegisterRequest request) {
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))  // ✅ Chiffrer
            .build();
        
        return userRepository.save(user);
    }
    
    // Vérifier le mot de passe
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    // Changer le mot de passe
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Incorrect old password");
        }
        
        // Mettre à jour avec le nouveau (chiffré)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
```

---

## 👥 Roles et Permissions

### Modèle RBAC (Role-Based Access Control)

```
User
  ↓ a des
Roles (ADMIN, USER, SELLER)
  ↓ ont des
Permissions (READ_PRODUCT, WRITE_PRODUCT, DELETE_PRODUCT)
```

### Implémentation

#### 1. Entités

```java
@Entity
@Data
public class Role {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;  // ROLE_ADMIN, ROLE_USER
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions = new HashSet<>();
}

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;  // READ_PRODUCT, WRITE_PRODUCT, DELETE_PRODUCT
}

@Entity
@Data
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    private String username;
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
}
```

#### 2. Vérification par permission

```java
@PreAuthorize("hasAuthority('WRITE_PRODUCT')")
@PostMapping("/products")
public Product createProduct(@RequestBody ProductRequest request) {
    return productService.create(request);
}

@PreAuthorize("hasAuthority('DELETE_PRODUCT')")
@DeleteMapping("/products/{id}")
public void deleteProduct(@PathVariable Long id) {
    productService.delete(id);
}
```

---

## 🌐 CORS et CSRF

### CORS (Cross-Origin Resource Sharing)

Permet aux applications frontend (React, Angular) d'appeler votre API.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // ...
    
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://myapp.com"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### CSRF (Cross-Site Request Forgery)

**Pour API REST avec JWT : DÉSACTIVER CSRF**

```java
http.csrf(csrf -> csrf.disable())  // Pas de cookies = pas de CSRF
```

**Pour applications avec sessions/cookies : ACTIVER CSRF**

```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
)
```

---

## ✅ Bonnes Pratiques

### 1. Sécurité des tokens JWT

```java
// ✅ Secret fort et aléatoire (256 bits minimum)
jwt.secret=bXlTdXBlclNlY3JldEtleVdpdGhNb3JlVGhhbjI1NkJpdHNGb3JTaGEyNTY=

// ✅ Expiration courte (15 min à 1h)
jwt.expiration=3600000

// ✅ Refresh token pour renouveler l'accès
jwt.refresh-expiration=604800000  // 7 jours
```

### 2. Validation des données

```java
@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain uppercase, lowercase and digit")
    private String password;
}
```

### 3. Gestion des exceptions

```java
@ControllerAdvice
public class SecurityExceptionHandler {
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse("Invalid username or password"));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("Access denied"));
    }
}
```

### 4. Audit et logs

```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditAspect.class);
    
    @Before("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void logSecurityCheck(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Security check: user={}, method={}", 
                    auth.getName(), joinPoint.getSignature());
    }
}
```

### 5. Rate Limiting (protection contre brute force)

```java
@Component
public class LoginAttemptService {
    
    private final LoadingCache<String, Integer> attemptsCache;
    
    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                public Integer load(String key) {
                    return 0;
                }
            });
    }
    
    public void loginFailed(String username) {
        int attempts = attemptsCache.getUnchecked(username);
        attemptsCache.put(username, attempts + 1);
    }
    
    public boolean isBlocked(String username) {
        return attemptsCache.getUnchecked(username) >= 5;  // Bloqué après 5 tentatives
    }
}
```

---

## 📝 Résumé

## 🔎 Comment Spring reconnaît les rôles et permissions

Spring ne lit pas directement les annotations JPA (`@ManyToMany`, `@ElementCollection`) pour autoriser un endpoint.
Ce qui compte pour l'autorisation, ce sont les `GrantedAuthority` présentes dans le `SecurityContext`.

### Flux réel en 5 étapes

1. **Chargement utilisateur en base**
    - `UserDetailsService.loadUserByUsername()` charge l'utilisateur + ses rôles/permissions.

2. **Conversion en authorities**
    - Chaque rôle/permission est converti en `SimpleGrantedAuthority`.
    - Exemples d'autorities: `ROLE_ADMIN`, `ROLE_USER`, `READ_PRODUCT`, `DELETE_PRODUCT`.

3. **Création de l'Authentication**
    - Spring crée un `UsernamePasswordAuthenticationToken` avec ces authorities.

4. **Injection dans le contexte sécurité**
    - Le token d'authentification est stocké dans `SecurityContextHolder`.

5. **Évaluation des règles**
    - `@PreAuthorize` lit les authorities du contexte.
    - `hasRole('ADMIN')` vérifie en réalité l'authority `ROLE_ADMIN`.
    - `hasAuthority('DELETE_PRODUCT')` vérifie exactement cette permission.

### Important

- **Role** = regroupement logique de permissions.
- **Authority** = droit technique effectivement évalué par Spring.
- Sans conversion explicite en `GrantedAuthority`, Spring ne peut pas appliquer correctement `hasRole()` / `hasAuthority()`.

### Schéma mental rapide

`DB (User->Roles->Permissions)` → `UserDetailsService` → `GrantedAuthority` → `SecurityContext` → `@PreAuthorize`

### Points clés à retenir

✅ **Spring Security** = Filtres + Authentification + Autorisation  
✅ **JWT** = Tokens stateless pour APIs REST  
✅ **BCrypt** = Toujours chiffrer les mots de passe  
✅ **Roles** = Groupes de permissions (@PreAuthorize)  
✅ **CORS** = Autoriser les appels cross-origin  
✅ **CSRF** = Désactivé pour JWT, activé pour sessions  

### Architecture typique API REST sécurisée

```
POST /api/auth/register → Créer compte (password encodé)
POST /api/auth/login    → Login (retourne JWT)
GET  /api/products      → Protégé par JWT (Bearer token)
POST /api/admin/users   → Protégé par JWT + ROLE_ADMIN
```

### Configuration minimale complète

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 🎯 Prochaines Étapes

Maintenant que vous connaissez la théorie, passons à la pratique :

1. **Exercice 1** : Configuration basique + Authentification JWT
2. **Exercice 2** : Roles et autorisation (@PreAuthorize)
3. **Exercice 3** : Refresh tokens et gestion avancée
4. **Mini-projet** : Système d'authentification complet avec gestion utilisateurs

**Prêt ? Ouvrez `EXERCICE1.md` pour commencer !** 🚀

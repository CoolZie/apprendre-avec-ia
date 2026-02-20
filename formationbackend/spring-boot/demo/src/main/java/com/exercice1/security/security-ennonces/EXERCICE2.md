# 🛡️ Exercice 2 : Rôles et Autorisations avancées

## 🎯 Objectifs

Implémenter un système de gestion des rôles et permissions :
- ✅ Gérer plusieurs rôles par utilisateur (USER, ADMIN, MODERATOR)
- ✅ Protéger les endpoints avec `@PreAuthorize`
- ✅ Créer des règles d'autorisation complexes
- ✅ Implémenter un système RBAC (Role-Based Access Control)
- ✅ Gérer les permissions granulaires

---

## 📋 Cahier des Charges

### Système de Rôles

#### Rôles disponibles
- **ROLE_USER** : Utilisateur standard (par défaut)
- **ROLE_MODERATOR** : Modérateur (peut gérer le contenu)
- **ROLE_ADMIN** : Administrateur (tous les droits)

#### Permissions par rôle

| Rôle | Permissions |
|------|------------|
| **USER** | Lire produits, Créer commandes, Voir son profil |
| **MODERATOR** | USER + Créer/Modifier produits, Voir tous les utilisateurs |
| **ADMIN** | MODERATOR + Supprimer produits, Gérer utilisateurs, Changer rôles |

### Endpoints à créer

| Méthode | Route | Rôle requis | Description |
|---------|-------|-------------|-------------|
| GET | `/api/products` | USER | Liste des produits |
| POST | `/api/products` | MODERATOR | Créer un produit |
| PUT | `/api/products/{id}` | MODERATOR | Modifier un produit |
| DELETE | `/api/products/{id}` | ADMIN | Supprimer un produit |
| GET | `/api/users` | MODERATOR | Liste des utilisateurs |
| GET | `/api/users/{id}` | USER (soi-même) ou ADMIN | Profil utilisateur |
| PATCH | `/api/users/{id}/role` | ADMIN | Changer le rôle d'un utilisateur |
| DELETE | `/api/users/{id}` | ADMIN | Supprimer un utilisateur |

---

## 📐 Architecture

```
src/main/java/com/formation/security/
├── model/
│   ├── User.java                    # Modifié : Set<String> roles
│   └── Product.java                 # Nouvelle entité
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java       # Nouveau
├── service/
│   ├── UserService.java             # Gestion utilisateurs
│   └── ProductService.java          # Nouveau
├── controller/
│   ├── UserController.java          # Nouveau
│   └── ProductController.java       # Nouveau
├── dto/
│   ├── ProductRequest.java          # Nouveau
│   ├── ProductResponse.java         # Nouveau
│   └── RoleUpdateRequest.java       # Nouveau
└── security/
    └── SecurityUtils.java           # Helper pour vérifications custom
```

---

## 🛠️ Instructions

### Étape 1 : Modifier l'entité User pour multi-rôles

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
    
    // ✅ MODIFIER : Un Set pour supporter plusieurs rôles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @PrePersist
    public void initRoles() {
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("ROLE_USER");  // Rôle par défaut
        }
    }
}
```

### Étape 2 : Créer l'entité Product

**Fichier** : `model/Product.java`

```java
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Étape 3 : Créer ProductRepository

**Fichier** : `repository/ProductRepository.java`

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCreatedBy(User user);
}
```

### Étape 4 : Créer les DTOs

**ProductRequest.java** :
```java
@Data
public class ProductRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
```

**ProductResponse.java** :
```java
@Data
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String createdBy;
    private LocalDateTime createdAt;
    
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getCreatedBy().getUsername(),
            product.getCreatedAt()
        );
    }
}
```

**RoleUpdateRequest.java** :
```java
@Data
public class RoleUpdateRequest {
    @NotEmpty(message = "Roles cannot be empty")
    private Set<
@Pattern(regexp = "^ROLE_(USER|MODERATOR|ADMIN)$", message = "Invalid role")
        String> roles;
}
```

### Étape 5 : Créer ProductService

**Fichier** : `service/ProductService.java`

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductResponse.from(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .stock(request.getStock())
            .createdBy(user)
            .build();
        
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, String username) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        // Vérifier que seul le créateur ou un admin peut modifier
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        if (!product.getCreatedBy().getId().equals(currentUser.getId()) && 
            !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("You can only update your own products");
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        
        Product updated = productRepository.save(product);
        return ProductResponse.from(updated);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
```

### Étape 6 : Créer ProductController avec @PreAuthorize

**Fichier** : `controller/ProductController.java`

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    // Accessible à tous les utilisateurs connectés
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    // Nécessite MODERATOR ou ADMIN
    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProductResponse created = productService.createProduct(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Nécessite MODERATOR ou ADMIN (+ vérification ownership dans service)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request, userDetails.getUsername()));
    }
    
    // Nécessite ADMIN uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Étape 7 : Créer UserService

**Fichier** : `service/UserService.java`

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id, String currentUsername) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow();
        
        // Vérifier que c'est soi-même ou un admin
        if (!user.getId().equals(currentUser.getId()) && 
            !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("You can only view your own profile");
        }
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.getCreatedAt()
        );
    }
    
    @Transactional
    public UserResponse updateUserRoles(Long id, Set<String> roles) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setRoles(roles);
        User updated = userRepository.save(user);
        
        return new UserResponse(
            updated.getId(),
            updated.getUsername(),
            updated.getEmail(),
            updated.getRoles(),
            updated.getCreatedAt()
        );
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
```

### Étape 8 : Créer UserController

**Fichier** : `controller/UserController.java`

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    // Liste des utilisateurs : MODERATOR ou ADMIN
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    // Profil utilisateur : soi-même ou ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("@securityUtils.isOwnerOrAdmin(#id, principal.username)")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getUserById(id, userDetails.getUsername()));
    }
    
    // Changer le rôle : ADMIN uniquement
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request.getRoles()));
    }
    
    // Supprimer utilisateur : ADMIN uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Étape 9 : Créer SecurityUtils (helper pour vérifications custom)

**Fichier** : `security/SecurityUtils.java`

```java
@Component
@RequiredArgsConstructor
public class SecurityUtils {
    
    private final UserRepository userRepository;
    
    /**
     * Vérifie si l'utilisateur est le propriétaire de la ressource ou un admin
     */
    public boolean isOwnerOrAdmin(Long userId, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Est admin ?
        if (currentUser.getRoles().contains("ROLE_ADMIN")) {
            return true;
        }
        
        // Est le propriétaire ?
        return currentUser.getId().equals(userId);
    }
    
    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String username, String role) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return user.getRoles().contains(role);
    }
}
```

### Étape 10 : Activer @PreAuthorize dans SecurityConfig

**Fichier** : `config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ✅ Activer les annotations @PreAuthorize
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
        
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        return http.build();
    }
}
```

### Étape 11 : Modifier UserDetailsServiceImpl pour gérer multi-rôles

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
        
        // ✅ Convertir Set<String> en authorities
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)  // Plusieurs rôles
            .build();
    }
}
```

### Étape 12 : Modifier AuthService pour gérer les rôles

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
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // ✅ Créer avec rôle USER par défaut
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of("ROLE_USER"))  // Rôle par défaut
            .build();
        
        userRepository.save(user);
        
        UserDetails userDetails = toUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        
        UserDetails userDetails = toUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
    
    private UserDetails toUserDetails(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }
}
```

### Étape 13 : Modifier JwtService pour inclure les rôles dans le token

**Fichier** : `security/JwtService.java`

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
        Map<String, Object> claims = new HashMap<>();
        
        // ✅ Inclure les rôles dans le token JWT
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        claims.put("roles", roles);
        
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }
    
    // Reste des méthodes inchangé
    // ...
}
```

---

## 🧪 Tests

### Scenario 1 : Créer utilisateurs avec différents rôles

```bash
# 1. Créer un utilisateur normal (USER)
POST /api/auth/register
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "Password123"
}
# → Token avec ROLE_USER

# 2. Créer un modérateur (manuellement en DB ou via endpoint admin)
# Utiliser H2 Console ou SQL :
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_MODERATOR');

# 3. Créer un admin
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
```

### Scenario 2 : Tester les permissions produits

```bash
# Alice (USER) : Peut LIRE les produits
GET /api/products
Authorization: Bearer <alice-token>
# → 200 OK

# Alice (USER) : NE PEUT PAS CRÉER de produit
POST /api/products
Authorization: Bearer <alice-token>
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1500.00,
  "stock": 10
}
# → 403 Forbidden

# Bob (MODERATOR) : PEUT CRÉER des produits
POST /api/products
Authorization: Bearer <bob-token>
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1500.00,
  "stock": 10
}
# → 201 Created

# Alice (USER) : NE PEUT PAS SUPPRIMER
DELETE /api/products/1
Authorization: Bearer <alice-token>
# → 403 Forbidden

# Admin : PEUT SUPPRIMER
DELETE /api/products/1
Authorization: Bearer <admin-token>
# → 204 No Content
```

### Scenario 3 : Gestion des utilisateurs

```bash
# Alice (USER) : NE PEUT PAS voir la liste des utilisateurs
GET /api/users
Authorization: Bearer <alice-token>
# → 403 Forbidden

# Bob (MODERATOR) : PEUT voir la liste
GET /api/users
Authorization: Bearer <bob-token>
# → 200 OK [ {...}, {...} ]

# Alice : PEUT voir SON profil
GET /api/users/1  # ID d'Alice
Authorization: Bearer <alice-token>
# → 200 OK

# Alice : NE PEUT PAS voir le profil de Bob
GET /api/users/2  # ID de Bob
Authorization: Bearer <alice-token>
# → 403 Forbidden

# Admin : PEUT changer les rôles
PATCH /api/users/1/role
Authorization: Bearer <admin-token>
{
  "roles": ["ROLE_USER", "ROLE_MODERATOR"]
}
# → 200 OK (Alice est maintenant USER + MODERATOR)
```

### Scenario 4 : Vérifier les rôles dans le JWT

```bash
# Décoder le token sur https://jwt.io

# Payload attendu :
{
  "sub": "alice",
  "roles": ["ROLE_USER", "ROLE_MODERATOR"],
  "iat": 1708255200,
  "exp": 1708341600
}
```

---

## ✅ Critères de Validation

### Fonctionnalités (50 points)

- [ ] **Multi-rôles par utilisateur** (10 pts) : Set<String> roles
- [ ] **CRUD Produits avec autorisations** (15 pts) :
  - GET : USER+
  - POST : MODERATOR+
  - PUT : MODERATOR+ (owner check)
  - DELETE : ADMIN
- [ ] **Gestion utilisateurs** (15 pts) :
  - GET liste : MODERATOR+
  - GET profil : owner ou ADMIN
  - PATCH rôles : ADMIN
  - DELETE : ADMIN
- [ ] **SecurityUtils custom** (10 pts) : isOwnerOrAdmin()

### Sécurité (30 points)

- [ ] **@PreAuthorize correct** (15 pts) : Toutes les routes protégées
- [ ] **Rôles dans JWT** (10 pts) : Claims "roles"
- [ ] **Vérifications custom** (5 pts) : Owner checks

### Tests (15 points)

- [ ] **Tous les scénarios testés** (10 pts)
- [ ] **Erreurs 403 vérifiées** (5 pts)

### Bonus (5 points)

- [ ] **Tests unitaires** (3 pts) : SecurityUtils, Services
- [ ] **Logs d'audit** (2 pts) : Qui fait quoi

**Score total : /100**

---

## 💡 Conseils

### Débugger @PreAuthorize

```java
// Activer les logs Spring Security
logging.level.org.springframework.security=DEBUG

// Afficher les authorities de l'utilisateur
@GetMapping("/debug")
public String debug(@AuthenticationPrincipal UserDetails user) {
    return "Authorities: " + user.getAuthorities();
}
```

### Expressions SpEL utiles

```java
// Rôle unique
@PreAuthorize("hasRole('ADMIN')")

// Plusieurs rôles (OR)
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")

// Permission spécifique
@PreAuthorize("hasAuthority('DELETE_PRODUCT')")

// Vérification custom
@PreAuthorize("@securityUtils.isOwner(#id, principal.username)")

// Conditions complexes
@PreAuthorize("hasRole('ADMIN') or (hasRole('MODERATOR') and #product.price < 1000)")

// Vérifier authentification
@PreAuthorize("isAuthenticated()")

// Vérifier anonyme
@PreAuthorize("isAnonymous()")
```

### Erreurs courantes

❌ **403 alors que le rôle est correct** → Vérifier `@EnableMethodSecurity` dans SecurityConfig  
❌ **"Failed to evaluate expression"** → Typo dans @PreAuthorize (ex: `@securityUtil` au lieu de `@securityUtils`)  
❌ **"Method not found"** → SecurityUtils pas un @Component ou méthode pas publique  
❌ **Rôles pas dans JWT** → JwtService pas modifié pour inclure claims "roles"  

---

**Bon courage ! 💪**  
*Temps estimé : 4-5 heures*  
*Difficulté : ⭐⭐⭐⭐☆*

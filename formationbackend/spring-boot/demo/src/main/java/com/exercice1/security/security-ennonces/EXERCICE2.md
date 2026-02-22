# 🛡️ Exercice 2 : Rôles et Autorisations avancées

## 🎯 Objectifs

Implémenter un système de gestion des rôles et permissions sur les contrôleurs existants :
- ✅ Gérer plusieurs rôles par utilisateur (USER, MODERATOR, ADMIN)
- ✅ Protéger les endpoints existants avec `@PreAuthorize`
- ✅ Créer un UserController pour la gestion des utilisateurs
- ✅ Implémenter un système RBAC (Role-Based Access Control)
- ✅ Gérer les permissions granulaires

> **Note :** Cet exercice se concentre sur Spring Security. Les entités Product, Customer, Order et leurs contrôleurs existent déjà dans le package `com.exercice1.demo`.

---

## 📋 Cahier des Charges

### Système de Rôles

#### Rôles disponibles
- **ROLE_USER** : Utilisateur standard (par défaut)
- **ROLE_MODERATOR** : Modérateur (peut gérer le contenu)
- **ROLE_ADMIN** : Administrateur (tous les droits)

#### Permissions par rôle sur les endpoints existants

| Rôle | Permissions |
|------|------------|
| **USER** | Lire produits/commandes, Créer ses commandes, Voir son profil |
| **MODERATOR** | USER + Créer/Modifier produits, Voir tous les clients |
| **ADMIN** | MODERATOR + Supprimer produits/commandes, Gérer utilisateurs, Changer rôles |

### Endpoints à sécuriser (déjà existants)

#### ProductController (`/api/products`)
| Méthode | Route | Rôle requis | Description |
|---------|-------|-------------|-------------|
| GET | `/api/products` | **Tous** (anonymous) | Liste des produits |
| GET | `/api/products/{id}` | **Tous** (anonymous) | Détail produit |
| POST | `/api/products` | **MODERATOR** | Créer un produit |
| PUT | `/api/products/{id}` | **MODERATOR** | Modifier un produit |
| DELETE | `/api/products/{id}` | **ADMIN** | Supprimer un produit |
| PATCH | `/api/products/{id}/stock` | **MODERATOR** | Mettre à jour le stock |

#### CustomerController (`/api/customers`)
| Méthode | Route | Rôle requis | Description |
|---------|-------|-------------|-------------|
| GET | `/api/customers` | **MODERATOR** | Liste des clients |
| GET | `/api/customers/{id}` | **USER** (soi-même) ou **ADMIN** | Profil client |
| POST | `/api/customers` | **PUBLIC** | Créer client (inscription) |
| PUT | `/api/customers/{id}` | **USER** (soi-même) ou **ADMIN** | Modifier client |
| DELETE | `/api/customers/{id}` | **ADMIN** | Supprimer client |

#### OrderController (`/api/orders`)
| Méthode | Route | Rôle requis | Description |
|---------|-------|-------------|-------------|
| GET | `/api/orders` | **MODERATOR** | Liste toutes les commandes |
| GET | `/api/orders/{id}` | **USER** (sa commande) ou **MODERATOR** | Détail commande |
| POST | `/api/orders` | **USER** | Créer une commande |
| PATCH | `/api/orders/{id}/status` | **MODERATOR** | Changer le statut |
| DELETE | `/api/orders/{id}` | **ADMIN** | Annuler/Supprimer |

### Nouveaux endpoints à créer

#### UserController (`/api/users`)
| Méthode | Route | Rôle requis | Description |
|---------|-------|-------------|-------------|
| GET | `/api/users` | **ADMIN** | Liste des utilisateurs |
| GET | `/api/users/{id}` | **USER** (soi-même) ou **ADMIN** | Profil utilisateur |
| PATCH | `/api/users/{id}/roles` | **ADMIN** | Changer les rôles |
| DELETE | `/api/users/{id}` | **ADMIN** | Supprimer utilisateur |

---

## 📐 Architecture

```
src/main/java/com/exercice1/
├── demo/                           # ✅ Déjà existant (ne pas toucher)
│   ├── model/                      # Product, Customer, Order, OrderItem
│   ├── repository/                 # ProductRepository, CustomerRepository...
│   ├── service/                    # ProductService, CustomerService...
│   └── controller/                 # ✅ À MODIFIER : Ajouter @PreAuthorize
│       ├── ProductController.java
│       ├── CustomerController.java
│       └── OrderController.java
│
└── security/                       # 🔒 Package Security
    ├── model/
    │   └── User.java              # ✅ À MODIFIER : Set<String> roles
    ├── repository/
    │   └── UserRepository.java
    ├── service/
    │   ├── UserService.java       # ✅ À CRÉER : Gestion utilisateurs
    │   └── UserDetailsServiceImpl.java
    ├── controller/
    │   ├── AuthController.java    # ✅ Déjà existant
    │   └── UserController.java    # ✅ À CRÉER : Gestion roles
    ├── dto/
    │   ├── UserResponse.java
    │   └── RoleUpdateRequest.java # ✅ À CRÉER
    ├── config/
    │   ├── SecurityConfig.java    # ✅ À MODIFIER : .permitAll() endpoints publics
    │   └── ApplicationConfig.java
    └── security/
        └── JwtAuthenticationFilter.java
```

---

## 🛠️ Instructions

### Étape 1 : Modifier l'entité User pour multi-rôles

**Fichier** : `security/model/User.java`

**Modification :**
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
    
    // ✅ DÉJÀ FAIT : Un Set pour supporter plusieurs rôles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

> ✅ **Note** : Cette modification a déjà été faite dans l'exercice 1. Si ce n'est pas le cas, applique-la.

---

### Étape 2 : Activer les annotations de sécurité

**Fichier** : `security/config/SecurityConfig.java`

**Ajouter l'annotation** :
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ← AJOUTER CETTE LIGNE
@RequiredArgsConstructor
public class SecurityConfig {
    // ... reste du code inchangé
}
```

**💡 Explication** : `@EnableMethodSecurity(prePostEnabled = true)` active les annotations `@PreAuthorize` et `@PostAuthorize`.

---

### Étape 3 : Configurer les endpoints publics

**Fichier** : `security/config/SecurityConfig.java`

**Modifier la méthode `securityFilterChain`** :
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Endpoints publics (pas d'authentification requise)
            .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()  // ← Lecture produits publique
            .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()   // ← Inscription client
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
            // Tous les autres endpoints nécessitent une authentification
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    // Pour H2 Console
    http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
    
    return http.build();
}
```

**💡 Explications** :
- `.permitAll()` : Accessible sans authentification
- `.authenticated()` : Authentification requise mais pas de rôle spécifique
- Les permissions par rôle seront gérées avec `@PreAuthorize` sur les méthodes

---

### Étape 4 : Sécuriser ProductController

**Fichier** : `demo/controller/ProductController.java`

**Ajouter les annotations de sécurité** :

```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    // ✅ GET : Public (pas d'annotation)
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(...) {
        // ... code existant
    }
    
    @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        // ... code existant
    }
    
    // ✅ POST : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        // ... code existant
    }
    
    // ✅ PUT : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody ProductRequest request) {
        // ... code existant
    }
    
    // ✅ PATCH : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
        @PathVariable Long id,
        @RequestBody UpdateStockRequest request) {
        // ... code existant
    }
    
    // ✅ DELETE : ADMIN seulement
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // ... code existant
    }
}
```

**💡 Explications** :
- `hasRole('ADMIN')` : Vérifie si l'utilisateur a le rôle ROLE_ADMIN
- `hasAnyRole('MODERATOR', 'ADMIN')` : Au moins un des deux rôles
- Spring Security ajoute automatiquement le préfixe `ROLE_` si absent

---

### Étape 5 : Sécuriser CustomerController

**Fichier** : `demo/controller/CustomerController.java`

```java
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    // ✅ GET all : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<PagedResponse<CustomerResponse>> getAllCustomers(...) {
        // ... code existant
    }
    
    // ✅ GET by ID : Vérification custom (voir étape 7)
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        // ... code existant
    }
    
    // ✅ POST : Public (inscription)
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        // ... code existant
    }
    
    // ✅ PUT : Propriétaire ou ADMIN
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
        @PathVariable Long id,
        @Valid @RequestBody CustomerRequest request) {
        // ... code existant
    }
    
    // ✅ DELETE : ADMIN seulement
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        // ... code existant
    }
}
```

---

### Étape 6 : Sécuriser OrderController

**Fichier** : `demo/controller/OrderController.java`

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    // ✅ GET all : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(...) {
        // ... code existant
    }
    
    // ✅ GET by ID : Propriétaire ou MODERATOR
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN') or @securityUtils.isOrderOwner(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        // ... code existant
    }
    
    // ✅ POST : USER authentifié
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        // ... code existant
    }
    
    // ✅ PATCH status : MODERATOR ou ADMIN
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody OrderStatusRequest request) {
        // ... code existant
    }
    
    // ✅ DELETE : ADMIN seulement
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        // ... code existant
    }
}
```

---

### Étape 7 : Créer SecurityUtils pour vérifications custom

**Fichier** : `security/security/SecurityUtils.java` (NOUVEAU)

```java
package com.exercice1.security.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.exercice1.demo.model.Customer;
import com.exercice1.demo.model.Order;
import com.exercice1.demo.repository.CustomerRepository;
import com.exercice1.demo.repository.OrderRepository;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Vérifie si l'utilisateur connecté est le propriétaire du Customer
     */
    public boolean isOwner(Long customerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return false;
        }
        
        // Vérifier si le customer est associé à cet utilisateur
        // (suppose que Customer a un email correspondant à User.email)
        return customer.getEmail().equals(user.getEmail());
    }
    
    /**
     * Vérifie si l'utilisateur connecté est le propriétaire de la commande
     */
    public boolean isOrderOwner(Long orderId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }
        
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        
        // Vérifier si la commande appartient au customer de cet utilisateur
        return order.getCustomer().getEmail().equals(user.getEmail());
    }
}
```

**💡 Explication** :
- `@Component("securityUtils")` : Bean accessible dans `@PreAuthorize` via `@securityUtils`
- Permet des vérifications complexes (propriété d'une ressource)
- Exemple : Un USER peut voir/modifier ses propres données mais pas celles des autres

---

### Étape 8 : Créer le DTO RoleUpdateRequest

**Fichier** : `security/dto/RoleUpdateRequest.java` (NOUVEAU)

```java
package com.exercice1.security.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    
    @NotEmpty(message = "Roles cannot be empty")
    private Set<@Pattern(
        regexp = "^ROLE_(USER|MODERATOR|ADMIN)$", 
        message = "Invalid role. Must be ROLE_USER, ROLE_MODERATOR, or ROLE_ADMIN"
    ) String> roles;
}
```

---

### Étape 9 : Créer UserService

**Fichier** : `security/service/UserService.java` (NOUVEAU)

```java
package com.exercice1.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exercice1.security.dto.RoleUpdateRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.exception.InvalidDataException;
import com.exercice1.security.model.User;
import com.exercice1.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
                user.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new InvalidDataException("User not found"));
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
    
    @Transactional
    public UserResponse updateUserRoles(Long id, RoleUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new InvalidDataException("User not found"));
        
        user.setRoles(request.getRoles());
        User updated = userRepository.save(user);
        
        return new UserResponse(
            updated.getId(),
            updated.getUsername(),
            updated.getEmail(),
            updated.getCreatedAt()
        );
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new InvalidDataException("User not found");
        }
        userRepository.deleteById(id);
    }
}
```

> **Note** : Créer `InvalidDataException` si elle n'existe pas :
```java
package com.exercice1.security.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
```

---

### Étape 10 : Créer UserController

**Fichier** : `security/controller/UserController.java` (NOUVEAU)

```java
package com.exercice1.security.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exercice1.security.dto.RoleUpdateRequest;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Liste tous les utilisateurs (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    /**
     * Voir un utilisateur spécifique
     * - ADMIN : peut voir n'importe qui
     * - USER : peut voir seulement son propre profil
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    /**
     * Changer les rôles d'un utilisateur (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
        @PathVariable Long id,
        @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }
    
    /**
     * Supprimer un utilisateur (ADMIN seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Étape 11 : Modifier AuthController pour supports multi-rôles au register

**Fichier** : `security/controller/AuthController.java`

**Modifier la méthode `register()`** pour permettre de choisir les rôles :

```java
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new DuplicateResourceException("Username already exists");
    }
    
    // Créer le nouvel utilisateur avec rôle USER par défaut
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .roles(Set.of("ROLE_USER"))  // Rôle par défaut
        .build();
    
    userRepository.save(user);
    
    String token = jwtService.generateToken(toUserDetails(user));
    
    return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
}
```

> Pour changer les rôles, utiliser l'endpoint PATCH `/api/users/{id}/roles` (ADMIN seulement)

---

## ✅ Tests et Validation

### Test 1 : Créer des utilisateurs avec différents rôles

**1. Créer un USER normal** :
```bash
POST http://localhost:8080/api/auth/register
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123"
}
# → Rôle par défaut : ROLE_USER
```

**2. Créer un USER et le promouvoir MODERATOR** :
```bash
# a) S'enregistrer
POST http://localhost:8080/api/auth/register
{
  "username": "bob",
  "email": "bob@example.com",
  "password": "password123"
}

# b) Se connecter en tant qu'ADMIN (créé manuellement en DB)
POST http://localhost:8080/api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

# c) Changer le rôle de bob (avec token ADMIN)
PATCH http://localhost:8080/api/users/2/roles
Authorization: Bearer <admin_token>
{
  "roles": ["ROLE_USER", "ROLE_MODERATOR"]
}
```

---

### Test 2 : Tester les permissions ProductController

**a) Lire les produits (PUBLIC - pas de token)** :
```bash
GET http://localhost:8080/api/products
# ✅ Doit fonctionner sans authentification
```

**b) Créer un produit (MODERATOR requis)** :
```bash
# Avec token USER
POST http://localhost:8080/api/products
Authorization: Bearer <user_token>
{
  "name": "Laptop",
  "price": 999.99,
  "stock": 10
}
# ❌ 403 Forbidden (USER n'a pas le droit)

# Avec token MODERATOR
POST http://localhost:8080/api/products
Authorization: Bearer <moderator_token>
{
  "name": "Laptop",
  "price": 999.99,
  "stock": 10
}
# ✅ 201 Created
```

**c) Supprimer un produit (ADMIN requis)** :
```bash
# Avec token MODERATOR
DELETE http://localhost:8080/api/products/1
Authorization: Bearer <moderator_token>
# ❌ 403 Forbidden (MODERATOR ne peut pas supprimer)

# Avec token ADMIN
DELETE http://localhost:8080/api/products/1
Authorization: Bearer <admin_token>
# ✅ 204 No Content
```

---

### Test 3 : Tester les permissions UserController

**a) Liste des utilisateurs (ADMIN seulement)** :
```bash
GET http://localhost:8080/api/users
Authorization: Bearer <user_token>
# ❌ 403 Forbidden

GET http://localhost:8080/api/users
Authorization: Bearer <admin_token>
# ✅ 200 OK
```

**b) Voir son propre profil (USER autorisé)** :
```bash
GET http://localhost:8080/api/users/1
Authorization: Bearer <user_token de l'utilisateur id=1>
# ✅ 200 OK (soi-même)

GET http://localhost:8080/api/users/2
Authorization: Bearer <user_token de l'utilisateur id=1>
# ❌ 403 Forbidden (pas son profil et pas ADMIN)
```

---

### Test 4 : Vérifier SecurityUtils (propriété)

**a) CustomerController - Voir son client** :
```bash
# User alice (id=1) associé au Customer (id=1, email=alice@example.com)
GET http://localhost:8080/api/customers/1
Authorization: Bearer <alice_token>
# ✅ 200 OK (son propre customer)

GET http://localhost:8080/api/customers/2
Authorization: Bearer <alice_token>
# ❌ 403 Forbidden (pas son customer)
```

---

## 📝 Récapitulatif

### Ce que tu as appris

1. **@EnableMethodSecurity** : Active les annotations de sécurité sur les méthodes
2. **@PreAuthorize** : Vérifie les permissions AVANT l'exécution
3. **hasRole()** : Vérifie un rôle spécifique
4. **hasAnyRole()** : Vérifie au moins un des rôles listés
5. **SpEL expressions** : `@securityUtils.isOwner()` pour vérifications custom
6. **SecurityUtils** : Bean pour logique de sécurité complexe
7. **Multi-rôles** : Un utilisateur peut avoir plusieurs rôles simultanément

### Différences clés

| Annotation | Description | Exemple |
|------------|-------------|---------|
| `hasRole('ADMIN')` | Un seul rôle requis | Suppression |
| `hasAnyRole('MODERATOR', 'ADMIN')` | Au moins un des rôles | Modification |
| `hasAuthority('ROLE_ADMIN')` | Autorité exacte (avec préfixe) | Rarement utilisé |
| `@securityUtils.method()` | Logique custom | Propriété ressource |

---

## 🎯 Critères de Réussite

- [ ] Multi-rôles configurés sur User
- [ ] @EnableMethodSecurity activé
- [ ] Endpoints publics configurés (GET /products, POST /customers)
- [ ] ProductController sécurisé (MODERATOR pour création, ADMIN](#)
 pour suppression)
- [ ] CustomerController sécurisé (propriétaire ou ADMIN)
- [ ] OrderController sécurisé (propriétaire ou MODERATOR)
- [ ] SecurityUtils créé pour vérifications custom
- [ ] UserController créé (gestion utilisateurs/rôles)
- [ ] Tests réussis pour les 3 rôles (USER, MODERATOR, ADMIN)
- [ ] 403 Forbidden retourné si permissions insuffisantes

---

## 💡 Conseils

1. **Commence par activer @EnableMethodSecurity** avant d'ajouter les @PreAuthorize
2. **Teste avec Postman** en créant une collection avec les 3 types de tokens
3. **Vérifie les logs** : Spring Security log les refus d'accès
4. **Utilise H2 Console** pour vérifier la table `user_roles`
5. **Crée un utilisateur ADMIN manuellement** en DB pour les premiers tests :
   ```sql
   INSERT INTO users (username, email, password, created_at) 
   VALUES ('admin', 'admin@example.com', '$2a$12$...', NOW());
   
   INSERT INTO user_roles (user_id, role) 
   VALUES (1, 'ROLE_ADMIN');
   ```

---

**Bon courage ! 🚀🔒**

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

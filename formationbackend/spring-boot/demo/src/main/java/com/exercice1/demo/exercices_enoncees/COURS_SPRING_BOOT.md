# 🚀 Module 3 : Spring Boot - Création d'APIs REST

## 📚 Table des matières

1. [Introduction à Spring Boot](#1-introduction-à-spring-boot)
2. [Architecture en couches](#2-architecture-en-couches)
3. [Création d'APIs REST](#3-création-dapis-rest)
4. [Validation des données](#4-validation-des-données)
5. [Gestion des exceptions](#5-gestion-des-exceptions)
6. [Documentation API](#6-documentation-api)
7. [Tests d'intégration](#7-tests-dintégration)
8. [Bonnes pratiques](#8-bonnes-pratiques)

---

## 1. Introduction à Spring Boot

### 1.1 Qu'est-ce que Spring Boot ?

Spring Boot est un framework qui simplifie le développement d'applications Spring en fournissant :
- **Configuration automatique** : pas besoin de XML
- **Serveur embarqué** : Tomcat, Jetty inclus
- **Dépendances gérées** : starters pour tout
- **Production-ready** : métriques, health checks

### 1.2 Structure d'un projet Spring Boot

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── DemoApplication.java    # Point d'entrée
│   │       ├── controller/             # Couche présentation
│   │       ├── service/                # Couche métier
│   │       ├── repository/             # Couche données
│   │       ├── model/                  # Entités JPA
│   │       ├── dto/                    # Data Transfer Objects
│   │       └── exception/              # Gestion erreurs
│   └── resources/
│       ├── application.properties      # Configuration
│       └── data.sql                    # Données initiales
└── test/
    └── java/                           # Tests
```

### 1.3 Fichier pom.xml minimal

```xml
<dependencies>
    <!-- Spring Boot Web (REST API) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Base de données H2 (dev) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok (optionnel mais recommandé) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Tests -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 1.4 Configuration application.properties

```properties
# Port serveur
server.port=8080

# Base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Console H2 (dev uniquement)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.org.springframework.web=DEBUG
logging.level.com.example=DEBUG
```

---

## 2. Architecture en couches

### 2.1 Principe de séparation des responsabilités

```
Client (Postman, navigateur)
        ↓
    Controller    → Gère les requêtes HTTP
        ↓
      Service     → Logique métier
        ↓
    Repository    → Accès aux données
        ↓
    Base de données
```

### 2.2 Couche Model (Entité)

```java
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Enumerated(EnumType.STRING)
    private Category category;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

enum Category {
    ELECTRONICS, CLOTHING, FOOD, BOOKS, OTHER
}
```

### 2.3 Couche DTO (Data Transfer Object)

**Pourquoi des DTOs ?**
- Séparation entre entité et API
- Validation des données entrantes
- Contrôle des données exposées
- Éviter la sur-exposition

```java
package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;
    
    @Size(max = 1000, message = "La description ne peut dépasser 1000 caractères")
    private String description;
    
    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private Double price;
    
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut être négatif")
    private Integer stock;
    
    @NotNull(message = "La catégorie est obligatoire")
    private String category;
}

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructeur depuis l'entité
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.category = product.getCategory().name();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}
```

### 2.4 Couche Repository

```java
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Méthodes dérivées (Spring génère automatiquement)
    List<Product> findByCategory(Category category);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByStockLessThan(Integer threshold);
    
    // JPQL personnalisé
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice AND p.stock > 0")
    List<Product> findAvailableProductsAbovePrice(Double minPrice);
    
    // SQL natif
    @Query(value = "SELECT * FROM products WHERE price = (SELECT MAX(price) FROM products)", 
           nativeQuery = true)
    Product findMostExpensiveProduct();
}
```

### 2.5 Couche Service

```java
package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Récupérer tous les produits
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un produit par ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));
        return new ProductResponse(product);
    }
    
    /**
     * Créer un nouveau produit
     */
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(Category.valueOf(request.getCategory()));
        
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }
    
    /**
     * Mettre à jour un produit
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(Category.valueOf(request.getCategory()));
        
        Product updatedProduct = productRepository.save(product);
        return new ProductResponse(updatedProduct);
    }
    
    /**
     * Supprimer un produit
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé avec l'id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    /**
     * Rechercher des produits par catégorie
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        Category cat = Category.valueOf(category.toUpperCase());
        return productRepository.findByCategory(cat)
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Mettre à jour le stock d'un produit
     */
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock insuffisant");
        }
        
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        return new ProductResponse(updatedProduct);
    }
}
```

### 2.6 Couche Controller

```java
package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * GET /api/products - Récupérer tous les produits
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/{id} - Récupérer un produit
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * POST /api/products - Créer un produit
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    /**
     * PUT /api/products/{id} - Mettre à jour un produit
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    /**
     * DELETE /api/products/{id} - Supprimer un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/products/category/{category} - Produits par catégorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    /**
     * PATCH /api/products/{id}/stock - Mettre à jour le stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        ProductResponse product = productService.updateStock(id, quantity);
        return ResponseEntity.ok(product);
    }
}
```

---

## 3. Création d'APIs REST

### 3.1 Codes de statut HTTP

| Code | Signification | Utilisation |
|------|---------------|-------------|
| 200 | OK | GET, PUT réussis |
| 201 | Created | POST réussi |
| 204 | No Content | DELETE réussi |
| 400 | Bad Request | Données invalides |
| 404 | Not Found | Ressource inexistante |
| 500 | Internal Server Error | Erreur serveur |

### 3.2 Verbes HTTP et leur usage

```java
// CREATE - POST
@PostMapping
public ResponseEntity<T> create(@RequestBody T request) { ... }

// READ - GET
@GetMapping
public ResponseEntity<List<T>> getAll() { ... }

@GetMapping("/{id}")
public ResponseEntity<T> getById(@PathVariable Long id) { ... }

// UPDATE - PUT (remplacement complet)
@PutMapping("/{id}")
public ResponseEntity<T> update(@PathVariable Long id, @RequestBody T request) { ... }

// UPDATE - PATCH (mise à jour partielle)
@PatchMapping("/{id}")
public ResponseEntity<T> partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) { ... }

// DELETE
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
```

### 3.3 Paramètres de requête

```java
// Path variable: /api/products/5
@GetMapping("/{id}")
public ResponseEntity<Product> getById(@PathVariable Long id) { ... }

// Query parameter: /api/products?category=ELECTRONICS
@GetMapping
public ResponseEntity<List<Product>> getProducts(@RequestParam String category) { ... }

// Optional query parameter avec valeur par défaut
@GetMapping
public ResponseEntity<List<Product>> getProducts(
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "10") int size
) { ... }

// Multiple query parameters: /api/products?minPrice=10&maxPrice=100
@GetMapping
public ResponseEntity<List<Product>> searchProducts(
    @RequestParam Double minPrice,
    @RequestParam Double maxPrice
) { ... }
```

### 3.4 Pagination et tri

```java
import org.springframework.data.domain.*;

@Service
public class ProductService {
    
    public Page<ProductResponse> getProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductResponse::new);
    }
}

@GetMapping
public ResponseEntity<Page<ProductResponse>> getProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "name") String sortBy
) {
    Page<ProductResponse> products = productService.getProducts(page, size, sortBy);
    return ResponseEntity.ok(products);
}
```

---

## 4. Validation des données

### 4.1 Annotations de validation

```java
import jakarta.validation.constraints.*;

public class ProductRequest {
    
    @NotNull(message = "Le nom ne peut être null")
    @NotBlank(message = "Le nom ne peut être vide")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;
    
    @Email(message = "Email invalide")
    private String contactEmail;
    
    @Min(value = 0, message = "Le prix ne peut être négatif")
    @Max(value = 10000, message = "Le prix ne peut dépasser 10000")
    private Double price;
    
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Code invalide")
    private String code;
    
    @Past(message = "La date doit être dans le passé")
    private LocalDate manufactureDate;
    
    @Future(message = "La date doit être dans le futur")
    private LocalDate expiryDate;
}
```

### 4.2 Validation personnalisée

```java
// Annotation personnalisée
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceValidator.class)
public @interface ValidPrice {
    String message() default "Prix invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator
public class PriceValidator implements ConstraintValidator<ValidPrice, Double> {
    
    @Override
    public boolean isValid(Double price, ConstraintValidatorContext context) {
        if (price == null) return true;
        return price > 0 && price < 100000;
    }
}

// Utilisation
public class ProductRequest {
    @ValidPrice(message = "Le prix doit être entre 0 et 100000")
    private Double price;
}
```

### 4.3 Validation au niveau de la classe

```java
@ValidDateRange
public class BookingRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "La date de fin doit être après la date de début";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, BookingRequest> {
    
    @Override
    public boolean isValid(BookingRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }
        return request.getEndDate().isAfter(request.getStartDate());
    }
}
```

---

## 5. Gestion des exceptions

### 5.1 Exception personnalisée

```java
package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
```

### 5.2 Global Exception Handler

```java
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Gestion des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Les données fournies sont invalides")
                .details(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Gestion des ressources non trouvées
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Gestion des données invalides
     */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(
            InvalidDataException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Gestion des doublons
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Gestion des erreurs génériques
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Une erreur interne est survenue")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
```

### 5.3 Objet ErrorResponse

```java
package com.example.demo.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
}
```

---

## 6. Documentation API

### 6.1 Swagger/OpenAPI avec SpringDoc

**Dépendance Maven:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Configuration:**
```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product API")
                        .version("1.0")
                        .description("API de gestion de produits")
                        .contact(new Contact()
                                .name("Votre nom")
                                .email("email@example.com")));
    }
}
```

**Annotations sur le Controller:**
```java
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API de gestion des produits")
public class ProductController {
    
    @Operation(summary = "Récupérer tous les produits")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        // ...
    }
    
    @Operation(summary = "Créer un nouveau produit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produit créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "Données du produit") @Valid @RequestBody ProductRequest request) {
        // ...
    }
}
```

**Accès:** http://localhost:8080/swagger-ui.html

---

## 7. Tests d'intégration

### 7.1 Test du Controller avec MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;
    
    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }
    
    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(1500.0);
        request.setStock(10);
        request.setCategory("ELECTRONICS");
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    void shouldReturnAllProducts() throws Exception {
        // Créer des produits de test
        Product p1 = new Product();
        p1.setName("Product 1");
        p1.setPrice(10.0);
        p1.setStock(5);
        p1.setCategory(Category.ELECTRONICS);
        productRepository.save(p1);
        
        Product p2 = new Product();
        p2.setName("Product 2");
        p2.setPrice(20.0);
        p2.setStock(3);
        p2.setCategory(Category.BOOKS);
        productRepository.save(p2);
        
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }
    
    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produit non trouvé avec l'id: 999"));
    }
    
    @Test
    void shouldValidateProductCreation() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("AB"); // Trop court
        request.setPrice(-10.0); // Négatif
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").exists())
                .andExpect(jsonPath("$.details.price").exists());
    }
}
```

---

## 8. Bonnes pratiques

### 8.1 Checklist des bonnes pratiques

✅ **Architecture**
- Séparation claire des couches (Controller, Service, Repository)
- Utilisation de DTOs pour l'API
- Services transactionnels

✅ **Naming**
- Controllers: `ProductController`, `UserController`
- Services: `ProductService`, `UserService`
- Repositories: `ProductRepository`, `UserRepository`
- DTOs: `ProductRequest`, `ProductResponse`

✅ **REST API**
- Utiliser les verbes HTTP correctement
- Codes de statut appropriés
- Endpoints cohérents (`/api/resources`)
- Pagination pour les listes

✅ **Validation**
- Valider toutes les entrées utilisateur
- Messages d'erreur clairs
- Gestion centralisée des exceptions

✅ **Performance**
- `@Transactional(readOnly = true)` pour les lectures
- Éviter N+1 queries (utiliser JOIN FETCH)
- Pagination des résultats

✅ **Sécurité**
- Ne jamais exposer les entités directement
- Valider et assainir les entrées
- Gestion des erreurs sans fuite d'informations

### 8.2 Patterns courants

**Pattern DTO <-> Entity:**
```java
// Mapper manuel
public ProductResponse toResponse(Product product) {
    return new ProductResponse(product);
}

public Product toEntity(ProductRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    // ...
    return product;
}

// Ou utiliser MapStruct pour automatiser
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);
    Product toEntity(ProductRequest request);
}
```

**Pattern Builder pour les tests:**
```java
public class ProductTestBuilder {
    private String name = "Default Product";
    private Double price = 10.0;
    private Integer stock = 5;
    
    public ProductTestBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ProductTestBuilder withPrice(Double price) {
        this.price = price;
        return this;
    }
    
    public Product build() {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }
}
```

---

## 🎯 Résumé des concepts clés

1. **Spring Boot** simplifie la configuration et le déploiement
2. **Architecture en couches** sépare les responsabilités
3. **DTOs** protègent vos entités et contrôlent l'exposition
4. **Validation** garantit la qualité des données
5. **Exception handling** unifie la gestion des erreurs
6. **REST API** suit les conventions HTTP
7. **Tests** assurent la fiabilité du code

---

## 📝 Exercices à venir

1. **Exercice 1**: Créer une API REST simple (Product CRUD)
2. **Exercice 2**: Ajouter validation et gestion d'erreurs
3. **Exercice 3**: Implémenter recherche et pagination
4. **Exercice 4**: Tests d'intégration complets
5. **Mini-projet**: API e-commerce complète

Prêt à commencer les exercices ? 🚀

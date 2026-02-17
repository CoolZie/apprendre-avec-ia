# 🛒 Mini-Projet : API E-commerce

## 🎯 Objectif

Développer une API REST complète pour un système e-commerce avec plusieurs entités liées, en appliquant toutes les compétences acquises dans les exercices précédents.

---

## 📋 Cahier des charges

### Fonctionnalités attendues

L'API doit permettre de :

1. **Gestion des clients (Customer)**
   - Créer un compte client
   - Consulter/modifier les informations d'un client
   - Lister tous les clients avec pagination

2. **Gestion des produits (Product)** - Déjà implémenté
   - Réutiliser le code existant des exercices 1-3
   - CRUD complet avec pagination, recherche, filtres

3. **Gestion des commandes (Order)**
   - Créer une commande avec plusieurs produits
   - Consulter l'historique des commandes d'un client
   - Calculer le total d'une commande
   - Gérer les statuts (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

4. **Gestion des lignes de commande (OrderItem)**
   - Associer des produits à une commande
   - Gérer les quantités et prix unitaires
   - Calculer le sous-total par ligne

5. **Statistiques e-commerce**
   - Chiffre d'affaires total
   - Nombre de commandes par statut
   - Top 10 des produits les plus vendus
   - Clients ayant le plus commandé

---

## 🗂️ Modèle de données

### Entités et relations

```
Customer (1) -----> (*) Order (1) -----> (*) OrderItem (*) <----- (1) Product
   |                      |
   id                     id                    
   firstName              orderDate
   lastName               status
   email                  totalAmount
   phone                  customer_id
   address
```

### Diagramme des relations

```
┌─────────────────┐
│    Customer     │
├─────────────────┤
│ id (PK)         │
│ firstName       │
│ lastName        │
│ email (unique)  │
│ phone           │
│ address         │
│ createdAt       │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐
│      Order      │
├─────────────────┤
│ id (PK)         │
│ customer_id(FK) │
│ orderDate       │
│ status          │
│ totalAmount     │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐         ┌─────────────────┐
│   OrderItem     │────────▶│    Product      │
├─────────────────┤   N:1   ├─────────────────┤
│ id (PK)         │         │ id (PK)         │
│ order_id (FK)   │         │ name            │
│ product_id (FK) │         │ description     │
│ quantity        │         │ price           │
│ unitPrice       │         │ category        │
│ subtotal        │         │ stock           │
└─────────────────┘         └─────────────────┘
```

---

## 📝 Spécifications détaillées

### 1. Entité Customer

**Fichier** : `model/Customer.java`

```java
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 500)
    private String address;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### 2. Entité Order

**Fichier** : `model/Order.java`

```java
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false)
    private Double totalAmount;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    // Méthode utilitaire pour calculer le total
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();
    }
}
```

### 3. Entité OrderItem

**Fichier** : `model/OrderItem.java`

```java
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double unitPrice;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
    }
}
```

### 4. Enum OrderStatus

**Fichier** : `model/OrderStatus.java`

```java
public enum OrderStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    SHIPPED("Expédiée"),
    DELIVERED("Livrée"),
    CANCELLED("Annulée");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

---

## 🎯 TODO : Implémentation

### Étape 1 : Créer les entités (30 min)

TODO :
- [ ] Créer `model/Customer.java`
- [ ] Créer `model/Order.java`
- [ ] Créer `model/OrderItem.java`
- [ ] Créer `model/OrderStatus.java`
- [ ] Vérifier que Product.java existe déjà (exercices précédents)

### Étape 2 : Créer les DTOs (45 min)

TODO :
- [ ] `dto/CustomerRequest.java` - Créer/modifier client
- [ ] `dto/CustomerResponse.java` - Réponse client
- [ ] `dto/OrderRequest.java` - Créer une commande
- [ ] `dto/OrderResponse.java` - Réponse commande avec items
- [ ] `dto/OrderItemRequest.java` - Ligne de commande dans la requête
- [ ] `dto/OrderItemResponse.java` - Ligne de commande dans la réponse
- [ ] `dto/OrderStatisticsResponse.java` - Statistiques commandes

**Exemple CustomerRequest** :
```java
public record CustomerRequest(
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    String firstName,
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    String lastName,
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    String email,
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Le téléphone doit contenir 10 chiffres")
    String phone,
    
    @Size(max = 500)
    String address
) {}
```

**Exemple OrderRequest** :
```java
public record OrderRequest(
    @NotNull(message = "L'ID du client est obligatoire")
    Long customerId,
    
    @NotEmpty(message = "La commande doit contenir au moins un produit")
    List<OrderItemRequest> items
) {}

public record OrderItemRequest(
    @NotNull(message = "L'ID du produit est obligatoire")
    Long productId,
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Max(value = 1000, message = "La quantité ne peut dépasser 1000")
    Integer quantity
) {}
```

### Étape 3 : Créer les repositories (20 min)

TODO :
- [ ] `repository/CustomerRepository.java`
- [ ] `repository/OrderRepository.java`
- [ ] `repository/OrderItemRepository.java`

**Exemple CustomerRepository** :
```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<Customer> findByIdWithOrders(@Param("id") Long id);
}
```

**Exemple OrderRepository** :
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    Double calculateTotalRevenue();
}
```

### Étape 4 : Créer les services (1h30)

TODO :
- [ ] `service/CustomerService.java`
- [ ] `service/OrderService.java`

**CustomerService - Méthodes requises** :
```java
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    // TODO: Implémenter ces méthodes
    public CustomerResponse createCustomer(CustomerRequest request);
    
    public CustomerResponse getCustomerById(Long id);
    
    public PagedResponse<CustomerResponse> getAllCustomers(int page, int size, String sortBy, String direction);
    
    public CustomerResponse updateCustomer(Long id, CustomerRequest request);
    
    public void deleteCustomer(Long id);
    
    public PagedResponse<OrderResponse> getCustomerOrders(Long customerId, int page, int size);
}
```

**OrderService - Méthodes requises** :
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    
    // TODO: Implémenter ces méthodes
    public OrderResponse createOrder(OrderRequest request);
    
    public OrderResponse getOrderById(Long id);
    
    public PagedResponse<OrderResponse> getAllOrders(int page, int size, String sortBy, String direction);
    
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus);
    
    public void cancelOrder(Long id);
    
    public PagedResponse<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size);
    
    public OrderStatisticsResponse getOrderStatistics();
    
    // Méthode privée helper pour créer les OrderItems
    private List<OrderItem> createOrderItems(Order order, List<OrderItemRequest> itemRequests);
}
```

**Logique métier importante dans OrderService.createOrder()** :
1. Vérifier que le client existe
2. Pour chaque produit :
   - Vérifier que le produit existe
   - Vérifier que le stock est suffisant
   - Déduire la quantité du stock
3. Créer les OrderItems avec le prix actuel du produit (unitPrice)
4. Calculer le total de la commande
5. Sauvegarder la commande

### Étape 5 : Créer les controllers (1h)

TODO :
- [ ] `controller/CustomerController.java`
- [ ] `controller/OrderController.java`

**CustomerController - Endpoints requis** :
```
POST   /api/customers                    - Créer un client
GET    /api/customers/{id}               - Détail d'un client
GET    /api/customers                    - Liste paginée des clients
PUT    /api/customers/{id}               - Modifier un client
DELETE /api/customers/{id}               - Supprimer un client
GET    /api/customers/{id}/orders        - Commandes d'un client
```

**OrderController - Endpoints requis** :
```
POST   /api/orders                       - Créer une commande
GET    /api/orders/{id}                  - Détail d'une commande
GET    /api/orders                       - Liste paginée des commandes
PATCH  /api/orders/{id}/status           - Modifier le statut
DELETE /api/orders/{id}                  - Annuler une commande
GET    /api/orders/status/{status}       - Commandes par statut
GET    /api/orders/statistics            - Statistiques
```

### Étape 6 : Gestion des exceptions (30 min)

TODO :
- [ ] Créer `exception/InsufficientStockException.java`
- [ ] Créer `exception/OrderCancelledException.java`
- [ ] Ajouter les handlers dans `GlobalExceptionHandler.java`

**Exceptions métier** :
```java
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Stock insuffisant pour %s : %d demandé(s), %d disponible(s)", 
            productName, requested, available));
    }
}

public class OrderCancelledException extends RuntimeException {
    public OrderCancelledException(Long orderId) {
        super(String.format("La commande #%d a été annulée et ne peut être modifiée", orderId));
    }
}
```

### Étape 7 : Tests manuels (30 min)

TODO :
- [ ] Tester avec Swagger UI ou Postman
- [ ] Créer un client
- [ ] Créer une commande avec 2-3 produits
- [ ] Vérifier que le stock diminue
- [ ] Modifier le statut de la commande
- [ ] Consulter les statistiques

---

## 🎨 Fonctionnalités bonus (optionnel)

Si tu termines avant le temps imparti :

1. **Validation avancée**
   - [ ] Empêcher la modification d'une commande livrée
   - [ ] Empêcher la suppression d'un client ayant des commandes
   - [ ] Valider les transitions de statut (PENDING → CONFIRMED → SHIPPED → DELIVERED)

2. **Endpoints supplémentaires**
   - [ ] GET `/api/products/top-sellers` - Top 10 produits les plus vendus
   - [ ] GET `/api/customers/top-buyers` - Top 10 clients par montant dépensé
   - [ ] GET `/api/orders/revenue/by-month` - CA par mois

3. **Filtrage avancé**
   - [ ] GET `/api/orders/filter?status=PENDING&customerId=5&minAmount=100`
   - [ ] GET `/api/customers/search?name=dupont&email=@gmail.com`

4. **Documentation Swagger**
   - [ ] Ajouter @Operation, @Schema sur tous les endpoints
   - [ ] Créer des exemples de requêtes/réponses

---

## ✅ Critères d'évaluation (sur 100 points)

| Critère | Points | Détails |
|---------|--------|---------|
| **Entités et relations** | 20 | Relations JPA correctes, cascade, fetch type approprié |
| **DTOs** | 15 | Request/Response séparés, validation complète |
| **Repositories** | 10 | Query methods, @Query JPQL |
| **Services** | 25 | Logique métier (stock, total), gestion des erreurs |
| **Controllers** | 15 | REST conventions, pagination, codes HTTP |
| **Exceptions** | 10 | Exceptions métier, GlobalExceptionHandler |
| **Tests fonctionnels** | 5 | L'API fonctionne end-to-end |
| **Code quality** | 0 | Nommage, structure, commentaires si nécessaire |

**Score minimum attendu** : 75/100

---

## 📖 Conseils

### Architecture

- Réutilise le code existant (Product, ProductRepository, ProductService)
- Suis le même pattern que les exercices précédents
- Sépare bien les responsabilités (Controller → Service → Repository)

### Gestion des transactions

```java
@Transactional
public OrderResponse createOrder(OrderRequest request) {
    // Toutes les opérations (vérifications, création, déduction stock)
    // sont dans une seule transaction
}
```

### Éviter les boucles infinies JSON

Utilise `@JsonIgnore` ou DTOs pour éviter les références circulaires :
```java
// Dans Customer.java
@OneToMany(mappedBy = "customer")
@JsonIgnore  // Évite de sérialiser orders quand on retourne un Customer
private List<Order> orders;
```

### Tester la déduction de stock

```java
// Exemple de test manuel
1. Créer un produit avec stock = 10
2. Créer une commande avec quantity = 3
3. Vérifier que le stock du produit est maintenant 7
4. Essayer de commander 15 → doit échouer (InsufficientStockException)
```

---

## 🚀 Une fois terminé

Quand tu as fini le mini-projet :

1. **Teste toutes les fonctionnalités** avec Swagger UI
2. **Demande une correction** en me montrant ton code
3. **Note les difficultés** rencontrées pour progresser

**Prochaine étape après le mini-projet** :
- Module 4 : Spring Security (authentification, autorisation, JWT)

Bon courage ! 💪

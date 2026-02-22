# 📋 Plan de Formation Backend - Progression

## 🎓 Vue d'ensemble

Cette formation vous accompagne du niveau débutant à développeur backend confirmé.
Chaque module comprend : théorie, exercices pratiques, mini-projet et correction.

---

## ✅ Modules complétés

### Module 1 : Java Fondamental ✓
- ✅ Streams API
- ✅ Lambdas et références de méthodes
- ✅ Collectors et opérations de groupement
- ✅ Records
- ✅ Mini-projet : Analyse de produits

**Fichiers :**
- `exercices/Exercice1.java` à `Exercice5.java`
- `exercices/MiniProjet.java`
- `revision/ExerciceStream.java`, `ExerciceCollectors.java`, etc.

---

## 🔄 Module en cours

### Module 2 : JPA/Hibernate - Persistance des données ✓

**Objectifs :**
- ✅ Maîtriser le mapping objet-relationnel
- ✅ Comprendre et utiliser les annotations JPA
- ✅ Gérer les relations entre entités
- ✅ Créer des repositories avec Spring Data JPA
- ✅ Appliquer les bonnes pratiques de persistance

**Fichiers créés :**
- 📘 `jpa/COURS_JPA.md` - Documentation complète
- ✅ `jpa/ExerciceJPA1.java` - Entités de base
- ✅ `jpa/ExerciceJPA2.java` - Relations OneToMany/ManyToOne
- ✅ `jpa/ExerciceJPA3.java` - Repositories et services (75/100)
- ✅ `jpa/mini_projet/` - Système de bibliothèque complet (87/100)
- ✅ `jpa/corrections/UserCorrection.java` - Correction détaillée

**✅ Progression complétée (2 février 2026) :**

1. ✅ **Jour 1-2 : Théorie et bases**
   - Lecture de `COURS_JPA.md`
   - Exercice 1 complété
   - Correction étudiée

2. ✅ **Jour 3-4 : Relations**
   - Exercice 2 complété
   - Relations bidirectionnelles maîtrisées

3. ✅ **Jour 5-6 : Spring Data JPA**
   - Exercice 3 complété (score: 75/100)
   - Points à améliorer : validation email, mise à jour utilisateur

4. ✅ **Jour 7-10 : Mini-projet Bibliothèque**
   - Projet complété (score: 87/100)
   - 4 entités créées (Author, Book, Member, Loan)
   - 4 repositories avec requêtes JPQL avancées
   - 2 services (LibraryService, StatisticsService)
   - Gestion des emprunts, retours, statistiques
   - Excellente utilisation des projections et agrégations

---

## � Module en cours

### Module 3 : Spring Boot - Création d'APIs REST
**Durée estimée : 2 semaines**
**Date de début : 4 février 2026**

**Objectifs :**
- ✅ Comprendre l'architecture Spring Boot
- ✅ Maîtriser l'architecture en couches
- ✅ Créer des APIs REST complètes
- ✅ Implémenter la validation des données
- ✅ Gérer les exceptions professionnellement
- ✅ Ajouter recherche, pagination et filtrage
- 🔄 Documenter l'API avec Swagger

**Fichiers créés :**
- 📘 `spring-boot/COURS_SPRING_BOOT.md` - Documentation complète
- ✅ `spring-boot/EXERCICE1.md` - CRUD basique (85/100)
- ✅ `spring-boot/EXERCICE2.md` - Validation et exceptions avancées (98/100) ⭐
- ✅ `spring-boot/EXERCICE3.md` - Recherche et pagination (100/100) ⭐⭐
- ✅ `spring-boot/EXERCICE4.md` - Documentation Swagger/OpenAPI (95/100) ⭐⭐
- 📝 `spring-boot/MINI_PROJET_ECOMMERCE.md` - **PROCHAINE ÉTAPE - Mini-projet API e-commerce**
- ✅ `spring-boot/demo/` - Projet Spring Boot avec documentation Swagger complète

**✅ Progression Exercice 1 (4 février 2026) :**

1. ✅ **Architecture créée**
   - model/ : Product.java, Category.java
   - dto/ : ProductRequest.java, ProductResponse.java
   - repository/ : ProductRepository.java
   - service/ : ProductService.java
   - controller/ : ProductController.java
   - exception/ : ResourceNotFoundException, GlobalExceptionHandler, ErrorResponse

2. ✅ **Fonctionnalités CRUD**
   - GET /api/products - Liste tous les produits
   - GET /api/products/{id} - Détail d'un produit
   - POST /api/products - Création
   - PUT /api/products/{id} - Mise à jour
   - DELETE /api/products/{id} - Suppression

3. ✅ **Fonctionnalités bonus**
   - GET /api/products/search?name={keyword} - Recherche par nom
   - GET /api/products/category/{category} - Filtrage par catégorie

4. ✅ **Corrections appliquées**
   - Timestamps automatiques (createdAt/updatedAt)
   - Validation des données améliorée
   - Messages d'erreur en français
   - REST conventions (PathVariable pour PUT/DELETE)
   - ErrorResponse dans fichier séparé

**✅ Progression Exercice 2 (17 février 2026) :**

1. ✅ **Validations personnalisées créées**
   - @ValidPrice : validation prix entre 0.01 et 100000
   - @ValidCategory : validation String correspond à enum Category
   - PriceValidator et CategoryValidator implémentés
   - ProductRequest amélioré avec @Pattern, @Max, validations custom
   - UpdateStockRequest créé avec validations quantité

2. ✅ **Exceptions métier créées**
   - InvalidDataException : erreurs métier (stock insuffisant, etc.)
   - DuplicateResourceException : conflits de ressources (doublons)
   - Héritage correct de RuntimeException

3. ✅ **GlobalExceptionHandler complété**
   - handleInvalidDataExceptions() : 400 BAD REQUEST
   - handleDuplicateResourceExceptions() : 409 CONFLICT  
   - handleGlobalExceptions() : 500 INTERNAL SERVER ERROR
   - Gestion cohérente des codes HTTP

4. ✅ **Repository enrichi**
   - existsByName() : vérification doublons (auto-généré par Spring Data)
   - findByName() : recherche par nom exact

5. ✅ **Service amélioré**
   - createProduct() : vérification doublons AVANT création
   - updateStock() : ajout/retrait de stock avec validation métier
   - Validation stock négatif avec exceptions appropriées

6. ✅ **Controller complété**
   - PATCH /api/products/{id}/stock : mise à jour partielle du stock
   - Sémantique REST correcte (PATCH pour modification partielle)

7. ✅ **Bugs corrigés par l'étudiant (autonomie) :**
   - Bug #1 : Logique inversée dans createProduct (if !exists)
   - Bug #2 : setStock(newStock) au lieu de setStock(quantity)
   - Bug #3 : Conflits de noms de méthodes dans GlobalExceptionHandler
   - Bug #4 : Incohérences HTTP Status (409 CONFLICT, 500 INTERNAL_SERVER_ERROR)
   - Bug #5 : Limite de prix 100000 vs 1000000
   - Bug #6 : Pattern regex \\s vs \\\\s
   - Amélioration : CategoryValidator avec toUpperCase()

**Score : 98/100** ⭐⭐⭐⭐⭐

**Compétences acquises :**
- Création d'annotations de validation personnalisées
- Implémentation de ConstraintValidator
- Gestion professionnelle des exceptions métier
- Distinction codes HTTP (400 vs 409 vs 500)
- Debugging et auto-correction

**✅ Progression Exercice 3 (18 février 2026) :**

1. ✅ **Pagination généralisée implémentée**
   - PagedResponse<T> : wrapper générique réutilisable
   - 9 métadonnées : content, pageNumber, pageSize, totalElements, totalPages, first, last, empty, sorted
   - Conversion automatique Page<Product> → PagedResponse<ProductResponse>
   - getAllProducts() avec pagination : GET /api/products?page=0&size=10&sortBy=name&direction=ASC

2. ✅ **Recherche multi-critères créée**
   - searchProducts() : recherche par mot-clé dans nom ET description
   - Repository : findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase()
   - Endpoint : GET /api/products/search?keyword=laptop&page=0&size=10&sortBy=price&direction=DESC
   - Support tri et pagination sur résultats de recherche

3. ✅ **Filtrage par catégorie avec pagination**
   - getProductsByCategory() : filtrage par catégorie
   - Conversion String → Category (valueOf avec toUpperCase)
   - Repository : findByCategory(Category, Pageable)
   - Endpoint : GET /api/products/category/ELECTRONICS?page=0&size=5&sortBy=price&direction=ASC

4. ✅ **Filtrage par plage de prix**
   - getProductsByPriceRange() : filtrage min/max prix
   - Repository : findByPriceBetween(Double, Double, Pageable)
   - Endpoint : GET /api/products/price-range?min=100&max=500&page=0&size=10
   - Validation min/max avec @RequestParam(required = true)

5. ✅ **Stock faible détecté**
   - getLowStockProducts() : produits sous seuil de stock
   - Repository : findByStockLessThanEqual(Integer, Pageable)
   - Endpoint : GET /api/products/low-stock?threshold=10&page=0&size=20
   - Défaut threshold=10 configurable

6. ✅ **Filtrage combiné multicritères**
   - filterProducts() : 6 critères combinables (catégorie, prix min/max, stock min/max, mot-clé)
   - Repository avec @Query JPQL et paramètres optionnels (IS NULL OR)
   - Support tri dynamique et pagination
   - Endpoint : GET /api/products/filter?category=BOOKS&minPrice=10&maxPrice=50&keyword=java&page=0&size=10
   - Bug corrigé : passage du paramètre `keyword` au lieu de `direction` au repository

7. ✅ **Statistiques globales calculées**
   - getStatistics() : statistiques sur tous les produits
   - ProductStatistics : totalProducts, totalValue, averagePrice, totalStock
   - Calculs avec Streams : count(), mapToDouble().sum(), average()
   - Endpoint : GET /api/products/statistics

8. ✅ **Statistiques par catégorie**
   - getStatisticsByCategory() : agrégation par catégorie
   - CategoryStatistics : category, productCount, totalValue, averagePrice
   - Groupement avec Collectors.groupingBy()
   - Endpoint : GET /api/products/statistics/by-category

9. ✅ **Refactorisation et nettoyage**
   - Suppression endpoints dupliqués (anciens endpoints sans pagination)
   - Endpoint /v1/search conservé pour compatibilité
   - Import inutilisé Category supprimé
   - Architecture propre : 13 endpoints REST cohérents

**Score : 100/100** ⭐⭐⭐⭐⭐

**Compétences acquises :**
- Pagination avec Spring Data JPA (Page<T>, Pageable, PageRequest)
- Création de wrappers génériques (PagedResponse<T>)
- Query methods dérivés (findByNameContaining, findByPriceBetween)
- @Query JPQL avec paramètres optionnels
- Tri dynamique (Sort.Direction, Sort.by())
- Statistiques et agrégations avec Streams
- Collectors.groupingBy() pour groupement
- Refactorisation et élimination de code dupliqué

**📋 Plan d'étude (10 jours) :**

1. ✅ **Jour 1-2 : Théorie et CRUD basique** (TERMINÉ - 4 février)
   - Lecture de `COURS_SPRING_BOOT.md`
   - Exercice 1 : API REST CRUD
   - Tests avec Postman
   - Score : 85/100

2. ✅ **Jour 3-4 : Validation et gestion d'erreurs** (TERMINÉ - 17 février)
   - Exercice 2 : Validation avancée
   - Global Exception Handler amélioré
   - Messages d'erreur clairs et cohérents
   - Score : 98/100 ⭐

3. ✅ **Jour 5-6 : Recherche et pagination** (TERMINÉ - 18 février)
   - Exercice 3 : Pagination complète
   - Filtrage combiné multicritères
   - Statistiques avancées
   - Score : 100/100 ⭐⭐

**✅ Configuration Swagger/OpenAPI (18 février) :**

1. ✅ **Dépendance SpringDoc ajoutée**
   - springdoc-openapi-starter-webmvc-ui version 2.6.0
   - Compatible avec Spring Boot 3.2.2

2. ✅ **Problème de compatibilité résolu**
   - Erreur : NoSuchMethodError avec Spring Boot 3.5.10
   - Solution : Downgrade vers Spring Boot 3.2.2 (version stable)
   - Configuration Lombok simplifiée (suppression annotationProcessorPaths)

3. ✅ **Swagger UI fonctionnel**
   - Interface : http://localhost:8080/swagger-ui/index.html
   - API Docs JSON : http://localhost:8080/v3/api-docs
   - Tous les 13 endpoints documentés automatiquement
   - Schémas DTOs générés (ProductRequest, ProductResponse, PagedResponse, etc.)

4. 📝 **EXERCICE4.md créé**
   - Guide complet sur Swagger/OpenAPI
   - Annotations @Operation, @Parameter, @Schema
   - Organisation par tags
   - Configuration OpenApiConfig
   - Exercices pratiques de documentation
   - **Note** : Exercice bonus optionnel, Swagger fonctionne déjà

**✅ Exercice 4 : Documentation Swagger/OpenAPI complété (18 février) :**

1. ✅ **ProductController documenté**
   - 14 endpoints avec @Operation (summary + description)
   - @ApiResponses avec codes HTTP appropriés (200, 201, 204, 400, 404, 409)
   - Organisation par tags : "1. CRUD de base", "2. Recherche et filtrage", "3. Statistiques", "4. Compatibilité"
   - Descriptions améliorées et corrections orthographiques
   - Suppression des schémas incorrects dans @Content

2. ✅ **OpenApiConfig personnalisé**
   - Déplacé dans package config/
   - Titre : "Product Management API"
   - Description complète de l'API
   - Contact : équipe backend formation
   - 2 serveurs : développement + production
   - Licence MIT

3. ✅ **DTOs entièrement documentés**
   - ProductRequest : 5 champs avec @Schema (description, example, requiredMode)
   - ProductResponse : 8 champs documentés avec exemples
   - PagedResponse<T> : 9 champs de pagination documentés
   - ProductStatistics : 6 champs statistiques
   - CategoryStatistics : 4 champs par catégorie

4. ✅ **Résultat dans Swagger UI**
   - Documentation interactive complète
   - Schémas JSON générés automatiquement
   - Exemples de requêtes/réponses
   - Endpoints groupés par fonctionnalité
   - Interface professionnelle et claire

**Score : 95/100** ⭐⭐⭐⭐⭐

**Compétences acquises :**
- Annotations OpenAPI (@Operation, @ApiResponse, @Schema, @Tag)
- Configuration personnalisée OpenAPI
- Organisation de la documentation par tags
- Documentation des DTOs avec exemples
- Codes HTTP appropriés par type d'opération
- Bonnes pratiques de documentation d'API

**✅ Jour 7-10 : Mini-projet e-commerce** (COMPLÉTÉ - 18 février)

**Objectif :** Créer une API REST complète de gestion de commandes e-commerce

**Architecture implémentée :**

1. ✅ **4 Entités JPA créées**
   - **Customer** : id, firstName, lastName, email, phone, address, createdAt, orders[]
   - **Order** : id, customer, orderDate, status (enum), totalAmount, items[]
   - **OrderItem** : id, order, product, quantity, unitPrice, subtotal
   - **Product** : (existant, enrichi pour le projet)
   - Relations bidirectionnelles : Customer ↔ Order, Order ↔ OrderItem

2. ✅ **4 Repositories Spring Data JPA**
   - CustomerRepository : méthodes de base JPA
   - OrderRepository : findByCustomerId, findByStatus, countByStatus, calculateTotalRevenue
   - OrderItemRepository : méthodes de base
   - ProductRepository : (déjà complet)
   - Requêtes personnalisées avec @Query JPQL

3. ✅ **3 Services avec logique métier**
   - **CustomerService** : CRUD complet, getCustomerOrders, validation suppression (pas de commandes)
   - **OrderService** : création avec déduction stock, gestion statuts, annulation, statistiques
   - **ProductService** : (déjà existant)
   - Gestion transactionnelle avec @Transactional

4. ✅ **3 Controllers REST (27 endpoints total)**
   
   **CustomerController (6 endpoints) :**
   - POST `/api/customers` - Créer un client
   - GET `/api/customers/{id}` - Détail d'un client
   - GET `/api/customers` - Liste paginée avec tri
   - PUT `/api/customers/{id}` - Modifier un client
   - DELETE `/api/customers/{id}` - Supprimer (validation : aucune commande)
   - GET `/api/customers/{id}/orders` - Historique commandes paginé
   
   **OrderController (7 endpoints) :**
   - POST `/api/orders` - Créer une commande (avec déduction stock automatique)
   - GET `/api/orders/{id}` - Détail d'une commande avec items
   - GET `/api/orders` - Liste paginée des commandes
   - PATCH `/api/orders/{id}/status` - Modifier le statut
   - DELETE `/api/orders/{id}` - Annuler une commande (statut = CANCELLED)
   - GET `/api/orders/status/{status}` - Filtrer par statut (PENDING, SHIPPED, DELIVERED, CANCELLED)
   - GET `/api/orders/statistics` - Statistiques (total ventes, revenus)
   
   **ProductController (14 endpoints)** : déjà complet

5. ✅ **DTOs avec validation Jakarta**
   - CustomerRequest : @NotBlank, @Email, @Pattern (téléphone 10 chiffres), @Size
   - CustomerResponse : constructeur depuis entité Customer
   - OrderRequest : customerId + liste OrderItemRequest
   - OrderItemRequest : productId + quantity
   - OrderResponse : inclut liste OrderItemResponse avec détails produits
   - OrderItemResponse : product, quantity, unitPrice, subtotal
   - OrderStatisticsResponse : totalOrders, totalRevenue

6. ✅ **Logique métier avancée implémentée**
   - **Gestion stock automatique** : vérification stock >= quantité avant commande
   - **Déduction stock** : product.stock -= quantity pour chaque item
   - **Exception métier** : InsufficientStockException si stock insuffisant
   - **Calcul automatique** : subtotal calculé dans @PrePersist/@PreUpdate (quantity × unitPrice)
   - **Total commande** : calculé avec Stream.mapToDouble().sum() sur items
   - **Statuts commandes** : PENDING par défaut, transitions contrôlées
   - **Validation métier** : empêche modification commande DELIVERED (OrderCancelledException)
   - **Suppression client** : exception si commandes existantes (CustomerException)

7. ✅ **Exceptions métier personnalisées**
   - InsufficientStockException : extends RuntimeException (stock insuffisant)
   - OrderCancelledException : tentative modification commande livrée
   - CustomerException : suppression client avec historique de commandes

8. ✅ **Corrections autonomes effectuées**
   - Bug routes REST : @PathVariable vs @RequestParam cohérents
   - OrderResponse.totalAmount : ligne ajoutée dans constructeur
   - Warnings Lombok @Builder : liste initialisée dans @PrePersist (Customer)
   - CustomerService.updateCustomer : mise à jour champs existants au lieu de créer nouveau
   - OrderService : vérification stock avec >= au lieu de > (permet achat total stock)
   - Routes cohérentes : {id} partout dans OrderController
   - CustomerService.createCustomer : bug copier-coller corrigé (.getLastName() au lieu de .getFirstName())
   - Import inutilisé lombok.Builder supprimé dans Order.java

**Score : 95/100** ⭐⭐⭐⭐⭐

**Détails de notation :**
- Architecture (20/20) : Couches bien séparées, structure professionnelle
- Entités JPA (20/20) : Relations bidirectionnelles correctes, FetchType appropriés
- Repositories (20/20) : Requêtes @Query avancées, méthodes dérivées
- Services (19/20) : Logique métier complexe bien implémentée
- Controllers (18/20) : 27 endpoints REST cohérents (225% de l'exigence)
- DTOs (20/20) : Validation complète, séparation Request/Response
- Exceptions (18/20) : Exceptions métier pertinentes, gestion appropriée
- Code Quality (20/20) : Code propre, bugs corrigés, pas d'erreurs compilation

**Compétences acquises :**
- Relations JPA complexes (@OneToMany bidirectionnel, @ManyToOne)
- Gestion de transactions distribuées (stock + commande)
- Calculs automatiques avec @PrePersist/@PreUpdate
- Logique métier e-commerce (stock, commandes, statuts, totaux)
- Statistiques avec agrégations JPQL (SUM, COUNT)
- Debugging et auto-correction (9 bugs identifiés et corrigés)
- Validation métier avancée (règles business)
- Exceptions métier personnalisées
- Architecture complète API e-commerce

**Points forts du projet :**
- ✅ 27 endpoints REST fonctionnels (225% de l'objectif)
- ✅ Architecture en couches respectée
- ✅ Gestion transactionnelle correcte
- ✅ Code propre sans erreurs de compilation
- ✅ Relations JPA bidirectionnelles maîtrisées
- ✅ Logique métier complexe (stock, totaux automatiques)
- ✅ Exceptions métier appropriées
- ✅ Requêtes JPQL personnalisées pour filtrage et statistiques
- ✅ Pagination sur tous les endpoints de liste
- ✅ Validation Jakarta complète

---

## 🔄 Module en cours

### Module 4 : Spring Security 
**Durée estimée : 1.5 semaine**
**Date de début : 18 février 2026**

**Objectifs :**
- ✅ Comprendre l'authentification et l'autorisation
- ✅ Implémenter JWT (JSON Web Tokens)
- ✅ Maîtriser le password encoding (BCrypt)
- 🔄 Gérer les roles et permissions
- ⏳ Comprendre OAuth2 et OpenID Connect
- ⏳ Configurer CORS et CSRF

**Fichiers créés :**
- 📘 `spring-security/COURS_SPRING_SECURITY.md` - Documentation complète
- ✅ `spring-security/EXERCICE1.md` - JWT Authentication basique
- 📝 `spring-security/EXERCICE2.md` - Roles et @PreAuthorize
- 📝 `spring-security/EXERCICE3.md` - Refresh tokens, logout, rate limiting
- 📝 `spring-security/MINI_PROJET.md` - Blog platform avec 4 roles
- ✅ `spring-boot/demo/src/main/java/com/exercice1/security/` - Implémentation JWT complète

**✅ Progression Exercice 1 (18-20 février 2026) :**

1. ✅ **Architecture Spring Security créée**
   - config/ : SecurityConfig.java, ApplicationConfig.java
   - security/ : JwtService.java, JwtAuthenticationFilter.java
   - model/ : User.java avec rôles
   - repository/ : UserRepository.java
   - service/ : UserDetailsServiceImpl.java
   - controller/ : AuthController.java (3 endpoints REST)
   - dto/ : RegisterRequest.java, LoginRequest.java, AuthResponse.java
   - exception/ : SecurityExceptionHandler.java

2. ✅ **JWT Authentication implémentée**
   - JwtService : génération et validation tokens JWT (JJWT 0.12.3)
   - JwtAuthenticationFilter : filtre OncePerRequestFilter pour validation Bearer tokens
   - Sécurisation avec clé secrète HMAC-SHA256
   - Claims personnalisés : roles inclus dans le token
   - Expiration configurable (24h par défaut)

3. ✅ **Endpoints REST d'authentification**
   - POST /api/auth/register : inscription nouvel utilisateur
   - POST /api/auth/login : connexion avec JWT en réponse
   - GET /api/auth/me : profil utilisateur connecté (@AuthenticationPrincipal)

4. ✅ **Password Encoding BCrypt**
   - BCryptPasswordEncoder avec strength 12
   - Hachage sécurisé des mots de passe
   - Bean unique dans ApplicationConfig

5. ✅ **Gestion des roles**
   - User.roles : Set<String> persisté avec @ElementCollection
   - Table de jointure user_roles créée automatiquement
   - Conversion correcte vers GrantedAuthority
   - Pas de double préfixe "ROLE_ROLE_"

6. ✅ **Corrections appliquées (auto-correction + feedback)**
   - Bug #1 : JwtAuthenticationFilter non activé dans SecurityConfig (CRITIQUE)
   - Bug #2 : User.roles sans annotations JPA @ElementCollection
   - Bug #3 : .roles() causant double préfixe → remplacé par .authorities()
   - Bug #4 : Endpoint /api/auth/me manquant
   - Bug #5 : Duplication bean PasswordEncoder (SecurityConfig + ApplicationConfig)
   - Bug #6 : Import UsernameNotFoundException manquant
   - Bug #7 : Imports inutilisés nettoyés

7. ✅ **Tests application**
   - Application Spring Boot démarrée avec succès
   - Aucune erreur de compilation (seulement 3 warnings @NonNull style)
   - Endpoints accessibles et fonctionnels
   - JWT tokens générés et validés correctement

**Score : 118/120 (98.3%)** ⭐⭐⭐⭐⭐

**Détails de notation :**
- Architecture (20/20) : Structure Spring Security professionnelle
- JWT Implementation (20/20) : JJWT 0.12.3 correctement configuré
- Filter Configuration (18/20) : Bug initial (filter non activé) mais corrigé
- User Entity (19/20) : Relations correctes après ajout @ElementCollection
- Authentication (20/20) : Endpoints login/register/me complets
- Password Encoding (20/20) : BCrypt strength 12 configuré
- Exception Handling (20/20) : GlobalExceptionHandler avec tous les cas
- Code Quality (19/20) : Code propre, corrections autonomes (6/7 bugs)

**Compétences acquises :**
- Configuration Spring Security avec SecurityFilterChain
- Implémentation JWT avec JJWT library
- Création de filtres personnalisés (OncePerRequestFilter)
- UserDetailsService et authentification personnalisée
- Gestion sécurisée des mots de passe (BCrypt)
- JPA @ElementCollection pour collections
- Debugging et auto-correction (6 bugs sur 7 corrigés seul)
- Architecture stateless avec JWT

**Points forts :**
- ✅ Architecture Spring Security complète et bien structurée
- ✅ JWT implémenté selon les bonnes pratiques
- ✅ Bonne séparation des responsabilités (config, service, filter)
- ✅ Configuration stateless appropriée pour API REST
- ✅ Gestion des exceptions professionnelle
- ✅ Auto-correction efficace (86% des bugs résolus seul)

**Prochaine étape : Exercice 2**
- Implémenter multi-roles (USER, MODERATOR, ADMIN)
- Ajouter @PreAuthorize sur endpoints
- Créer ProductController avec permissions par rôle

**🔍 Mise à jour de vérification (22 février 2026) :**

- ✅ Audit technique Exercice 2 effectué
- ✅ Correction appliquée : vérification owner dans `@PreAuthorize` via `@securityUtils.isCurrentUser(#id, authentication)`
- ✅ Exercice 2 validé techniquement avec tests RBAC (403/200) sur rôles USER/MODERATOR/ADMIN
- ✅ Configuration Security ajustée pour endpoints publics Exercice 3 (`/refresh`, `/verify/**`, `/resend-verification`)
- ✅ Exercice 3 implémenté : refresh token, email verification, logout, change password, rate limiting
- ✅ Tests Exercice 3 ajoutés et passants (`Ex3SecurityFlowTests` : scénarios refresh/verify/resend/login bloqué/change-password)
- ✅ Gestion d'erreur améliorée : `AccountBlockedException` retourne désormais HTTP 429 (au lieu de 500)

---

### Module 5 : SQL Avancé et Optimisation
**Durée estimée : 1 semaine**

- Requêtes complexes (JOIN, sous-requêtes, CTE)
- Index et performance
- Transactions et isolation
- Procédures stockées
- Triggers
- Analyse de plans d'exécution

**Mini-projet :** Optimisation d'une base existante

---

### Module 6 : Tests
**Durée estimée : 1 semaine**

- JUnit 5
- Mockito
- Tests d'intégration avec TestContainers
- Test de repositories
- Test de services
- Test d'APIs REST (MockMvc)

**Mini-projet :** Suite de tests complète

---

### Module 7 : Architecture et Design Patterns
**Durée estimée : 2 semaines**

- Clean Architecture
- Hexagonal Architecture
- SOLID principles
- Design Patterns (Strategy, Factory, Builder, etc.)
- DTOs et Mappers
- Exception handling patterns

**Mini-projet :** Refactoring d'une application

---

### Module 8 : Résilience et Performance
**Durée estimée : 1.5 semaine**

- Circuit Breaker (Resilience4j)
- Retry et Fallback
- Rate Limiting
- Caching (Redis, Caffeine)
- Async processing
- Monitoring avec Actuator

**Mini-projet :** API robuste et performante

---

### Module 9 : Microservices (Optionnel)
**Durée estimée : 2 semaines**

- Spring Cloud
- Service Discovery (Eureka)
- API Gateway
- Config Server
- Message Brokers (Kafka, RabbitMQ)
- Distributed Tracing

**Mini-projet :** Architecture microservices

---

## 📊 Progression globale

```
[████████████████████████████████████] 85%

✅ Module 1 : Java Fondamental (100%)
✅ Module 2 : JPA/Hibernate (100%) 🎉
✅ Module 3 : Spring Boot (100%) 🎉 ⭐⭐
   ✅ Exercice 1 : CRUD basique (85/100) ✓
   ✅ Exercice 2 : Validation avancée (98/100) ✓ ⭐
   ✅ Exercice 3 : Recherche et pagination (100/100) ✓ ⭐⭐
   ✅ Exercice 4 : Documentation Swagger/OpenAPI (95/100) ✓ ⭐⭐
   ✅ Mini-projet : API e-commerce complète (95/100) ✓ 🏆 ⭐⭐
🔄 Module 4 : Spring Security (90%)
   ✅ Cours théorique : JWT, BCrypt, Roles (100%) ✓
   ✅ Exercice 1 : JWT Authentication (118/120 - 98.3%) ✓ ⭐⭐
   ✅ Exercice 2 : Roles et permissions (validé techniquement avec tests RBAC) ✓ ⭐
   ✅ Exercice 3 : Refresh tokens et sécurité avancée (validé techniquement avec tests) ✓ ⭐
   📝 Mini-projet : Blog platform multi-roles (0%)
⏳ Module 5 : SQL Avancé (0%)
⏳ Module 6 : Tests (0%)
⏳ Module 7 : Architecture (0%)
⏳ Module 8 : Résilience (0%)
⏳ Module 9 : Microservices (0%)
```

---

## 🎯 Objectifs d'apprentissage

### Court terme (1 mois)
- ✅ Maîtriser Java Streams et Collections
- ✅ Comprendre JPA et les relations entre entités
- ✅ Créer des APIs REST avec Spring Boot (100%)
- ✅ Valider les données et gérer les exceptions professionnellement
- ✅ Implémenter pagination, recherche et statistiques
- ✅ Développer une API e-commerce complète avec relations complexes
- 🔄 Sécuriser les APIs avec Spring Security et JWT (90%)

### Moyen terme (3 mois)
- 🔄 Développer des APIs sécurisées et testées (en cours)
- Appliquer les bonnes pratiques d'architecture
- Optimiser les performances et la résilience

### Long terme (6 mois)
- Architecturer des applications backend complètes
- Comprendre et implémenter des microservices
- Être autonome sur un projet backend professionnel

---

## � Protocole de création des exercices

### ⚠️ RÈGLES STRICTES
**🎯 PRINCIPE FONDAMENTAL : PRIORISER LA PRATIQUE DE L'ÉTUDIANT**
   - ❌ NE JAMAIS compléter les exercices à la place de l'étudiant
   - ❌ NE JAMAIS écrire le code des solutions directement
   - ✅ TOUJOURS donner des conseils, pistes et explications
   - ✅ TOUJOURS attendre que l'étudiant code et teste avant d'intervenir
   - ✅ L'apprentissage se fait par la PRATIQUE, pas par la lecture de solutions
1. **Les exercices doivent contenir UNIQUEMENT les énoncés**
   - ❌ PAS de code de solution dans les fichiers d'exercices
   - ❌ PAS de correction visible directement
   - ✅ Uniquement l'énoncé, les consignes et les TODO
   - ✅ L'étudiant doit réfléchir et coder par lui-même

2. **Structure des fichiers d'exercices**
   ```java
   /**
    * EXERCICE X : [Titre]
    * 
    * Objectif : [Description claire]
    * 
    * Consignes :
    * 1. ...
    * 2. ...
    * 
    * TODO: Implémenter [...]
    */
   ```

3. **Les corrections - Approche par feedback**
   - ❌ PAS de fichiers de correction séparés
   - ✅ Feedback direct sur le code de l'étudiant
   - ✅ Mentionner les erreurs concrètes
   - ✅ Proposer des pistes d'amélioration
   - ✅ Expliquer les concepts mal compris
   - ⚠️ À faire APRÈS que l'étudiant ait codé et testé

4. **Format du feedback de correction**
   - Analyser le code écrit par l'étudiant
   - Identifier les erreurs (bugs, mauvaises pratiques)
   - Suggérer des améliorations (performance, lisibilité)
   - Féliciter les bonnes pratiques appliquées
   - Donner des explications pédagogiques
   - **Suggérer des pistes plutôt que corriger directement**
   - Documenter les changements dans PROGRESSION.md

5. **Approche pédagogique - Socratique**
   - Poser des questions qui guident la réflexion
   - Donner des indices progressifs (du plus général au plus précis)
   - Expliquer les concepts avant de donner la solution
   - Encourager l'expérimentation et les erreurs
   - Célébrer les réussites et les progrès
   - Chaque exercice doit permettre la réflexion
   - Difficulté progressive
   - Indices fournis sans donner la solution

---

## 💡 Conseils de progression

1. **Pratiquez quotidiennement**
   - Au moins 1h par jour
   - Alternez théorie et pratique

2. **Ne sautez pas les exercices**
   - Chaque exercice renforce un concept
   - ⚠️ **Essayez TOUJOURS avant de regarder les corrections**
   - Bloquez-vous au moins 30 minutes avant de consulter l'aide

3. **Posez des questions**
   - Si un concept n'est pas clair, demandez
   - Reformulez pour vérifier votre compréhension

4. **Construisez vos propres projets**
   - Appliquez les concepts sur vos idées
   - Créez un portfolio GitHub

5. **Révisez régulièrement**
   - Revoyez les modules précédents
   - Identifiez vos points faibles

---

## 📚 Ressources complémentaires

### Documentation officielle
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Java Documentation](https://docs.oracle.com/en/java/)
- [JPA Specification](https://jakarta.ee/specifications/persistence/)

### Livres recommandés
- "Effective Java" - Joshua Bloch
- "Clean Code" - Robert Martin
- "Spring in Action" - Craig Walls

### Pratique en ligne
- [Baeldung](https://www.baeldung.com)
- [LeetCode](https://leetcode.com) (algorithmes)
- [HackerRank](https://www.hackerrank.com) (Java)

---

## 🚀 Prochaine étape

**Action immédiate : Démarrer le mini-projet Spring Security 🔐**

1. ✅ Exercice 1 JWT Authentication complété (118/120) ⭐⭐
2. ✅ Exercice 2 Roles & Permissions validé techniquement (tests RBAC 403/200)
3. 🏗️ **Objectif** : lancer le mini-projet multi-rôles
4. 🔧 **Tâches principales** :
   - Créer les rôles USER/MODERATOR/ADMIN sur le mini-projet
   - Protéger les endpoints CRUD avec règles RBAC
   - Ajouter refresh token + logout + change-password
   - Ajouter tests MockMvc sur les scénarios critiques
5. 🧪 Exécuter tests et valider les scénarios de sécurité

**Durée estimée** : 2-4 heures

**Concepts à maîtriser :**
- Annotation @PreAuthorize
- Expression SpEL pour sécurité
- hasRole() vs hasAuthority()
- Hiérarchie des rôles
- Tests de permissions

**Commande pour me solliciter :**
- "Corrige mon code"
- "J'ai une erreur sur [partie]"
- "Comment implémenter [fonctionnalité] ?"
- "Explique-moi [concept]"

Bon courage ! 💪🎓

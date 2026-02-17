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
- 📝 `spring-boot/EXERCICE4.md` - Documentation Swagger/OpenAPI (bonus - optionnel)
- 📝 `spring-boot/MINI_PROJET_ECOMMERCE.md` - **EN COURS - Mini-projet API e-commerce**
- ✅ `spring-boot/demo/` - Projet Spring Boot avec Swagger opérationnel

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

📝 **Jour 7-10 : Mini-projet e-commerce** (EN COURS - 18 février)
   - 🛒 MINI_PROJET_ECOMMERCE.md créé
   - API complète avec 4 entités (Customer, Order, OrderItem, Product)
   - Relations JPA complexes (@OneToMany, @ManyToOne)
   - Logique métier avancée (gestion stock, calcul totaux, statuts)
   - 12 endpoints REST minimum
   - Statistiques e-commerce
   - **Action** : Ouvre MINI_PROJET_ECOMMERCE.md et commence l'étape 1

---

## 📅 Modules à venir

### Module 4 : Spring Security
**Durée estimée : 1.5 semaine**

- Authentification et autorisation
- JWT (JSON Web Tokens)
- Password encoding (BCrypt)
- Roles et permissions
- OAuth2 et OpenID Connect
- CORS et CSRF

**Mini-projet :** Système d'authentification complet

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
[████████████████████████████████] 75%

✅ Module 1 : Java Fondamental (100%)
✅ Module 2 : JPA/Hibernate (100%) 🎉
🔄 Module 3 : Spring Boot (75% → 90% en cours)
   ✅ Exercice 1 : CRUD basique (85/100) ✓
   ✅ Exercice 2 : Validation avancée (98/100) ✓ ⭐
   ✅ Exercice 3 : Recherche et pagination (100/100) ✓ ⭐⭐
   🔄 Mini-projet : API e-commerce complète (0/100 → EN COURS)
   ✅ Bonus : Swagger UI opérationnel
⏳ Module 4 : Spring Security (0%)
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
- 🔄 Créer des APIs REST avec Spring Boot (en cours - 75%)
- ✅ Valider les données et gérer les exceptions professionnellement
- ✅ Implémenter pagination, recherche et statistiques
- 🔄 Développer une API e-commerce complète avec relations complexes (EN COURS)

### Moyen terme (3 mois)
- Développer des APIs sécurisées et testées
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

**Action immédiate : Mini-projet e-commerce 🛒**

1. ✅ Swagger fonctionne : http://localhost:8080/swagger-ui/index.html
2. 📖 **Ouvre `spring-boot/MINI_PROJET_ECOMMERCE.md`**
3. 🏗️ **Commence l'Étape 1** : Créer les 4 entités (Customer, Order, OrderItem, OrderStatus)
4. 📝 Suis les TODO dans l'ordre
5. 🧪 Teste avec Swagger UI au fur et à mesure

**Objectif** : API e-commerce complète avec :
- Gestion des clients
- Création de commandes avec plusieurs produits
- Gestion des stocks automatique
- Calcul des totaux
- Statistiques de vente

**Durée estimée** : 4-6 heures

**Commande pour me solliciter :**
- "Corrige mon code"
- "J'ai une erreur sur [partie]"
- "Comment implémenter [fonctionnalité] ?"
- "Explique-moi [concept]"

Bon courage ! 💪🎓

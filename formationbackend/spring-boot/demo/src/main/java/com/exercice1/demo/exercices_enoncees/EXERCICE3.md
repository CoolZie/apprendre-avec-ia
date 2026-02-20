# 📝 EXERCICE 3 : RECHERCHE, PAGINATION ET FILTRAGE

## 🎯 Objectif

Implémenter des fonctionnalités de recherche avancées avec pagination et filtrage

**Durée estimée :** 2-3 heures  
**Difficulté :** ⭐⭐⭐☆☆

---

## 📖 Contexte

L'API a besoin de fonctionnalités de recherche, filtrage et pagination pour gérer efficacement un grand nombre de produits. Actuellement, `GET /api/products` retourne tous les produits, ce qui n'est pas performant avec des milliers d'enregistrements.

**Prérequis :** Avoir terminé les exercices 1 et 2

---

## 🛠️ PARTIE 1 : PAGINATION

### 1. TODO : Créer PagedResponse<T>

📁 **Fichier :** `src/main/java/com/example/demo/dto/PagedResponse.java`

**Objectif :** Encapsuler les résultats paginés avec les métadonnées de pagination.

**Champs requis :**
- `content` : List<T> - Les données de la page
- `pageNumber` : int - Numéro de la page actuelle (0-based)
- `pageSize` : int - Taille de la page
- `totalElements` : long - Nombre total d'éléments
- `totalPages` : int - Nombre total de pages
- `isFirst` : boolean - Est-ce la première page ?
- `isLast` : boolean - Est-ce la dernière page ?
- `hasNext` : boolean - Y a-t-il une page suivante ?
- `hasPrevious` : boolean - Y a-t-il une page précédente ?

**Consignes :**
- Utiliser `@Data` et `@AllArgsConstructor`
- Créer un constructeur qui accepte un objet `Page<T>` de Spring Data
- Ce constructeur doit extraire toutes les informations du `Page<T>` pour remplir les champs

💡 **Astuce :** L'objet `Page<T>` de Spring Data contient déjà toutes ces informations

---

### 2. TODO : Modifier getAllProducts pour supporter la pagination

📁 **Fichier :** `src/main/java/com/example/demo/service/ProductService.java`

**Nouvelle signature :**
```java
public PagedResponse<ProductResponse> getAllProducts(
    int page, int size, String sortBy, String direction)
```

**Consignes :**
1. Créer un objet `Sort` en fonction de `sortBy` et `direction`
   - Si direction = "DESC", utiliser `Sort.by(sortBy).descending()`
   - Sinon, utiliser `Sort.by(sortBy).ascending()`
2. Créer un objet `Pageable` avec `PageRequest.of(page, size, sort)`
3. Appeler `productRepository.findAll(pageable)`
4. Convertir le `Page<Product>` en `Page<ProductResponse>` avec `.map()`
5. Créer et retourner un `PagedResponse<ProductResponse>`

💡 **Réfléchissez :** Comment convertir un Page<Product> en Page<ProductResponse> ?

---

### 3. TODO : Mettre à jour le ProductController

📁 **Fichier :** `src/main/java/com/example/demo/controller/ProductController.java`

**Modifier l'endpoint GET /api/products :**

**Paramètres de requête :**
- `page` : int (défaut: 0)
- `size` : int (défaut: 10)
- `sortBy` : String (défaut: "name")
- `direction` : String (défaut: "ASC")

**Consignes :**
- Utiliser `@RequestParam(defaultValue = "0")` pour chaque paramètre
- Appeler la nouvelle version de `getAllProducts()`
- Retourner `ResponseEntity<PagedResponse<ProductResponse>>`

---

## 🛠️ PARTIE 2 : RECHERCHE ET FILTRAGE

### 1. TODO : Ajouter des méthodes au ProductRepository

📁 **Fichier :** `src/main/java/com/example/demo/repository/ProductRepository.java`

**Méthodes à ajouter :**

#### A. Recherche par keyword

```java
Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
    String name, String description, Pageable pageable);
```

💡 Spring Data JPA génère automatiquement l'implémentation !

---

#### B. Filtrage par catégorie

```java
Page<Product> findByCategory(Category category, Pageable pageable);
```

---

#### C. Filtrage par fourchette de prix

```java
Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);
```

---

#### D. Produits en stock faible

```java
Page<Product> findByStockLessThan(Integer threshold, Pageable pageable);
```

---

#### E. Filtrage combiné avec @Query

Créer une requête JPQL qui permet de filtrer par plusieurs critères optionnels :

```java
@Query("SELECT p FROM Product p WHERE " +
       "(:category IS NULL OR p.category = :category) AND " +
       "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
       "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
       "(:minStock IS NULL OR p.stock >= :minStock) AND " +
       "(:maxStock IS NULL OR p.stock <= :maxStock) AND " +
       "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
       "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
Page<Product> findByFilters(
    @Param("category") Category category,
    @Param("minPrice") Double minPrice,
    @Param("maxPrice") Double maxPrice,
    @Param("minStock") Integer minStock,
    @Param("maxStock") Integer maxStock,
    @Param("keyword") String keyword,
    Pageable pageable
);
```

💡 **Réfléchissez :** Pourquoi utiliser `IS NULL OR` ? Comment gérer les paramètres optionnels ?

---

#### F. Requêtes pour statistiques

```java
@Query("SELECT COUNT(p) FROM Product p GROUP BY p.category")
List<Object[]> countByCategory();

@Query("SELECT p.category, COUNT(p), AVG(p.price), SUM(p.price * p.stock) " +
       "FROM Product p GROUP BY p.category")
List<Object[]> getStatisticsByCategory();

long countByStockLessThan(Integer threshold);

long countByStock(Integer stock);

@Query("SELECT SUM(p.price * p.stock) FROM Product p")
Double calculateTotalStockValue();

@Query("SELECT AVG(p.price) FROM Product p")
Double calculateAveragePrice();
```

---

## 🛠️ PARTIE 3 : DTOS POUR STATISTIQUES

### 1. TODO : Créer ProductStatistics

📁 **Fichier :** `src/main/java/com/example/demo/dto/ProductStatistics.java`

**Champs :**
- `totalProducts` : long
- `totalValue` : double (valeur totale du stock = somme de price * stock)
- `averagePrice` : double
- `categoryCounts` : Map<String, Long> (nombre de produits par catégorie)
- `lowStockCount` : long (produits avec stock < 10)
- `outOfStockCount` : long (produits avec stock = 0)

**Consignes :**
- Utiliser `@Data` et `@Builder`

---

### 2. TODO : Créer CategoryStatistics

📁 **Fichier :** `src/main/java/com/example/demo/dto/CategoryStatistics.java`

**Champs :**
- `category` : String
- `productCount` : long
- `averagePrice` : double
- `totalValue` : double

**Consignes :**
- Utiliser `@Data` et `@Builder`

---

## 🛠️ PARTIE 4 : ENRICHIR LE SERVICE

📁 **Fichier :** `src/main/java/com/example/demo/service/ProductService.java`

### TODO : Créer les méthodes suivantes

#### 1. searchProducts

```java
public PagedResponse<ProductResponse> searchProducts(
    String keyword, int page, int size, String sortBy, String direction)
```

**Consignes :**
- Utiliser `findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase()`
- Passer le keyword deux fois (pour name et description)
- Retourner un PagedResponse

---

#### 2. getProductsByCategory

```java
public PagedResponse<ProductResponse> getProductsByCategory(
    String category, int page, int size, String sortBy, String direction)
```

**Consignes :**
- Convertir la String category en enum : `Category.valueOf(category.toUpperCase())`
- Utiliser `findByCategory()`

---

#### 3. getProductsByPriceRange

```java
public PagedResponse<ProductResponse> getProductsByPriceRange(
    Double min, Double max, int page, int size, String sortBy, String direction)
```

---

#### 4. getLowStockProducts

```java
public PagedResponse<ProductResponse> getLowStockProducts(
    Integer threshold, int page, int size, String sortBy, String direction)
```

---

#### 5. filterProducts (filtrage combiné)

```java
public PagedResponse<ProductResponse> filterProducts(
    String category, Double minPrice, Double maxPrice,
    Integer minStock, Integer maxStock, String keyword,
    int page, int size, String sortBy, String direction)
```

**Consignes :**
- Convertir category en enum (ou null si category est null)
- Utiliser `findByFilters()` du repository

---

#### 6. getStatistics

```java
public ProductStatistics getStatistics()
```

**Consignes :**
1. Récupérer le nombre total de produits avec `productRepository.count()`
2. Calculer la valeur totale avec `calculateTotalStockValue()`
3. Calculer le prix moyen avec `calculateAveragePrice()`
4. Récupérer les comptes par catégorie avec `countByCategory()` et construire la Map
5. Compter les produits en stock faible avec `countByStockLessThan(10)`
6. Compter les produits en rupture avec `countByStock(0)`
7. Créer et retourner un ProductStatistics

💡 **Astuce :** Gérer les valeurs null (si aucun produit, totalValue et averagePrice peuvent être null)

---

#### 7. getStatisticsByCategory

```java
public List<CategoryStatistics> getStatisticsByCategory()
```

**Consignes :**
- Appeler `getStatisticsByCategory()` du repository
- Pour chaque Object[], extraire les valeurs et créer un CategoryStatistics
- Retourner la liste

💡 **Astuce :** `Object[]` contient : [Category, Long, Double, Double]

---

## 🛠️ PARTIE 5 : ENRICHIR LE CONTROLLER

📁 **Fichier :** `src/main/java/com/example/demo/controller/ProductController.java`

### TODO : Ajouter les endpoints suivants

#### 1. GET /api/products/search

**Paramètres :**
- `keyword` : String (obligatoire)
- `page`, `size`, `sortBy`, `direction` : comme avant

**Consignes :**
- Utiliser `@GetMapping("/search")`
- `@RequestParam String keyword`

---

#### 2. GET /api/products/category/{category}

**Paramètres :**
- `category` : String (path variable)
- Pagination standard

**Consignes :**
- Utiliser `@GetMapping("/category/{category}")`
- `@PathVariable String category`

---

#### 3. GET /api/products/price-range

**Paramètres :**
- `min` : Double
- `max` : Double
- Pagination standard

---

#### 4. GET /api/products/low-stock

**Paramètres :**
- `threshold` : Integer (défaut: 10)
- Pagination standard

---

#### 5. GET /api/products/filter

**Paramètres :** (tous optionnels sauf pagination)
- `category` : String (optionnel)
- `minPrice` : Double (optionnel)
- `maxPrice` : Double (optionnel)
- `minStock` : Integer (optionnel)
- `maxStock` : Integer (optionnel)
- `keyword` : String (optionnel)
- Pagination standard

**Consignes :**
- Utiliser `@RequestParam(required = false)` pour les paramètres optionnels

---

#### 6. GET /api/products/statistics

**Retour :** `ProductStatistics`

---

#### 7. GET /api/products/statistics/by-category

**Retour :** `List<CategoryStatistics>`

---

## 🧪 Tests à effectuer

### 1. Pagination basique

```http
GET http://localhost:8080/api/products?page=0&size=5
```

**Attendu :** 5 produits max avec métadonnées de pagination

---

### 2. Tri

```http
GET http://localhost:8080/api/products?sortBy=price&direction=DESC
```

**Attendu :** Produits triés par prix décroissant

---

### 3. Recherche

```http
GET http://localhost:8080/api/products/search?keyword=laptop
```

**Attendu :** Produits contenant "laptop" dans nom ou description

---

### 4. Filtrage par catégorie

```http
GET http://localhost:8080/api/products/category/ELECTRONICS
```

**Attendu :** Seulement les produits électroniques

---

### 5. Filtrage par prix

```http
GET http://localhost:8080/api/products/price-range?min=100&max=500
```

**Attendu :** Produits entre 100 et 500

---

### 6. Produits en stock faible

```http
GET http://localhost:8080/api/products/low-stock?threshold=10
```

**Attendu :** Produits avec stock < 10

---

### 7. Filtrage combiné

```http
GET http://localhost:8080/api/products/filter?category=ELECTRONICS&minPrice=500&keyword=gaming
```

**Attendu :** Produits électroniques > 500€ contenant "gaming"

---

### 8. Statistiques

```http
GET http://localhost:8080/api/products/statistics
```

**Attendu :** Statistiques globales

---

### 9. Statistiques par catégorie

```http
GET http://localhost:8080/api/products/statistics/by-category
```

**Attendu :** Liste des statistiques par catégorie

---

## 📊 Exemples de réponses attendues

### PagedResponse

```json
{
  "content": [
    { "id": 1, "name": "Product 1", ... },
    { "id": 2, "name": "Product 2", ... }
  ],
  "pageNumber": 0,
  "pageSize": 5,
  "totalElements": 23,
  "totalPages": 5,
  "isFirst": true,
  "isLast": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### ProductStatistics

```json
{
  "totalProducts": 150,
  "totalValue": 125000.50,
  "averagePrice": 250.75,
  "categoryCounts": {
    "ELECTRONICS": 50,
    "CLOTHING": 30,
    "FOOD": 40,
    "BOOKS": 20,
    "OTHER": 10
  },
  "lowStockCount": 12,
  "outOfStockCount": 3
}
```

---

## 📁 Structure attendue

```
src/main/java/com/example/demo/
├── dto/
│   ├── PagedResponse.java (nouveau)
│   ├── ProductStatistics.java (nouveau)
│   └── CategoryStatistics.java (nouveau)
├── repository/
│   └── ProductRepository.java (enrichi avec nombreuses méthodes)
├── service/
│   └── ProductService.java (enrichi avec recherche, filtrage, stats)
└── controller/
    └── ProductController.java (enrichi avec nouveaux endpoints)
```

---

## 📊 Barème d'auto-évaluation (100 points)

- PagedResponse bien conçu : **10 points**
- Pagination fonctionnelle sur getAllProducts : **10 points**
- Recherche par keyword : **10 points**
- Filtrage par catégorie : **10 points**
- Filtrage par prix : **10 points**
- Produits en stock faible : **5 points**
- Filtrage combiné : **15 points**
- Statistiques globales : **15 points**
- Statistiques par catégorie : **15 points**

---

## 🌟 Bonus (optionnel)

- [ ] Tri multi-critères (ex: trier par catégorie puis par prix)
- [ ] Endpoint d'export CSV des produits
- [ ] Cache avec `@Cacheable` sur les statistiques
- [ ] Utiliser Spring Data Specifications pour le filtrage dynamique

---

## 💡 Conseils

- ✅ **Testez avec des données volumineuses** (créez un `data.sql` avec 100+ produits)
- ✅ Vérifiez les performances des requêtes avec `show-sql=true`
- ✅ `PageRequest.of(page, size, Sort.by(...))` est votre ami
- ✅ Les paramètres optionnels doivent être `required = false`
- ✅ Gérez les valeurs null dans les statistiques

---

## ⏱️ Temps recommandé : 2-3 heures

**⚠️ Important :** Essayez de résoudre l'exercice par vous-même pendant au moins 1 heure avant de consulter la correction ou demander de l'aide.

---

## 📚 Ressources

- [Spring Data JPA Pagination](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
- [JPQL Queries](https://www.baeldung.com/spring-data-jpa-query)
- [Query Methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
- Cours : `COURS_SPRING_BOOT.md`

---

## 🆘 Besoin d'aide ?

Si vous êtes bloqué après avoir essayé :
1. Vérifiez les noms de méthodes du repository (Spring Data est sensible aux noms)
2. Testez chaque endpoint séparément avec Postman
3. Consultez les logs SQL pour comprendre les requêtes générées
4. Demandez de l'aide en précisant où vous êtes bloqué

**BON COURAGE ! 🚀**

# 📝 EXERCICE 1 : API REST BASIQUE - CRUD PRODUITS

## 🎯 Objectif

Créer votre première API REST avec Spring Boot pour gérer un catalogue de produits.

**Durée estimée :** 2-3 heures  
**Difficulté :** ⭐⭐☆☆☆

---

## 📖 Contexte

Vous devez créer une API pour gérer un catalogue de produits. L'API doit permettre de créer, lire, mettre à jour et supprimer des produits (opérations CRUD).

---

## 🛠️ Étapes à suivre

### 1. Créer un nouveau projet Spring Boot

1. **Allez sur** https://start.spring.io/
2. **Configuration :**
   - Project: **Maven**
   - Language: **Java**
   - Spring Boot: **3.2.x** (dernière stable)
   - Group: `com.example`
   - Artifact: `demo`
   - Java: **17** ou **21**

3. **Dépendances à ajouter :**
   - ✅ Spring Web
   - ✅ Spring Data JPA
   - ✅ H2 Database
   - ✅ Lombok
   - ✅ Validation

4. **Téléchargez et décompressez** le projet

---

### 2. TODO : Créer l'entité Product

📁 **Fichier :** `src/main/java/com/example/demo/model/Product.java`

**Champs requis :**
- `id` : Long, auto-généré
- `name` : String, obligatoire en base de données
- `description` : String (limite 1000 caractères)
- `price` : Double, obligatoire
- `stock` : Integer, obligatoire
- `category` : Enum (ELECTRONICS, CLOTHING, FOOD, BOOKS, OTHER)
- `createdAt` : LocalDateTime
- `updatedAt` : LocalDateTime

**Consignes :**
- Utiliser les annotations JPA appropriées (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, etc.)
- Utiliser Lombok (`@Data`, `@NoArgsConstructor`)
- La catégorie doit être stockée comme `String` en base (`@Enumerated(EnumType.STRING)`)
- Implémenter les méthodes `@PrePersist` et `@PreUpdate` pour gérer automatiquement les timestamps

💡 **Réfléchissez :** Comment mapper une classe Java vers une table SQL ? Quelles annotations utiliser ?

---

### 3. TODO : Créer l'enum Category

📁 **Fichier :** `src/main/java/com/example/demo/model/Category.java`

**Valeurs de l'enum :**
- ELECTRONICS
- CLOTHING
- FOOD
- BOOKS
- OTHER

---

### 4. TODO : Créer le ProductRepository

📁 **Fichier :** `src/main/java/com/example/demo/repository/ProductRepository.java`

**Consignes :**
- Créer une interface qui étend `JpaRepository<Product, Long>`
- Annoter avec `@Repository`
- Aucune méthode supplémentaire n'est nécessaire pour le moment

💡 **Réfléchissez :** Pourquoi Spring Data JPA ne nécessite pas d'implémentation ?

---

### 5. TODO : Créer les DTOs

#### A. ProductRequest (pour créer/modifier un produit)

📁 **Fichier :** `src/main/java/com/example/demo/dto/ProductRequest.java`

**Champs :**
- `name` : String
- `description` : String
- `price` : Double
- `stock` : Integer
- `category` : String

**Validations requises :**
- `name` : obligatoire, entre 3 et 100 caractères
- `description` : optionnel, maximum 1000 caractères
- `price` : obligatoire, doit être positif
- `stock` : obligatoire, minimum 0
- `category` : obligatoire

💡 **Indices :** Utilisez les annotations de validation : `@NotBlank`, `@Size`, `@NotNull`, `@Positive`, `@Min`

---

#### B. ProductResponse (pour retourner un produit)

📁 **Fichier :** `src/main/java/com/example/demo/dto/ProductResponse.java`

**Champs :**
- `id` : Long
- `name` : String
- `description` : String
- `price` : Double
- `stock` : Integer
- `category` : String (nom de l'enum en String)
- `createdAt` : LocalDateTime
- `updatedAt` : LocalDateTime

**Consignes :**
- Créer un constructeur qui prend un objet `Product` en paramètre et mappe tous les champs
- Utiliser Lombok `@Data`

💡 **Réfléchissez :** Pourquoi séparer les DTOs (Request/Response) de l'entité ?

---

### 6. TODO : Créer le ProductService

📁 **Fichier :** `src/main/java/com/example/demo/service/ProductService.java`

**Méthodes à implémenter :**

1. `getAllProducts()` → `List<ProductResponse>`
2. `getProductById(Long id)` → `ProductResponse`
3. `createProduct(ProductRequest request)` → `ProductResponse`
4. `updateProduct(Long id, ProductRequest request)` → `ProductResponse`
5. `deleteProduct(Long id)` → `void`

**Consignes :**
- Annoter la classe avec `@Service` et `@Transactional`
- Utiliser `@RequiredArgsConstructor` pour l'injection du `ProductRepository`
- Les méthodes de lecture doivent être annotées `@Transactional(readOnly = true)`
- En cas de produit non trouvé, lever une `ResourceNotFoundException`
- Pour `createProduct` et `updateProduct`, convertir la String category en Enum avec `Category.valueOf()`

💡 **Réfléchissez :** 
- Comment gérer la conversion ProductRequest → Product ?
- Comment gérer la conversion Product → ProductResponse ?
- Que faire si l'ID n'existe pas ?

---

### 7. TODO : Créer le ProductController

📁 **Fichier :** `src/main/java/com/example/demo/controller/ProductController.java`

**Endpoints à créer :**

| Méthode HTTP | URL | Action | Code retour |
|--------------|-----|--------|-------------|
| GET | `/api/products` | Récupérer tous les produits | 200 OK |
| GET | `/api/products/{id}` | Récupérer un produit | 200 OK |
| POST | `/api/products` | Créer un produit | 201 CREATED |
| PUT | `/api/products/{id}` | Mettre à jour un produit | 200 OK |
| DELETE | `/api/products/{id}` | Supprimer un produit | 204 NO CONTENT |

**Consignes :**
- Utiliser `@RestController` et `@RequestMapping("/api/products")`
- Injecter le `ProductService` avec `@RequiredArgsConstructor`
- Pour POST et PUT, valider le body avec `@Valid`
- Retourner des `ResponseEntity<T>` avec les bons codes HTTP

💡 **Réfléchissez :** 
- Quelle annotation pour chaque type de requête HTTP ?
- Comment extraire l'ID de l'URL ?
- Comment valider automatiquement les données ?

---

### 8. TODO : Créer les exceptions personnalisées

#### A. ResourceNotFoundException

📁 **Fichier :** `src/main/java/com/example/demo/exception/ResourceNotFoundException.java`

**Consignes :**
- Créer une exception qui étend `RuntimeException`
- Ajouter un constructeur qui prend un message

---

#### B. ErrorResponse (DTO pour les erreurs)

📁 **Fichier :** `src/main/java/com/example/demo/exception/ErrorResponse.java`

**Champs :**
- `timestamp` : LocalDateTime
- `status` : int
- `error` : String
- `message` : String
- `path` : String (optionnel)
- `details` : Map<String, String> (optionnel, pour les erreurs de validation)

**Consignes :**
- Utiliser `@Data` et `@Builder`

---

#### C. GlobalExceptionHandler

📁 **Fichier :** `src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`

**Méthodes à implémenter :**

1. **Gérer les erreurs de validation** (`MethodArgumentNotValidException`)
   - Retourner 400 BAD REQUEST
   - Extraire tous les messages d'erreur dans un Map<String, String>
   - Créer un ErrorResponse avec tous les détails

2. **Gérer les ressources non trouvées** (`ResourceNotFoundException`)
   - Retourner 404 NOT FOUND
   - Créer un ErrorResponse avec le message

**Consignes :**
- Annoter avec `@RestControllerAdvice`
- Utiliser `@ExceptionHandler` pour chaque type d'exception

💡 **Réfléchissez :** Comment centraliser la gestion des erreurs dans une API REST ?

---

### 9. TODO : Configurer application.properties

📁 **Fichier :** `src/main/resources/application.properties`

**Configuration à ajouter :**

```properties
# TODO: Configurer le port du serveur (8080)

# TODO: Configurer H2 en mémoire (jdbc:h2:mem:productdb)

# TODO: Configurer JPA (dialect H2, ddl-auto=update, show-sql=true)

# TODO: Activer la console H2 (/h2-console)

# TODO: Configurer les logs (DEBUG pour com.example.demo)
```

💡 **Indices :** Consultez le cours COURS_SPRING_BOOT.md section "Configuration"

---

## ✅ Critères de validation

Votre application doit :

- [ ] Démarrer sans erreur sur le port 8080
- [ ] Exposer tous les endpoints CRUD
- [ ] Retourner les bons codes de statut HTTP (200, 201, 204, 400, 404)
- [ ] Persister les données en base H2
- [ ] Valider les données reçues (nom obligatoire, prix positif, etc.)
- [ ] Retourner des messages d'erreur clairs en cas de problème

---

## 🧪 Tests à effectuer (avec Postman ou cURL)

### 1. Créer un produit

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Dell XPS",
  "description": "Laptop professionnel haute performance",
  "price": 1299.99,
  "stock": 15,
  "category": "ELECTRONICS"
}
```

**Attendu :** 201 Created avec le produit créé

### 2. Récupérer tous les produits

```http
GET http://localhost:8080/api/products
```

**Attendu :** 200 OK avec liste de produits

### 3. Récupérer un produit par ID

```http
GET http://localhost:8080/api/products/1
```

**Attendu :** 200 OK avec le produit

### 4. Mettre à jour un produit

```http
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Dell XPS 15",
  "description": "Laptop professionnel mis à jour",
  "price": 1499.99,
  "stock": 12,
  "category": "ELECTRONICS"
}
```

**Attendu :** 200 OK avec produit mis à jour

### 5. Supprimer un produit

```http
DELETE http://localhost:8080/api/products/1
```

**Attendu :** 204 No Content

### 6. Tester la validation (données invalides)

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "",
  "price": -10,
  "stock": -5
}
```

**Attendu :** 400 Bad Request avec messages d'erreur de validation

### 7. Tester une ressource inexistante

```http
GET http://localhost:8080/api/products/999
```

**Attendu :** 404 Not Found avec message d'erreur

---

## 🌟 Bonus (optionnel)

Si vous terminez avant le temps imparti :

- [ ] Ajouter un endpoint `GET /api/products/search?name={keyword}` pour rechercher par nom
- [ ] Ajouter un endpoint `GET /api/products/category/{category}` pour filtrer par catégorie
- [ ] Créer un fichier `data.sql` pour insérer des données initiales au démarrage
- [ ] Ajouter des logs avec `@Slf4j` dans le service

---

## 📁 Structure attendue du projet

```
src/main/java/com/example/demo/
├── DemoApplication.java
├── model/
│   ├── Product.java
│   └── Category.java
├── dto/
│   ├── ProductRequest.java
│   └── ProductResponse.java
├── repository/
│   └── ProductRepository.java
├── service/
│   └── ProductService.java
├── controller/
│   └── ProductController.java
└── exception/
    ├── ResourceNotFoundException.java
    ├── GlobalExceptionHandler.java
    └── ErrorResponse.java

src/main/resources/
├── application.properties
└── data.sql (optionnel)
```

---

## 💡 Conseils

- ✅ **Procédez étape par étape** : entité → repository → service → controller
- ✅ **Testez après chaque étape** avant de passer à la suivante
- ✅ **Utilisez la console H2** pour vérifier les données : http://localhost:8080/h2-console
- ✅ **Consultez le cours** `COURS_SPRING_BOOT.md` en cas de doute
- ✅ **Utilisez Lombok** pour réduire le code (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`)
- ✅ **Pensez aux imports** nécessaires pour chaque annotation

---

## 📊 Barème d'auto-évaluation (100 points)

- Entité Product correcte avec toutes les annotations : **15 points**
- Repository fonctionnel : **10 points**
- DTOs bien conçus avec validations : **15 points**
- Service complet avec toutes les méthodes : **20 points**
- Controller avec tous les endpoints : **20 points**
- Gestion des erreurs (GlobalExceptionHandler) : **10 points**
- Validation fonctionnelle : **10 points**
- **Bonus : +10 points max**

---

## ⏱️ Temps recommandé : 2-3 heures

**⚠️ Important :** Essayez de résoudre l'exercice par vous-même pendant au moins 1 heure avant de consulter la correction ou demander de l'aide.

---

## 📚 Ressources

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [Guide Spring Data JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)
- Cours : `COURS_SPRING_BOOT.md`

---

## 🆘 Besoin d'aide ?

Si vous êtes bloqué après avoir essayé :
1. Relisez le cours `COURS_SPRING_BOOT.md`
2. Vérifiez vos annotations et imports
3. Consultez les logs d'erreur
4. Demandez de l'aide en précisant où vous êtes bloqué

**BON COURAGE ! 🚀**

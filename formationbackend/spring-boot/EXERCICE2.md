# 📝 EXERCICE 2 : VALIDATION ET GESTION D'ERREURS AVANCÉE

## 🎯 Objectif

Maîtriser la validation des données et la gestion professionnelle des exceptions

**Durée estimée :** 2-3 heures  
**Difficulté :** ⭐⭐⭐☆☆

---

## 📖 Contexte

Vous devez améliorer l'API de l'exercice 1 en ajoutant une validation robuste et une gestion d'erreurs professionnelle. Les validations de base ne suffisent pas toujours : vous devez créer des validations personnalisées et gérer tous les cas d'erreur possibles.

**Prérequis :** Avoir terminé l'exercice 1

---

## 🛠️ PARTIE 1 : VALIDATION PERSONNALISÉE

### 1. TODO : Créer une validation personnalisée @ValidPrice

📁 **Fichiers à créer :**
- `src/main/java/com/example/demo/validation/ValidPrice.java`
- `src/main/java/com/example/demo/validation/PriceValidator.java`

**Objectif :** Créer une annotation `@ValidPrice` qui valide qu'un prix est entre 0.01 et 100000.

**Consignes pour ValidPrice.java :**
- Créer une annotation avec `@Target({ElementType.FIELD})`
- `@Retention(RetentionPolicy.RUNTIME)`
- `@Constraint(validatedBy = PriceValidator.class)`
- Message par défaut : "Le prix doit être entre 0.01 et 100000"

**Consignes pour PriceValidator.java :**
- Implémenter `ConstraintValidator<ValidPrice, Double>`
- La méthode `isValid()` doit :
  - Retourner `true` si price est null (laissez `@NotNull` gérer les valeurs null)
  - Retourner `true` si price >= 0.01 ET price <= 100000
  - Retourner `false` sinon

💡 **Réfléchissez :** Comment créer une annotation personnalisée en Java ? Pourquoi séparer l'annotation et le validateur ?

---

### 2. TODO : Créer une validation personnalisée @ValidCategory

📁 **Fichiers à créer :**
- `src/main/java/com/example/demo/validation/ValidCategory.java`
- `src/main/java/com/example/demo/validation/CategoryValidator.java`

**Objectif :** Créer une annotation `@ValidCategory` qui vérifie que la String correspond à une valeur de l'enum Category.

**Consignes pour ValidCategory.java :**
- Même structure que `@ValidPrice`
- Message par défaut : "Catégorie invalide. Valeurs acceptées : ELECTRONICS, CLOTHING, FOOD, BOOKS, OTHER"

**Consignes pour CategoryValidator.java :**
- Implémenter `ConstraintValidator<ValidCategory, String>`
- La méthode `isValid()` doit :
  - Retourner `true` si category est null
  - Essayer de convertir la String en enum avec `Category.valueOf(category.toUpperCase())`
  - Si ça réussit, retourner `true`
  - Si `IllegalArgumentException` est levée, retourner `false`

💡 **Astuce :** Utilisez un try-catch pour gérer la conversion d'enum

---

### 3. TODO : Améliorer ProductRequest

📁 **Fichier :** `src/main/java/com/example/demo/dto/ProductRequest.java`

**Consignes :** Ajouter les validations suivantes :

- `name` :
  - Ajouter `@Pattern` pour n'autoriser que lettres, chiffres, espaces et tirets
  - Pattern : `^[a-zA-Z0-9\\s-]+$`
  - Message : "Le nom ne peut contenir que des lettres, chiffres, espaces et tirets"

- `price` :
  - Remplacer `@Positive` par votre annotation personnalisée `@ValidPrice`

- `stock` :
  - Ajouter `@Max(value = 10000)` avec message "Le stock ne peut dépasser 10000"

- `category` :
  - Remplacer par votre annotation personnalisée `@ValidCategory`

💡 **Réfléchissez :** Pourquoi créer des validations personnalisées au lieu d'utiliser seulement les annotations standard ?

---

### 4. TODO : Créer UpdateStockRequest

📁 **Fichier :** `src/main/java/com/example/demo/dto/UpdateStockRequest.java`

**Champs :**
- `quantity` : Integer

**Validations :**
- `@NotNull` avec message "La quantité est obligatoire"
- `@Min(value = -10000)` avec message "La quantité minimale est -10000"
- `@Max(value = 10000)` avec message "La quantité maximale est 10000"

**Objectif :** Ce DTO permettra d'ajouter ou retirer du stock (quantité négative pour retirer)

---

## 🛠️ PARTIE 2 : EXCEPTIONS PERSONNALISÉES

### 1. TODO : InvalidDataException

📁 **Fichier :** `src/main/java/com/example/demo/exception/InvalidDataException.java`

**Consignes :**
- Créer une exception qui étend `RuntimeException`
- Ajouter un constructeur qui prend un String message

**Objectif :** Pour les erreurs liées aux données métier (ex: stock insuffisant)

---

### 2. TODO : DuplicateResourceException

📁 **Fichier :** `src/main/java/com/example/demo/exception/DuplicateResourceException.java`

**Consignes :**
- Même structure que `InvalidDataException`
- Sera utilisée pour les doublons (ex: nom de produit déjà existant)

---

## 🛠️ PARTIE 3 : AMÉLIORER LE GLOBAL EXCEPTION HANDLER

📁 **Fichier :** `src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`

**Méthodes supplémentaires à ajouter :**

### 1. TODO : Gérer InvalidDataException

**Consignes :**
- Annoter avec `@ExceptionHandler(InvalidDataException.class)`
- Retourner un `ResponseEntity<ErrorResponse>`
- Code HTTP : 400 BAD REQUEST
- Remplir ErrorResponse avec :
  - timestamp : `LocalDateTime.now()`
  - status : 400
  - error : "Bad Request"
  - message : le message de l'exception
  - path : extraire du WebRequest

---

### 2. TODO : Gérer DuplicateResourceException

**Consignes :**
- Annoter avec `@ExceptionHandler(DuplicateResourceException.class)`
- Retourner un `ResponseEntity<ErrorResponse>`
- Code HTTP : 409 CONFLICT
- Remplir ErrorResponse avec :
  - status : 409
  - error : "Conflict"
  - message : le message de l'exception

💡 **Réfléchissez :** Quel code HTTP pour les doublons ? Pourquoi 409 et pas 400 ?

---

### 3. TODO : Gérer les erreurs génériques

**Consignes :**
- Annoter avec `@ExceptionHandler(Exception.class)`
- Retourner 500 INTERNAL SERVER ERROR
- Message : "Une erreur interne est survenue"
- ⚠️ **Important :** Ne jamais exposer les détails techniques de l'erreur au client

---

## 🛠️ PARTIE 4 : AMÉLIORER LE SERVICE

### 1. TODO : Ajouter des méthodes au ProductRepository

📁 **Fichier :** `src/main/java/com/example/demo/repository/ProductRepository.java`

**Méthodes à ajouter :**

```java
boolean existsByName(String name);
Optional<Product> findByName(String name);
```

💡 **Astuce :** Spring Data JPA génère automatiquement l'implémentation de ces méthodes !

---

### 2. TODO : Améliorer la méthode createProduct

📁 **Fichier :** `src/main/java/com/example/demo/service/ProductService.java`

**Consignes :**
- AVANT de créer le produit, vérifier si un produit avec ce nom existe déjà
- Utiliser `productRepository.existsByName(request.getName())`
- Si oui, lever une `DuplicateResourceException` avec le message :
  `"Un produit avec le nom '" + request.getName() + "' existe déjà"`

---

### 3. TODO : Créer la méthode updateStock

📁 **Fichier :** `src/main/java/com/example/demo/service/ProductService.java`

**Signature :** 
```java
public ProductResponse updateStock(Long id, Integer quantity)
```

**Consignes :**
1. Récupérer le produit par son ID (lever `ResourceNotFoundException` si introuvable)
2. Calculer le nouveau stock : `newStock = product.getStock() + quantity`
3. Vérifier que le nouveau stock n'est pas négatif
4. Si négatif, lever une `InvalidDataException` avec le message :
   `"Stock insuffisant. Stock actuel: X, quantité demandée: Y"`
5. Mettre à jour le stock et sauvegarder
6. Retourner le ProductResponse

💡 **Réfléchissez :** Pourquoi utiliser une quantité signée (positive ou négative) plutôt que deux méthodes séparées ?

---

## 🛠️ PARTIE 5 : AMÉLIORER LE CONTROLLER

📁 **Fichier :** `src/main/java/com/example/demo/controller/ProductController.java`

### TODO : Ajouter l'endpoint PATCH pour le stock

**Endpoint :**
```
PATCH /api/products/{id}/stock
```

**Paramètres :**
- Path variable : `id` (Long)
- Request param : `quantity` (Integer)

**Consignes :**
- Méthode annotée avec `@PatchMapping("/{id}/stock")`
- Utiliser `@PathVariable Long id` et `@RequestParam Integer quantity`
- Appeler `productService.updateStock(id, quantity)`
- Retourner 200 OK avec le ProductResponse mis à jour

💡 **Réfléchissez :** Pourquoi PATCH et pas PUT pour cette opération ?

---

## 🧪 Tests à effectuer

### 1. Validation du nom (trop court)

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "AB",
  "price": 100,
  "stock": 5,
  "category": "ELECTRONICS"
}
```

**Attendu :** 400 avec "Le nom doit contenir entre 3 et 100 caractères"

---

### 2. Validation du prix (hors limites)

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Test Product",
  "price": 200000,
  "stock": 5,
  "category": "ELECTRONICS"
}
```

**Attendu :** 400 avec "Le prix doit être entre 0.01 et 100000"

---

### 3. Validation de la catégorie (invalide)

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Test Product",
  "price": 100,
  "stock": 5,
  "category": "INVALID_CATEGORY"
}
```

**Attendu :** 400 avec message sur catégorie invalide

---

### 4. Doublon de nom

Créer deux fois le même produit :

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Dell XPS",
  "price": 1299.99,
  "stock": 15,
  "category": "ELECTRONICS"
}
```

**Attendu (2ème fois) :** 409 CONFLICT avec "Un produit avec le nom 'Laptop Dell XPS' existe déjà"

---

### 5. Stock insuffisant

D'abord créer un produit avec stock = 5, puis :

```http
PATCH http://localhost:8080/api/products/1/stock?quantity=-10
```

**Attendu :** 400 avec "Stock insuffisant. Stock actuel: 5, quantité demandée: -10"

---

### 6. Ajouter du stock (succès)

```http
PATCH http://localhost:8080/api/products/1/stock?quantity=20
```

**Attendu :** 200 OK avec stock mis à jour

---

### 7. Validation multiple

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "A",
  "price": -10,
  "stock": 20000,
  "category": "WRONG"
}
```

**Attendu :** 400 avec TOUS les messages d'erreur dans le champ `details`

---

## 📊 Exemples de réponses attendues

### Erreur de validation (400)

```json
{
  "timestamp": "2026-02-04T14:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Les données fournies sont invalides",
  "details": {
    "name": "Le nom doit contenir entre 3 et 100 caractères",
    "price": "Le prix doit être entre 0.01 et 100000"
  }
}
```

### Stock insuffisant (400)

```json
{
  "timestamp": "2026-02-04T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Stock insuffisant. Stock actuel: 5, quantité demandée: -10"
}
```

### Doublon (409)

```json
{
  "timestamp": "2026-02-04T14:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Un produit avec le nom 'Laptop Dell XPS' existe déjà"
}
```

---

## 📁 Structure attendue

```
src/main/java/com/example/demo/
├── validation/
│   ├── ValidPrice.java
│   ├── PriceValidator.java
│   ├── ValidCategory.java
│   └── CategoryValidator.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── InvalidDataException.java
│   ├── DuplicateResourceException.java
│   ├── GlobalExceptionHandler.java (amélioré)
│   └── ErrorResponse.java
├── dto/
│   ├── ProductRequest.java (amélioré)
│   ├── ProductResponse.java
│   └── UpdateStockRequest.java (nouveau)
└── ... (autres packages)
```

---

## 📊 Barème d'auto-évaluation (100 points)

- Validation @ValidPrice : **10 points**
- Validation @ValidCategory : **10 points**
- ProductRequest avec toutes les validations : **15 points**
- Exceptions personnalisées (InvalidData, Duplicate) : **10 points**
- GlobalExceptionHandler complet : **25 points**
- Méthode updateStock fonctionnelle : **10 points**
- Vérification des doublons dans createProduct : **10 points**
- Tous les tests validés : **10 points**

---

## 🌟 Bonus (optionnel)

- [ ] Créer `@ValidStockQuantity` pour UpdateStockRequest
- [ ] Ajouter des logs avec `@Slf4j` dans chaque gestionnaire d'exception
- [ ] Ajouter le champ `trace` dans ErrorResponse (mode dev uniquement)
- [ ] Créer un endpoint `PATCH /api/products/{id}/price` avec validation de cohérence

---

## 💡 Conseils

- ✅ **Testez chaque validation individuellement** avant de passer à la suivante
- ✅ Les messages d'erreur doivent être **clairs et utiles** pour le client
- ✅ Utilisez **Postman** pour créer une collection de tests réutilisable
- ✅ Consultez `COURS_SPRING_BOOT.md` sections 4 et 5 sur la validation et les exceptions
- ✅ Les exceptions métier (InvalidData, Duplicate) sont différentes des exceptions techniques

---

## ⏱️ Temps recommandé : 2-3 heures

**⚠️ Important :** Essayez de résoudre l'exercice par vous-même pendant au moins 1 heure avant de consulter la correction ou demander de l'aide.

---

## 📚 Ressources

- [Bean Validation Documentation](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)
- [Custom Constraint Validators](https://www.baeldung.com/spring-mvc-custom-validator)
- [Spring Exception Handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)
- Cours : `COURS_SPRING_BOOT.md`

---

## 🆘 Besoin d'aide ?

Si vous êtes bloqué après avoir essayé :
1. Vérifiez que toutes les annotations sont bien importées
2. Testez vos validateurs séparément
3. Consultez les logs d'erreur détaillés
4. Demandez de l'aide en précisant où vous êtes bloqué

**BON COURAGE ! 🚀**

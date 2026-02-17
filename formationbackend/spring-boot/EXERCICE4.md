# 📚 Exercice 4 : Documentation API avec Swagger / OpenAPI

## 🎯 Objectif

Apprendre à documenter et tester une API REST avec Swagger/OpenAPI, un standard industriel pour la documentation d'APIs.

---

## 📖 Introduction à Swagger/OpenAPI

### Qu'est-ce que Swagger ?

Swagger (maintenant OpenAPI Specification) est un standard pour :
- **Documenter** les APIs REST de manière interactive
- **Tester** les endpoints directement depuis le navigateur
- **Générer** du code client automatiquement
- **Partager** la documentation avec les équipes frontend/mobile

### Pourquoi c'est important ?

✅ **Communication** : Les développeurs frontend comprennent immédiatement votre API  
✅ **Tests** : Testez vos endpoints sans Postman  
✅ **Maintenance** : Documentation toujours à jour (générée depuis le code)  
✅ **Standard industrie** : Utilisé dans 90% des entreprises

---

## 🚀 Configuration réalisée

### 1. Dépendance SpringDoc

Dans `pom.xml`, nous avons ajouté :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

**SpringDoc** génère automatiquement la documentation OpenAPI 3.0 depuis vos controllers.

### 2. Version Spring Boot compatible

```xml
<version>3.2.2</version>
```

Spring Boot 3.2.2 est stable et compatible avec SpringDoc 2.6.0.

---

## 🌐 Accès à Swagger UI

### Interface graphique interactive

Ouvre ton navigateur et va sur :

```
http://localhost:8080/swagger-ui/index.html
```

Tu verras :
- 📋 **Liste de tous tes endpoints** (GET, POST, PUT, DELETE, PATCH)
- 📝 **Schémas des DTOs** (ProductRequest, ProductResponse, etc.)
- 🎮 **Bouton "Try it out"** pour tester chaque endpoint
- 📊 **Réponses en temps réel** avec codes HTTP et données

### Documentation JSON (OpenAPI spec)

```
http://localhost:8080/v3/api-docs
```

Format JSON brut utilisé par les outils tiers (code generators, etc.).

---

## 🎨 Personnalisation de la documentation

### Ajouter des informations sur l'API

Crée une classe de configuration `OpenApiConfig.java` :

```java
package com.exercice1.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product API")
                .description("API de gestion de produits avec pagination, recherche et statistiques")
                .version("1.0")
                .contact(new Contact()
                    .name("Ton nom")
                    .email("ton.email@example.com")
                    .url("https://github.com/tonprofil"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Serveur de développement"),
                new Server()
                    .url("https://api-production.com")
                    .description("Serveur de production")
            ));
    }
}
```

### Documenter les endpoints avec des annotations

Dans `ProductController.java`, ajoute des annotations pour décrire chaque endpoint :

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits du catalogue")
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Rechercher des produits",
        description = "Recherche des produits par mot-clé dans le nom ou la description avec pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Recherche réussie",
            content = @Content(schema = @Schema(implementation = PagedResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Paramètres invalides"
        )
    })
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
        @Parameter(description = "Mot-clé à rechercher", required = true)
        @RequestParam(required = true) String keyword,
        
        @Parameter(description = "Numéro de page (commence à 0)")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "Taille de la page")
        @RequestParam(defaultValue = "10") int size,
        
        @Parameter(description = "Champ de tri (name, price, stock)")
        @RequestParam(defaultValue = "name") String sortBy,
        
        @Parameter(description = "Direction du tri (ASC ou DESC)")
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size, sortBy, direction));
    }

    // Autres endpoints...
}
```

### Documenter les schémas (DTOs)

Dans `ProductRequest.java` :

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête de création ou mise à jour d'un produit")
public record ProductRequest(
    
    @Schema(description = "Nom du produit", example = "Laptop HP Pavilion", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères")
    String name,
    
    @Schema(description = "Description détaillée", example = "Ordinateur portable 15 pouces avec processeur Intel i7")
    @Size(max = 200, message = "La description ne doit pas dépasser 200 caractères")
    String description,
    
    @Schema(description = "Prix en euros", example = "899.99", minimum = "0.01", maximum = "100000")
    @ValidPrice
    Double price,
    
    @Schema(description = "Catégorie du produit", example = "ELECTRONICS", allowableValues = {"ELECTRONICS", "BOOKS", "CLOTHING", "FOOD", "OTHER"})
    @ValidCategory
    String category,
    
    @Schema(description = "Quantité en stock", example = "50", minimum = "0")
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Max(value = 1000000, message = "Le stock ne peut pas dépasser 1000000")
    Integer stock
) {}
```

---

## ✅ Exercice pratique

### Partie 1 : Explorer Swagger UI (15 min)

1. **Ouvre Swagger UI** : http://localhost:8080/swagger-ui/index.html

2. **Teste chaque endpoint** :
   - Clique sur un endpoint (ex: `GET /api/products/search`)
   - Clique sur "Try it out"
   - Remplis les paramètres (keyword: "test", page: 0, size: 10)
   - Clique sur "Execute"
   - Observe la réponse (code 200, données JSON)

3. **Teste la création d'un produit** :
   - `POST /api/products`
   - Modifie le JSON d'exemple :
   ```json
   {
     "name": "Laptop Gaming",
     "description": "PC portable haute performance",
     "price": 1299.99,
     "category": "ELECTRONICS",
     "stock": 15
   }
   ```
   - Execute et note l'ID retourné

4. **Teste la recherche du produit créé** :
   - `GET /api/products/{id}` avec l'ID reçu
   - Vérifie que les données correspondent

5. **Teste les filtres combinés** :
   - `GET /api/products/filter`
   - Paramètres : category=ELECTRONICS, minPrice=1000, maxPrice=2000

### Partie 2 : Documenter ton API (30 min)

1. **Crée** `config/OpenApiConfig.java` avec tes informations personnelles

2. **Ajoute des annotations** sur 3 endpoints minimum dans ProductController :
   - `@Operation` avec summary et description
   - `@Parameter` sur les paramètres
   - `@ApiResponses` pour documenter les codes HTTP

3. **Documente** ProductRequest et ProductResponse avec `@Schema`

4. **Vérifie** que la documentation est mise à jour dans Swagger UI

### Partie 3 : Grouper les endpoints par tag (15 min)

Organise les endpoints en groupes logiques :

```java
@Tag(name = "1. CRUD de base", description = "Opérations de base : créer, lire, modifier, supprimer")
public class ProductController {
    
    @Operation(summary = "Créer un produit", tags = "1. CRUD de base")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(...) { }
    
    @Operation(summary = "Obtenir un produit", tags = "1. CRUD de base")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(...) { }
}
```

Ajoute des tags :
- **1. CRUD de base** : GET by ID, POST, PUT, DELETE, PATCH stock
- **2. Recherche et filtrage** : search, filter, by category, by price range, low stock
- **3. Pagination** : getAllProducts avec pagination
- **4. Statistiques** : statistics, statistics by category

---

## 🎯 Résultat attendu

À la fin de cet exercice, tu dois avoir :

✅ Une interface Swagger UI complète et fonctionnelle  
✅ Tous les endpoints documentés avec descriptions claires  
✅ Des exemples JSON pour chaque DTO  
✅ Les endpoints organisés par tags (groupes logiques)  
✅ Informations de contact et licence configurées  

---

## 📚 Ressources complémentaires

### Documentation officielle

- [SpringDoc OpenAPI](https://springdoc.org/)
- [OpenAPI Specification 3.0](https://swagger.io/specification/)
- [Swagger Annotations Guide](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

### Bonnes pratiques

1. **Descriptions claires** : Explique ce que fait chaque endpoint
2. **Exemples réalistes** : Utilise des données d'exemple pertinentes
3. **Codes HTTP** : Documente tous les cas (200, 400, 404, 500)
4. **Groupement logique** : Organise par domaine fonctionnel
5. **Versioning** : Indique la version de l'API

---

## 🐛 Troubleshooting

### Problème : Erreur 500 sur /v3/api-docs

**Cause** : Incompatibilité entre Spring Boot et SpringDoc

**Solution** :
- Spring Boot 3.2.2 + SpringDoc 2.6.0 (versions compatibles)
- Éviter Spring Boot 3.5.x (trop récent)

### Problème : Swagger UI ne charge pas

**Vérifications** :
1. L'application est démarrée : `curl http://localhost:8080/actuator/health`
2. Le port est correct : vérifier `server.port` dans application.properties
3. Vider le cache du navigateur : Ctrl+Shift+R

### Problème : Schémas DTOs non affichés

**Solution** : Assure-toi que les DTOs sont publics et ont des getters (ou utilise Lombok `@Data`)

---

## 🚀 Pour aller plus loin

### Générer un client TypeScript/JavaScript

```bash
npm install @openapitools/openapi-generator-cli -g
openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g typescript-axios -o ./client
```

### Sécuriser Swagger en production

```properties
# application-prod.properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

Ou sécurise avec Spring Security (module suivant !).

### Exporter la spec OpenAPI

```bash
curl http://localhost:8080/v3/api-docs > api-spec.json
```

Partage ce fichier avec les équipes frontend/mobile.

---

## ✨ Prochaine étape

Une fois cet exercice terminé, tu seras prêt pour :

**Option 1** : Mini-projet e-commerce complet (plusieurs entités, relations)  
**Option 2** : Tests automatisés (JUnit, MockMvc, TestContainers)  
**Option 3** : Spring Security (JWT, OAuth2, roles)

**Félicitations** ! Tu maîtrises maintenant la documentation d'APIs REST professionnelles ! 🎉

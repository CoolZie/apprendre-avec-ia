# 🏆 Mini-Projet : Système d'Authentification Complet

## 🎯 Objectif Global

Créer une **plateforme de gestion de contenu** (Blog/CMS) avec un système d'authentification et d'autorisation complet intégrant tous les concepts vus dans le module Spring Security.

**Nom du projet** : `BlogSecurityPlatform`

---

## 📋 Cahier des Charges

### Vue d'ensemble

Une plateforme où :
- **Lecteurs** (ROLE_READER) : Lire les articles publics
- **Auteurs** (ROLE_AUTHOR) : Créer et gérer leurs propres articles
- **Éditeurs** (ROLE_EDITOR) : Modifier tous les articles
- **Administrateurs** (ROLE_ADMIN) : Gestion complète (users, articles, catégories)

### Entités du Domaine

#### 1. User
- `id`, `username`, `email`, `password`
- `roles` (Set<String>)
- `enabled`, `verificationToken`
- Timestamps

#### 2. Article
- `id`, `title`, `content`
- `author` (User)
- `category` (Category)
- `status` (DRAFT, PUBLISHED, ARCHIVED)
- `published` (boolean)
- Timestamps

#### 3. Category
- `id`, `name`, `description`
- `createdBy` (User)
- Timestamps

#### 4. Comment
- `id`, `content`
- `author` (User)
- `article` (Article)
- `approved` (boolean)
- Timestamps

### Fonctionnalités Requises

#### Authentification (25 points)
- [x] Inscription avec email verification
- [x] Login avec JWT (access + refresh tokens)
- [x] Logout avec blacklist
- [x] Change password
- [x] Reset password (bonus)
- [x] Rate limiting (5 tentatives max)

#### Gestion Utilisateurs (15 points)
- [x] Liste utilisateurs (EDITOR+)
- [x] Profil utilisateur (owner ou ADMIN)
- [x] Modifier rôles (ADMIN)
- [x] Désactiver/Activer compte (ADMIN)
- [x] Supprimer utilisateur (ADMIN)

#### Gestion Articles (30 points)
- [x] Créer article (AUTHOR+)
- [x] Liste articles publics (READER+)
- [x] Liste mes articles (AUTHOR)
- [x] Modifier article (owner ou EDITOR+)
- [x] Supprimer article (owner ou ADMIN)
- [x] Publier/Dépublier (owner ou EDITOR+)
- [x] Changer status (DRAFT/PUBLISHED/ARCHIVED)

#### Gestion Catégories (10 points)
- [x] CRUD catégories (EDITOR+)
- [x] Liste publique (tous)

#### Gestion Commentaires (15 points)
- [x] Créer commentaire (READER+)
- [x] Approuver commentaire (EDITOR+)
- [x] Supprimer commentaire (owner ou ADMIN)

#### Sécurité (5 points)
- [x] CORS configuré
- [x] Headers sécurité
- [x] Validation complète (DTOs)
- [x] Gestion erreurs

---

## 📐 Architecture Complète

```
src/main/java/com/formation/blog/
├── model/
│   ├── User.java
│   ├── Article.java
│   ├── Category.java
│   ├── Comment.java
│   ├── RefreshToken.java
│   ├── TokenBlacklist.java
│   └── enums/
│       ├── ArticleStatus.java
│       └── UserRole.java
├── repository/
│   ├── UserRepository.java
│   ├── ArticleRepository.java
│   ├── CategoryRepository.java
│   ├── CommentRepository.java
│   ├── RefreshTokenRepository.java
│   └── TokenBlacklistRepository.java
├── service/
│   ├── auth/
│   │   ├── AuthService.java
│   │   ├── JwtService.java
│   │   ├── RefreshTokenService.java
│   │   ├── TokenBlacklistService.java
│   │   └── LoginAttemptService.java
│   ├── UserService.java
│   ├── ArticleService.java
│   ├── CategoryService.java
│   ├── CommentService.java
│   └── EmailService.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ArticleController.java
│   ├── CategoryController.java
│   └── CommentController.java
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── ArticleRequest.java
│   │   ├── CategoryRequest.java
│   │   └── CommentRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── ArticleResponse.java
│       ├── CategoryResponse.java
│       └── CommentResponse.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfig.java
│   ├── SecurityUtils.java
│   └── UserDetailsServiceImpl.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── AccessDeniedException.java
│   └── ...
└── config/
    └── ApplicationConfig.java
```

---

## 🛠️ Spécifications Détaillées

### Endpoints API

#### Authentication (`/api/auth`)
| Méthode | Route | Rôle | Description |
|---------|-------|------|-------------|
| POST | `/register` | Public | Inscription |
| POST | `/login` | Public | Connexion |
| POST | `/refresh` | Public | Renouveler token |
| POST | `/logout` | Auth | Déconnexion |
| POST | `/change-password` | Auth | Changer password |
| POST | `/verify/{token}` | Public | Vérifier email |
| POST | `/resend-verification` | Public | Renvoyer email |

#### Users (`/api/users`)
| Méthode | Route | Rôle | Description |
|---------|-------|------|-------------|
| GET | `/` | EDITOR+ | Liste utilisateurs |
| GET | `/{id}` | Owner/ADMIN | Profil |
| PATCH | `/{id}/roles` | ADMIN | Modifier rôles |
| PATCH | `/{id}/status` | ADMIN | Activer/Désactiver |
| DELETE | `/{id}` | ADMIN | Supprimer |

#### Articles (`/api/articles`)
| Méthode | Route | Rôle | Description |
|---------|-------|------|-------------|
| GET | `/` | Public | Articles publics |
| GET | `/my-articles` | AUTHOR+ | Mes articles |
| GET | `/{id}` | Public/READER+ | Détail article |
| POST | `/` | AUTHOR+ | Créer article |
| PUT | `/{id}` | Owner/EDITOR+ | Modifier |
| DELETE | `/{id}` | Owner/ADMIN | Supprimer |
| PATCH | `/{id}/publish` | Owner/EDITOR+ | Publier |
| PATCH | `/{id}/status` | Owner/EDITOR+ | Changer status |

#### Categories (`/api/categories`)
| Méthode | Route | Rôle | Description |
|---------|-------|------|-------------|
| GET | `/` | Public | Liste |
| GET | `/{id}` | Public | Détail |
| POST | `/` | EDITOR+ | Créer |
| PUT | `/{id}` | EDITOR+ | Modifier |
| DELETE | `/{id}` | ADMIN | Supprimer |

#### Comments (`/api/comments`)
| Méthode | Route | Rôle | Description |
|---------|-------|------|-------------|
| GET | `/article/{articleId}` | Public | Commentaires approuvés |
| POST | `/` | READER+ | Créer commentaire |
| PATCH | `/{id}/approve` | EDITOR+ | Approuver |
| DELETE | `/{id}` | Owner/ADMIN | Supprimer |

### Règles Métier Importantes

#### Articles
1. **DRAFT** : Visible uniquement par owner et EDITOR+
2. **PUBLISHED** : Visible par tous si `published = true`
3. **ARCHIVED** : Visible uniquement EDITOR+
4. Seul l'auteur ou EDITOR+ peut modifier
5. Supprimer un article supprime ses commentaires (CASCADE)

#### Commentaires
1. Non approuvés invisibles aux READER
2. EDITOR+ peut approuver/désapprouver
3. Owner du commentaire peut toujours le supprimer

#### Utilisateurs
1. Email doit être vérifié pour publier des articles
2. ADMIN ne peut pas modifier son propre rôle
3. Désactiver un utilisateur révoque tous ses tokens

### DTOs Importants

**ArticleRequest** :
```java
@Data
public class ArticleRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200)
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 50)
    private String content;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
}
```

**ArticleResponse** :
```java
@Data
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String categoryName;
    private ArticleStatus status;
    private boolean published;
    private int commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Enums

**ArticleStatus** :
```java
public enum ArticleStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
```

**UserRole** (Optionnel, ou garder Set<String>) :
```java
public enum UserRole {
    ROLE_READER,
    ROLE_AUTHOR,
    ROLE_EDITOR,
    ROLE_ADMIN
}
```

---

## 🧪 Scénarios de Test Obligatoires

### Scenario 1 : Inscription et Vérification
```bash
# 1. Inscription
POST /api/auth/register
{"username": "alice", "email": "alice@blog.com", "password": "Author123"}
# → Token + email vérification dans logs

# 2. Vérifier email
POST /api/auth/verify/abc-123-token
# → "Email verified"

# 3. Login
POST /api/auth/login
{"username": "alice", "password": "Author123"}
# → Access + Refresh tokens
```

### Scenario 2 : Workflow Publication Article
```bash
# 1. Alice (AUTHOR) crée un article DRAFT
POST /api/articles
Authorization: Bearer <alice-token>
{
  "title": "My First Article",
  "content": "Lorem ipsum dolor sit amet...",
  "categoryId": 1
}
# → Article créé avec status DRAFT, published = false

# 2. Bob (READER) ne peut pas voir le DRAFT
GET /api/articles
Authorization: Bearer <bob-token>
# → [] (liste vide)

# 3. Alice publie son article
PATCH /api/articles/1/publish
Authorization: Bearer <alice-token>
# → published = true

# 4. Bob peut maintenant lire
GET /api/articles
# → [{"id": 1, "title": "My First Article", ...}]
```

### Scenario 3 : Gestion Rôles
```bash
# 1. Admin liste les utilisateurs
GET /api/users
Authorization: Bearer <admin-token>
# → [{...}, {...}]

# 2. Admin change Alice de AUTHOR à EDITOR
PATCH /api/users/1/roles
Authorization: Bearer <admin-token>
{"roles": ["ROLE_AUTHOR", "ROLE_EDITOR"]}

# 3. Alice peut maintenant modifier l'article de Bob
PUT /api/articles/2
Authorization: Bearer <alice-token>
# → 200 OK (avant : 403 Forbidden)
```

### Scenario 4 : Commentaires et Approbation
```bash
# 1. Bob commente l'article d'Alice
POST /api/comments
Authorization: Bearer <bob-token>
{
  "articleId": 1,
  "content": "Great article!"
}
# → approved = false par défaut

# 2. Alice (READER anonyme) ne voit pas le commentaire non approuvé
GET /api/comments/article/1
# → []

# 3. Charlie (EDITOR) approuve le commentaire
PATCH /api/comments/1/approve
Authorization: Bearer <charlie-token>

# 4. Maintenant visible
GET /api/comments/article/1
# → [{"content": "Great article!", "author": "bob", ...}]
```

### Scenario 5 : Logout et Blacklist
```bash
# 1. Alice se connecte
POST /api/auth/login
# → token1

# 2. Alice utilise son token
GET /api/articles/my-articles
Authorization: Bearer token1
# → 200 OK

# 3. Alice se déconnecte
POST /api/auth/logout
Authorization: Bearer token1

# 4. Token blacklisté
GET /api/articles/my-articles
Authorization: Bearer token1
# → 401 "Token is blacklisted"
```

---

## ✅ Critères de Validation

### Architecture et Code (20 points)
- [ ] Structure projet respectée (5 pts)
- [ ] DTOs complets et validés (5 pts)
- [ ] Services avec @Transactional (5 pts)
- [ ] Repositories avec queries custom (5 pts)

### Authentification (25 points)
- [ ] Registration + Email verification (5 pts)
- [ ] Login JWT avec refresh token (5 pts)
- [ ] Logout avec blacklist (5 pts)
- [ ] Change password (5 pts)
- [ ] Rate limiting (5 pts)

### Autorisation (25 points)
- [ ] 4 rôles fonctionnels (READER, AUTHOR, EDITOR, ADMIN) (10 pts)
- [ ] @PreAuthorize sur tous les endpoints (10 pts)
- [ ] Ownership checks (SecurityUtils) (5 pts)

### Gestion Articles (15 points)
- [ ] CRUD complet avec statuts (5 pts)
- [ ] Publish/Unpublish (3 pts)
- [ ] Filtres par status/category (4 pts)
- [ ] Compteur commentaires (3 pts)

### Gestion Commentaires (10 points)
- [ ] CRUD commentaires (5 pts)
- [ ] Approbation workflow (5 pts)

### Tests et Documentation (5 points)
- [ ] Tous les scénarios testés (3 pts)
- [ ] README.md avec guide utilisation (2 pts)

**Score total : /100**

---

## 📝 Livrables

### Code Source
```
blog-security-platform/
├── src/
│   ├── main/
│   │   ├── java/com/formation/blog/...
│   │   └── resources/
│   │       └── application.properties
├── pom.xml
└── README.md
```

### README.md (obligatoire)

Doit contenir :
1. **Description projet**
2. **Technologies utilisées**
3. **Installation** :
   ```bash
   git clone ...
   cd blog-security-platform
   mvn clean install
   mvn spring-boot:run
   ```
4. **Comptes de test** :
   - Admin : admin / Admin123
   - Editor : editor / Editor123
   - Author : alice / Alice123
   - Reader : bob / Bob123
5. **Exemples d'appels API** (Postman collection ou cURL)
6. **Architecture** (diagramme ou description)

### Collection Postman

Fournir une collection avec :
- Toutes les routes documentées
- Variables d'environnement (baseUrl, tokens)
- Tests pour chaque scénario

---

## 💡 Conseils

### Ordre de développement recommandé

1. **Phase 1 : Base** (2-3h)
   - Entités (User, RefreshToken, TokenBlacklist)
   - Security config basique
   - Auth endpoints (register, login, logout)

2. **Phase 2 : Articles** (2-3h)
   - Entités (Article, Category)
   - CRUD articles
   - Permissions par rôle

3. **Phase 3 : Commentaires** (1-2h)
   - Entité Comment
   - CRUD + approbation

4. **Phase 4 : Finitions** (1-2h)
   - Gestion users
   - Tests complets
   - Documentation

### Points d'attention

#### 1. Cascade Operations
```java
@Entity
public class Article {
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
```

#### 2. Query Performance
```java
@Query("SELECT a FROM Article a JOIN FETCH a.author JOIN FETCH a.category WHERE a.published = true")
List<Article> findPublishedArticles();
```

#### 3. DTO Mapping
```java
public static ArticleResponse from(Article article) {
    return new ArticleResponse(
        article.getId(),
        article.getTitle(),
        article.getContent(),
        article.getAuthor().getUsername(),
        article.getCategory().getName(),
        article.getStatus(),
        article.isPublished(),
        article.getComments().size(),  // Attention N+1
        article.getCreatedAt(),
        article.getUpdatedAt()
    );
}
```

#### 4. Data Initialization
```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Créer admin, categories par défaut, etc.
    }
}
```

### Erreurs courantes à éviter

❌ Oublier `@Transactional` sur deleteByUser()  
❌ Exposer les mots de passe dans UserResponse  
❌ Permettre à un READER de créer des articles  
❌ Oublier de vérifier `enabled` avant autorisation  
❌ N+1 queries sur `article.getComments().size()`  

---

## 🚀 Pour aller plus loin (Bonus)

### +5 points : Fonctionnalités Avancées

- [ ] **Reset Password** : Lien par email pour réinitialiser
- [ ] **Like/Unlike** : Système de likes sur articles
- [ ] **Search** : Recherche full-text (titre + contenu)
- [ ] **Pagination** : Sur tous les listings
- [ ] **Audit Logs** : Tracker qui fait quoi

### +5 points : Tests Unitaires

```java
@SpringBootTest
class ArticleServiceTest {
    
    @Test
    void authorCanPublishOwnArticle() {
        // Given: Article DRAFT
        // When: publish()
        // Then: published = true
    }
    
    @Test
    void readerCannotPublishArticle() {
        // Given: User avec ROLE_READER
        // When: publish()
        // Then: AccessDeniedException
    }
}
```

### +3 points : Swagger Documentation

```java
@Tag(name = "Articles", description = "Gestion des articles")
@RestController
public class ArticleController {
    
    @Operation(summary = "Liste des articles publics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<ArticleResponse> getPublicArticles() { ... }
}
```

---

## 📊 Grille d'Auto-évaluation

Avant de soumettre, vérifiez :

- [ ] Le projet compile sans erreurs
- [ ] Tous les endpoints sont accessibles
- [ ] Les 5 scénarios de test fonctionnent
- [ ] Le README est complet
- [ ] Les mots de passe sont chiffrés en DB
- [ ] Les tokens expirés sont nettoyés
- [ ] Pas de données sensibles dans les logs
- [ ] Les exceptions sont gérées proprement
- [ ] CORS configuré correctement
- [ ] Code commenté (méthodes complexes)

---

**Bon courage pour ce projet final ! 🚀**

*Temps estimé : 8-12 heures*  
*Difficulté : ⭐⭐⭐⭐⭐*

> Ce mini-projet est l'occasion de démontrer votre maîtrise complète de Spring Security. Prenez le temps de bien architecturer, tester et documenter votre travail. **Qualité > Quantité** !

# 🏆 Mini-Projet Final : Spring Security (Capstone)

## 🎯 Objectif global

Ce mini-projet doit te permettre d’implémenter **tout ce qui a été vu dans les autres exercices Spring Security**, avec un domaine métier volontairement minimal.

- ✅ Focus principal : **sécurité**
- ✅ Complexité métier : **1 seule entité business**
- ✅ Projet cible : `BlogSecurityPlatform`

---

## ✅ Règle de simplification (obligatoire)

Pour éviter le superflu métier, on garde uniquement :

- **Entité business unique : `Article`**
- Aucun autre module métier complexe (pas de Category, Comment, Order, etc.)

Tu investis ton temps sur :
- Authentification
- JWT + filtres
- RBAC / autorisations
- Refresh token
- Logout / révocation
- Change password
- Email verification
- Rate limiting

---

## 📦 Domaine métier minimal

### Entité `Article`

Champs minimum :
- `id`
- `title`
- `content`
- `published` (boolean)
- `createdAt`
- `updatedAt`

Endpoints métier de base :
- `GET /api/articles`
- `GET /api/articles/{id}`
- `POST /api/articles`
- `PUT /api/articles/{id}`
- `PATCH /api/articles/{id}/publish`
- `DELETE /api/articles/{id}`

---

## 🧱 Architecture recommandée

```text
src/main/java/com/formation/blog/app/
├── model/
│   ├── User.java
│   ├── RefreshToken.java
│   └── Article.java
├── repository/
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   └── ArticleRepository.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   └── ArticleController.java
├── service/
│   ├── UserService.java
│   ├── RefreshTokenService.java
│   ├── LoginAttemptService.java
│   └── ArticleService.java
├── security/
│   ├── SecurityConfig.java
│   ├── ApplicationConfig.java
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   ├── UserDetailsServiceImpl.java
│   └── SecurityUtils.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserResponse.java
│   ├── RoleUpdateRequest.java
│   ├── RefreshTokenRequest.java
│   ├── ChangePasswordRequest.java
│   ├── VerificationRequest.java
│   ├── ArticleRequest.java
│   └── ArticleResponse.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── DuplicateResourceException.java
    ├── InvalidCredentialsException.java
    ├── AccountBlockedException.java
    └── ResourceNotFoundException.java
```

---

## 🔐 Spécifications de sécurité à implémenter

## Bloc A — Base JWT (EXERCICE 1)

### Authentification
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Contraintes
- Password encodé avec `BCryptPasswordEncoder`
- JWT signé (secret en config)
- Filtre JWT (`JwtAuthenticationFilter`) actif
- Endpoints protégés accessibles uniquement avec Bearer token valide

---

## Bloc B — Rôles et autorisations (EXERCICE 2)

### Rôles
- `ROLE_READER`
- `ROLE_AUTHOR`
- `ROLE_EDITOR`
- `ROLE_ADMIN`

### Gestion des rôles utilisateurs
- `GET /api/users` (ADMIN)
- `GET /api/users/{id}` (owner ou ADMIN)
- `PATCH /api/users/{id}/roles` (ADMIN)
- `DELETE /api/users/{id}` (ADMIN)

### Autorisations Article (RBAC)
- `GET /api/articles` : public ou READER+
- `GET /api/articles/{id}` : public ou READER+
- `POST /api/articles` : AUTHOR+
- `PUT /api/articles/{id}` : owner ou EDITOR+
- `PATCH /api/articles/{id}/publish` : owner ou EDITOR+
- `DELETE /api/articles/{id}` : ADMIN (ou owner + ADMIN selon ton choix)

> Utiliser `@PreAuthorize` + `SecurityUtils` pour les règles owner.

---

## Bloc C — Fonctionnalités avancées (EXERCICE 3)

### Refresh token
- `POST /api/auth/refresh`
- Persisté en base
- Vérification expiration + révocation

### Logout
- `POST /api/auth/logout`
- Révoque le refresh token

### Change password
- `POST /api/auth/change-password`
- Vérifie ancien password
- Valide le nouveau password
- Révoque tous les refresh tokens de l’utilisateur

### Email verification (simulation logs)
- Génération d’un token de vérification à l’inscription
- `GET /api/auth/verify/{token}`
- `POST /api/auth/resend-verification`
- Login interdit tant que le compte n’est pas vérifié

### Rate limiting login
- max 5 échecs
- blocage temporaire (ex: 1h)
- message avec tentatives restantes / temps restant

---

## Bloc D — Refresh Token & Remember Me (GUIDE)

Implémenter les 2 modes :

- **Sans rememberMe**
  - Access token court
  - Refresh token moyen

- **Avec rememberMe**
  - Access token plus long
  - Refresh token plus long

Le endpoint login accepte un flag `rememberMe`.

---

## 🛡️ Configuration sécurité attendue

- `SecurityFilterChain` stateless (`SessionCreationPolicy.STATELESS`)
- Activation method security (`@EnableMethodSecurity`)
- gestion propre des erreurs 401/403
- CORS configuré si frontend séparé
- validation DTO complète (`@Valid`)
- endpoints publics explicitement listés

---

## 🧪 Tests minimum à fournir

- Register/Login OK
- Accès refusé sans token sur route protégée
- Accès refusé si rôle insuffisant
- `refresh` fonctionne avec token valide
- `logout` invalide le refresh token
- `change-password` invalide les anciennes sessions
- `verify email` active le compte
- blocage après trop de tentatives de login

(Tests recommandés : `spring-security-test` + `MockMvc`)

---

## ✅ Définition de “terminé”

Le mini-projet est validé si :
1. Le métier reste limité à **1 seule entité business (`Article`)**.
2. Tous les blocs A/B/C/D sont implémentés.
3. Les règles d’accès par rôles sont démontrées.
4. Les flux sensibles (refresh/logout/change-password/verify/rate-limit) sont fonctionnels.
5. Le code reste clair, lisible et maintenable.

---

## 🚀 Conseil d’exécution

Ordre recommandé :
1. Bloc A (JWT de base)
2. Bloc B (RBAC)
3. Bloc D (refresh + rememberMe)
4. Bloc C (logout, password, verify, rate limit)
5. Tests de sécurité

Bon build 🔐
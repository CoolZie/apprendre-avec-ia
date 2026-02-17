package jpa;

/**
 * EXERCICE 3 : Repository Pattern et CRUD avec Spring Data JPA
 * 
 * 🎯 OBJECTIF :
 * Maîtriser Spring Data JPA en créant un repository et un service complets.
 * 
 * 📚 CONTEXTE :
 * Spring Data JPA génère automatiquement les requêtes SQL à partir des noms de méthodes.
 * Vous allez implémenter le pattern Repository + Service Layer.
 * 
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 📝 PARTIE 1 - Interface Repository
 * 
 * Créez une interface UserRepository qui :
 * 1. Étend JpaRepository<User, Long>
 * 2. Déclare ces méthodes de requête (Spring Data les implémente automatiquement) :
 *    - Trouver un user par username → retourne Optional<User>
 *    - Trouver un user par email → retourne Optional<User>
 *    - Trouver tous les users avec age supérieur à X → retourne List<User>
 *    - Rechercher des users dont le username contient un mot-clé (insensible à la casse)
 * 
 * 💡 Astuce : Utilisez les conventions de nommage Spring Data JPA
 *    (findBy..., ...GreaterThan, ...ContainingIgnoreCase, etc.)
 * 
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 📝 PARTIE 2 - Service Layer
 * 
 * Créez une classe UserService avec ces méthodes :
 * 
 * A. CRUD de base :
 *    - createUser(User user) : Crée un nouvel utilisateur
 *    - getUserById(Long id) : Récupère un user par son ID
 *    - getAllUsers() : Récupère tous les users
 *    - updateUser(Long id, User userData) : Met à jour un user existant
 *    - deleteUser(Long id) : Supprime un user
 *    - searchUsersByKeyword(String keyword) : Recherche par mot-clé
 * 
 * B. Gestion des erreurs :
 *    - Vérifiez que l'email n'est pas déjà utilisé avant création
 *    - Vérifiez que le user existe avant mise à jour/suppression
 *    - Lancez une exception si user non trouvé
 * 
 * C. Injection de dépendance :
 *    - Injectez le UserRepository dans le service
 *    - Utilisez @Autowired ou l'injection par constructeur
 * 
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 📝 PARTIE 3 - Transactions
 * 
 * 1. Annotez les méthodes avec @Transactional selon leur nature :
 *    - Méthodes de lecture : @Transactional(readOnly = true)
 *    - Méthodes d'écriture : @Transactional
 * 
 * 2. Créez une méthode complexe : transferUserBooks(fromUserId, toUserId)
 *    - Récupère tous les livres du user source
 *    - Les transfère au user destination
 *    - Doit être atomique (tout réussit ou tout échoue)
 *    - Utilisez @Transactional pour garantir l'atomicité
 * 
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 💡 INDICES :
 * - JpaRepository fournit déjà save(), findById(), findAll(), deleteById()...
 * - Optional.orElseThrow() est utile pour lancer des exceptions
 * - @Transactional garantit que tout se fait ou rien ne se fait (rollback)
 * 
 * ⚠️ NE PAS REGARDER LA CORRECTION AVANT D'AVOIR ESSAYÉ !
 */

// TODO: Implémenter l'exercice ici

public class ExerciceJPA3 {
    // Votre code ici
}

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 📚 CONCEPTS À CONNAÎTRE
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 
 * 1. Conventions de nommage Spring Data JPA :
 *    findBy, findAllBy, countBy, deleteBy, existsBy
 *    And, Or, Between, LessThan, GreaterThan, Like, OrderBy
 * 
 * 2. @Query pour requêtes personnalisées (si besoin)
 * 
 * 3. Projections pour récupérer seulement certains champs
 * 
 * 4. Pagination avec Pageable et Page<T>
 */

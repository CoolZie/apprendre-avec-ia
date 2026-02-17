package jpa;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📘 GUIDE COMPLET : Projections avec Spring Data JPA
 * 
 * Les projections permettent de récupérer seulement certains champs
 * d'une entité au lieu de l'entité complète.
 */

public class ExempleProjections {

    // ═══════════════════════════════════════════════════════════════
    // ENTITÉ COMPLÈTE (avec beaucoup de champs)
    // ═══════════════════════════════════════════════════════════════
    
    // @Entity
    static class User {
        // @Id
        private Long id;
        private String username;
        private String email;
        private String password;          // Sensible !
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;
        private boolean active;
        
        // Getters/Setters...
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isActive() { return active; }
    }

    // ═══════════════════════════════════════════════════════════════
    // 1️⃣ PROJECTION SIMPLE : Interface de base
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Interface de projection qui expose UNIQUEMENT username et email
     * 
     * ✅ Avantages :
     * - Sécurité : password n'est pas exposé
     * - Performance : seulement 2 champs récupérés en BD
     * - Simplicité : pas besoin de DTO
     */
    interface UserSummary {
        String getUsername();
        String getEmail();
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 2️⃣ PROJECTION AVEC MÉTHODE CALCULÉE : @Value
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Projection avec champ calculé/combiné
     * 
     * La méthode getFullName() combine firstName et lastName
     * en utilisant SpEL (Spring Expression Language)
     */
    interface UserDetails {
        String getUsername();
        String getEmail();
        
        // @Value est une annotation Spring
        // @Value("#{target.firstName + ' ' + target.lastName}")
        String getFullName(); // Retourne "John Doe"
        
        boolean isActive();
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 3️⃣ PROJECTION NESTED : Relations
    // ═══════════════════════════════════════════════════════════════
    
    // Entité liée
    static class Address {
        private String street;
        private String city;
        private String country;
        
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getCountry() { return country; }
    }
    
    static class UserWithAddress {
        private Long id;
        private String username;
        private Address address; // Relation
        
        public Address getAddress() { return address; }
    }
    
    /**
     * Projection qui inclut des champs d'une relation
     * 
     * On peut accéder aux champs de Address via une nested projection
     */
    interface UserWithLocation {
        String getUsername();
        
        // Projection imbriquée pour Address
        AddressInfo getAddress();
        
        interface AddressInfo {
            String getCity();
            String getCountry();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 4️⃣ PROJECTION DYNAMIQUE : Choisir au runtime
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Repository avec projections dynamiques
     * 
     * On peut choisir quelle projection utiliser en passant la classe
     * en paramètre générique
     */
    interface UserRepository { // extends JpaRepository<User, Long>
        
        // Projection fixe
        List<UserSummary> findAllProjectedBy();
        
        // Projection dynamique : choisir au runtime
        <T> List<T> findAllBy(Class<T> type);
        
        // Exemples d'utilisation :
        // List<UserSummary> summaries = repo.findAllBy(UserSummary.class);
        // List<UserDetails> details = repo.findAllBy(UserDetails.class);
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 5️⃣ DTO vs PROJECTION : Comparaison
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * OPTION A : DTO Classique (classe concrète)
     * 
     * ✅ Avantages :
     * - Contrôle total sur les données
     * - Peut contenir de la logique métier
     * - Plus facile à tester
     * 
     * ❌ Inconvénients :
     * - Plus de code à écrire
     * - Nécessite un mapping manuel ou MapStruct
     */
    record UserDTO(Long id, String username, String email) {
        // Conversion manuelle
        public static UserDTO from(User user) {
            return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
            );
        }
    }
    
    /**
     * OPTION B : Interface de Projection (dynamique)
     * 
     * ✅ Avantages :
     * - Moins de code
     * - Spring Data fait le mapping automatiquement
     * - Optimisation automatique des requêtes SQL
     * 
     * ❌ Inconvénients :
     * - Moins de contrôle
     * - Difficile d'ajouter de la logique
     * - Dépend de Spring Data
     */
    interface UserProjection {
        Long getId();
        String getUsername();
        String getEmail();
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 📊 REQUÊTES SQL GÉNÉRÉES
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * SANS projection (entité complète) :
     * 
     * SELECT id, username, email, password, first_name, last_name,
     *        phone_number, address, created_at, updated_at, 
     *        last_login, active
     * FROM users;
     * 
     * ⚠️ Récupère TOUS les champs (inefficace si on n'en veut que 2)
     */
    
    /**
     * AVEC projection (UserSummary) :
     * 
     * SELECT username, email
     * FROM users;
     * 
     * ✅ Récupère UNIQUEMENT les champs nécessaires (optimisé)
     */
    
    // ═══════════════════════════════════════════════════════════════
    // 💡 EXEMPLES D'UTILISATION RÉELLE
    // ═══════════════════════════════════════════════════════════════
    
    static class ExemplesUtilisation {
        
        // Injection du repository
        // @Autowired
        private UserRepository userRepository;
        
        /**
         * Exemple 1 : Liste d'utilisateurs pour un dropdown
         * On veut juste id et username, pas besoin du reste
         */
        public void exempleDropdown() {
            interface UserOption {
                Long getId();
                String getUsername();
            }
            
            // List<UserOption> options = userRepository.findAllBy(UserOption.class);
            // options.forEach(opt -> 
            //     System.out.println(opt.getId() + ": " + opt.getUsername())
            // );
        }
        
        /**
         * Exemple 2 : Statistiques utilisateurs
         * On veut juste compter les actifs/inactifs
         */
        public void exempleStatistiques() {
            interface UserStats {
                boolean isActive();
                LocalDateTime getCreatedAt();
            }
            
            // List<UserStats> stats = userRepository.findAllBy(UserStats.class);
            // long activeUsers = stats.stream().filter(UserStats::isActive).count();
        }
        
        /**
         * Exemple 3 : Export CSV
         * On veut certains champs pour un export
         */
        public void exempleExport() {
            interface UserExport {
                String getUsername();
                String getEmail();
                String getPhoneNumber();
                LocalDateTime getCreatedAt();
            }
            
            // List<UserExport> exports = userRepository.findAllBy(UserExport.class);
            // Générer CSV avec ces données
        }
    }
    
    // ═══════════════════════════════════════════════════════════════
    // ⚠️ PIÈGES COURANTS À ÉVITER
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * ❌ ERREUR 1 : Nom de méthode incorrect
     */
    interface MauvaiseProjection {
        // ❌ La méthode doit correspondre EXACTEMENT au getter de l'entité
        String getUserName();  // Si le champ est "username", ça ne marche pas !
        
        // ✅ Correct : correspond à getUsername()
        String getUsername();
    }
    
    /**
     * ❌ ERREUR 2 : Accéder à des champs non définis
     */
    void erreurAccesChamp() {
        UserSummary summary = null; // obtenu du repository
        
        // ✅ OK
        String username = summary.getUsername();
        String email = summary.getEmail();
        
        // ❌ ERREUR : password n'existe pas dans UserSummary
        // String password = summary.getPassword(); // Erreur de compilation
    }
    
    /**
     * ❌ ERREUR 3 : Oublier la projection dans le repository
     */
    interface MauvaisRepository { // extends JpaRepository<User, Long>
        // ❌ Retourne User complet (pas optimisé)
        List<User> findAll();
        
        // ✅ Retourne seulement les champs de UserSummary
        List<UserSummary> findAllProjectedBy();
    }
    
    // ═══════════════════════════════════════════════════════════════
    // 🎯 QUAND UTILISER LES PROJECTIONS ?
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * ✅ UTILISEZ les projections quand :
     * 
     * 1. Vous avez une entité avec beaucoup de champs
     * 2. Vous voulez afficher seulement quelques infos (liste, dropdown)
     * 3. Vous voulez cacher des données sensibles (password, etc.)
     * 4. Vous optimisez les performances (moins de données = plus rapide)
     * 5. Vous créez une API publique (exposer seulement le nécessaire)
     * 
     * ❌ N'UTILISEZ PAS les projections quand :
     * 
     * 1. Vous avez besoin de toute l'entité
     * 2. Vous devez modifier les données (les projections sont read-only)
     * 3. Vous avez besoin de logique métier complexe (utilisez des DTOs)
     * 4. L'entité est déjà petite (3-4 champs)
     */
}

/**
 * 📚 RÉSUMÉ
 * 
 * Projection = Interface qui définit quels champs récupérer d'une entité
 * 
 * Syntaxe de base :
 * ```
 * interface MonProjection {
 *     Type getChamp();
 * }
 * ```
 * 
 * Dans le repository :
 * ```
 * List<MonProjection> findAllProjectedBy();
 * ```
 * 
 * Avantages :
 * - ✅ Performance (moins de données)
 * - ✅ Sécurité (masquer des champs sensibles)
 * - ✅ Simplicité (pas de mapping manuel)
 * 
 * C'est tout ! Les projections sont simples mais très puissantes.
 */

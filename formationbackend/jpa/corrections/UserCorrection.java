package jpa.corrections;

// Note : Annotations commentées pour éviter les erreurs de compilation en phase théorique
// import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ✅ CORRECTION - Exercice JPA 1
 * 
 * Cette correction montre comment créer une entité JPA complète
 * avec toutes les bonnes pratiques
 */

// @Entity
// @Table(name = "users", uniqueConstraints = {
//     @UniqueConstraint(name = "uk_username", columnNames = "username"),
//     @UniqueConstraint(name = "uk_email", columnNames = "email")
// })
public class UserCorrection {
    
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    // @Column(nullable = false)
    private String email;
    
    private Integer age;
    
    // @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeur vide requis par JPA
    protected UserCorrection() {
    }
    
    // Constructeur pour créer un nouvel utilisateur
    public UserCorrection(String username, String email, Integer age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }
    
    // Callback JPA - appelé automatiquement avant l'insertion
    // @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    // Callback JPA - appelé automatiquement avant la mise à jour
    // @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // equals() et hashCode() basés sur l'identifiant métier (username)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCorrection)) return false;
        UserCorrection user = (UserCorrection) o;
        return username != null && username.equals(user.getUsername());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                '}';
    }
}

/**
 * 📝 EXPLICATIONS DÉTAILLÉES
 * 
 * 1. @Entity
 *    Marque cette classe comme une entité JPA persistante
 *    JPA créera une table correspondante dans la base de données
 * 
 * 2. @Table(name = "users")
 *    Spécifie le nom de la table (par défaut: nom de la classe en minuscules)
 *    Les uniqueConstraints définissent des contraintes d'unicité au niveau table
 * 
 * 3. @Id et @GeneratedValue
 *    @Id : marque la clé primaire
 *    GenerationType.IDENTITY : utilise l'auto-increment de la BD
 *    Autres options : SEQUENCE, TABLE, AUTO
 * 
 * 4. @Column
 *    - name : nom de la colonne en BD (défaut: nom du champ)
 *    - nullable : peut être null ? (défaut: true)
 *    - unique : doit être unique ? (défaut: false)
 *    - length : longueur max pour les String (défaut: 255)
 *    - updatable : peut être mis à jour ? (défaut: true)
 * 
 * 5. Constructeur vide
 *    OBLIGATOIRE pour JPA (peut être protected ou public)
 *    JPA l'utilise pour créer des instances via réflexion
 * 
 * 6. @PrePersist et @PreUpdate
 *    Callbacks du cycle de vie de l'entité
 *    @PrePersist : avant l'insertion
 *    @PreUpdate : avant la mise à jour
 *    Autres callbacks : @PostPersist, @PostUpdate, @PreRemove, @PostRemove
 * 
 * 7. equals() et hashCode()
 *    IMPORTANT pour les collections et les relations bidirectionnelles
 *    - Ne pas utiliser l'ID auto-généré (null avant persist)
 *    - Utiliser un identifiant métier naturel (username, email, etc.)
 *    - Rester cohérent entre equals() et hashCode()
 * 
 * 8. toString()
 *    Utile pour le debug
 *    Attention : éviter de charger des relations lazy dans toString()
 * 
 * ⚠️ PIÈGES COURANTS À ÉVITER
 * 
 * 1. Oublier le constructeur vide
 *    → Erreur : No default constructor for entity
 * 
 * 2. Utiliser l'ID dans equals()/hashCode()
 *    → Problème avec les collections avant persist()
 * 
 * 3. Accéder à des relations LAZY dans toString()
 *    → LazyInitializationException hors transaction
 * 
 * 4. Ne pas spécifier nullable=false pour les champs obligatoires
 *    → Erreurs silencieuses ou tardives
 * 
 * 5. Oublier @Column(updatable = false) pour createdAt
 *    → Date de création modifiée à tort
 */

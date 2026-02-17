package jpa;

import java.lang.reflect.GenericArrayType;
import java.time.LocalDateTime;

/**
 * EXERCICE 1 : Créer votre première entité JPA
 * 
 * Objectif : Comprendre les annotations de base pour mapper une classe Java 
 *            à une table de base de données.
 * 
 * INSTRUCTIONS :
 * 
 * 1. Créez une classe User avec les propriétés suivantes :
 *    - id (Long) : clé primaire auto-générée
 *    - username (String) : unique, non null, max 50 caractères
 *    - email (String) : unique, non null
 *    - age (Integer) : peut être null
 *    - createdAt (LocalDateTime) : date de création automatique
 

 * 
 * 2. Utilisez les annotations appropriées :
 *    - @Entity : marquer la classe comme entité JPA
 *    - @Table(name = "users") : spécifier le nom de la table
 *    - @Id : marquer la clé primaire
 *    - @GeneratedValue : génération automatique de l'ID
 *    - @Column : spécifier les contraintes de colonnes
 * 
 * 3. Ajoutez des constructeurs :
 *    - Un constructeur vide (requis par JPA)
 *    - Un constructeur avec tous les champs (sauf id et createdAt)
 * 
 * 4. BONUS : Ajoutez une méthode @PrePersist pour initialiser automatiquement
 *            la date de création avant l'insertion en base
 * 
 * EXEMPLE DE STRUCTURE :
 */

// TODO: Ajouter les imports nécessaires
// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// TODO: Annoter la classe avec @Entity et @Table

public class ExerciceJPA1 {
    
    // TODO: Déclarer les propriétés avec les bonnes annotations
      
    // TODO: Créer un constructeur vide
    
    // TODO: Créer un constructeur avec paramètres
    
    // TODO: Générer les getters/setters
    
    // TODO: BONUS - Ajouter @PrePersist pour createdAt
    
    // TODO: Override toString() pour faciliter le debug
 
    // @Entity
    // @Table(name = "users")
    static class User {
        // @Id
        // @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        // @Column(unique=true, nullable=false, length=50, name="username")
        private String username;
        
        // @Column(unique=true, nullable=false, name="email")
        private String email;
        
        // @Column(name="age")
        private Integer age;
        
        // @Column(name="created_at", updatable=false)
        private LocalDateTime createdAt;
        
        // @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
        
        public User(){}
        
        public User(
            String username,
            String email,
            Integer age
        ){
            this.username = username;
            this.email = email;
            this.age = age;
        }

        public Integer getAge() {
            return age;
        }
        
        public String getEmail() {
            return email;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setAge(Integer age) {
            this.age = age;
        }
    
        public void setEmail(String email) {
            this.email = email;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        // ⚠️ Pas de setId() - l'ID est géré par la base de données
        // ⚠️ Pas de setCreatedAt() - la date est automatique et non modifiable
        
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
  
}

/**
 * QUESTIONS DE RÉFLEXION :
 * 
 * 1. Pourquoi JPA requiert-il un constructeur sans paramètres ?
 * JPA required un constructeur sans parametre par ce quil instancie a la creation de la table 0 parametre
 * 
 * 2. Quelle est la différence entre @GeneratedValue(strategy = GenerationType.IDENTITY)
 *    et @GeneratedValue(strategy = GenerationType.AUTO) ?
 * IDENTITY va faire une incrementation de id , AUTO va gerer automatiquement
 * 
 * 3. Que se passe-t-il si on oublie @Column(unique = true) sur un champ ?
 * Si on OUBLIE unique=true, on PEUT avoir des doublons (pas de contrainte d'unicité).
 * Avec unique=true → empêche les doublons ✅
 * 
 * 4. Comment JPA gère-t-il les noms de tables et colonnes si on ne spécifie 
 *    pas @Table et @Column ?
 *     Il utilise le nom de la table en snakecase avec s pour les tables exemple User deviens users , productNumber devient product_number
 */

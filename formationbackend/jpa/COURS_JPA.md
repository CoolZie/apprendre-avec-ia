# 📚 Formation Backend - Module JPA/Hibernate

## 🎯 Objectifs du module
- Comprendre le mapping objet-relationnel (ORM)
- Maîtriser les annotations JPA
- Gérer les relations entre entités
- Utiliser Spring Data JPA efficacement
- Appliquer les bonnes pratiques de persistance

---

## 1️⃣ Introduction à JPA

### Qu'est-ce que JPA ?
**JPA (Java Persistence API)** est une spécification Java qui décrit une interface commune pour les frameworks ORM (Object-Relational Mapping).

**Hibernate** est l'implémentation de référence de JPA.

### Pourquoi utiliser JPA ?
✅ Abstraction du SQL  
✅ Portabilité entre bases de données  
✅ Gestion automatique du mapping objet-relationnel  
✅ Cache et optimisation  
✅ Requêtes type-safe avec Criteria API  

---

## 2️⃣ Annotations essentielles

### Entité de base
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    private Integer age; // Column optionnelle
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Stratégies de génération d'ID

| Stratégie | Description |
|-----------|-------------|
| `IDENTITY` | Auto-increment de la BD (MySQL, PostgreSQL) |
| `SEQUENCE` | Utilise une séquence (Oracle, PostgreSQL) |
| `TABLE` | Table spéciale pour générer les IDs |
| `AUTO` | JPA choisit automatiquement |

---

## 3️⃣ Relations entre entités

### @OneToMany et @ManyToOne

```java
// Côté "One" (Author)
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    // Méthode helper pour maintenir la bidirectionnalité
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
}

// Côté "Many" (Book)
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
}
```

### @ManyToMany

```java
@Entity
public class Student {
    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

---

## 4️⃣ Spring Data JPA

### Repository Interface

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Méthodes automatiques : save, findById, findAll, delete, etc.
    
    // Query methods (générées automatiquement)
    Optional<User> findByUsername(String username);
    List<User> findByAgeGreaterThan(Integer age);
    List<User> findByUsernameContainingIgnoreCase(String keyword);
    
    // Query JPQL personnalisée
    @Query("SELECT u FROM User u WHERE u.age BETWEEN :min AND :max")
    List<User> findUsersInAgeRange(@Param("min") Integer min, @Param("max") Integer max);
    
    // Query native SQL
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain%", nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);
}
```

### Méthodes de requête (Query Methods)

| Mot-clé | Exemple | JPQL équivalent |
|---------|---------|-----------------|
| `findBy` | `findByUsername` | `WHERE username = ?` |
| `And` | `findByUsernameAndAge` | `WHERE username = ? AND age = ?` |
| `Or` | `findByUsernameOrEmail` | `WHERE username = ? OR email = ?` |
| `Between` | `findByAgeBetween` | `WHERE age BETWEEN ? AND ?` |
| `LessThan` | `findByAgeLessThan` | `WHERE age < ?` |
| `GreaterThan` | `findByAgeGreaterThan` | `WHERE age > ?` |
| `Like` | `findByUsernameLike` | `WHERE username LIKE ?` |
| `OrderBy` | `findByAgeOrderByUsernameAsc` | `WHERE age = ? ORDER BY username ASC` |

---

## 5️⃣ Gestion des transactions

### @Transactional

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User createUser(User user) {
        // Tout le code dans cette méthode est dans une transaction
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public void transferUserBooks(Long fromUserId, Long toUserId) {
        User from = userRepository.findById(fromUserId)
            .orElseThrow(() -> new UserNotFoundException(fromUserId));
        User to = userRepository.findById(toUserId)
            .orElseThrow(() -> new UserNotFoundException(toUserId));
        
        // Si une exception est lancée, TOUT est annulé (rollback)
        List<Book> books = new ArrayList<>(from.getBooks());
        books.forEach(book -> {
            from.removeBook(book);
            to.addBook(book);
        });
    }
}
```

---

## 6️⃣ Fetch Types et N+1 Problem

### FetchType.LAZY vs EAGER

```java
// LAZY : charge l'entité associée uniquement quand elle est accédée
@ManyToOne(fetch = FetchType.LAZY)
private Author author;

// EAGER : charge l'entité associée immédiatement
@ManyToOne(fetch = FetchType.EAGER)
private Author author;
```

### Le problème N+1

```java
// ❌ MAUVAIS : génère N+1 requêtes
List<Book> books = bookRepository.findAll(); // 1 requête
books.forEach(book -> {
    System.out.println(book.getAuthor().getName()); // N requêtes
});

// ✅ BON : utilise un JOIN
@Query("SELECT b FROM Book b JOIN FETCH b.author")
List<Book> findAllWithAuthors(); // 1 seule requête
```

---

## 7️⃣ Bonnes pratiques

### ✅ DO
1. Utilisez `@Transactional(readOnly = true)` pour les lectures
2. Maintenez la bidirectionnalité dans les relations
3. Utilisez LAZY par défaut, EAGER avec parcimonie
4. Évitez les requêtes dans des boucles (N+1)
5. Utilisez des DTOs pour les projections
6. Implémentez `equals()` et `hashCode()` correctement

### ❌ DON'T
1. Ne chargez pas tout en EAGER
2. N'oubliez pas le constructeur vide
3. Ne modifiez pas les entités hors transaction
4. Ne retournez pas d'entités directement aux contrôleurs (utilisez des DTOs)
5. N'utilisez pas `CascadeType.ALL` partout

---

## 8️⃣ Pagination et Tri

```java
// Repository
Page<User> findAll(Pageable pageable);

// Service
public Page<User> getUsersPaginated(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return userRepository.findAll(pageable);
}

// Utilisation
Page<User> users = userService.getUsersPaginated(0, 10, "username");
System.out.println("Total pages: " + users.getTotalPages());
System.out.println("Total elements: " + users.getTotalElements());
```

---

## 9️⃣ Projections et DTOs

```java
// Interface de projection
public interface UserSummary {
    String getUsername();
    String getEmail();
}

// Repository
List<UserSummary> findAllProjectedBy();

// DTO classique
public record UserDTO(Long id, String username, String email) {
    public static UserDTO from(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
```

---

## 🎓 Prochaines étapes

Après avoir maîtrisé JPA, vous pourrez aborder :
1. **Spring Boot** : Création d'APIs REST
2. **Spring Security** : Authentification et autorisation
3. **Tests** : JUnit, Mockito, TestContainers
4. **Architecture** : Clean Architecture, Hexagonal
5. **Microservices** : Spring Cloud, Kafka

---

## 📚 Ressources

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Baeldung JPA Tutorials](https://www.baeldung.com/jpa-hibernate-persistence)

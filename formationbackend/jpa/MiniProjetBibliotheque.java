package jpa;

/**
 * 🎯 MINI-PROJET : Système de Gestion de Bibliothèque
 * 
 * Objectif : Appliquer tous les concepts JPA dans un projet réaliste
 * 
 * CONTEXTE :
 * Vous devez créer un système de gestion de bibliothèque qui permet de :
 * - Gérer des auteurs, livres, membres et emprunts
 * - Rechercher des livres par titre, auteur, catégorie
 * - Enregistrer les emprunts et retours de livres
 * - Générer des statistiques (livres les plus empruntés, etc.)
 * 
 * ═════════════════════════════════════════════════════════════════════
 * PARTIE 1 : Modèle de données (Entités JPA)
 * ═════════════════════════════════════════════════════════════════════
 * 
 * 1. Author (Auteur)
 *    - id : Long
 *    - firstName : String (50 caractères max)
 *    - lastName : String (50 caractères max, non null)
 *    - birthYear : Integer
 *    - nationality : String
 *    - books : List<Book> (OneToMany)
 *    - biography : String (texte long, optionnel)
 * 
 * 2. Book (Livre)
 *    - id : Long
 *    - title : String (200 caractères max, non null)
 *    - isbn : String (unique, non null)
 *    - publicationYear : Integer
 *    - numberOfPages : Integer
 *    - category : BookCategory (enum: FICTION, NON_FICTION, SCIENCE, HISTORY, etc.)
 *    - author : Author (ManyToOne)
 *    - loans : List<Loan> (OneToMany)
 *    - available : Boolean (calculé : true si aucun prêt actif)
 * 
 * 3. Member (Membre)
 *    - id : Long
 *    - firstName : String
 *    - lastName : String
 *    - email : String (unique, non null)
 *    - phoneNumber : String
 *    - membershipDate : LocalDate (date d'inscription)
 *    - active : Boolean (membre actif ou non)
 *    - loans : List<Loan> (OneToMany)
 * 
 * 4. Loan (Emprunt)
 *    - id : Long
 *    - book : Book (ManyToOne)
 *    - member : Member (ManyToOne)
 *    - loanDate : LocalDate (date d'emprunt)
 *    - dueDate : LocalDate (date de retour prévue, 14 jours après l'emprunt)
 *    - returnDate : LocalDate (date de retour réelle, null si non retourné)
 *    - status : LoanStatus (enum: ACTIVE, RETURNED, OVERDUE)
 * 
 * ═════════════════════════════════════════════════════════════════════
 * PARTIE 2 : Repositories
 * ═════════════════════════════════════════════════════════════════════
 * 
 * Créez les interfaces repository suivantes :
 * 
 * 1. AuthorRepository
 *    - findByLastName(String lastName)
 *    - findByNationality(String nationality)
 *    - findAuthorsWithBooks() (avec FETCH JOIN)
 * 
 * 2. BookRepository
 *    - findByTitleContainingIgnoreCase(String keyword)
 *    - findByAuthor(Author author)
 *    - findByCategory(BookCategory category)
 *    - findByIsbn(String isbn)
 *    - findAvailableBooks() (livres sans prêt actif)
 *    - findByAuthorLastName(String lastName)
 * 
 * 3. MemberRepository
 *    - findByEmail(String email)
 *    - findByActiveTrue()
 *    - findMembersWithActiveLoans()
 * 
 * 4. LoanRepository
 *    - findByMember(Member member)
 *    - findByBook(Book book)
 *    - findByStatus(LoanStatus status)
 *    - findOverdueLoans(LocalDate currentDate)
 *    - findActiveLoansForMember(Member member)
 * 
 * ═════════════════════════════════════════════════════════════════════
 * PARTIE 3 : Services métier
 * ═════════════════════════════════════════════════════════════════════
 * 
 * 1. LibraryService
 *    Méthodes :
 *    - createLoan(Long memberId, Long bookId) : Loan
 *      → Vérifie que le membre est actif
 *      → Vérifie que le livre est disponible
 *      → Vérifie que le membre n'a pas déjà 3 emprunts actifs
 *      → Crée l'emprunt avec dueDate = today + 14 jours
 * 
 *    - returnBook(Long loanId) : Loan
 *      → Met à jour la date de retour
 *      → Change le status à RETURNED
 * 
 *    - getMemberLoans(Long memberId) : List<Loan>
 *      → Retourne tous les emprunts d'un membre
 * 
 *    - searchBooks(String keyword) : List<Book>
 *      → Recherche par titre ou nom d'auteur
 * 
 *    - getBooksByCategoryOrderedByTitle(BookCategory category) : List<Book>
 *      → Livres d'une catégorie triés par titre
 * 
 *    - checkOverdueLoans() : List<Loan>
 *      → Retourne les emprunts en retard et met à jour leur status
 * 
 * 2. StatisticsService
 *    Méthodes :
 *    - getMostBorrowedBooks(int limit) : List<BookStatistics>
 *      → Top N livres les plus empruntés
 * 
 *    - getMostActiveMembers(int limit) : List<MemberStatistics>
 *      → Top N membres avec le plus d'emprunts
 * 
 *    - getAverageLoansPerBook() : Double
 *      → Nombre moyen d'emprunts par livre
 * 
 *    - getCategoryStatistics() : Map<BookCategory, Long>
 *      → Nombre de livres par catégorie
 * 
 * ═════════════════════════════════════════════════════════════════════
 * PARTIE 4 : Règles métier et validations
 * ═════════════════════════════════════════════════════════════════════
 * 
 * Implémentez les règles suivantes :
 * 
 * 1. Un membre ne peut pas emprunter plus de 3 livres simultanément
 * 2. Un membre inactif ne peut pas emprunter de livres
 * 3. Un livre déjà emprunté ne peut pas être emprunté à nouveau
 * 4. Un emprunt a une durée de 14 jours
 * 5. Un emprunt en retard de plus de 7 jours suspend le compte du membre
 * 6. Un ISBN doit être unique
 * 7. L'email d'un membre doit être unique
 * 
 * ═════════════════════════════════════════════════════════════════════
 * PARTIE 5 : Tests et données de démonstration
 * ═════════════════════════════════════════════════════════════════════
 * 
 * Créez une classe DataInitializer pour charger des données de test :
 * - 5 auteurs minimum
 * - 15 livres minimum (répartis entre les auteurs)
 * - 10 membres minimum
 * - Quelques emprunts (certains actifs, certains retournés, certains en retard)
 * 
 * ═════════════════════════════════════════════════════════════════════
 * BONUS (Fonctionnalités avancées)
 * ═════════════════════════════════════════════════════════════════════
 * 
 * 1. Système de réservation : un membre peut réserver un livre emprunté
 * 2. Historique : garder un historique complet de tous les emprunts
 * 3. Amendes : calculer automatiquement les amendes pour retard
 * 4. Notifications : générer des rappels pour les retours proches
 * 5. Recherche avancée : recherche par année, nombre de pages, etc.
 * 6. Évaluation : les membres peuvent noter les livres (1-5 étoiles)
 * 
 * ═════════════════════════════════════════════════════════════════════
 * STRUCTURE ATTENDUE
 * ═════════════════════════════════════════════════════════════════════
 * 
 * jpa/miniprojet/
 * ├── model/
 * │   ├── Author.java
 * │   ├── Book.java
 * │   ├── Member.java
 * │   ├── Loan.java
 * │   ├── BookCategory.java (enum)
 * │   └── LoanStatus.java (enum)
 * ├── repository/
 * │   ├── AuthorRepository.java
 * │   ├── BookRepository.java
 * │   ├── MemberRepository.java
 * │   └── LoanRepository.java
 * ├── service/
 * │   ├── LibraryService.java
 * │   └── StatisticsService.java
 * ├── exception/
 * │   ├── BookNotAvailableException.java
 * │   ├── MemberInactiveException.java
 * │   └── LoanLimitExceededException.java
 * └── DataInitializer.java
 * 
 * ═════════════════════════════════════════════════════════════════════
 * CRITÈRES DE RÉUSSITE
 * ═════════════════════════════════════════════════════════════════════
 * 
 * ✅ Toutes les entités sont correctement mappées avec JPA
 * ✅ Les relations sont bidirectionnelles et cohérentes
 * ✅ Les méthodes de repository utilisent les conventions Spring Data JPA
 * ✅ Les services sont transactionnels
 * ✅ Les règles métier sont appliquées
 * ✅ Gestion appropriée des exceptions
 * ✅ Code propre, commenté et testable
 * 
 * BON COURAGE ! 🚀
 */

public class MiniProjetBibliotheque {
    // Votre implémentation complète ici
    // Suivez la structure décrite ci-dessus
}

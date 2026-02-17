

import java.util.ArrayList;
import java.util.List;

/**
 * EXERCICE 2 : Relations JPA - @OneToMany et @ManyToOne
 * 
 * Objectif : Comprendre et implémenter les relations entre entités
 * 
 * CONTEXTE :
 * Un auteur (Author) peut écrire plusieurs livres (Book).
 * Un livre appartient à un seul auteur.
 * 
 * INSTRUCTIONS :
 * 
 * PARTIE 1 - Entité Author
 * 1. Créez une classe Author avec :
 *    - id (Long)
 *    - name (String)
 *    - email (String)
 *    - books (List<Book>) : relation OneToMany
 * 
 * 2. Configurez la relation @OneToMany :
 *    - mappedBy = "author" (côté inverse de la relation)
 *    - cascade = CascadeType.ALL (propager les opérations)
 *    - orphanRemoval = true (supprimer les livres orphelins)
 * 
 * PARTIE 2 - Entité Book
 * 1. Créez une classe Book avec :
 *    - id (Long)
 *    - title (String)
 *    - isbn (String) : unique
 *    - publicationYear (Integer)
 *    - author (Author) : relation ManyToOne
 * 
 * 2. Configurez la relation @ManyToOne :
 *    - @JoinColumn(name = "author_id") : clé étrangère
 *    - fetch = FetchType.LAZY : chargement paresseux
 * 
 * PARTIE 3 - Méthodes utilitaires
 * Dans Author, ajoutez :
 *    - addBook(Book book) : ajoute un livre et maintient la bidirectionnalité
 *    - removeBook(Book book) : retire un livre
 * 
 * EXEMPLE D'UTILISATION ATTENDU :
 * 
 * Author author = new Author("J.K. Rowling", "jk@example.com");
 * Book book1 = new Book("Harry Potter 1", "ISBN-001", 1997);
 * Book book2 = new Book("Harry Potter 2", "ISBN-002", 1998);
 * 
 * author.addBook(book1);
 * author.addBook(book2);
 * // Les deux livres sont maintenant liés à l'auteur
 */

// TODO: Implémenter l'exercice complet

public class ExerciceJPA2 {
    // Votre code ici
    static class Author {
        //@Id
        //@GeneratedValue(strategy=GenerationType.IDENITY)
        private Long id;
        private String name;
        private String email;
        //@OneToMany(mappedBy= "author",cascade = CascadeType.ALL,orphanRemoval = true)
        List<Book> books = new ArrayList<>();
        public void setBooks(List<Book> books) {
            this.books = books;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<Book> getBooks() {
            return books;
        }
        public String getEmail() {
            return email;
        }
        public Long getId() {
            return id;
        }
        public String getName() {
            return name;
        }

        public void addBook(Book book){
            books.add(book);
            book.setAuthor(this);
        }
        public void removeBook(Book book){
            books.remove(book);
        }
    }

    static class Book {
        //@Id
        //@GeneratedValue(strategy=GenerationType.IDENTITY)
        private Long id;
        private String isbn;
        private String title;
        private Integer publicationYear;
        //@JoinColumn(name="author_id")
        //@ManyToOne(fetch = FetchType.LAZY)
        private Author author;
        public Author getAuthor() {
            return author;
        }
        public Long getId() {
            return id;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getIsbn() {
            return isbn;
        }
        public Integer getPublicationYear() {
            return publicationYear;
        }
        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }
        public void setPublicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
        }
        

        public void setAuthor(Author author) {
            this.author = author;
        }
        
    }
}

/**
 * QUESTIONS DE RÉFLEXION :
 * 
 * 1. Quelle est la différence entre @OneToMany(mappedBy = "author") 
 *    et @OneToMany sans mappedBy ?
 * Avec on precise le champ qui va mapper au cas ou il est different dans l'autre on laisse jpa sen charger
 * 
 * 2. Pourquoi est-il important de maintenir la bidirectionnalité 
 *    dans la méthode addBook() ?
 * Pour eviter les inconherences et ne pas avoir le book ophelin
 * 
 * 3. Que signifie "orphanRemoval = true" ?
 * Supprimer les ophelin
 * 
 * 4. Quelle est la différence entre FetchType.LAZY et FetchType.EAGER ?
 * LAZY charge lentite quand elle est accede EAGER charge immediatement
 * 
 * 5. Que se passe-t-il si on essaie d'accéder à author.getBooks() 
 *    après la fermeture de la session avec LAZY ?
 * Je ne suis pas sure 
 */

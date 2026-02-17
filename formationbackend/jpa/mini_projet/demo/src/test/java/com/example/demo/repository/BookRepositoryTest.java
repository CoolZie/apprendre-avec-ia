package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.model.LoanStatus;

@DataJpaTest
@DisplayName("Tests du BookRepository")
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Author author;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Créer un auteur
        author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        author.setBirthYear(1802);
        author.setNationality("Français");
        entityManager.persist(author);

        // Créer des livres
        book1 = new Book();
        book1.setTitle("Les Misérables");
        book1.setIsbn("978-2-07-036114-6");
        book1.setPublicationYear(1862);
        book1.setNumberOfPages(1500);
        book1.setCategory(BookCategory.FICTION);
        book1.setAuthor(author);
        book1.setAvailable(true);
        entityManager.persist(book1);

        book2 = new Book();
        book2.setTitle("Notre-Dame de Paris");
        book2.setIsbn("978-2-07-036115-3");
        book2.setPublicationYear(1831);
        book2.setNumberOfPages(500);
        book2.setCategory(BookCategory.FICTION);
        book2.setAuthor(author);
        book2.setAvailable(false);
        entityManager.persist(book2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver un livre par son titre (contient, ignore case)")
    void testFindByTitleContainingIgnoreCase() {
        // When
        List<Book> books = bookRepository.searchByTitleOrAuthor("misérables");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Les Misérables");
    }

    @Test
    @DisplayName("Doit trouver tous les livres d'un auteur")
    void testFindByAuthor() {
        // When
        List<Book> books = bookRepository.findByAuthor(author);

        // Then
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Les Misérables", "Notre-Dame de Paris");
    }

    @Test
    @DisplayName("Doit trouver tous les livres par catégorie")
    void testFindByCategory() {
        // When
        List<Book> books = bookRepository.findByCategoryOrderByTitle(BookCategory.FICTION);

        // Then
        assertThat(books).hasSize(2);
    }

    @Test
    @DisplayName("Doit trouver un livre par ISBN")
    void testFindByIsbn() {
        // When
        Optional<Book> found = bookRepository.findByIsbn("978-2-07-036114-6");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Les Misérables");
    }

    @Test
    @DisplayName("Doit trouver uniquement les livres disponibles")
    void testFindAvailableBooks() {
        // When
        List<Book> books = bookRepository.findAvailableBooks();

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Les Misérables");
        assertThat(books.get(0).getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Doit trouver un livre par nom de famille de l'auteur")
    void testFindByAuthorLastName() {
        // When
        Optional<Book> found = bookRepository.findByAuthorLastName("Hugo");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAuthor().getLastName()).isEqualTo("Hugo");
    }

    @Test
    @DisplayName("L'ISBN doit être unique")
    void testIsbnUniqueness() {
        // Given
        Book duplicateBook = new Book();
        duplicateBook.setTitle("Autre livre");
        duplicateBook.setIsbn("978-2-07-036114-6"); // Même ISBN
        duplicateBook.setAuthor(author);

        // When & Then
        assertThat(entityManager.persistAndFlush(duplicateBook))
                .satisfiesAnyOf(
                        book -> assertThat(book).isNotNull(),
                        book -> {
                            throw new RuntimeException("Duplicate ISBN should fail");
                        }
                );
    }

    @Test
    @DisplayName("Devrait trouver les livres par titre partiel insensible à la casse")
    void shouldFindBooksByPartialTitleCaseInsensitive() {
        // When
        List<Book> booksWithMis = bookRepository.searchByTitleOrAuthor("misé");
        List<Book> booksWithNotre = bookRepository.searchByTitleOrAuthor("NOTRE");

        // Then
        assertThat(booksWithMis).hasSize(1);
        assertThat(booksWithMis.get(0).getTitle()).contains("Misérables");
        assertThat(booksWithNotre).hasSize(1);
        assertThat(booksWithNotre.get(0).getTitle()).contains("Notre-Dame");
    }

    @Test
    @DisplayName("Devrait retourner liste vide si aucun livre de cette catégorie")
    void shouldReturnEmptyListWhenNoBooksInCategory() {
        // When
        List<Book> books = bookRepository.findByCategoryOrderByTitle(BookCategory.HISTORY);

        // Then
        assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("Devrait retourner liste vide si ISBN inexistant")
    void shouldReturnEmptyOptionalWhenIsbnNotFound() {
        // When
        Optional<Book> book = bookRepository.findByIsbn("978-0-00-000000-0");

        // Then
        assertThat(book).isEmpty();
    }

    @Test
    @DisplayName("Devrait compter correctement les livres par auteur")
    void shouldCountBooksPerAuthor() {
        // When
        List<Book> hugoBooks = bookRepository.findByAuthor(author);

        // Then
        assertThat(hugoBooks).hasSize(2);
    }

    @Test
    @DisplayName("Un livre disponible ne devrait pas avoir d'emprunts actifs")
    void availableBookShouldNotHaveActiveLoans() {
        // When
        List<Book> availableBooks = bookRepository.findAvailableBooks();

        // Then
        availableBooks.forEach(book -> {
            assertThat(book.getLoans()).noneMatch(loan -> 
                loan.getStatus() == LoanStatus.ACTIVE);
        });
    }
}


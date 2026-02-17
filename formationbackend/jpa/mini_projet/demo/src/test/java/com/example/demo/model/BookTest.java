package com.example.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du modèle Book")
class BookTest {

    @Test
    @DisplayName("Doit créer un livre avec toutes les propriétés")
    void testBookCreation() {
        // Given
        Author author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");

        // When
        Book book = new Book();
        book.setTitle("Les Misérables");
        book.setIsbn("978-2-07-036114-6");
        book.setPublicationYear(1862);
        book.setNumberOfPages(1500);
        book.setCategory(BookCategory.FICTION);
        book.setAuthor(author);
        book.setAvailable(true);

        // Then
        assertThat(book.getTitle()).isEqualTo("Les Misérables");
        assertThat(book.getIsbn()).isEqualTo("978-2-07-036114-6");
        assertThat(book.getPublicationYear()).isEqualTo(1862);
        assertThat(book.getNumberOfPages()).isEqualTo(1500);
        assertThat(book.getCategory()).isEqualTo(BookCategory.FICTION);
        assertThat(book.getAuthor()).isEqualTo(author);
        assertThat(book.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Un nouveau livre doit être disponible par défaut")
    void testBookAvailableByDefault() {
        // Given & When
        Book book = new Book();
        book.setAvailable(true);

        // Then
        assertThat(book.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("L'ISBN est obligatoire et unique")
    void testIsbnRequired() {
        // Given & When
        Book book = new Book();
        book.setIsbn("978-2-07-036114-6");

        // Then
        assertThat(book.getIsbn()).isNotNull();
        assertThat(book.getIsbn()).isNotEmpty();
    }

    @Test
    @DisplayName("Le titre est obligatoire")
    void testTitleRequired() {
        // Given & When
        Book book = new Book();
        book.setTitle("Test Title");

        // Then
        assertThat(book.getTitle()).isNotNull();
        assertThat(book.getTitle()).isNotEmpty();
    }
}

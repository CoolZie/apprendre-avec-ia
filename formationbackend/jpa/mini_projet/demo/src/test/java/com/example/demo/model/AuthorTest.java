package com.example.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du modèle Author")
class AuthorTest {

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        author.setBirthYear(1802);
        author.setNationality("Français");
        author.setBiography("Grand écrivain français");
        author.setBooks(new ArrayList<>());
    }

    @Test
    @DisplayName("Devrait créer un auteur avec toutes les propriétés")
    void shouldCreateAuthorWithAllProperties() {
        assertThat(author.getFirstName()).isEqualTo("Victor");
        assertThat(author.getLastName()).isEqualTo("Hugo");
        assertThat(author.getBirthYear()).isEqualTo(1802);
        assertThat(author.getNationality()).isEqualTo("Français");
        assertThat(author.getBiography()).isEqualTo("Grand écrivain français");
        assertThat(author.getBooks()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Devrait permettre d'ajouter des livres à un auteur")
    void shouldAllowAddingBooksToAuthor() {
        Book book1 = new Book();
        book1.setTitle("Les Misérables");
        
        Book book2 = new Book();
        book2.setTitle("Notre-Dame de Paris");

        author.getBooks().add(book1);
        author.getBooks().add(book2);

        assertThat(author.getBooks()).hasSize(2);
        assertThat(author.getBooks()).contains(book1, book2);
    }

    @Test
    @DisplayName("Devrait avoir un nom complet correct")
    void shouldHaveCorrectFullName() {
        String fullName = author.getFirstName() + " " + author.getLastName();
        assertThat(fullName).isEqualTo("Victor Hugo");
    }

    @Test
    @DisplayName("Deux auteurs avec le même ID devraient être égaux")
    void twoAuthorsWithSameIdShouldBeEqual() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Victor");
        author1.setLastName("Hugo");

        Author author2 = new Author();
        author2.setId(1L);
        author2.setFirstName("Victor");
        author2.setLastName("Hugo");

        assertThat(author1).isEqualTo(author2);
        assertThat(author1.hashCode()).isEqualTo(author2.hashCode());
    }

    @Test
    @DisplayName("Devrait gérer les propriétés optionnelles")
    void shouldHandleOptionalProperties() {
        Author minimalAuthor = new Author();
        minimalAuthor.setLastName("Anonyme");

        assertThat(minimalAuthor.getFirstName()).isNull();
        assertThat(minimalAuthor.getBirthYear()).isNull();
        assertThat(minimalAuthor.getNationality()).isNull();
        assertThat(minimalAuthor.getBiography()).isNull();
    }
}

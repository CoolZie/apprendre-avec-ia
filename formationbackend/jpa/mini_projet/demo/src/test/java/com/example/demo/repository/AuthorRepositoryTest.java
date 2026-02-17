package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Author;

@DataJpaTest
@DisplayName("Tests du AuthorRepository")
class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {
        author1 = new Author();
        author1.setFirstName("Victor");
        author1.setLastName("Hugo");
        author1.setBirthYear(1802);
        author1.setNationality("Français");
        author1.setBiography("Écrivain français du XIXe siècle");
        entityManager.persist(author1);

        author2 = new Author();
        author2.setFirstName("Albert");
        author2.setLastName("Camus");
        author2.setBirthYear(1913);
        author2.setNationality("Français");
        author2.setBiography("Écrivain et philosophe français");
        entityManager.persist(author2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver un auteur par son nom de famille")
    void testFindByLastName() {
        // When
        Optional<Author> found = authorRepository.findByLastName("Hugo");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Victor");
        assertThat(found.get().getLastName()).isEqualTo("Hugo");
    }

    @Test
    @DisplayName("Doit retourner vide si l'auteur n'existe pas")
    void testFindByLastName_NotFound() {
        // When
        Optional<Author> found = authorRepository.findByLastName("Inconnu");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Doit trouver tous les auteurs d'une nationalité")
    void testFindByNationality() {
        // When
        List<Author> authors = authorRepository.findByNationality("Français");

        // Then
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getLastName)
                .containsExactlyInAnyOrder("Hugo", "Camus");
    }

    @Test
    @DisplayName("Doit retourner une liste vide pour une nationalité inexistante")
    void testFindByNationality_Empty() {
        // When
        List<Author> authors = authorRepository.findByNationality("Allemand");

        // Then
        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Doit sauvegarder un nouvel auteur")
    void testSaveAuthor() {
        // Given
        Author newAuthor = new Author();
        newAuthor.setFirstName("Ernest");
        newAuthor.setLastName("Hemingway");
        newAuthor.setBirthYear(1899);
        newAuthor.setNationality("Américain");

        // When
        Author saved = authorRepository.save(newAuthor);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLastName()).isEqualTo("Hemingway");
    }
}

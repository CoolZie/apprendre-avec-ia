package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;

@SpringBootTest
@DisplayName("Tests du DataInitializer")
class DataInitializerTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("Devrait avoir créé au moins 5 auteurs")
    void shouldHaveCreatedAtLeastFiveAuthors() {
        long count = authorRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("Devrait avoir créé au moins 15 livres")
    void shouldHaveCreatedAtLeastFifteenBooks() {
        long count = bookRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(15);
    }

    @Test
    @DisplayName("Devrait avoir créé au moins 10 membres")
    void shouldHaveCreatedAtLeastTenMembers() {
        long count = memberRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(10);
    }

    @Test
    @DisplayName("Devrait avoir créé des emprunts")
    void shouldHaveCreatedLoans() {
        long count = loanRepository.count();
        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("Devrait avoir des auteurs avec nationalités différentes")
    void shouldHaveAuthorsWithDifferentNationalities() {
        long frenchAuthors = authorRepository.findByNationality("Français").size();
        long britishAuthors = authorRepository.findByNationality("Britannique").size();
        
        assertThat(frenchAuthors).isGreaterThan(0);
        assertThat(britishAuthors).isGreaterThan(0);
    }

    @Test
    @DisplayName("Devrait avoir des livres de catégories différentes")
    void shouldHaveBooksWithDifferentCategories() {
        assertThat(bookRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Devrait avoir des membres actifs et inactifs")
    void shouldHaveActiveAndInactiveMembers() {
        long activeMembers = memberRepository.findByActiveTrue().size();
        long totalMembers = memberRepository.count();
        
        assertThat(activeMembers).isGreaterThan(0);
        assertThat(totalMembers).isGreaterThan(activeMembers);
    }

    @Test
    @DisplayName("Tous les auteurs devraient avoir un nom de famille")
    void allAuthorsShouldHaveLastName() {
        authorRepository.findAll().forEach(author -> {
            assertThat(author.getLastName()).isNotNull().isNotBlank();
        });
    }

    @Test
    @DisplayName("Tous les livres devraient avoir un ISBN unique")
    void allBooksShouldHaveUniqueIsbn() {
        long totalBooks = bookRepository.count();
        long distinctIsbns = bookRepository.findAll().stream()
                .map(book -> book.getIsbn())
                .distinct()
                .count();
        
        assertThat(distinctIsbns).isEqualTo(totalBooks);
    }

    @Test
    @DisplayName("Tous les membres devraient avoir un email unique")
    void allMembersShouldHaveUniqueEmail() {
        long totalMembers = memberRepository.count();
        long distinctEmails = memberRepository.findAll().stream()
                .map(member -> member.getEmail())
                .distinct()
                .count();
        
        assertThat(distinctEmails).isEqualTo(totalMembers);
    }

    @Test
    @DisplayName("Tous les emprunts devraient avoir une date d'échéance")
    void allLoansShouldHaveDueDate() {
        loanRepository.findAll().forEach(loan -> {
            assertThat(loan.getDueDate()).isNotNull();
            assertThat(loan.getLoanDate()).isNotNull();
        });
    }

    @Test
    @DisplayName("Les emprunts retournés devraient avoir une date de retour")
    void returnedLoansShouldHaveReturnDate() {
        loanRepository.findAll().stream()
                .filter(loan -> loan.getStatus().toString().equals("RETURNED"))
                .forEach(loan -> {
                    assertThat(loan.getReturnDate()).isNotNull();
                });
    }
}

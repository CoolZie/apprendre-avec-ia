package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.Member;

@DataJpaTest
@DisplayName("Tests du LoanRepository")
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    private Member member;
    private Book book1;
    private Book book2;
    private Loan loan1;
    private Loan loan2;

    @BeforeEach
    void setUp() {
        // Créer un auteur
        Author author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        entityManager.persist(author);

        // Créer un membre
        member = new Member();
        member.setFirstName("Jean");
        member.setLastName("Dupont");
        member.setEmail("jean.dupont@email.com");
        member.setActive(true);
        entityManager.persist(member);

        // Créer des livres
        book1 = new Book();
        book1.setTitle("Les Misérables");
        book1.setIsbn("978-2-07-036114-6");
        book1.setCategory(BookCategory.FICTION);
        book1.setAuthor(author);
        book1.setAvailable(false);
        entityManager.persist(book1);

        book2 = new Book();
        book2.setTitle("Notre-Dame de Paris");
        book2.setIsbn("978-2-07-036115-3");
        book2.setCategory(BookCategory.FICTION);
        book2.setAuthor(author);
        book2.setAvailable(true);
        entityManager.persist(book2);

        // Créer des emprunts
        loan1 = new Loan();
        loan1.setBook(book1);
        loan1.setMember(member);
        loan1.setLoanDate(LocalDate.now().minusDays(5));
        loan1.setDueDate(LocalDate.now().plusDays(9));
        loan1.setStatus(LoanStatus.ACTIVE);
        entityManager.persist(loan1);

        loan2 = new Loan();
        loan2.setBook(book2);
        loan2.setMember(member);
        loan2.setLoanDate(LocalDate.now().minusDays(20));
        loan2.setDueDate(LocalDate.now().minusDays(6));
        loan2.setReturnDate(LocalDate.now().minusDays(5));
        loan2.setStatus(LoanStatus.RETURNED);
        entityManager.persist(loan2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver tous les emprunts d'un membre")
    void testFindByMember() {
        // When
        List<Loan> loans = loanRepository.findByMember(member);

        // Then
        assertThat(loans).hasSize(2);
        assertThat(loans).extracting(Loan::getStatus)
                .containsExactlyInAnyOrder(LoanStatus.ACTIVE, LoanStatus.RETURNED);
    }

    @Test
    @DisplayName("Doit trouver tous les emprunts d'un livre")
    void testFindByBook() {
        // When
        List<Loan> loans = loanRepository.findByBook(book1);

        // Then
        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).getBook().getTitle()).isEqualTo("Les Misérables");
    }

    @Test
    @DisplayName("Doit trouver tous les emprunts par statut")
    void testFindByStatus() {
        // When
        List<Loan> activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE);

        // Then
        assertThat(activeLoans).hasSize(1);
        assertThat(activeLoans.get(0).getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Doit trouver les emprunts en retard")
    void testFindOverdueLoans() {
        // Given - Créer un emprunt en retard
        Loan overdueLoan = new Loan();
        overdueLoan.setBook(book2);
        overdueLoan.setMember(member);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(30));
        overdueLoan.setDueDate(LocalDate.now().minusDays(16));
        overdueLoan.setStatus(LoanStatus.OVERDUE);
        entityManager.persist(overdueLoan);
        entityManager.flush();

        // When
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        // Then
        assertThat(overdueLoans).isNotEmpty();
        assertThat(overdueLoans).allMatch(loan -> 
            loan.getDueDate().isBefore(LocalDate.now()) && 
            loan.getStatus() != LoanStatus.RETURNED
        );
    }

    @Test
    @DisplayName("Doit trouver les emprunts actifs d'un membre")
    void testFindActiveLoansForMember() {
        // When
        List<Loan> activeLoans = loanRepository.findActiveLoansForMember(member);

        // Then
        assertThat(activeLoans).hasSize(1);
        assertThat(activeLoans.get(0).getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Doit créer un nouvel emprunt")
    void testSaveLoan() {
        // Given
        Author author = new Author();
        author.setFirstName("Albert");
        author.setLastName("Camus");
        entityManager.persist(author);

        Book newBook = new Book();
        newBook.setTitle("L'Étranger");
        newBook.setIsbn("978-2-07-036116-0");
        newBook.setCategory(BookCategory.FICTION);
        newBook.setAuthor(author);
        newBook.setAvailable(true);
        entityManager.persist(newBook);

        Loan newLoan = new Loan();
        newLoan.setBook(newBook);
        newLoan.setMember(member);
        newLoan.setLoanDate(LocalDate.now());
        newLoan.setDueDate(LocalDate.now().plusDays(14));
        newLoan.setStatus(LoanStatus.ACTIVE);

        // When
        Loan saved = loanRepository.save(newLoan);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(saved.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));
    }
}

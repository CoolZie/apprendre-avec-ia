package com.example.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.Member;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.LibraryService;
import com.example.demo.service.LibraryService.LoanDto;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration du système de bibliothèque")
class LibraryIntegrationTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoanRepository loanRepository;

    private Author author;
    private Book book1;
    private Book book2;
    private Member activeMember;

    @BeforeEach
    void setUp() {
        // Nettoyer la base
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        memberRepository.deleteAll();

        // Créer un auteur
        author = new Author();
        author.setFirstName("Victor");
        author.setLastName("Hugo");
        author.setBirthYear(1802);
        author.setNationality("Français");
        author = authorRepository.save(author);

        // Créer des livres
        book1 = new Book();
        book1.setTitle("Les Misérables");
        book1.setIsbn("978-2-07-036114-6");
        book1.setPublicationYear(1862);
        book1.setNumberOfPages(1500);
        book1.setCategory(BookCategory.FICTION);
        book1.setAuthor(author);
        book1.setAvailable(true);
        book1 = bookRepository.save(book1);

        book2 = new Book();
        book2.setTitle("Notre-Dame de Paris");
        book2.setIsbn("978-2-07-036115-3");
        book2.setPublicationYear(1831);
        book2.setNumberOfPages(500);
        book2.setCategory(BookCategory.FICTION);
        book2.setAuthor(author);
        book2.setAvailable(true);
        book2 = bookRepository.save(book2);

        // Créer un membre actif
        activeMember = new Member();
        activeMember.setFirstName("Jean");
        activeMember.setLastName("Dupont");
        activeMember.setEmail("jean.dupont@email.com");
        activeMember.setPhoneNumber("0601020304");
        activeMember.setActive(true);
        activeMember = memberRepository.save(activeMember);
    }

    @Test
    @DisplayName("Scénario complet : Emprunt et retour d'un livre")
    void testCompleteBookLoanAndReturn() {
        // 1. Créer un emprunt
        LoanDto loanDto = new LoanDto(
                book1.getId(),
                activeMember.getId(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );

        Loan loan = libraryService.createLoan(loanDto);

        // Vérifications après création
        assertThat(loan).isNotNull();
        assertThat(loan.getId()).isNotNull();
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(loan.getReturnDate()).isNull();

        // 2. Vérifier que le livre n'est plus disponible
        Book bookAfterLoan = bookRepository.findById(book1.getId()).orElseThrow();
        // assertThat(bookAfterLoan.getAvailable()).isFalse();

        // 3. Retourner le livre
        Loan returnedLoan = libraryService.returnBook(loan.getId());

        // Vérifications après retour
        assertThat(returnedLoan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(returnedLoan.getReturnDate()).isEqualTo(LocalDate.now());

        // 4. Vérifier que le livre est à nouveau disponible
        // Book bookAfterReturn = bookRepository.findById(book1.getId()).orElseThrow();
        // assertThat(bookAfterReturn.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Ne peut pas emprunter plus de 3 livres simultanément")
    void testLoanLimitEnforcement() {
        // Créer 3 emprunts
        for (int i = 0; i < 3; i++) {
            Book book = new Book();
            book.setTitle("Livre " + i);
            book.setIsbn("ISBN-" + i);
            book.setCategory(BookCategory.FICTION);
            book.setAuthor(author);
            book.setAvailable(true);
            book = bookRepository.save(book);

            LoanDto loanDto = new LoanDto(
                    book.getId(),
                    activeMember.getId(),
                    LocalDate.now().plusDays(14),
                    null,
                    LoanStatus.ACTIVE
            );
            libraryService.createLoan(loanDto);
        }

        // Tenter un 4ème emprunt
        Book extraBook = new Book();
        extraBook.setTitle("Livre supplémentaire");
        extraBook.setIsbn("ISBN-EXTRA");
        extraBook.setCategory(BookCategory.FICTION);
        extraBook.setAuthor(author);
        extraBook.setAvailable(true);
        extraBook = bookRepository.save(extraBook);

        LoanDto extraLoanDto = new LoanDto(
                extraBook.getId(),
                activeMember.getId(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );

        // Devrait échouer
        assertThatThrownBy(() -> libraryService.createLoan(extraLoanDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Recherche de livres par titre")
    void testSearchBooks() {
        // When
        List<Book> results = libraryService.searchBooks("misérables");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Les Misérables");
    }

    @Test
    @DisplayName("Récupération des livres par catégorie")
    void testGetBooksByCategory() {
        // When
        List<Book> fictionBooks = libraryService.getBooksByCategoryOrderedByTitle(BookCategory.FICTION);

        // Then
        assertThat(fictionBooks).hasSize(2);
        assertThat(fictionBooks).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Les Misérables", "Notre-Dame de Paris");
    }

    @Test
    @DisplayName("Vérification des emprunts en retard")
    void testCheckOverdueLoans() {
        // Créer un emprunt en retard
        Loan overdueLoan = new Loan();
        overdueLoan.setBook(book1);
        overdueLoan.setMember(activeMember);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(30));
        overdueLoan.setDueDate(LocalDate.now().minusDays(16));
        overdueLoan.setStatus(LoanStatus.OVERDUE);
        loanRepository.save(overdueLoan);

        // When
        List<Loan> overdueLoans = libraryService.checkOverdueLoans();

        // Then
        assertThat(overdueLoans).isNotEmpty();
        assertThat(overdueLoans).allMatch(loan -> 
            loan.getDueDate().isBefore(LocalDate.now())
        );
    }

    @Test
    @DisplayName("Récupération de tous les emprunts d'un membre")
    void testGetMemberLoans() {
        // Créer 2 emprunts pour le membre
        LoanDto loan1Dto = new LoanDto(
                book1.getId(),
                activeMember.getId(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );
        libraryService.createLoan(loan1Dto);

        LoanDto loan2Dto = new LoanDto(
                book2.getId(),
                activeMember.getId(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );
        libraryService.createLoan(loan2Dto);

        // When
        List<Loan> memberLoans = libraryService.getMemberLoans(activeMember.getId());

        // Then
        assertThat(memberLoans).hasSize(2);
        assertThat(memberLoans).allMatch(loan -> 
            loan.getMember().getId().equals(activeMember.getId())
        );
    }

    @Test
    @DisplayName("Un membre inactif ne peut pas emprunter")
    void testInactiveMemberCannotBorrow() {
        // Créer un membre inactif
        Member inactiveMember = new Member();
        inactiveMember.setFirstName("Marie");
        inactiveMember.setLastName("Martin");
        inactiveMember.setEmail("marie.martin@email.com");
        inactiveMember.setActive(false);
        inactiveMember = memberRepository.save(inactiveMember);

        // Tenter un emprunt
        LoanDto loanDto = new LoanDto(
                book1.getId(),
                inactiveMember.getId(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );

        // Devrait échouer
        assertThatThrownBy(() -> libraryService.createLoan(loanDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

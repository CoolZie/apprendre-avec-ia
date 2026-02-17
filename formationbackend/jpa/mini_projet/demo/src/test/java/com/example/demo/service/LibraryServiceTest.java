package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.Member;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.LibraryService.LoanDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du LibraryService")
class LibraryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LibraryService libraryService;

    private Member activeMember;
    private Member inactiveMember;
    private Book availableBook;
    private Book unavailableBook;
    private Author author;

    @BeforeEach
    void setUp() {
        // Créer un auteur
        author = new Author();
        author.setId(1L);
        author.setFirstName("Victor");
        author.setLastName("Hugo");

        // Créer un membre actif sans emprunts
        activeMember = new Member();
        activeMember.setId(1L);
        activeMember.setFirstName("Jean");
        activeMember.setLastName("Dupont");
        activeMember.setEmail("jean.dupont@email.com");
        activeMember.setActive(true);
        activeMember.setLoans(new ArrayList<>());

        // Créer un membre inactif
        inactiveMember = new Member();
        inactiveMember.setId(2L);
        inactiveMember.setFirstName("Marie");
        inactiveMember.setLastName("Martin");
        inactiveMember.setEmail("marie.martin@email.com");
        inactiveMember.setActive(false);
        inactiveMember.setLoans(new ArrayList<>());

        // Créer un livre disponible
        availableBook = new Book();
        availableBook.setId(1L);
        availableBook.setTitle("Les Misérables");
        availableBook.setIsbn("978-2-07-036114-6");
        availableBook.setCategory(BookCategory.FICTION);
        availableBook.setAuthor(author);
        availableBook.setAvailable(true);

        // Créer un livre non disponible
        unavailableBook = new Book();
        unavailableBook.setId(2L);
        unavailableBook.setTitle("Notre-Dame de Paris");
        unavailableBook.setIsbn("978-2-07-036115-3");
        unavailableBook.setCategory(BookCategory.FICTION);
        unavailableBook.setAuthor(author);
        unavailableBook.setAvailable(false);
    }

    @Test
    @DisplayName("Doit créer un emprunt avec succès")
    void testCreateLoan_Success() {
        // Given
        LoanDto loanDto = new LoanDto(1L, 1L, LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        
        Loan expectedLoan = new Loan(availableBook, activeMember, loanDto.dueDate(), null, LoanStatus.ACTIVE);
        expectedLoan.setId(1L);
        when(loanRepository.save(any(Loan.class))).thenReturn(expectedLoan);

        // When
        Loan result = libraryService.createLoan(loanDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBook()).isEqualTo(availableBook);
        assertThat(result.getMember()).isEqualTo(activeMember);
        assertThat(result.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Doit échouer si le membre est inactif")
    void testCreateLoan_InactiveMember() {
        // Given
        LoanDto loanDto = new LoanDto(1L, 2L, LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        when(memberRepository.findById(2L)).thenReturn(Optional.of(inactiveMember));

        // When & Then
        assertThatThrownBy(() -> libraryService.createLoan(loanDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Doit échouer si le livre n'est pas disponible")
    void testCreateLoan_BookNotAvailable() {
        // Given
        LoanDto loanDto = new LoanDto(2L, 1L, LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(unavailableBook));

        // When & Then
        assertThatThrownBy(() -> libraryService.createLoan(loanDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Doit échouer si le membre a déjà 3 emprunts actifs")
    void testCreateLoan_LoanLimitExceeded() {
        // Given
        // Ajouter 3 emprunts actifs au membre
        List<Loan> loans = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Loan loan = new Loan();
            loan.setStatus(LoanStatus.ACTIVE);
            loans.add(loan);
        }
        activeMember.setLoans(loans);

        LoanDto loanDto = new LoanDto(1L, 1L, LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));

        // When & Then
        assertThatThrownBy(() -> libraryService.createLoan(loanDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Doit retourner un livre avec succès")
    void testReturnBook_Success() {
        // Given
        Loan activeLoan = new Loan(availableBook, activeMember, 
                LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE);
        activeLoan.setId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);

        // When
        Loan result = libraryService.returnBook(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnDate()).isEqualTo(LocalDate.now());
        verify(loanRepository, times(1)).save(activeLoan);
    }

    @Test
    @DisplayName("Doit récupérer tous les emprunts d'un membre")
    void testGetMemberLoans() {
        // Given
        List<Loan> loans = List.of(
                new Loan(availableBook, activeMember, LocalDate.now().plusDays(14), null, LoanStatus.ACTIVE),
                new Loan(unavailableBook, activeMember, LocalDate.now(), LocalDate.now(), LoanStatus.RETURNED)
        );
        
        when(memberRepository.getReferenceById(1L)).thenReturn(activeMember);
        when(loanRepository.findByMember(activeMember)).thenReturn(loans);

        // When
        List<Loan> result = libraryService.getMemberLoans(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(loans);
    }

    @Test
    @DisplayName("Doit rechercher des livres par mot-clé")
    void testSearchBooks() {
        // Given
        List<Book> books = List.of(availableBook, unavailableBook);
        when(bookRepository.searchByTitleOrAuthor("notre")).thenReturn(List.of(unavailableBook));

        // When
        List<Book> result = libraryService.searchBooks("notre");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Notre-Dame de Paris");
    }

    @Test
    @DisplayName("Doit récupérer les livres par catégorie")
    void testGetBooksByCategoryOrderedByTitle() {
        // Given
        List<Book> books = List.of(availableBook, unavailableBook);
        when(bookRepository.findByCategoryOrderByTitle(BookCategory.FICTION)).thenReturn(books);

        // When
        List<Book> result = libraryService.getBooksByCategoryOrderedByTitle(BookCategory.FICTION);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(books);
    }

    @Test
    @DisplayName("Doit vérifier et retourner les emprunts en retard")
    void testCheckOverdueLoans() {
        // Given
        Loan overdueLoan = new Loan(availableBook, activeMember, 
                LocalDate.now().minusDays(20), null, LoanStatus.OVERDUE);
        List<Loan> overdueLoans = List.of(overdueLoan);
        
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(overdueLoans);

        // When
        List<Loan> result = libraryService.checkOverdueLoans();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(LoanStatus.OVERDUE);
    }

    @Test
    @DisplayName("Ne devrait pas permettre à un membre d'emprunter plus de 3 livres")
    void shouldNotAllowMoreThanThreeActiveLoans() {
        // Given
        Member memberWithThreeLoans = new Member();
        memberWithThreeLoans.setId(1L);
        memberWithThreeLoans.setActive(true);
        
        List<Loan> threeActiveLoans = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Loan loan = new Loan();
            loan.setStatus(LoanStatus.ACTIVE);
            threeActiveLoans.add(loan);
        }
        
        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberWithThreeLoans));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(loanRepository.findActiveLoansForMember(memberWithThreeLoans)).thenReturn(threeActiveLoans);

        // When & Then
        LoanDto loanDto = new LoanDto(1L, 1L, null, null, null);
        assertThatThrownBy(() -> libraryService.createLoan(loanDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("maximum");
    }

    @Test
    @DisplayName("Devrait retourner une liste vide quand aucun emprunt en retard")
    void shouldReturnEmptyListWhenNoOverdueLoans() {
        // Given
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(new ArrayList<>());

        // When
        List<Loan> result = libraryService.checkOverdueLoans();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devrait gérer les livres de différentes catégories")
    void shouldHandleBooksWithDifferentCategories() {
        // Given
        Book fictionBook = new Book();
        fictionBook.setCategory(BookCategory.FICTION);
        
        Book scienceBook = new Book();
        scienceBook.setCategory(BookCategory.SCIENCE);
        
        List<Book> fictionBooks = List.of(fictionBook);
        List<Book> scienceBooks = List.of(scienceBook);
        
        when(bookRepository.findByCategoryOrderByTitle(BookCategory.FICTION)).thenReturn(fictionBooks);
        when(bookRepository.findByCategoryOrderByTitle(BookCategory.SCIENCE)).thenReturn(scienceBooks);

        // When
        List<Book> fiction = libraryService.getBooksByCategoryOrderedByTitle(BookCategory.FICTION);
        List<Book> science = libraryService.getBooksByCategoryOrderedByTitle(BookCategory.SCIENCE);

        // Then
        assertThat(fiction).hasSize(1);
        assertThat(science).hasSize(1);
        assertThat(fiction.get(0).getCategory()).isEqualTo(BookCategory.FICTION);
        assertThat(science.get(0).getCategory()).isEqualTo(BookCategory.SCIENCE);
    }

    @Test
    @DisplayName("Devrait calculer correctement la date d'échéance (14 jours)")
    void shouldCalculateDueDateCorrectly() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(loanRepository.findActiveLoansForMember(activeMember)).thenReturn(new ArrayList<>());
        
        Loan savedLoan = new Loan(availableBook, activeMember, 
                LocalDate.now(), LocalDate.now().plusDays(14), LoanStatus.ACTIVE);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // When
        LoanDto loanDto = new LoanDto(1L, 1L, null, null, null);
        Loan result = libraryService.createLoan(loanDto);

        // Then
        assertThat(result.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));
        assertThat(result.getDueDate()).isAfter(result.getLoanDate());
    }

    @Test
    @DisplayName("Devrait mettre à jour le statut à RETURNED lors du retour")
    void shouldUpdateStatusToReturnedWhenReturningBook() {
        // Given
        Loan activeLoan = new Loan(availableBook, activeMember, 
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(9), LoanStatus.ACTIVE);
        activeLoan.setId(1L);
        
        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Loan result = libraryService.returnBook(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnDate()).isNotNull();
        assertThat(result.getReturnDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Devrait lancer une exception si le livre à retourner n'existe pas")
    void shouldThrowExceptionWhenReturningNonExistentLoan() {
        // Given
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> libraryService.returnBook(999L))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Devrait permettre à un membre de créer plusieurs emprunts successifs")
    void shouldAllowMemberToCreateMultipleLoansOverTime() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        
        // Premier emprunt
        when(loanRepository.findActiveLoansForMember(activeMember)).thenReturn(new ArrayList<>());
        Loan loan1 = new Loan(availableBook, activeMember, 
                LocalDate.now(), LocalDate.now().plusDays(14), LoanStatus.ACTIVE);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan1);
        
        LoanDto loanDto1 = new LoanDto(1L, 1L, null, null, null);
        Loan firstLoan = libraryService.createLoan(loanDto1);
        
        // Deuxième emprunt
        List<Loan> oneActiveLoan = List.of(firstLoan);
        when(loanRepository.findActiveLoansForMember(activeMember)).thenReturn(oneActiveLoan);
        Loan loan2 = new Loan(availableBook, activeMember, 
                LocalDate.now(), LocalDate.now().plusDays(14), LoanStatus.ACTIVE);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan2);
        
        LoanDto loanDto2 = new LoanDto(1L, 1L, null, null, null);
        Loan secondLoan = libraryService.createLoan(loanDto2);

        // Then
        assertThat(firstLoan).isNotNull();
        assertThat(secondLoan).isNotNull();
        verify(loanRepository, times(2)).save(any(Loan.class));
    }
}

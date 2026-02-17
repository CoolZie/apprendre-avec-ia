package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.BookCategory;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.StatisticsService.BookStatistics;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du StatisticsService")
class StatisticsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        // Configuration commune si nécessaire
    }

    @Test
    @DisplayName("Doit retourner les N livres les plus empruntés")
    void testGetMostBorrowedBooks() {
        // Given
        List<BookStatistics> allStats = List.of(
                new BookStatistics("Les Misérables", 50L),
                new BookStatistics("Notre-Dame de Paris", 45L),
                new BookStatistics("L'Étranger", 40L),
                new BookStatistics("La Peste", 35L),
                new BookStatistics("Germinal", 30L)
        );
        
        // TODO: Implémenter le mock après avoir créé la méthode dans BookRepository
        // when(bookRepository.findMostBorrowedBooks()).thenReturn(allStats);

        // When
        // List<BookStatistics> result = statisticsService.getMostBorrowedBooks(3);

        // Then
        // assertThat(result).hasSize(3);
        // assertThat(result.get(0).bookTitle()).isEqualTo("Les Misérables");
        // assertThat(result.get(0).borrowCount()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Doit gérer le cas où limit est supérieur au nombre de livres")
    void testGetMostBorrowedBooks_LimitGreaterThanResults() {
        // Given
        List<BookStatistics> allStats = List.of(
                new BookStatistics("Les Misérables", 50L),
                new BookStatistics("Notre-Dame de Paris", 45L)
        );
        
        // TODO: Implémenter après création de la méthode repository
        // when(bookRepository.findMostBorrowedBooks()).thenReturn(allStats);

        // When
        // List<BookStatistics> result = statisticsService.getMostBorrowedBooks(10);

        // Then
        // assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Doit retourner les N membres les plus actifs")
    void testGetMostActiveMembers() {
        // TODO: À implémenter après création de MemberStatistics record et méthode repository
        
        // Given
        // Créer des données de test pour les statistiques de membres

        // When
        // List<MemberStatistics> result = statisticsService.getMostActiveMembers(5);

        // Then
        // assertThat(result).hasSize(5);
        // Vérifier que les membres sont triés par nombre d'emprunts décroissant
    }

    @Test
    @DisplayName("Doit calculer le nombre moyen d'emprunts par livre")
    void testGetAverageLoansPerBook() {
        // Given
        when(loanRepository.count()).thenReturn(100L);
        when(bookRepository.count()).thenReturn(20L);

        // When
        // Double result = statisticsService.getAverageLoansPerBook();

        // Then
        // assertThat(result).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Doit gérer le cas où il n'y a pas de livres")
    void testGetAverageLoansPerBook_NoBooks() {
        // Given
        when(loanRepository.count()).thenReturn(100L);
        when(bookRepository.count()).thenReturn(0L);

        // When
        // Double result = statisticsService.getAverageLoansPerBook();

        // Then
        // assertThat(result).isEqualTo(0.0);
        // Ou lever une exception selon l'implémentation choisie
    }

    @Test
    @DisplayName("Doit retourner les statistiques par catégorie")
    void testGetCategoryStatistics() {
        // TODO: À implémenter après création de la méthode repository
        
        // Given
        // Map<BookCategory, Long> expectedStats = Map.of(
        //     BookCategory.FICTION, 50L,
        //     BookCategory.SCIENCE, 30L,
        //     BookCategory.HISTORY, 20L
        // );
        
        // when(bookRepository.findCategoryStatistics()).thenReturn(expectedStats);

        // When
        // Map<BookCategory, Long> result = statisticsService.getCategoryStatistics();

        // Then
        // assertThat(result).hasSize(3);
        // assertThat(result.get(BookCategory.FICTION)).isEqualTo(50L);
        // assertThat(result.get(BookCategory.SCIENCE)).isEqualTo(30L);
    }

    @Test
    @DisplayName("Doit retourner un map vide si aucune catégorie n'existe")
    void testGetCategoryStatistics_Empty() {
        // Given
        // when(bookRepository.findCategoryStatistics()).thenReturn(Map.of());

        // When
        // Map<BookCategory, Long> result = statisticsService.getCategoryStatistics();

        // Then
        // assertThat(result).isEmpty();
    }
}

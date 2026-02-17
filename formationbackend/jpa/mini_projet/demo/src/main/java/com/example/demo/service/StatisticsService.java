package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.BookCategory;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.MemberRepository;

@Service
public class StatisticsService {
    private MemberRepository memberRepository;
    private BookRepository bookRepository;

    public StatisticsService(BookRepository bookRepository, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;

    }

    public record BookStatistics(String bookTitle, Long borrowCount) {
    }

    public record MemberStatistics(String memberName, Long borrowCount) {
    }

    public record CategoryStatistics(com.example.demo.model.BookCategory category, Long bookCount) {
    }

    /// TODO: Implement getMostBorrowedBooks with proper JPQL or native query
    public List<BookStatistics> getMostBorrowedBooks(int limit) {
        // Top N livres les plus empruntés
        List<BookStatistics> allBooks = bookRepository.getMostBorrowedBooks();
        return allBooks.stream().limit(limit).toList();
    }

    public List<MemberStatistics> getMostActiveMembers(int limit) {
        // Top N membres avec le plus d'emprunts
        List<MemberStatistics> allMembers = memberRepository.getMostActiveMembers();
        return allMembers.stream().limit(limit).toList();
    }

    public Map<BookCategory, Long> getCategoryStatistics() {
        // Nombre de livres par catégorie
        return bookRepository.getCategoryStatistics()
                .stream()
                .collect(Collectors.toMap(
                        CategoryStatistics::category,
                        CategoryStatistics::bookCount));
    }

    public Double getAverageLoansPerBook() {
        // Nombre moyen d'emprunts par livre
        Double average = bookRepository.getAverageLoansPerBook();
        return average;
    }

}

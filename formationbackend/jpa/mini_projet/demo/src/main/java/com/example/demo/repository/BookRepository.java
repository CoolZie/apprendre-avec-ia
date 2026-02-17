package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.service.StatisticsService.BookStatistics;
import com.example.demo.service.StatisticsService.CategoryStatistics;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

   @Query("SELECT b FROM Book b LEFT JOIN b.author a " +
       "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
       "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);

    List<Book> findByAuthor(Author author);

    List<Book> findByCategoryOrderByTitle(BookCategory category);

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where b.available=true")
    List<Book> findAvailableBooks();

    Optional<Book> findByAuthorLastName(String lastName);

    @Query("SELECT new com.example.demo.service.StatisticsService$BookStatistics(b.title, COUNT(l)) " +
           "FROM Book b LEFT JOIN b.loans l " +
           "GROUP BY b.id, b.title " +
           "ORDER BY COUNT(l) DESC")
    List<BookStatistics> getMostBorrowedBooks();

    @Query("SELECT new com.example.demo.service.StatisticsService$CategoryStatistics(b.category, COUNT(b)) " +
           "FROM Book b " +
           "GROUP BY b.category")
    List<CategoryStatistics> getCategoryStatistics();

    @Query("SELECT CAST(COUNT(l) AS double) / CAST(COUNT(DISTINCT b) AS double) " +
           "FROM Book b LEFT JOIN b.loans l")
    Double getAverageLoansPerBook();
}

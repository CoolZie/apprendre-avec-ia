package com.example.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 200, nullable = false)
    private String title;
    @Column(unique = true, nullable = false)
    private String isbn;
    private Integer publicationYear;
    private Integer numberOfPages;
    @Enumerated(EnumType.STRING)  
private BookCategory category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Loan> loans;
    private Boolean available = true;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public Integer getPublicationYear() {
        return publicationYear;
    }
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    public Integer getNumberOfPages() {
        return numberOfPages;
    }
    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
    public BookCategory getCategory() {
        return category;
    }
    public void setCategory(BookCategory category) {
        this.category = category;
    }
    public Author getAuthor() {
        return author;
    }
    public void setAuthor(Author author) {
        this.author = author;
    }
    public List<Loan> getLoans() {
        return loans;
    }
    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
    public Boolean getAvailable() {
        return available;
    }
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    public Book(String title, String isbn, Integer publicationYear, Integer numberOfPages, BookCategory category,
            Author author, List<Loan> loans, Boolean available) {
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.numberOfPages = numberOfPages;
        this.category = category;
        this.author = author;
        this.loans = loans;
        this.available = available;
    }
    public Book() {
    }
}

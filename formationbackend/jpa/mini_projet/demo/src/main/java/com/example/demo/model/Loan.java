package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    private Book book;
    @ManyToOne()
    private Member member;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    public Loan(Book book, Member member, LocalDate dueDate, LocalDate returnDate,
            LoanStatus status) {
        this.book = book;
        this.member = member;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Loan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    @PrePersist
protected void onCreate() {
    if (loanDate == null) {
        loanDate = LocalDate.now();
    }
    if (dueDate == null) {
        dueDate = loanDate.plusWeeks(2); // 14 jours par défaut
    }
}

}

package com.example.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du modèle Loan")
class LoanTest {

    @Test
    @DisplayName("Doit créer un emprunt avec toutes les propriétés")
    void testLoanCreation() {
        // Given
        Book book = new Book();
        book.setTitle("Test Book");
        
        Member member = new Member();
        member.setFirstName("Jean");
        member.setLastName("Dupont");

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(14);

        // When
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.ACTIVE);

        // Then
        assertThat(loan.getBook()).isEqualTo(book);
        assertThat(loan.getMember()).isEqualTo(member);
        assertThat(loan.getLoanDate()).isEqualTo(loanDate);
        assertThat(loan.getDueDate()).isEqualTo(dueDate);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(loan.getReturnDate()).isNull();
    }

    @Test
    @DisplayName("Doit vérifier si un emprunt est en retard")
    void testIsOverdue() {
        // Given
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().minusDays(1)); // Date dépassée
        loan.setStatus(LoanStatus.ACTIVE);

        // Then
        assertThat(loan.getDueDate()).isBefore(LocalDate.now());
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    @DisplayName("Doit vérifier qu'un emprunt n'est pas en retard")
    void testIsNotOverdue() {
        // Given
        Loan loan = new Loan();
        loan.setDueDate(LocalDate.now().plusDays(5));
        loan.setStatus(LoanStatus.ACTIVE);

        // Then
        assertThat(loan.getDueDate()).isAfter(LocalDate.now());
    }

    @Test
    @DisplayName("Doit marquer un emprunt comme retourné")
    void testMarkAsReturned() {
        // Given
        Loan loan = new Loan();
        loan.setStatus(LoanStatus.ACTIVE);

        // When
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Then
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(loan.getReturnDate()).isEqualTo(LocalDate.now());
    }
}

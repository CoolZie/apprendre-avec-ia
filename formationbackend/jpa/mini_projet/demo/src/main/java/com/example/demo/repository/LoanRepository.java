package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Book;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.Member;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMember(Member member);

    List<Loan> findByBook(Book book);

    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.dueDate < :currentDate")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT l FROM Loan l WHERE l.member = :member AND l.status = com.example.demo.model.LoanStatus.ACTIVE")
    List<Loan> findActiveLoansForMember(@Param("member") Member member);


}

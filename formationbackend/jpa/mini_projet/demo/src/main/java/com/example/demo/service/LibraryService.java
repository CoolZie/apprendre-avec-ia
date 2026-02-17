package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BookNotAvailableException;
import com.example.demo.exception.LoanNotExistExceededException;
import com.example.demo.exception.MemberInactiveException;
import com.example.demo.model.Book;
import com.example.demo.model.BookCategory;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.Member;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;

@Service
public class LibraryService {
    private MemberRepository memberRepository;
    private BookRepository bookRepository;
    private LoanRepository loanRepository;

    public record LoanDto(Long bookId, Long memberId, LocalDate dueDate, LocalDate returnDate,
            LoanStatus status) {
    }

    public LibraryService(LoanRepository loanRepository, MemberRepository memberRepository,
            BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Loan createLoan(LoanDto loanDto) {

        Member member = memberRepository.findById(loanDto.memberId)
                .orElseThrow(() -> new MemberInactiveException("Membre inexistant"));
        List<Loan> loans = loanRepository.findActiveLoansForMember(member);
        if (member.getActive()) {
            if (loans.size() < 3) {
                Book book = bookRepository.findById(loanDto.bookId)
                        .orElseThrow(() -> new BookNotAvailableException("bookId inexistant"));
                if (book.getAvailable()) {
                    Loan loan = new Loan(book, member, loanDto.dueDate, loanDto.returnDate, loanDto.status);
                    book.setAvailable(false);
                    bookRepository.save(book);
                    member.getLoans().add(loan);
                    book.getLoans().add(loan);
                    return loanRepository.save(loan);
                } else {
                    throw new BookNotAvailableException("Livre indisponible");
                }
            } else {
                throw new BookNotAvailableException(" Membre possede deja 3 emprunts actifs");
            }
        } else {
            throw new MemberInactiveException("Membre inactif");
        }

    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotExistExceededException("Pret inexistant"));
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);
        return loanRepository.save(loan);

    }

    @Transactional(readOnly = true)
    public List<Loan> getMemberLoans(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        return loanRepository.findByMember(member);
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByCategoryOrderedByTitle(BookCategory category) {
        return bookRepository.findByCategoryOrderByTitle(category);
    }

    @Transactional
    public List<Loan> checkOverdueLoans() {
        List<Loan> loans = loanRepository.findOverdueLoans(LocalDate.now());
        loans.forEach(l -> {
            l.setStatus(LoanStatus.OVERDUE);
            if (ChronoUnit.DAYS.between(l.getDueDate(), LocalDate.now()) > 7) {
                Member member = l.getMember();
                member.setActive(false);
                memberRepository.save(member);
            }

        });

        loanRepository.saveAll(loans);
        return loans;
    }

}

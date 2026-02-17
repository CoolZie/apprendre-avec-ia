package com.example.demo;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

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

/**
 * Initialise la base de données avec des données de démonstration
 * Respecte les exigences de l'énoncé :
 * - 5 auteurs minimum
 * - 15 livres minimum
 * - 10 membres minimum
 * - Emprunts variés (actifs, retournés, en retard)
 */
@Configuration
public class DataInitializer {
    
    //@Bean
    CommandLineRunner initDatabase(
            AuthorRepository authorRepository,
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository) {
        
        return args -> {
            // ========================================
            // CRÉATION DES AUTEURS (minimum 5)
            // ========================================
            Author hugo = new Author();
            hugo.setFirstName("Victor");
            hugo.setLastName("Hugo");
            hugo.setBirthYear(1802);
            hugo.setNationality("Français");
            hugo.setBiography("Poète, dramaturge, écrivain, romancier et dessinateur romantique français");
            authorRepository.save(hugo);

            Author camus = new Author();
            camus.setFirstName("Albert");
            camus.setLastName("Camus");
            camus.setBirthYear(1913);
            camus.setNationality("Français");
            camus.setBiography("Écrivain, philosophe, romancier, dramaturge, essayiste et nouvelliste français");
            authorRepository.save(camus);

            Author orwell = new Author();
            orwell.setFirstName("George");
            orwell.setLastName("Orwell");
            orwell.setBirthYear(1903);
            orwell.setNationality("Britannique");
            orwell.setBiography("Écrivain et journaliste britannique");
            authorRepository.save(orwell);

            Author tolkien = new Author();
            tolkien.setFirstName("J.R.R.");
            tolkien.setLastName("Tolkien");
            tolkien.setBirthYear(1892);
            tolkien.setNationality("Britannique");
            tolkien.setBiography("Écrivain, poète, philologue, essayiste et professeur d'université britannique");
            authorRepository.save(tolkien);

            Author rowling = new Author();
            rowling.setFirstName("J.K.");
            rowling.setLastName("Rowling");
            rowling.setBirthYear(1965);
            rowling.setNationality("Britannique");
            rowling.setBiography("Romancière et scénariste britannique");
            authorRepository.save(rowling);

            Author asimov = new Author();
            asimov.setFirstName("Isaac");
            asimov.setLastName("Asimov");
            asimov.setBirthYear(1920);
            asimov.setNationality("Américain");
            asimov.setBiography("Écrivain américano-russe et professeur de biochimie");
            authorRepository.save(asimov);

            // ========================================
            // CRÉATION DES LIVRES (minimum 15)
            // ========================================
            
            // Livres de Victor Hugo
            Book lesMiserables = new Book();
            lesMiserables.setTitle("Les Misérables");
            lesMiserables.setIsbn("978-2-07-036668-0");
            lesMiserables.setPublicationYear(1862);
            lesMiserables.setNumberOfPages(1900);
            lesMiserables.setCategory(BookCategory.FICTION);
            lesMiserables.setAuthor(hugo);
            bookRepository.save(lesMiserables);

            Book notreDame = new Book();
            notreDame.setTitle("Notre-Dame de Paris");
            notreDame.setIsbn("978-2-07-036669-7");
            notreDame.setPublicationYear(1831);
            notreDame.setNumberOfPages(700);
            notreDame.setCategory(BookCategory.FICTION);
            notreDame.setAuthor(hugo);
            bookRepository.save(notreDame);

            // Livres d'Albert Camus
            Book letranger = new Book();
            letranger.setTitle("L'Étranger");
            letranger.setIsbn("978-2-07-036002-2");
            letranger.setPublicationYear(1942);
            letranger.setNumberOfPages(185);
            letranger.setCategory(BookCategory.FICTION);
            letranger.setAuthor(camus);
            bookRepository.save(letranger);

            Book laPeste = new Book();
            laPeste.setTitle("La Peste");
            laPeste.setIsbn("978-2-07-036003-9");
            laPeste.setPublicationYear(1947);
            laPeste.setNumberOfPages(330);
            laPeste.setCategory(BookCategory.FICTION);
            laPeste.setAuthor(camus);
            bookRepository.save(laPeste);

            Book leMythe = new Book();
            leMythe.setTitle("Le Mythe de Sisyphe");
            leMythe.setIsbn("978-2-07-032288-4");
            leMythe.setPublicationYear(1942);
            leMythe.setNumberOfPages(211);
            leMythe.setCategory(BookCategory.NON_FICTION);
            leMythe.setAuthor(camus);
            bookRepository.save(leMythe);

            // Livres de George Orwell
            Book nineteen84 = new Book();
            nineteen84.setTitle("1984");
            nineteen84.setIsbn("978-0-452-28423-4");
            nineteen84.setPublicationYear(1949);
            nineteen84.setNumberOfPages(328);
            nineteen84.setCategory(BookCategory.FICTION);
            nineteen84.setAuthor(orwell);
            bookRepository.save(nineteen84);

            Book animalFarm = new Book();
            animalFarm.setTitle("Animal Farm");
            animalFarm.setIsbn("978-0-452-28424-1");
            animalFarm.setPublicationYear(1945);
            animalFarm.setNumberOfPages(112);
            animalFarm.setCategory(BookCategory.FICTION);
            animalFarm.setAuthor(orwell);
            bookRepository.save(animalFarm);

            // Livres de J.R.R. Tolkien
            Book lordOfRings = new Book();
            lordOfRings.setTitle("The Lord of the Rings");
            lordOfRings.setIsbn("978-0-618-00222-1");
            lordOfRings.setPublicationYear(1954);
            lordOfRings.setNumberOfPages(1178);
            lordOfRings.setCategory(BookCategory.FICTION);
            lordOfRings.setAuthor(tolkien);
            bookRepository.save(lordOfRings);

            Book theHobbit = new Book();
            theHobbit.setTitle("The Hobbit");
            theHobbit.setIsbn("978-0-618-00221-4");
            theHobbit.setPublicationYear(1937);
            theHobbit.setNumberOfPages(310);
            theHobbit.setCategory(BookCategory.FICTION);
            theHobbit.setAuthor(tolkien);
            bookRepository.save(theHobbit);

            // Livres de J.K. Rowling
            Book harryPotter1 = new Book();
            harryPotter1.setTitle("Harry Potter and the Philosopher's Stone");
            harryPotter1.setIsbn("978-0-7475-3269-9");
            harryPotter1.setPublicationYear(1997);
            harryPotter1.setNumberOfPages(223);
            harryPotter1.setCategory(BookCategory.FICTION);
            harryPotter1.setAuthor(rowling);
            bookRepository.save(harryPotter1);

            Book harryPotter2 = new Book();
            harryPotter2.setTitle("Harry Potter and the Chamber of Secrets");
            harryPotter2.setIsbn("978-0-7475-3849-3");
            harryPotter2.setPublicationYear(1998);
            harryPotter2.setNumberOfPages(251);
            harryPotter2.setCategory(BookCategory.FICTION);
            harryPotter2.setAuthor(rowling);
            bookRepository.save(harryPotter2);

            // Livres d'Isaac Asimov
            Book foundation = new Book();
            foundation.setTitle("Foundation");
            foundation.setIsbn("978-0-553-29335-0");
            foundation.setPublicationYear(1951);
            foundation.setNumberOfPages(255);
            foundation.setCategory(BookCategory.SCIENCE);
            foundation.setAuthor(asimov);
            bookRepository.save(foundation);

            Book iRobot = new Book();
            iRobot.setTitle("I, Robot");
            iRobot.setIsbn("978-0-553-38256-3");
            iRobot.setPublicationYear(1950);
            iRobot.setNumberOfPages(224);
            iRobot.setCategory(BookCategory.SCIENCE);
            iRobot.setAuthor(asimov);
            bookRepository.save(iRobot);

            Book foundationEmpire = new Book();
            foundationEmpire.setTitle("Foundation and Empire");
            foundationEmpire.setIsbn("978-0-553-29337-4");
            foundationEmpire.setPublicationYear(1952);
            foundationEmpire.setNumberOfPages(247);
            foundationEmpire.setCategory(BookCategory.SCIENCE);
            foundationEmpire.setAuthor(asimov);
            bookRepository.save(foundationEmpire);

            Book secondFoundation = new Book();
            secondFoundation.setTitle("Second Foundation");
            secondFoundation.setIsbn("978-0-553-29336-7");
            secondFoundation.setPublicationYear(1953);
            secondFoundation.setNumberOfPages(256);
            secondFoundation.setCategory(BookCategory.SCIENCE);
            secondFoundation.setAuthor(asimov);
            bookRepository.save(secondFoundation);

            // ========================================
            // CRÉATION DES MEMBRES (minimum 10)
            // ========================================
            Member marie = new Member();
            marie.setFirstName("Marie");
            marie.setLastName("Dupont");
            marie.setEmail("marie.dupont@email.com");
            marie.setPhoneNumber("0601020304");
            marie.setMembershipDate(LocalDate.now().minusYears(2));
            marie.setActive(true);
            memberRepository.save(marie);

            Member pierre = new Member();
            pierre.setFirstName("Pierre");
            pierre.setLastName("Martin");
            pierre.setEmail("pierre.martin@email.com");
            pierre.setPhoneNumber("0602030405");
            pierre.setMembershipDate(LocalDate.now().minusYears(1));
            pierre.setActive(true);
            memberRepository.save(pierre);

            Member sophie = new Member();
            sophie.setFirstName("Sophie");
            sophie.setLastName("Bernard");
            sophie.setEmail("sophie.bernard@email.com");
            sophie.setPhoneNumber("0603040506");
            sophie.setMembershipDate(LocalDate.now().minusMonths(6));
            sophie.setActive(true);
            memberRepository.save(sophie);

            Member jean = new Member();
            jean.setFirstName("Jean");
            jean.setLastName("Dubois");
            jean.setEmail("jean.dubois@email.com");
            jean.setPhoneNumber("0604050607");
            jean.setMembershipDate(LocalDate.now().minusMonths(3));
            jean.setActive(true);
            memberRepository.save(jean);

            Member alice = new Member();
            alice.setFirstName("Alice");
            alice.setLastName("Petit");
            alice.setEmail("alice.petit@email.com");
            alice.setPhoneNumber("0605060708");
            alice.setMembershipDate(LocalDate.now().minusMonths(8));
            alice.setActive(true);
            memberRepository.save(alice);

            Member bob = new Member();
            bob.setFirstName("Bob");
            bob.setLastName("Robert");
            bob.setEmail("bob.robert@email.com");
            bob.setPhoneNumber("0606070809");
            bob.setMembershipDate(LocalDate.now().minusMonths(5));
            bob.setActive(true);
            memberRepository.save(bob);

            Member claire = new Member();
            claire.setFirstName("Claire");
            claire.setLastName("Moreau");
            claire.setEmail("claire.moreau@email.com");
            claire.setPhoneNumber("0607080910");
            claire.setMembershipDate(LocalDate.now().minusMonths(9));
            claire.setActive(true);
            memberRepository.save(claire);

            Member david = new Member();
            david.setFirstName("David");
            david.setLastName("Simon");
            david.setEmail("david.simon@email.com");
            david.setPhoneNumber("0608091011");
            david.setMembershipDate(LocalDate.now().minusMonths(4));
            david.setActive(false); // Membre inactif
            memberRepository.save(david);

            Member emma = new Member();
            emma.setFirstName("Emma");
            emma.setLastName("Laurent");
            emma.setEmail("emma.laurent@email.com");
            emma.setPhoneNumber("0609101112");
            emma.setMembershipDate(LocalDate.now().minusMonths(7));
            emma.setActive(true);
            memberRepository.save(emma);

            Member francois = new Member();
            francois.setFirstName("François");
            francois.setLastName("Leroy");
            francois.setEmail("francois.leroy@email.com");
            francois.setPhoneNumber("0610111213");
            francois.setMembershipDate(LocalDate.now().minusMonths(2));
            francois.setActive(true);
            memberRepository.save(francois);

            // ========================================
            // CRÉATION DES EMPRUNTS
            // ========================================
            
            // Emprunts ACTIFS (en cours)
            Loan loan1 = new Loan();
            loan1.setBook(lesMiserables);
            loan1.setMember(marie);
            loan1.setLoanDate(LocalDate.now().minusDays(5));
            loan1.setDueDate(LocalDate.now().plusDays(9)); // 14 jours au total
            loan1.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan1);

            Loan loan2 = new Loan();
            loan2.setBook(nineteen84);
            loan2.setMember(pierre);
            loan2.setLoanDate(LocalDate.now().minusDays(3));
            loan2.setDueDate(LocalDate.now().plusDays(11));
            loan2.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan2);

            Loan loan3 = new Loan();
            loan3.setBook(harryPotter1);
            loan3.setMember(sophie);
            loan3.setLoanDate(LocalDate.now().minusDays(7));
            loan3.setDueDate(LocalDate.now().plusDays(7));
            loan3.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan3);

            // Emprunts EN RETARD (OVERDUE)
            Loan loan4 = new Loan();
            loan4.setBook(letranger);
            loan4.setMember(jean);
            loan4.setLoanDate(LocalDate.now().minusDays(20));
            loan4.setDueDate(LocalDate.now().minusDays(6)); // Retard de 6 jours
            loan4.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan4);

            Loan loan5 = new Loan();
            loan5.setBook(foundation);
            loan5.setMember(alice);
            loan5.setLoanDate(LocalDate.now().minusDays(25));
            loan5.setDueDate(LocalDate.now().minusDays(11)); // Retard de 11 jours
            loan5.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan5);

            // Emprunts RETOURNÉS (RETURNED)
            Loan loan6 = new Loan();
            loan6.setBook(laPeste);
            loan6.setMember(bob);
            loan6.setLoanDate(LocalDate.now().minusDays(30));
            loan6.setDueDate(LocalDate.now().minusDays(16));
            loan6.setReturnDate(LocalDate.now().minusDays(17)); // Retourné à temps
            loan6.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan6);

            Loan loan7 = new Loan();
            loan7.setBook(theHobbit);
            loan7.setMember(claire);
            loan7.setLoanDate(LocalDate.now().minusDays(40));
            loan7.setDueDate(LocalDate.now().minusDays(26));
            loan7.setReturnDate(LocalDate.now().minusDays(25)); // Retourné à temps
            loan7.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan7);

            Loan loan8 = new Loan();
            loan8.setBook(animalFarm);
            loan8.setMember(emma);
            loan8.setLoanDate(LocalDate.now().minusDays(35));
            loan8.setDueDate(LocalDate.now().minusDays(21));
            loan8.setReturnDate(LocalDate.now().minusDays(15)); // Retourné en retard
            loan8.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan8);

            // Emprunts supplémentaires pour enrichir les données
            Loan loan9 = new Loan();
            loan9.setBook(iRobot);
            loan9.setMember(francois);
            loan9.setLoanDate(LocalDate.now().minusDays(2));
            loan9.setDueDate(LocalDate.now().plusDays(12));
            loan9.setStatus(LoanStatus.ACTIVE);
            loanRepository.save(loan9);

            Loan loan10 = new Loan();
            loan10.setBook(lordOfRings);
            loan10.setMember(marie);
            loan10.setLoanDate(LocalDate.now().minusDays(50));
            loan10.setDueDate(LocalDate.now().minusDays(36));
            loan10.setReturnDate(LocalDate.now().minusDays(35));
            loan10.setStatus(LoanStatus.RETURNED);
            loanRepository.save(loan10);

            System.out.println("✅ Base de données initialisée avec succès !");
            System.out.println("📚 " + authorRepository.count() + " auteurs créés");
            System.out.println("📖 " + bookRepository.count() + " livres créés");
            System.out.println("👥 " + memberRepository.count() + " membres créés");
            System.out.println("📋 " + loanRepository.count() + " emprunts créés");
        };
    }
}

package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByLastName(String lastName);
    List<Author> findByNationality(String nationality);
    @Query("select a from Author a JOIN FETCH a.books ")
    List<Author> findAuthorsWithBooks();

}

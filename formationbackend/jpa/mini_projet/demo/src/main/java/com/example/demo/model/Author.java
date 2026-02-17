package com.example.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    private Integer birthYear;
    private String nationality;
    @OneToMany(mappedBy = "author",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Book> books;
    @Column()
    private String biography;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Integer getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public List<Book> getBooks() {
        return books;
    }
    public void setBooks(List<Book> books) {
        this.books = books;
    }
    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
    public Author(String firstName, String lastName, Integer birthYear, String nationality, List<Book> books,
            String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.nationality = nationality;
        this.books = books;
        this.biography = biography;
    }
    public Author() {
    }

}

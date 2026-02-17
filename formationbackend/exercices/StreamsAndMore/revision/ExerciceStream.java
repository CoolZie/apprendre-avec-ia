package exercices.StreamsAndMore.revision;

import java.util.*;
import java.util.stream.*;

public class ExerciceStream {
    record Book(String title, String author, int year, double price, String genre) {
    }

    public static void main(String[] args) {
        List<Book> books = List.of(
                new Book("1984", "George Orwell", 1949, 15.99, "Fiction"),
                new Book("Sapiens", "Yuval Harari", 2011, 22.50, "History"),
                new Book("Clean Code", "Robert Martin", 2008, 45.00, "Tech"),
                new Book("Dune", "Frank Herbert", 1965, 18.99, "SciFi"),
                new Book("Effective Java", "Joshua Bloch", 2018, 50.00, "Tech"),
                new Book("The Hobbit", "J.R.R. Tolkien", 1937, 12.99, "Fantasy"));

        // TODO 1: Livres tech de plus de 40€, triés par prix décroissant
        List<Book> booksTechSup40 = books.stream()
                .filter(book -> book.genre().equals("Tech") && book.price > 40)
                .sorted(Comparator.comparingDouble(Book::price).reversed())
                .collect(Collectors.toList());

        // TODO 2: Top 3 des livres les plus récents
        List<Book> top3Recente = books.stream()
                .sorted(Comparator.comparingInt(Book::year).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // TODO 3: Tous les auteurs distincts, en majuscules, triés alphabétiquement
        List<String> authorBooks = books.stream()
                .map(Book::author)
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // TODO 4: Prix moyen des livres de fiction/fantasy
        double averageFantsy = books.stream()
        .filter(book->(book.genre().equals("Fantasy") || book.genre().equals("Fiction")))
        .collect(Collectors.averagingDouble(Book::price));
                


        // TODO 5: Y a-t-il un livre publié avant 1950 qui coûte moins de 15€ ?
        boolean isBook = books.stream()
        .anyMatch(b->(b.year < 1950) && (b.price < 15));

        // TODO 6: Titre du livre le plus cher
        String plusCherBook= books.stream()
        .max(Comparator.comparingDouble(Book::price))
        .map(Book::title)
        .orElse("Aucun");

        // TODO 7: Nombre de livres par genre
        Map<String,Long> nbrebyGenre = books.stream()
        .collect(Collectors.groupingBy(Book::genre,
            Collectors.counting()
        ));

        // TODO 8: Prix total de tous les livres
        double total = books.stream()
        .collect(Collectors.summingDouble(Book::price));

        

    }
}
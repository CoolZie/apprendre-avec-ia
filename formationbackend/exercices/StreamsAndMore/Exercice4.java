package exercices.StreamsAndMore;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import exercices.StreamsAndMore.Exercice1.Product;

public class Exercice4 {
    void play() {
        List<Product> products = List.of(
                new Product("Laptop", 999.99, "Electronics"),
                new Product("Mouse", 29.99, "Electronics"),
                new Product("Desk Chair", 89.99, "Furniture"),
                new Product("Notebook", 2.99, "Stationery"),
                new Product("Pen", 1.49, "Stationery"));

        double prixMoyen = products.stream()
                .collect(Collectors.averagingDouble(Product::price))
                .doubleValue();

        List<String> topThree = products.stream()
                .sorted(Comparator.comparingDouble(Product::price).reversed())
                .limit(3).map(Product::name)
                .collect(Collectors.toList());

        products.stream()
                .collect(Collectors.groupingBy(Product::category))
                .forEach((category, productsList) -> {
                    double val = productsList.stream()
                    .collect(Collectors.averagingDouble(p->p.price()))
                    .doubleValue();

                    System.out.println(val);

                });
        

        System.out.println("=== RAPPORT GLOBAL ===");
        System.out.println("Total produits: " + products.size());
        System.out.println("Prix moyen: ..." + prixMoyen);
        System.out.println("Catégorie moyenne la + chère: ...");
        System.out.println("Top 3: ..." + topThree.toString());

    }
}

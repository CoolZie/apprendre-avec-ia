package exercices.StreamsAndMore;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exercices.StreamsAndMore.Exercice1.Product;

public class Exercice2 {
    void play(){
List<Product> products = List.of(
                new Product("Laptop", 999.99, "Electronics"),
                new Product("Mouse", 29.99, "Electronics"),
                new Product("Desk Chair", 89.99, "Furniture"),
                 new Product("Notebook", 2.99, "Stationery"),
                new Product("Pen", 1.49, "Stationery"));
    Map<String, DoubleSummaryStatistics> statistiques = products.stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.summarizingDouble(Product::price)));

        statistiques.forEach((category, stats) -> {
            System.out.printf("%s : moyenne=%.2f, count=%d, max=%.2f",
                    category,
                    stats.getAverage(),
                    stats.getCount(),
                    stats.getMax());
        });
    }
    
}

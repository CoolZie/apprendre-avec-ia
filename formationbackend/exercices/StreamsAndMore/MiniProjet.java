package exercices.StreamsAndMore;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exercices.StreamsAndMore.Exercice1.Product;

public class MiniProjet {

        public static void jobxx() {
                List<Product> products = List.of(
                                new Product("Laptop", 999.99, "Electronics"),
                                new Product("Mouse", 29.99, "Electronics"),
                                new Product("Desk Chair", 89.99, "Furniture"),
                                new Product("Notebook", 2.99, "Stationery"),
                                new Product("Pen", 1.49, "Stationery"));

                DoubleSummaryStatistics statistics = products.stream()
                                .collect(Collectors.summarizingDouble(Product::price));

                List<String> topThree = products.stream()
                                .sorted(Comparator.comparingDouble(Product::price).reversed())
                                .limit(3).map(Product::name)
                                .collect(Collectors.toList());

                Map<String, Double> averageByCategory = products.stream()
                                .collect(Collectors.groupingBy(Product::category,
                                                Collectors.averagingDouble(Product::price)));

                Map<String, Long> xcount = products.stream()
                                .collect(Collectors.groupingBy(Product::category, Collectors.counting()));

                String xcat = xcount.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("NA");

                String maxcat = averageByCategory.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("NA");

                System.out.println("=== RAPPORT GLOBAL ===");
                System.out.println("Total produits: " + statistics.getCount());
                System.out.println("Prix moyen: ..." + statistics.getAverage());
                System.out.println("Catégorie moyenne la + chère: ..." + maxcat);
                System.out.println("Catégorie avec + produits: ..." + maxcat);
                System.out.println("Top 3: ..." + topThree.toString());
                System.out.println("Le prix min" + statistics.getMin());
                System.out.println("Le prix max" + statistics.getMax());

        }
}

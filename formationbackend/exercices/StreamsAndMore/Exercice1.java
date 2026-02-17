package exercices.StreamsAndMore;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Exercice1 {
    public record Product(
            String name,
            double price,
            String category) {
    }

    public void play() {
        List<Product> products = List.of(
                new Product("Laptop", 999.99, "Electronics"),
                new Product("Mouse", 29.99, "Electronics"),
                new Product("Desk Chair", 89.99, "Furniture"),
                new Product("Notebook", 2.99, "Stationery"),
                new Product("Pen", 1.49, "Stationery"));
        List<String> filteredProducts = products.stream()
                .filter(p -> p.price > 50)
                .sorted(Comparator.comparingDouble(Product::price).reversed())
                .map(Product::name)
                .collect(Collectors.toList());

        System.out.println(filteredProducts);
    }

}

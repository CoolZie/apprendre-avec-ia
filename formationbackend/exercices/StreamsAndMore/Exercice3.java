package exercices.StreamsAndMore;

import java.util.List;
import java.util.stream.Collectors;

import exercices.StreamsAndMore.Exercice1.Product;

public class Exercice3 {
    void play() {
        List<Product> products = List.of(
                new Product("Laptop", 999.99, "Electronics"),
                new Product("Mouse", 29.99, "Electronics"),
                new Product("Desk Chair", 89.99, "Furniture"),
                new Product("Notebook", 2.99, "Stationery"),
                new Product("Pen", 1.49, "Stationery"));

        products.stream()
                .collect(Collectors.groupingBy(Product::category))
                .forEach((category, productsList) -> {
                    List<String> names = productsList.stream()
                            .map(Product::name)
                            .collect(Collectors.toList());
                    System.out.printf("%s -> %s %n", category, names.toString());
                });
    }
}

package exercices.StreamsAndMore.revision;

import java.util.*;
import java.util.stream.*;

public class StreamOperations {
    record Product(String name, double price, String category) {}
    
    public static void main(String[] args) {
        List<Product> products = List.of(
            new Product("Laptop", 999.99, "Electronics"),
            new Product("Mouse", 29.99, "Electronics"),
            new Product("Desk", 299.99, "Furniture"),
            new Product("Chair", 149.99, "Furniture"),
            new Product("Pen", 1.99, "Stationery")
        );
        
        // ═══════════════════════════════════════════
        // OPÉRATIONS INTERMÉDIAIRES (renvoient Stream)
        // ═══════════════════════════════════════════
        
        // 1. filter() - Filtrer
        products.stream()
            .filter(p -> p.price() > 100)  // Garde seulement prix > 100
            .collect(Collectors.toList());
        
        // 2. map() - Transformer
        products.stream()
            .map(Product::name)  // Transforme Product en String
            .collect(Collectors.toList());
        
        // 3. sorted() - Trier
        products.stream()
            .sorted(Comparator.comparingDouble(Product::price))
            .collect(Collectors.toList());
        
        // 4. distinct() - Supprimer doublons
        products.stream()
            .map(Product::category)
            .distinct()  // ["Electronics", "Furniture", "Stationery"]
            .collect(Collectors.toList());
        
        // 5. limit() - Limiter le nombre
        products.stream()
            .limit(3)  // Garde seulement les 3 premiers
            .collect(Collectors.toList());
        
        // 6. skip() - Sauter les N premiers
        products.stream()
            .skip(2)  // Ignore les 2 premiers
            .collect(Collectors.toList());
        
        // 7. peek() - Observer (pour debug)
        products.stream()
            .peek(p -> System.out.println("Processing: " + p.name()))
            .filter(p -> p.price() > 100)
            .collect(Collectors.toList());
        
        // ═══════════════════════════════════════════
        // OPÉRATIONS TERMINALES (déclenchent l'exécution)
        // ═══════════════════════════════════════════
        
        // 1. collect() - Collecter en collection
        List<Product> list = products.stream().collect(Collectors.toList());
        
        // 2. forEach() - Itérer (void)
        products.stream().forEach(p -> System.out.println(p.name()));
        
        // 3. count() - Compter
        long count = products.stream().filter(p -> p.price() > 100).count();
        
        // 4. findFirst() - Premier élément
        Optional<Product> first = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .findFirst();
        
        // 5. findAny() - N'importe quel élément
        Optional<Product> any = products.stream().findAny();
        
        // 6. anyMatch() / allMatch() / noneMatch()
        boolean hasExpensive = products.stream().anyMatch(p -> p.price() > 500);
        boolean allCheap = products.stream().allMatch(p -> p.price() < 2000);
        boolean noneNegative = products.stream().noneMatch(p -> p.price() < 0);
        
        // 7. min() / max()
        Optional<Product> cheapest = products.stream()
            .min(Comparator.comparingDouble(Product::price));
        
        // 8. reduce() - Réduction
        double total = products.stream()
            .mapToDouble(Product::price)
            .reduce(0, (sum, price) -> sum + price);
        // Ou plus simple:
        double total2 = products.stream()
            .mapToDouble(Product::price)
            .sum();
    }
}
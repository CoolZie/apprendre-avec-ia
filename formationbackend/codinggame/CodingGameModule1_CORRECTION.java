package codinggame;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

/**
 * 🎯 CORRECTION COMPLÈTE - Coding Game Module 1
 * 
 * Cette correction montre les meilleures pratiques et solutions optimales.
 * Étudiez ce code après avoir tenté les exercices vous-même !
 * 
 * @author Formation Backend Java
 */
public class CodingGameModule1_CORRECTION {

    // ============================================
    // ⭐ DÉFI 1 : Stream Warriors (10 points)
    // ============================================
    
    /**
     * SOLUTION 1 : Approche classique
     */
    public static List<Integer> getSquaresOfEvenNumbers_Solution1(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)           // Garde uniquement les pairs
                .map(n -> n * n)                   // Calcule le carré
                .collect(Collectors.toList());      // Collecte en liste
    }
    
    /**
     * SOLUTION 2 : Avec référence de méthode (plus concis)
     */
    public static List<Integer> getSquaresOfEvenNumbers_Solution2(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .toList();  // Java 16+ : plus concis
    }

    // ============================================
    // ⭐ DÉFI 2 : Lambda Master (15 points)
    // ============================================
    
    /**
     * SOLUTION : Tri alphabétique
     */
    public static List<String> sortAlphabetically(List<String> words) {
        return words.stream()
                .sorted()  // Tri naturel (alphabétique pour les strings)
                .toList();
    }

    /**
     * SOLUTION : Tri par longueur
     */
    public static List<String> sortByLength(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparingInt(String::length))  // Référence de méthode
                .toList();
    }

    /**
     * SOLUTION : Tri combiné (longueur desc puis alpha)
     */
    public static List<String> sortByLengthDescThenAlpha(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparingInt(String::length)
                        .reversed()                    // Inverse l'ordre
                        .thenComparing(Comparator.naturalOrder()))  // Puis alphabétique
                .toList();
    }

    // ============================================
    // ⭐⭐ DÉFI 3 : Collector Champion (20 points)
    // ============================================
    
    /**
     * SOLUTION : Somme totale
     */
    public static double calculateTotalAmount(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.summingDouble(Double::doubleValue));
        
        // Alternative plus simple :
        // return amounts.stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * SOLUTION : Moyenne
     */
    public static double calculateAverageAmount(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.averagingDouble(Double::doubleValue));
    }

    /**
     * SOLUTION : Maximum
     */
    public static double findMaxAmount(List<Double> amounts) {
        return amounts.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);  // Valeur par défaut si liste vide
    }

    /**
     * SOLUTION : Statistiques complètes
     */
    public static DoubleSummaryStatistics getStatistics(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue));
    }

    // ============================================
    // ⭐⭐ DÉFI 4 : Grouping Guru (20 points)
    // ============================================
    
    /**
     * SOLUTION : Groupement simple par département
     */
    public static Map<String, List<Employee>> groupByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    /**
     * SOLUTION : Salaire moyen par département
     */
    public static Map<String, Double> getAverageSalaryByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)  // Downstream collector
                ));
    }

    /**
     * SOLUTION : Comptage par département
     */
    public static Map<String, Long> countEmployeesByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.counting()
                ));
    }

    /**
     * SOLUTION : Groupement par tranche de salaire
     */
    public static Map<String, List<Employee>> groupBySalaryRange(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(employee -> {
                    double salary = employee.getSalary();
                    if (salary < 30000) return "LOW";
                    else if (salary <= 50000) return "MEDIUM";
                    else return "HIGH";
                }));
    }

    // ============================================
    // ⭐⭐⭐ DÉFI 5 : Record Architect (20 points)
    // ============================================
    
    /**
     * SOLUTION : Record Customer avec validation
     */
    public record Customer(String name, String email, boolean vipStatus) {
        
        // Compact constructor avec validation
        public Customer {
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Email invalide : " + email);
            }
        }
    }

    /**
     * SOLUTION : Record OrderItem
     */
    public record OrderItem(String productName, int quantity, double price) {
        
        public double getTotal() {
            return quantity * price;
        }
    }

    /**
     * SOLUTION : Record Order avec méthodes personnalisées
     */
    public record Order(String id, Customer customer, List<OrderItem> items, double totalPrice) {
        
        /**
         * Retourne un nouvel Order avec réduction appliquée.
         * Pattern immutable : on crée une nouvelle instance.
         */
        public Order withDiscount(double discountPercent) {
            double discountedPrice = totalPrice * (1 - discountPercent / 100.0);
            // Création d'une nouvelle instance (immutabilité)
            return new Order(id, customer, items, discountedPrice);
        }

        /**
         * Calcule le total des items commandés.
         */
        public int getTotalItems() {
            return items.stream()
                    .mapToInt(OrderItem::quantity)
                    .sum();
        }
    }

    // ============================================
    // ⭐⭐⭐ DÉFI 6 : Expert Challenge (15 points)
    // ============================================
    
    /**
     * SOLUTION : Top N produits par région
     * Pipeline complexe : filter → group → sum → sort → limit
     */
    public static List<String> getTopProductsByRegion(List<Sale> sales, String region, int topN) {
        return sales.stream()
                // 1. Filtrer par région
                .filter(sale -> sale.region().equals(region))
                // 2. Grouper par produit et sommer les montants
                .collect(Collectors.groupingBy(
                        Sale::product,
                        Collectors.summingDouble(Sale::amount)
                ))
                .entrySet().stream()
                // 3. Trier par montant décroissant
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                // 4. Limiter au top N
                .limit(topN)
                // 5. Extraire les noms de produits
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * SOLUTION : Catégories générant >X% du CA
     * Nécessite deux passes : calcul CA total puis filtrage
     */
    public static List<String> getHighRevenueCategories(List<Sale> sales, double thresholdPercent) {
        // 1. Calculer le CA total
        double totalRevenue = sales.stream()
                .mapToDouble(Sale::amount)
                .sum();
        
        // 2. Seuil absolu
        double threshold = totalRevenue * (thresholdPercent / 100.0);
        
        // 3. Grouper par catégorie et filtrer
        return sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::category,
                        Collectors.summingDouble(Sale::amount)
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= threshold)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * SOLUTION : Tendance mensuelle du CA
     * Utilise un format de date standardisé YYYY-MM
     */
    public static Map<String, Double> getMonthlySalesTrend(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(
                        // Extraction du mois au format "YYYY-MM"
                        sale -> sale.date().toString().substring(0, 7),
                        Collectors.summingDouble(Sale::amount)
                ));
    }

    // ============================================
    // Classes Helper
    // ============================================
    
    public static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        public Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }

        @Override
        public String toString() {
            return String.format("Employee{name='%s', dept='%s', salary=%.0f}",
                    name, department, salary);
        }
    }

    public record Sale(LocalDate date, String product, String category,
                      double amount, String region) {
    }

    // ============================================
    // Exemples d'utilisation et explications
    // ============================================
    
    public static void main(String[] args) {
        demonstrateAllChallenges();
    }

    private static void demonstrateAllChallenges() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   📚 CORRECTION COMPLÈTE - Coding Game       ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");

        // DÉFI 1
        System.out.println("⭐ DÉFI 1 : Stream Warriors");
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("Entrée : " + numbers);
        System.out.println("Carrés des pairs : " + getSquaresOfEvenNumbers_Solution1(numbers));
        System.out.println();

        // DÉFI 2
        System.out.println("⭐ DÉFI 2 : Lambda Master");
        List<String> words = List.of("Java", "Python", "C++", "JavaScript", "Go");
        System.out.println("Alphabétique : " + sortAlphabetically(words));
        System.out.println("Par longueur : " + sortByLength(words));
        System.out.println("Longueur desc + alpha : " + sortByLengthDescThenAlpha(words));
        System.out.println();

        // DÉFI 3
        System.out.println("⭐⭐ DÉFI 3 : Collector Champion");
        List<Double> amounts = List.of(100.0, 200.0, 150.0, 300.0, 250.0);
        System.out.println("Somme : " + calculateTotalAmount(amounts));
        System.out.println("Moyenne : " + calculateAverageAmount(amounts));
        System.out.println("Maximum : " + findMaxAmount(amounts));
        System.out.println("Statistiques : " + getStatistics(amounts));
        System.out.println();

        // DÉFI 4
        System.out.println("⭐⭐ DÉFI 4 : Grouping Guru");
        List<Employee> employees = List.of(
            new Employee("Alice", "IT", 55000),
            new Employee("Bob", "HR", 45000),
            new Employee("Charlie", "IT", 65000)
        );
        System.out.println("Par département : " + groupByDepartment(employees));
        System.out.println("Salaire moyen : " + getAverageSalaryByDepartment(employees));
        System.out.println();

        // DÉFI 5
        System.out.println("⭐⭐⭐ DÉFI 5 : Record Architect");
        Customer customer = new Customer("John Doe", "john@example.com", true);
        OrderItem item1 = new OrderItem("Laptop", 2, 999.99);
        System.out.println("Customer : " + customer);
        System.out.println("Item total : " + item1.getTotal());
        
        Order order = new Order("ORD-001", customer, List.of(item1), 1999.98);
        Order discounted = order.withDiscount(10);
        System.out.println("Prix original : " + order.totalPrice());
        System.out.println("Prix avec -10% : " + discounted.totalPrice());
        System.out.println();

        // DÉFI 6
        System.out.println("⭐⭐⭐ DÉFI 6 : Expert Challenge");
        List<Sale> sales = List.of(
            new Sale(LocalDate.of(2024, 1, 15), "Laptop", "Electronics", 1200, "North"),
            new Sale(LocalDate.of(2024, 1, 20), "Mouse", "Electronics", 25, "North"),
            new Sale(LocalDate.of(2024, 2, 10), "Desk", "Furniture", 350, "North")
        );
        System.out.println("Top 2 produits North : " + getTopProductsByRegion(sales, "North", 2));
        System.out.println("Tendance mensuelle : " + getMonthlySalesTrend(sales));
        
        System.out.println("\n✅ Toutes les solutions sont fonctionnelles !");
        System.out.println("📖 Étudiez le code et les commentaires pour comprendre.");
    }

    // ============================================
    // 💡 EXPLICATIONS ET BONNES PRATIQUES
    // ============================================
    
    /*
     * BEST PRACTICES UTILISÉES DANS CETTE CORRECTION :
     * 
     * 1. RÉFÉRENCES DE MÉTHODES
     *    - Préférez Employee::getDepartment à e -> e.getDepartment()
     *    - Plus concis et lisible
     * 
     * 2. COLLECTORS COMBINÉS
     *    - groupingBy() avec downstream collectors
     *    - Permet des opérations complexes en une passe
     * 
     * 3. IMMUTABILITÉ DES RECORDS
     *    - withDiscount() crée une nouvelle instance
     *    - Ne modifie jamais l'objet original
     * 
     * 4. GESTION DES CAS LIMITES
     *    - orElse() pour les Optional
     *    - Validation dans les compact constructors
     * 
     * 5. LISIBILITÉ
     *    - Un stream par ligne
     *    - Commentaires sur les étapes complexes
     *    - Noms de variables explicites
     * 
     * 6. PERFORMANCE
     *    - filter() avant map() pour réduire les éléments
     *    - Éviter les streams imbriqués si possible
     *    - toList() au lieu de collect(Collectors.toList()) en Java 16+
     * 
     * 7. COMPARATEURS
     *    - Comparator.comparing...() pour le tri
     *    - .reversed() pour inverser l'ordre
     *    - .thenComparing() pour les tris secondaires
     * 
     * 8. COLLECTORS AVANCÉS
     *    - groupingBy() pour les groupements
     *    - mapping(), filtering() pour transformer pendant le collect
     *    - summarizingDouble() pour les statistiques
     */
}

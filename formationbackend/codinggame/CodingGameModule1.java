package codinggame;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

/**
 * 🎮 CODING GAME - Module 1 : Java Fondamental
 * 
 * Complétez les méthodes pour relever tous les défis !
 * Lancez CodingGameTests pour vérifier votre code.
 * 
 * @author Zie Sidiki Coulibaly
 * @date ${date}
 */
public class CodingGameModule1 {

    // ============================================
    // ⭐ DÉFI 1 : Stream Warriors (10 points)
    // ============================================

    /**
     * Retourne les carrés des nombres pairs uniquement.
     * 
     * Exemple:
     * Entrée: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
     * Sortie: [4, 16, 36, 64, 100]
     * 
     * @param numbers liste de nombres
     * @return liste des carrés des nombres pairs
     */
    public static List<Integer> getSquaresOfEvenNumbers(List<Integer> numbers) {
        return numbers.stream()
                .filter(number -> number % 2 == 0)
                .map(number -> number * number)
                .collect(Collectors.toList());
    }

    // ============================================
    // ⭐ DÉFI 2 : Lambda Master (15 points)
    // ============================================

    /**
     * Trie les mots par ordre alphabétique.
     * 
     * @param words liste de mots
     * @return liste triée alphabétiquement
     */
    public static List<String> sortAlphabetically(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparing(word -> word))
                .collect(Collectors.toList());
    }

    /**
     * Trie les mots par longueur croissante.
     * 
     * @param words liste de mots
     * @return liste triée par longueur
     */
    public static List<String> sortByLength(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.toList());

    }

    /**
     * Trie les mots par longueur décroissante, puis alphabétiquement.
     * 
     * @param words liste de mots
     * @return liste triée par longueur desc puis alpha
     */
    public static List<String> sortByLengthDescThenAlpha(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparing(String::length).reversed().thenComparing(Comparator.comparing(w -> w)))
                .collect(Collectors.toList());

    }

    // ============================================
    // ⭐⭐ DÉFI 3 : Collector Champion (20 points)
    // ============================================

    /**
     * Calcule la somme totale des transactions.
     * 
     * @param amounts montants des transactions
     * @return somme totale
     */
    public static double calculateTotalAmount(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.summingDouble(amount -> amount));
    }

    /**
     * Calcule la moyenne des montants.
     * 
     * @param amounts montants
     * @return moyenne
     */
    public static double calculateAverageAmount(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.averagingDouble(amount -> amount));
    }

    /**
     * Trouve le montant maximum.
     * 
     * @param amounts montants
     * @return montant maximum
     */
    public static double findMaxAmount(List<Double> amounts) {
        return amounts.stream()
                .max(Comparator.comparing(amount -> amount))
                .orElse(0.0);
    }

    /**
     * Retourne les statistiques complètes (count, sum, min, max, average).
     * 
     * @param amounts montants
     * @return DoubleSummaryStatistics
     */
    public static DoubleSummaryStatistics getStatistics(List<Double> amounts) {
        return amounts.stream()
                .collect(Collectors.summarizingDouble(amout -> amout));
    }

    // ============================================
    // ⭐⭐ DÉFI 4 : Grouping Guru (20 points)
    // ============================================

    /**
     * Groupe les employés par département.
     * 
     * @param employees liste d'employés
     * @return Map<Département, List<Employee>>
     */
    public static Map<String, List<Employee>> groupByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.toList()));
    }

    /**
     * Groupe les départements avec leur salaire moyen.
     * 
     * @param employees liste d'employés
     * @return Map<Département, Salaire moyen>
     */
    public static Map<String, Double> getAverageSalaryByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)));
    }

    /**
     * Compte le nombre d'employés par département.
     * 
     * @param employees liste d'employés
     * @return Map<Département, Nombre>
     */
    public static Map<String, Long> countEmployeesByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
    }

    /**
     * Groupe les employés par tranche de salaire.
     * <30K: "LOW", 30-50K: "MEDIUM", >50K: "HIGH"
     * 
     * @param employees liste d'employés
     * @return Map<Tranche, List<Employee>>
     */
    public static Map<String, List<Employee>> groupBySalaryRange(List<Employee> employees) {
        employees.stream()
                .collect(Collectors.groupingBy((employee) -> {
                    double salary = employee.getSalary();
                    if (salary < 30000) {
                        return "LOW";
                    } else if (salary >= 30000 && salary <= 50000) {
                        return "MEDIUM";
                    } else {
                        return "HIGH";
                    }
                }));
        throw new UnsupportedOperationException("À implémenter !");
    }

    // ============================================
    // ⭐⭐⭐ DÉFI 5 : Record Architect (20 points)
    // ============================================

    /**
     * Record Customer avec validation.
     * Doit valider que l'email contient un @.
     */
    public record Customer(String name, String email, boolean vipStatus) {
        public Customer {
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("email invalide");
            }
        }

    }

    /**
     * Record OrderItem.
     */
    public record OrderItem(String productName, int quantity, double price) {
        // TODO: Calculez le total de l'item
        public double getTotal() {
            return quantity * price;
        }
    }

    /**
     * Record Order avec méthodes personnalisées.
     */
    public record Order(String id, Customer customer, List<OrderItem> items, double totalPrice) {

        /**
         * Crée un nouvel Order avec une réduction appliquée.
         * 
         * @param discountPercent pourcentage de réduction (0-100)
         * @return nouvel Order avec prix réduit
         */
        public Order withDiscount(double discountPercent) {
            // TODO: Retournez un nouvel Order avec le prix réduit
            // Records sont immutables : créez une nouvelle instance
            return new Order(id, customer, items, totalPrice - (totalPrice * (discountPercent / 100)));
        }

        /**
         * Calcule le nombre total d'items dans la commande.
         * 
         * @return nombre total d'items
         */
        public int getTotalItems() {
            return items.stream()
                    .collect(Collectors.summingInt(OrderItem::quantity));
        }
    }

    // ============================================
    // ⭐⭐⭐ DÉFI 6 : Expert Challenge (15 points)
    // ============================================

    /**
     * Trouve le top N des produits par région.
     * 
     * @param sales  liste de ventes
     * @param region région ciblée
     * @param topN   nombre de produits à retourner
     * @return liste des N produits les plus vendus (par montant total)
     */
    public static List<String> getTopProductsByRegion(List<Sale> sales, String region, int topN) {
        // TODO: Filtrez par région, groupez par produit, sommez les montants,
        // triez par montant desc, limitez à topN, collectez les noms
        List<String> rest = sales.stream()
                .filter(sale -> sale.region().equals(region))
                .collect(Collectors.groupingBy(
                        Sale::product, Collectors.summingDouble(Sale::amount)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.err.println(rest);
        return rest;
    }

    /**
     * Trouve les catégories générant plus de X% du CA total.
     * 
     * @param sales            liste de ventes
     * @param thresholdPercent seuil en pourcentage (ex: 50.0 pour 50%)
     * @return liste des catégories au-dessus du seuil
     */
    public static List<String> getHighRevenueCategories(List<Sale> sales, double thresholdPercent) {
        // TODO: Calculez le CA total, groupez par catégorie, calculez les %,
        // filtrez celles au-dessus du seuil
        double CATotal = sales.stream().collect(Collectors.summingDouble(Sale::amount));
        return sales.stream()
                .collect(
                        Collectors.groupingBy(
                                Sale::category,
                                Collectors.summingDouble(Sale::amount)))
                .entrySet().stream()
                .filter(x -> (x.getValue() / CATotal) * 100 > thresholdPercent)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Groupe les ventes par mois et calcule le CA mensuel.
     * 
     * @param sales liste de ventes
     * @return Map<Mois (String "YYYY-MM"), CA mensuel>
     */
    public static Map<String, Double> getMonthlySalesTrend(List<Sale> sales) {
        return sales.stream().collect(Collectors.groupingBy(
                sale -> sale.date().toString().substring(0, 7),
                Collectors.summingDouble(Sale::amount)));
        // TODO: Groupez par mois (utilisez date.toString().substring(0, 7)),
        // sommez les montants par mois

    }

    // ============================================
    // Classes Helper pour les exercices
    // ============================================

    /**
     * Classe Employee pour les exercices de groupement.
     */
    public static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        public Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public String getDepartment() {
            return department;
        }

        public double getSalary() {
            return salary;
        }

        @Override
        public String toString() {
            return String.format("Employee{name='%s', dept='%s', salary=%.0f}",
                    name, department, salary);
        }
    }

    /**
     * Record Sale pour le défi expert.
     */
    public record Sale(LocalDate date, String product, String category,
            double amount, String region) {
    }

    // ============================================
    // Main pour tester manuellement
    // ============================================

    public static void main(String[] args) {
        System.out.println("🎮 CODING GAME - Module 1 : Java Fondamental");
        System.out.println("============================================\n");

        System.out.println("Pour tester votre code, lancez :");
        System.out.println("  javac CodingGameModule1.java CodingGameTests.java");
        System.out.println("  java CodingGameTests");
        System.out.println("\nOu utilisez votre IDE pour lancer CodingGameTests\n");

        // Vous pouvez tester vos méthodes ici pendant le développement
        System.out.println("Testez vos méthodes ici...\n");
    }
}

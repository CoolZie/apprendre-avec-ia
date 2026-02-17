package codinggame;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

/**
 * 🧪 Tests automatisés pour le Coding Game Module 1
 * 
 * Ce fichier teste automatiquement votre code et calcule votre score.
 * NE MODIFIEZ PAS CE FICHIER !
 */
public class CodingGameTests {
    
    private static int totalScore = 0;
    private static int maxScore = 100;
    private static List<String> feedback = new ArrayList<>();
    private static long startTime;
    private static Map<String, Long> exerciseTimes = new LinkedHashMap<>();
    
    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        
        printHeader();
        
        // Lancement de tous les tests
        testDefi1();
        testDefi2();
        testDefi3();
        testDefi4();
        testDefi5();
        testDefi6();
        
        // Affichage des résultats
        printResults();
    }

    // ============================================
    // Tests Défi 1 : Stream Warriors (10 points)
    // ============================================
    
    private static void testDefi1() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐ DÉFI 1 : Stream Warriors");
        System.out.println("─────────────────────────────");
        
        try {
            List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            List<Integer> expected = List.of(4, 16, 36, 64, 100);
            List<Integer> result = CodingGameModule1.getSquaresOfEvenNumbers(input);
            
            if (result.equals(expected)) {
                addScore(10, "✅ Carrés des nombres pairs : PARFAIT !");
            } else {
                addScore(0, "❌ Résultat incorrect. Attendu: " + expected + ", Obtenu: " + result);
            }
            
            // Test edge case
            List<Integer> emptyResult = CodingGameModule1.getSquaresOfEvenNumbers(List.of());
            if (emptyResult.isEmpty()) {
                System.out.println("   ✓ Gestion correcte de la liste vide");
            }
            
        } catch (UnsupportedOperationException e) {
            addScore(0, "❌ Méthode non implémentée");
        } catch (Exception e) {
            addScore(0, "❌ Erreur : " + e.getMessage());
        }
        
        exerciseTimes.put("Défi 1", System.currentTimeMillis() - defiStart);
    }

    // ============================================
    // Tests Défi 2 : Lambda Master (15 points)
    // ============================================
    
    private static void testDefi2() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐ DÉFI 2 : Lambda Master");
        System.out.println("─────────────────────────────");
        
        List<String> words = List.of("Java", "Python", "C++", "JavaScript", "Go");
        int score = 0;
        
        // Test 1 : Tri alphabétique (5 points)
        try {
            List<String> result = CodingGameModule1.sortAlphabetically(words);
            List<String> expected = List.of("C++", "Go", "Java", "JavaScript", "Python");
            
            if (result.equals(expected)) {
                score += 5;
                System.out.println("✅ Tri alphabétique : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Tri alphabétique incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ sortAlphabetically() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans sortAlphabetically()");
        }
        
        // Test 2 : Tri par longueur (5 points)
        try {
            List<String> result = CodingGameModule1.sortByLength(words);
            List<String> expected = List.of("Go", "C++", "Java", "Python", "JavaScript");
            
            if (result.equals(expected)) {
                score += 5;
                System.out.println("✅ Tri par longueur : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Tri par longueur incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ sortByLength() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans sortByLength()");
        }
        
        // Test 3 : Tri combiné (5 points)
        try {
            List<String> result = CodingGameModule1.sortByLengthDescThenAlpha(words);
            // JavaScript (10), Python (6), Java (4), C++ (3), Go (2)
            List<String> expected = List.of("JavaScript", "Python", "Java", "C++", "Go");
            
            if (result.equals(expected)) {
                score += 5;
                System.out.println("✅ Tri combiné : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Tri combiné incorrect. Attendu: " + expected + ", Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ sortByLengthDescThenAlpha() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans sortByLengthDescThenAlpha()");
        }
        
        addScore(score, score == 15 ? "Maîtrise parfaite des lambdas !" : "Continuez à pratiquer les lambdas");
        exerciseTimes.put("Défi 2", System.currentTimeMillis() - defiStart);
    }

    // ============================================
    // Tests Défi 3 : Collector Champion (20 points)
    // ============================================
    
    private static void testDefi3() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐⭐ DÉFI 3 : Collector Champion");
        System.out.println("─────────────────────────────");
        
        List<Double> amounts = List.of(100.0, 200.0, 150.0, 300.0, 250.0);
        int score = 0;
        
        // Test 1 : Somme (5 points)
        try {
            double result = CodingGameModule1.calculateTotalAmount(amounts);
            if (Math.abs(result - 1000.0) < 0.01) {
                score += 5;
                System.out.println("✅ Somme totale : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Somme incorrecte. Attendu: 1000.0, Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ calculateTotalAmount() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans calculateTotalAmount()");
        }
        
        // Test 2 : Moyenne (5 points)
        try {
            double result = CodingGameModule1.calculateAverageAmount(amounts);
            if (Math.abs(result - 200.0) < 0.01) {
                score += 5;
                System.out.println("✅ Moyenne : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Moyenne incorrecte. Attendu: 200.0, Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ calculateAverageAmount() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans calculateAverageAmount()");
        }
        
        // Test 3 : Maximum (5 points)
        try {
            double result = CodingGameModule1.findMaxAmount(amounts);
            if (Math.abs(result - 300.0) < 0.01) {
                score += 5;
                System.out.println("✅ Maximum : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Maximum incorrect. Attendu: 300.0, Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ findMaxAmount() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans findMaxAmount()");
        }
        
        // Test 4 : Statistiques (5 points)
        try {
            DoubleSummaryStatistics stats = CodingGameModule1.getStatistics(amounts);
            if (stats.getCount() == 5 && 
                Math.abs(stats.getSum() - 1000.0) < 0.01 &&
                Math.abs(stats.getAverage() - 200.0) < 0.01 &&
                Math.abs(stats.getMin() - 100.0) < 0.01 &&
                Math.abs(stats.getMax() - 300.0) < 0.01) {
                score += 5;
                System.out.println("✅ Statistiques complètes : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Statistiques incorrectes");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ getStatistics() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getStatistics()");
        }
        
        addScore(score, score == 20 ? "Champion des Collectors !" : "Bon début, continuez !");
        exerciseTimes.put("Défi 3", System.currentTimeMillis() - defiStart);
    }

    // ============================================
    // Tests Défi 4 : Grouping Guru (20 points)
    // ============================================
    
    private static void testDefi4() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐⭐ DÉFI 4 : Grouping Guru");
        System.out.println("─────────────────────────────");
        
        List<CodingGameModule1.Employee> employees = List.of(
            new CodingGameModule1.Employee("Alice", "IT", 55000),
            new CodingGameModule1.Employee("Bob", "HR", 45000),
            new CodingGameModule1.Employee("Charlie", "IT", 65000),
            new CodingGameModule1.Employee("Diana", "Sales", 35000),
            new CodingGameModule1.Employee("Eve", "HR", 48000)
        );
        
        int score = 0;
        
        // Test 1 : Groupement simple (5 points)
        try {
            Map<String, List<CodingGameModule1.Employee>> result = 
                CodingGameModule1.groupByDepartment(employees);
            
            if (result.size() == 3 && 
                result.get("IT").size() == 2 &&
                result.get("HR").size() == 2 &&
                result.get("Sales").size() == 1) {
                score += 5;
                System.out.println("✅ Groupement par département : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Groupement par département incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ groupByDepartment() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans groupByDepartment()");
        }
        
        // Test 2 : Salaire moyen (5 points)
        try {
            Map<String, Double> result = 
                CodingGameModule1.getAverageSalaryByDepartment(employees);
            
            if (result.size() == 3 &&
                Math.abs(result.get("IT") - 60000.0) < 0.01 &&
                Math.abs(result.get("HR") - 46500.0) < 0.01 &&
                Math.abs(result.get("Sales") - 35000.0) < 0.01) {
                score += 5;
                System.out.println("✅ Salaire moyen par département : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Salaire moyen incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ getAverageSalaryByDepartment() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getAverageSalaryByDepartment()");
        }
        
        // Test 3 : Comptage (5 points)
        try {
            Map<String, Long> result = 
                CodingGameModule1.countEmployeesByDepartment(employees);
            
            if (result.size() == 3 &&
                result.get("IT") == 2 &&
                result.get("HR") == 2 &&
                result.get("Sales") == 1) {
                score += 5;
                System.out.println("✅ Comptage par département : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Comptage incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ countEmployeesByDepartment() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans countEmployeesByDepartment()");
        }
        
        // Test 4 : Tranches de salaire (5 points)
        try {
            Map<String, List<CodingGameModule1.Employee>> result = 
                CodingGameModule1.groupBySalaryRange(employees);
            
            if (result.containsKey("LOW") && result.get("LOW").size() == 1 &&
                result.containsKey("MEDIUM") && result.get("MEDIUM").size() == 2 &&
                result.containsKey("HIGH") && result.get("HIGH").size() == 2) {
                score += 5;
                System.out.println("✅ Groupement par tranche : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Groupement par tranche incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ groupBySalaryRange() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans groupBySalaryRange()");
        }
        
        addScore(score, score == 20 ? "Guru du groupement !" : "Bon travail, continuez !");
        exerciseTimes.put("Défi 4", System.currentTimeMillis() - defiStart);
    }

    // ============================================
    // Tests Défi 5 : Record Architect (20 points)
    // ============================================
    
    private static void testDefi5() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐⭐⭐ DÉFI 5 : Record Architect");
        System.out.println("─────────────────────────────");
        
        int score = 0;
        
        // Test 1 : Validation Customer (5 points)
        try {
            CodingGameModule1.Customer validCustomer = 
                new CodingGameModule1.Customer("John", "john@example.com", true);
            score += 2;
            System.out.println("✅ Customer valide créé (2 pts)");
            
            try {
                CodingGameModule1.Customer invalidCustomer = 
                    new CodingGameModule1.Customer("Jane", "invalid-email", false);
                System.out.println("❌ Validation email manquante");
            } catch (IllegalArgumentException e) {
                score += 3;
                System.out.println("✅ Validation email fonctionne (3 pts)");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ Record Customer non implémenté");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans Customer : " + e.getMessage());
        }
        
        // Test 2 : OrderItem.getTotal() (5 points)
        try {
            CodingGameModule1.OrderItem item = 
                new CodingGameModule1.OrderItem("Laptop", 2, 999.99);
            double total = item.getTotal();
            
            if (Math.abs(total - 1999.98) < 0.01) {
                score += 5;
                System.out.println("✅ OrderItem.getTotal() : CORRECT (5 pts)");
            } else {
                System.out.println("❌ getTotal() incorrect. Attendu: 1999.98, Obtenu: " + total);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ OrderItem.getTotal() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans OrderItem");
        }
        
        // Test 3 : Order.withDiscount() (5 points)
        try {
            CodingGameModule1.Customer customer = 
                new CodingGameModule1.Customer("John", "john@test.com", true);
            List<CodingGameModule1.OrderItem> items = List.of(
                new CodingGameModule1.OrderItem("Product", 1, 100.0)
            );
            CodingGameModule1.Order order = 
                new CodingGameModule1.Order("ORD-1", customer, items, 100.0);
            
            CodingGameModule1.Order discounted = order.withDiscount(20);
            
            if (Math.abs(discounted.totalPrice() - 80.0) < 0.01 && 
                order.totalPrice() == 100.0) { // Immutabilité
                score += 5;
                System.out.println("✅ Order.withDiscount() : CORRECT (5 pts)");
            } else {
                System.out.println("❌ withDiscount() incorrect");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ Order.withDiscount() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans withDiscount()");
        }
        
        // Test 4 : Order.getTotalItems() (5 points)
        try {
            CodingGameModule1.Customer customer = 
                new CodingGameModule1.Customer("John", "john@test.com", true);
            List<CodingGameModule1.OrderItem> items = List.of(
                new CodingGameModule1.OrderItem("Product1", 3, 50.0),
                new CodingGameModule1.OrderItem("Product2", 2, 30.0)
            );
            CodingGameModule1.Order order = 
                new CodingGameModule1.Order("ORD-2", customer, items, 210.0);
            
            int totalItems = order.getTotalItems();
            
            if (totalItems == 5) {
                score += 5;
                System.out.println("✅ Order.getTotalItems() : CORRECT (5 pts)");
            } else {
                System.out.println("❌ getTotalItems() incorrect. Attendu: 5, Obtenu: " + totalItems);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ Order.getTotalItems() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getTotalItems()");
        }
        
        addScore(score, score == 20 ? "Architecte expert des Records !" : "Bon début avec les Records !");
        exerciseTimes.put("Défi 5", System.currentTimeMillis() - defiStart);
    }

    // ============================================
    // Tests Défi 6 : Expert Challenge (15 points)
    // ============================================
    
    private static void testDefi6() {
        long defiStart = System.currentTimeMillis();
        System.out.println("\n⭐⭐⭐ DÉFI 6 : Expert Challenge");
        System.out.println("─────────────────────────────");
        
        List<CodingGameModule1.Sale> sales = List.of(
            new CodingGameModule1.Sale(LocalDate.of(2024, 1, 15), "Laptop", "Electronics", 1200, "North"),
            new CodingGameModule1.Sale(LocalDate.of(2024, 1, 20), "Mouse", "Electronics", 25, "North"),
            new CodingGameModule1.Sale(LocalDate.of(2024, 1, 25), "Laptop", "Electronics", 1200, "North"),
            new CodingGameModule1.Sale(LocalDate.of(2024, 2, 10), "Keyboard", "Electronics", 75, "South"),
            new CodingGameModule1.Sale(LocalDate.of(2024, 2, 15), "Desk", "Furniture", 350, "North"),
            new CodingGameModule1.Sale(LocalDate.of(2024, 2, 20), "Chair", "Furniture", 200, "South")
        );
        
        int score = 0;
        
        // Test 1 : Top produits par région (5 points)
        try {
            List<String> result = CodingGameModule1.getTopProductsByRegion(sales, "North", 2);
            
            if (result.size() == 2 && result.get(0).equals("Laptop") && result.get(1).equals("Desk")) {
                score += 5;
                System.out.println("✅ Top produits par région : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Top produits incorrect. Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ getTopProductsByRegion() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getTopProductsByRegion()");
        }
        
        // Test 2 : Catégories haute revenue (5 points)
        try {
            List<String> result = CodingGameModule1.getHighRevenueCategories(sales, 50.0);
            
            if (result.size() == 1 && result.contains("Electronics")) {
                score += 5;
                System.out.println("✅ Catégories haute revenue : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Catégories incorrectes. Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ getHighRevenueCategories() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getHighRevenueCategories()");
        }
        
        // Test 3 : Tendance mensuelle (5 points)
        try {
            Map<String, Double> result = CodingGameModule1.getMonthlySalesTrend(sales);
            
            if (result.size() == 2 &&
                Math.abs(result.get("2024-01") - 2425.0) < 0.01 &&
                Math.abs(result.get("2024-02") - 625.0) < 0.01) {
                score += 5;
                System.out.println("✅ Tendance mensuelle : CORRECT (5 pts)");
            } else {
                System.out.println("❌ Tendance mensuelle incorrecte. Obtenu: " + result);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("❌ getMonthlySalesTrend() non implémentée");
        } catch (Exception e) {
            System.out.println("❌ Erreur dans getMonthlySalesTrend()");
        }
        
        addScore(score, score == 15 ? "Expert Challenge RÉUSSI ! 🏆" : "Continuez, vous progressez !");
    }

    // ============================================
    // Utilitaires
    // ============================================
    
    private static void addScore(int points, String message) {
        totalScore += points;
        feedback.add(message);
        if (points > 0) {
            System.out.println("   +" + points + " points");
        }
    }

    private static void printHeader() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║   🎮 CODING GAME - Tests Module 1           ║");
        System.out.println("║   Java Fondamental : Streams, Lambdas, Records║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");
    }

    private static void printResults() {
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        
        System.out.println("\n\n╔═══════════════════════════════════════════════╗");
        System.out.println("║            📊 RÉSULTATS FINAUX               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        System.out.println("\n🎯 Score : " + totalScore + " / " + maxScore);
        
        double percentage = (totalScore * 100.0) / maxScore;
        System.out.println("📈 Pourcentage : " + String.format("%.1f", percentage) + "%");
        System.out.println("⏱️  Temps : " + duration + " secondes\n");
        
        // Évaluation
        System.out.println("═══════════════════════════════════════════════");
        if (percentage >= 90) {
            System.out.println("🏆 NIVEAU : EXPERT JAVA !");
            System.out.println("   Félicitations ! Vous maîtrisez parfaitement");
            System.out.println("   les Streams, Lambdas, Collectors et Records.");
            System.out.println("   → Passez au Module 2 : JPA/Hibernate");
        } else if (percentage >= 70) {
            System.out.println("⭐⭐ NIVEAU : CONFIRMÉ");
            System.out.println("   Très bon travail ! Quelques concepts avancés");
            System.out.println("   méritent encore de la pratique.");
            System.out.println("   → Revoyez les parties non réussies puis passez au Module 2");
        } else if (percentage >= 50) {
            System.out.println("⭐ NIVEAU : JUNIOR SOLIDE");
            System.out.println("   Bonnes bases, mais vous devez pratiquer davantage.");
            System.out.println("   → Refaites les exercices 3-4 avant de continuer");
        } else {
            System.out.println("📚 NIVEAU : EN APPRENTISSAGE");
            System.out.println("   Retravaillez le Module 1 avant de continuer.");
            System.out.println("   → Concentrez-vous sur les Streams et Lambdas de base");
        }
        System.out.println("═══════════════════════════════════════════════\n");
        
        // Détails des points
        System.out.println("📝 Détails :");
        for (String fb : feedback) {
            System.out.println("   " + fb);
        }
        
        System.out.println("\n💡 Prochaines étapes :");
        if (percentage >= 70) {
            System.out.println("   1. Consultez le fichier jpa/COURS_JPA.md");
            System.out.println("   2. Commencez le Module 2 : JPA/Hibernate");
        } else {
            System.out.println("   1. Revoyez les exercices du Module 1");
            System.out.println("   2. Étudiez les corrections détaillées");
            System.out.println("   3. Relancez ce Coding Game dans 2-3 jours");
        }
        
        System.out.println("\n🎓 Bonne continuation dans votre formation !\n");
    }
}

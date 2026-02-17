package exercices.StreamsAndMore.revision;

import java.util.List;

public class ExerciceRecords {
    // TODO 1: Créez un record Order avec:
    public record Order(int id, String custumerName, double totalAmount, List<String> items) {
        public boolean isExpensive() {
            return totalAmount > 1000;
        }

        // Validation dans le constructeur
        public Order {
            if (totalAmount < 0) {
                throw new IllegalArgumentException("Prix négatif !");
            }
        }
    }

    // TODO 2: Créez un record Customer avec:
    public record Customer(int id, String name, String email, int age) {
    }

    public static void main(String[] args) {

        // Testez vos records ici
    }
}
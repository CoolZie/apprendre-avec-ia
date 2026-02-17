package exercices.StreamsAndMore;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import exercices.StreamsAndMore.Exercice1.Product;

public class Exercice5 {

        public static void afficherRapportCategorie(List<Product> products, String categorie) {

                // TODO:
                // - Nombre de produits
                // - Prix moyen
                // - Produit le moins cher
                // - Produit le plus cher
                // - Liste des produits triés par prix

                DoubleSummaryStatistics statistics = products.stream()
                                .filter(p -> p.category().equals(categorie))
                                .collect(Collectors.summarizingDouble(Product::price));

                List<String> sorteProducts = products.stream()
                                .filter(p -> p.category().equals(categorie))
                                .sorted(Comparator.comparingDouble(Product::price))
                                .map(Product::name)
                                .collect(Collectors.toList());
                Product moinsCher = products.stream()
                                .filter(p -> p.category().equals(categorie))
                                .min(Comparator.comparingDouble(Product::price))
                                .orElse(null);

                Product plusCher = products.stream()
                                .filter(p -> p.category().equals(categorie))
                                .max(Comparator.comparingDouble(Product::price))
                                .orElse(null);

                // Affichage
                System.out.printf("=== RAPPORT : %s ===%n", categorie);
                System.out.printf("Produits: %d%n", statistics.getCount());
                System.out.printf("Prix moyen: %.2f€%n", statistics.getAverage());

                if (moinsCher != null) {
                        System.out.printf("Moins cher: %s (%.2f€)%n",
                                        moinsCher.name(), moinsCher.price());
                }

                if (plusCher != null) {
                        System.out.printf("Plus cher: %s (%.2f€)%n",
                                        plusCher.name(), plusCher.price());
                }

                System.out.println("Liste: " + String.join(",", sorteProducts));

        }
}

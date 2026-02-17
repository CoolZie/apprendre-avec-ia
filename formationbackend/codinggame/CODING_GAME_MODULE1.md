# 🎮 CODING GAME - Module 1 : Java Fondamental

## 🎯 Objectif
Relevez les défis progressifs pour évaluer et prouver vos compétences en Java moderne !

**Niveau :** Junior à Confirmé  
**Durée estimée :** 45-60 minutes  
**Score minimum pour réussir :** 70/100

---

## 📋 Instructions

1. **Complétez le fichier** `CodingGameModule1.java`
2. **Lancez les tests** pour vérifier votre code
3. **Consultez votre score** final
4. **Comparez** votre solution avec les best practices

---

## 🏆 Système de points

| Niveau | Points | Description |
|--------|--------|-------------|
| ⭐ Défi 1 | 10 pts | Manipulation de Streams basique |
| ⭐ Défi 2 | 15 pts | Lambdas et filtres |
| ⭐⭐ Défi 3 | 20 pts | Collectors complexes |
| ⭐⭐ Défi 4 | 20 pts | Groupements et transformations |
| ⭐⭐⭐ Défi 5 | 20 pts | Records et immutabilité |
| ⭐⭐⭐ Défi 6 | 15 pts | Défi Expert : Combinaison complète |

**Total :** 100 points

---

## 🎲 Les Défis

### ⭐ Défi 1 : Stream Warriors (10 points)
**Niveau :** Junior

Vous recevez une liste de nombres. Transformez-la !

**Mission :**
```java
// Entrée : [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
// Sortie : [4, 16, 36, 64, 100] (carrés des nombres pairs uniquement)
```

**Ce qui est testé :**
- Utilisation de `filter()`
- Utilisation de `map()`
- Utilisation de `collect()`

---

### ⭐ Défi 2 : Lambda Master (15 points)
**Niveau :** Junior

Créez des lambdas pour trier une liste de mots de 3 façons différentes.

**Mission :**
```java
// Entrée : ["Java", "Python", "C++", "JavaScript", "Go"]
// Sorties attendues :
// 1. Par ordre alphabétique
// 2. Par longueur croissante
// 3. Par longueur décroissante puis alphabétique
```

**Ce qui est testé :**
- Syntaxe lambda
- Références de méthodes
- Comparateurs personnalisés

---

### ⭐⭐ Défi 3 : Collector Champion (20 points)
**Niveau :** Intermédiaire

Analysez une liste de transactions financières.

**Mission :**
```java
// Calculez :
// 1. La somme totale des transactions
// 2. La moyenne des montants
// 3. Le montant maximum
// 4. Les statistiques complètes (count, sum, min, max, average)
```

**Ce qui est testé :**
- `Collectors.summingDouble()`
- `Collectors.averagingDouble()`
- `Collectors.summarizingDouble()`
- Utilisation combinée de plusieurs collectors

---

### ⭐⭐ Défi 4 : Grouping Guru (20 points)
**Niveau :** Intermédiaire

Groupez des employés par département et calculez des statistiques.

**Mission :**
```java
// Groupez les employés :
// 1. Par département (Map<String, List<Employee>>)
// 2. Par département avec leur salaire moyen (Map<String, Double>)
// 3. Par département avec le nombre d'employés (Map<String, Long>)
// 4. Par tranche de salaire (<30K, 30-50K, >50K)
```

**Ce qui est testé :**
- `Collectors.groupingBy()`
- Collectors en cascade (downstream)
- Logique de partitionnement complexe

---

### ⭐⭐⭐ Défi 5 : Record Architect (20 points)
**Niveau :** Confirmé

Créez et manipulez des Records immuables.

**Mission :**
```java
// Créez un système de gestion de commandes avec Records :
// - Order (id, customer, items, totalPrice)
// - OrderItem (productName, quantity, price)
// - Customer (name, email, vipStatus)

// Implémentez :
// 1. Création d'orders immutables
// 2. Méthode withDiscount() qui retourne un nouvel Order
// 3. Validation dans le compact constructor
// 4. Méthode calculée getTotalItems()
```

**Ce qui est testé :**
- Syntaxe des Records
- Immutabilité
- Compact constructors
- Méthodes personnalisées dans Records

---

### ⭐⭐⭐ Défi 6 : Expert Challenge (15 points)
**Niveau :** Confirmé

Combinez tous vos skills pour résoudre un problème réel !

**Mission :**
```java
// Système d'analyse de ventes e-commerce
// Données : Liste de Sales (date, product, category, amount, region)

// Trouvez :
// 1. Le top 3 des produits par région
// 2. Les catégories générant >50% du CA total
// 3. La tendance mensuelle (groupement par mois)
// 4. Les régions avec croissance >10% sur 2 périodes
```

**Ce qui est testé :**
- Stream pipelines complexes
- Collectors avancés multiples
- Logique métier
- Performance et lisibilité

---

## 🚀 Lancer le Coding Game

```bash
# Étape 1 : Ouvrir le fichier de jeu
code CodingGameModule1.java

# Étape 2 : Lancer les tests
javac CodingGameModule1.java CodingGameTests.java
java CodingGameTests

# Ou avec votre IDE
# Run → CodingGameTests
```

---

## 📊 Interprétation des résultats

### Score 90-100 ⭐⭐⭐
**Expert Java !** Vous maîtrisez parfaitement les Streams, Lambdas, Collectors et Records.
Vous êtes prêt pour des architectures complexes et du code de production.

### Score 70-89 ⭐⭐
**Confirmé.** Vous avez de bonnes bases mais quelques concepts avancés méritent approfondissement.
Revoyez les collectors en cascade et les Records.

### Score 50-69 ⭐
**Junior solide.** Vous comprenez les bases mais manquez de pratique sur les cas complexes.
Refaites les exercices 3-4 et étudiez les corrections.

### Score <50
**En apprentissage.** Retravaillez le Module 1 avant de continuer.
Concentrez-vous sur les Streams et Lambdas de base.

---

## 💡 Conseils pour réussir

### ✅ Bonnes pratiques
- Utilisez des noms de variables explicites
- Privilégiez les références de méthodes aux lambdas quand possible
- Évitez les side-effects dans les streams
- Commentez la logique complexe

### ❌ Pièges à éviter
- Ne modifiez pas les collections sources dans un stream
- N'utilisez pas `peek()` pour la logique métier
- Évitez les streams trop longs (>3-4 opérations)
- Ne mélangez pas streams parallèles et séquentiels

### 🎯 Optimisations
- Utilisez `filter()` avant `map()` pour réduire les éléments
- Préférez `findFirst()` à `collect()` quand vous cherchez un élément
- Utilisez `count()` au lieu de `collect().size()`

---

## 📚 Ressources

Si vous êtes bloqué, consultez :
- `exercices/Exercice1.java` à `Exercice5.java` : Exemples similaires
- `revision/ExerciceStream.java` : Révision Streams
- `revision/ExerciceCollectors.java` : Révision Collectors
- `revision/ExerciceRecords.java` : Révision Records

---

## 🎓 Après le Coding Game

### Si vous avez réussi (>70%) :
✅ Passez au **Module 2 : JPA/Hibernate**  
✅ Consultez le fichier `jpa/COURS_JPA.md`

### Si vous devez vous améliorer (<70%) :
📖 Refaites les exercices du Module 1  
📖 Étudiez les corrections détaillées  
📖 Relancez le Coding Game dans 2-3 jours

---

## 🏅 Hall of Fame

À la fin du Coding Game, votre score sera enregistré avec :
- Votre temps de résolution
- Les défis réussis
- Les optimisations détectées
- Votre niveau global

**Bon courage, Developer ! 🚀**

---

*Ce Coding Game est conçu pour être auto-corrigé et fournir un feedback immédiat.*
*Tous les tests sont automatisés et vérifient la correction fonctionnelle ET les bonnes pratiques.*

# 🚀 Guide de démarrage - Coding Game Module 1

## 📋 Contenu créé

Votre Coding Game est prêt ! Voici les fichiers créés :

1. **CODING_GAME_MODULE1.md** - Instructions détaillées et règles du jeu
2. **CodingGameModule1.java** - Fichier à compléter (vos réponses)
3. **CodingGameTests.java** - Tests automatisés pour vérifier votre code
4. **CodingGameModule1_CORRECTION.java** - Correction complète avec explications

---

## ⚡ Démarrage rapide (3 étapes)

### Étape 1 : Lisez les instructions
```bash
Ouvrir : CODING_GAME_MODULE1.md
```
📖 Ce fichier contient toutes les règles et défis à relever.

### Étape 2 : Codez vos solutions
```bash
Ouvrir : CodingGameModule1.java
```
✏️ Complétez les méthodes marquées avec "TODO"

### Étape 3 : Testez votre code
```bash
# Compilation
javac CodingGameModule1.java CodingGameTests.java

# Exécution des tests
java CodingGameTests
```
✅ Le système calcule automatiquement votre score !

---

## 🎯 Structure du jeu

### Les 6 défis à relever

| Défi | Niveau | Points | Concepts testés |
|------|--------|--------|-----------------|
| 1. Stream Warriors | ⭐ Junior | 10 pts | filter, map, collect |
| 2. Lambda Master | ⭐ Junior | 15 pts | Lambdas, comparateurs |
| 3. Collector Champion | ⭐⭐ Confirmé | 20 pts | Collectors avancés |
| 4. Grouping Guru | ⭐⭐ Confirmé | 20 pts | groupingBy, downstream |
| 5. Record Architect | ⭐⭐⭐ Expert | 20 pts | Records, immutabilité |
| 6. Expert Challenge | ⭐⭐⭐ Expert | 15 pts | Pipeline complet |

**Total : 100 points**

---

## 📊 Système d'évaluation

### Score > 90 : Expert 🏆
→ Passez directement au Module 2 (JPA)

### Score 70-89 : Confirmé ⭐⭐
→ Revoyez les parties manquées puis Module 2

### Score 50-69 : Junior ⭐
→ Refaites les exercices 3-4 avant de continuer

### Score < 50 : En apprentissage 📚
→ Retravaillez le Module 1 complet

---

## 💡 Conseils pour réussir

### ✅ À faire
- Testez chaque méthode au fur et à mesure
- Utilisez des noms de variables clairs
- Privilégiez les références de méthodes
- Commentez votre raisonnement

### ❌ À éviter
- Modifier les collections sources dans un stream
- Utiliser peek() pour la logique métier
- Créer des streams trop longs (>4 opérations)
- Copier-coller sans comprendre

---

## 🔍 En cas de blocage

### Option 1 : Indices progressifs
Les messages d'erreur des tests vous guident vers la solution.

### Option 2 : Exemples similaires
Consultez les exercices du Module 1 :
- `exercices/Exercice1.java` à `Exercice5.java`
- `revision/ExerciceStream.java`
- `revision/ExerciceCollectors.java`

### Option 3 : Correction complète
Ouvrez `CodingGameModule1_CORRECTION.java` mais seulement APRÈS avoir essayé !

---

## 🎮 Exemple d'utilisation

```bash
# 1. Ouvrir le fichier de travail
code CodingGameModule1.java

# 2. Compléter le premier défi (exemple)
public static List<Integer> getSquaresOfEvenNumbers(List<Integer> numbers) {
    return numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .collect(Collectors.toList());
}

# 3. Compiler et tester
javac CodingGameModule1.java CodingGameTests.java
java CodingGameTests

# 4. Voir les résultats
⭐ DÉFI 1 : Stream Warriors
─────────────────────────────
✅ Carrés des nombres pairs : PARFAIT !
   +10 points
```

---

## 📚 Ressources disponibles

### Documentation Java
- [Streams API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html)
- [Collectors](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html)
- [Records](https://docs.oracle.com/en/java/javase/16/language/records.html)

### Dans votre workspace
- `PROGRESSION.md` - Votre plan de formation
- `jpa/COURS_JPA.md` - Module suivant (JPA)

---

## 🏁 Après le Coding Game

### Si vous réussissez (≥70%)
1. ✅ Bravo ! Passez au Module 2
2. 📖 Ouvrez `jpa/COURS_JPA.md`
3. 🚀 Commencez les exercices JPA

### Si vous devez progresser (<70%)
1. 📝 Notez vos difficultés
2. 📖 Refaites les exercices concernés
3. 🔄 Relancez le Coding Game dans 2-3 jours

---

## ⚙️ Configuration IDE (optionnel)

### Visual Studio Code
```json
// .vscode/tasks.json
{
  "tasks": [
    {
      "label": "Run Coding Game",
      "type": "shell",
      "command": "javac CodingGameModule1.java CodingGameTests.java && java CodingGameTests"
    }
  ]
}
```

### IntelliJ IDEA
1. Right-click sur `CodingGameTests.java`
2. Run 'CodingGameTests.main()'

---

## 🎯 Objectifs pédagogiques

Ce Coding Game évalue votre maîtrise de :

1. **Streams API** - Manipulation fluide des collections
2. **Lambdas** - Programmation fonctionnelle
3. **Collectors** - Agrégation et transformation
4. **Records** - Structures de données immutables
5. **Bonnes pratiques** - Code lisible et maintenable

---

## 🤝 Support

Si vous rencontrez des problèmes :

1. Vérifiez que Java 16+ est installé : `java -version`
2. Compilez d'abord sans erreurs
3. Lisez attentivement les messages d'erreur
4. Consultez les exemples dans le dossier `exercices/`

---

## 🎊 Bonne chance !

N'oubliez pas : l'objectif n'est pas seulement de réussir,  
mais de **comprendre** chaque concept pour construire  
des applications backend robustes !

**Let's code! 🚀**

---

*Créé pour la Formation Backend Java - Module 1*  
*Auto-évaluation avec feedback immédiat*

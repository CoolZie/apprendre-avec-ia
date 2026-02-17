# 📋 Template pour les futurs Coding Games

## 🎯 Exigences standard pour tous les Coding Games

### ⏱️ Suivi du temps (OBLIGATOIRE)

Chaque Coding Game doit inclure :

1. **Temps par défi/exercice**
   - Variable `long defiStart = System.currentTimeMillis();` au début de chaque test
   - Enregistrement dans une Map : `exerciseTimes.put("Défi X", System.currentTimeMillis() - defiStart);`

2. **Affichage dans les résultats**
   ```
   ⏱️  Temps total : X secondes
   
   ⏱️  Temps par défi :
      Défi 1 : X ms
      Défi 2 : X ms
      ...
   ```

3. **Structure de données**
   ```java
   private static Map<String, Long> exerciseTimes = new LinkedHashMap<>();
   ```

### 📊 Structure des tests

Chaque méthode de test doit suivre ce pattern :

```java
private static void testDefiX() {
    long defiStart = System.currentTimeMillis();
    System.out.println("\n⭐ DÉFI X : Nom du défi");
    System.out.println("─────────────────────────────");
    
    // Tests...
    
    exerciseTimes.put("Défi X", System.currentTimeMillis() - defiStart);
}
```

### 🎮 Composants obligatoires

1. **Fichier Markdown** : `CODING_GAME_MODULEX.md`
   - Instructions détaillées
   - Système de points
   - Critères d'évaluation

2. **Fichier Java à compléter** : `CodingGameModuleX.java`
   - Méthodes avec TODO
   - Classes helper
   - Documentation claire

3. **Fichier de tests** : `CodingGameTestsX.java`
   - Tests automatisés
   - Calcul du score
   - **Suivi du temps par exercice**
   - Feedback détaillé

4. **Fichier de correction** : `CodingGameModuleX_CORRECTION.java`
   - Solutions complètes
   - Best practices
   - Explications détaillées

5. **Guide de démarrage** : `GUIDE_DEMARRAGE_CODING_GAME_X.md`
   - Instructions rapides
   - Commandes à exécuter

### 🏆 Système d'évaluation

- Score total : 100 points
- Niveaux :
  - 90-100 : Expert ⭐⭐⭐
  - 70-89 : Confirmé ⭐⭐
  - 50-69 : Junior ⭐
  - <50 : En apprentissage 📚

### ✅ Checklist de création

- [ ] Markdown avec instructions complètes
- [ ] Fichier Java avec exercices
- [ ] Tests automatisés avec suivi du temps
- [ ] Correction détaillée
- [ ] Guide de démarrage
- [ ] Tests compilent sans erreur
- [ ] Tous les défis sont vérifiables

---

**Date de création du template** : 28 janvier 2026  
**Basé sur** : CodingGameModule1 (avec améliorations)

package exercices.StreamsAndMore.revision;

import java.util.*;
import java.util.function.*;

public class ExerciceLambda {
    record Student(String name, int age, double grade) {
    }

    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("Alice", 20, 15.5),
                new Student("Bob", 22, 12.0),
                new Student("Charlie", 21, 18.0),
                new Student("Diana", 19, 14.5));

        // TODO 1: Créez un Predicate pour vérifier si grade >= 15
        Predicate<Student> hasGoodGrade = s -> s.grade >= 15;

        // TODO 2: Créez une Function pour extraire le nom
        Function<Student, String> getName = Student::name;

        // TODO 3: Créez un Consumer pour afficher "Nom: X, Note: Y"
        Consumer<Student> printStudent = s -> System.out.printf("Nom %s , Note: %f", s.name, s.grade);

        // TODO 4: Utilisez-les ensemble
        students.stream()
                .filter(hasGoodGrade)
                .forEach(printStudent);

        // TODO 5: Réécrivez avec method references
        students.stream()
                .filter(s -> s.grade >= 15)
                .forEach(s -> System.out.printf("Nom %s , Note: %f", s.name, s.grade));


                

    }
}
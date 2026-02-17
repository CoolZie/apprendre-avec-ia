package exercices.StreamsAndMore.revision;

import java.util.*;
import java.util.stream.*;

public class ExerciceCollectors {
    record Employee(String name, String department, int age, double salary) {
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Alice", "IT", 30, 75000),
                new Employee("Bob", "IT", 35, 85000),
                new Employee("Charlie", "HR", 28, 55000),
                new Employee("Diana", "HR", 32, 60000),
                new Employee("Eve", "Sales", 29, 70000),
                new Employee("Frank", "Sales", 40, 90000),
                new Employee("Grace", "IT", 26, 65000));

        // TODO 1: Grouper par département (Map<String, List<Employee>>)
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department));

        // TODO 2: Compter le nombre d'employés par département
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));

        // TODO 3: Salaire total par département
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.summingDouble(Employee::salary)));

        // TODO 4: Salaire moyen par département
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));

        // TODO 5: Employé le mieux payé par département
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
                                opt -> opt.map(Employee::name).orElse("NA"))

                ));

        // TODO 6: Noms des employés par département
        employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.mapping(Employee::name, Collectors.toList())));

        // TODO 7: Séparer en 2 groupes : seniors (age >= 30) vs juniors
        employees.stream()
                .collect(Collectors.partitioningBy(
                        emp -> emp.age >= 30));

        // TODO 8: Grouper par département, puis par tranche d'âge (<30, 30-35, >35)
        Map<Boolean, List<Employee>> byfilter = employees.stream()
                .collect(Collectors.partitioningBy(
                        emp -> emp.age > 30));
        // <30
        byfilter.get(true);
        // 30-35
        Map<Boolean, List<Employee>> byfilterAgain = byfilter.get(false).stream()
                .collect(Collectors.partitioningBy(
                        emp -> emp.age < 30 && emp.age > 35));
        // >35
        byfilterAgain.get(false);

        // TODO 9: Départements avec plus de 2 employés (filtrer la Map)
        Map<String, Long> mapCount = employees.stream()
                .collect(Collectors.groupingBy(Employee::department,
                        Collectors.counting()));
        List<String> departement = mapCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // TODO 10: Créer un rapport complet par département :
        // - Nombre d'employés
        // - Salaire moyen
        // - Employé le mieux payé
        record DepartementStatics(long nbreEmp, double salaryaverage, String empname) {
        }
        Map<String, DepartementStatics> statsByStats = employees
                .stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                (List<Employee> empList) -> {
                                    long count = empList.size();
                                    double avgSalary = empList.stream().mapToDouble(Employee::salary).average()
                                            .orElse(0.0);
                                    String topEarner = empList.stream()
                                            .max(Comparator.comparingDouble(Employee::salary)).map(Employee::name)
                                            .orElse("NA");
                                    return new DepartementStatics(count, avgSalary, topEarner);
                                })));
    }
}
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsTask {
    public static void main(String[] args) {
        /*Найдите в списке целых чисел 3-е наибольшее число (пример: 5 2 10 9 4 3 10 1 13 => 10)*/
        var numbers = List.of(5, 2, 10, 9, 4, 3, 10, 1, 13);
        System.out.println(
                numbers
                        .stream()
                        .sorted(Comparator.comparing(Integer::intValue).reversed())
                        .skip(2)
                        .findFirst()
                        .orElseThrow()
        );
        /*Найдите в списке целых чисел 3-е наибольшее «уникальное» число (пример: 5 2 10 9 4 3 10 1 13 => 9,
        в отличие от прошлой задачи здесь разные 10 считает за одно число)*/
        System.out.println(
                numbers
                        .stream()
                        .distinct()
                        .sorted(Comparator.comparing(Integer::intValue).reversed())
                        .skip(2)
                        .findFirst()
                        .orElseThrow()
        );

        //Список сотрудников
        var listOfEngineers = List.of(
                new Employee.EmployeeBuilder().name("Test").age(12).position("Engineer").build(),
                new Employee.EmployeeBuilder().name("TestTest").age(13).position("Инженер").build(),
                new Employee.EmployeeBuilder().name("TestTestTestTest").age(14).position("Инженер").build(),
                new Employee.EmployeeBuilder().name("TestTestTestTestTestTest").age(15).position("Engineer").build(),
                new Employee.EmployeeBuilder().name("TestTestTestTestTestTestTestTest").age(16).position("Инженер").build()
        );
        /*Имеется список объектов типа Сотрудник (имя, возраст, должность),
        необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер», в порядке убывания возраста*/
        listOfEngineers
                .stream()
                .sorted(Comparator.comparing(Employee::getAge).reversed())
                .filter(employee -> employee.getPosition().equals("Инженер"))
                .limit(3)
                .forEach(System.out::println);
        /*Имеется список объектов типа Сотрудник (имя, возраст, должность),
        посчитайте средний возраст сотрудников с должностью «Инженер»*/
        listOfEngineers
                .stream()
                .filter(employee -> employee.getPosition().equals("Инженер"))
                .mapToInt(Employee::getAge)
                .average()
                .ifPresent(System.out::println);
        /*Найдите в списке слов самое длинное*/
        var wordList = List.of("Test", "TestTest", "TestTestTestTest", "TestTestTestTestTestTest",
                "TestTestTestTestTestTestTestTest", "BestTestTestTestTestTestTestTest");
        wordList
                .stream()
                .max(Comparator.comparing(String::length))
                .ifPresent(System.out::println);
        /*Имеется строка с набором слов в нижнем регистре, разделенных пробелом. Постройте хеш-мапы,
        в которой будут хранится пары: слово - сколько раз оно встречается во входной строке*/
        var string = "this is a test test this is only a sample sample";
        System.out.println(
                Stream
                        .of(string.toLowerCase().split(" "))
                        .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
        );
        /*Отпечатайте в консоль строки из списка в порядке увеличения длины слова,
        если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок*/
        wordList
                .stream()
                .sorted(Comparator.comparing(String::length).thenComparing(String::compareTo))
                .forEach(System.out::println);

        /*Имеется массив строк, в каждой из которых лежит набор из 5 слов, разделенных пробелом,
        найдите среди всех слов самое длинное, если таких слов несколько, получите любое из них*/
        var listOfWordsList = List.of(
                List.of("Test", "TestTest", "TestTestTestTest",
                        "TestTestTestTestTestTest", "TestTestTestTestTestTestTestTest"),
                List.of("Best", "BestBest", "BestBestBestBest",
                        "BestBestBestBestBestBest", "BestBestBestBestBestBestBestBest")
        );
        listOfWordsList
                .stream()
                .flatMap(List::stream)
                .max(Comparator.comparing(String::length))
                .ifPresent(System.out::println);
    }
}

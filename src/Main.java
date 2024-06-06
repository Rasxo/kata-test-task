import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }

    static int romanToArabic(String input) {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }
        return result;
    }

    static String arabicToRoman(int number) {
        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }
        return sb.toString();
    }
}

class Data {

    private String firstOperand;

    private String secondOperand;

    private String operation;

    private boolean isRomanNumerals;

    public Data(String firstOperand, String secondOperand, String operation, boolean isRomanNumerals) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operation = operation;
        this.isRomanNumerals = isRomanNumerals;
    }

    public String getFirstOperand() {
        return firstOperand;
    }

    public String getSecondOperand() {
        return secondOperand;
    }

    public String getOperation() {
        return operation;
    }

    public boolean isRomanNumerals() {
        return isRomanNumerals;
    }
}

class Calculator {

    static int addition(int firstOperand, int secondOperand) {
        return firstOperand + secondOperand;
    }

    static int subtraction(int firstOperand, int secondOperand) {
        return firstOperand - secondOperand;
    }

    static int division(int firstOperand, int secondOperand) {
        return firstOperand / secondOperand;
    }

    static int multiply(int firstOperand, int secondOperand) {
        return firstOperand * secondOperand;
    }
}

class StringParser {

    static String romanRegex = "^M*(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
    static String arabicRegex = "\\d+";
    static String operatorRegex = "[+/*-]";

    static Data parse(String input) throws Exception {
        String[] operands = input.split(operatorRegex);

        verificationOperation(input);
        boolean romanNumerals = verificationOperands(operands);

        int startPosition = operands[0].length();
        String operation = input.substring(startPosition, startPosition + 1);

        return new Data(operands[0], operands[1], operation, romanNumerals);
    }

    static boolean isRomanNumerals(String[] operands) throws Exception {
        int romanNumeralsCount = 0;
        for (String operand : operands) {
            if (operand.matches(romanRegex) && !operand.equals("")) {
                romanNumeralsCount++;
            }
        }
        if (romanNumeralsCount == 1) {
            throw new Exception("используются одновременно разные системы счисления");
        }
        return romanNumeralsCount != 0;
    }

    static boolean verificationOperands(String[] operands) throws Exception {
        if (operands.length != 2) {
            throw new Exception("строка не является математической операцией");
        }

        boolean isRomanNumerals = isRomanNumerals(operands);

        for (String operand : operands) {
            boolean isArabic = operand.matches(arabicRegex);
            boolean isRoman = operand.matches(romanRegex);
            boolean isEmpty = operand.equals("");

            if (isEmpty) {
                throw new Exception("строка не является математической операцией");
            } else if (!isArabic && !isRoman) {
                throw new Exception("калькулятор умеет работать только с целыми числами");
            }

            if (!isRomanNumerals) {
                if (Integer.parseInt(operand) < 1 || Integer.parseInt(operand) > 10) {
                    throw new Exception("калькулятор должен принимать на вход арабские числа" +
                            " от 1 до 10 включительно");
                }
            } else {
                if (RomanNumeral.romanToArabic(operand) > 10) {
                    throw new Exception("калькулятор должен принимать на вход римские числа" +
                            " от 1 до 10 включительно");
                }
            }
        }
        return isRomanNumerals;
    }

    static void verificationOperation(String input) throws Exception {
        long count = input.chars().filter(ch -> (ch == '+' || ch == '-' || ch == '/' || ch == '*')).count();

        if (count != 1) {
            throw new Exception("формат математической операции не удовлетворяет заданию" +
                    " - два операнда и один оператор (+, -, /, *)");
        }
    }
}

public class Main {

    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("Input:");
            Scanner scanner = new Scanner(System.in);
            String output = calc(scanner.nextLine().replaceAll("\\s", ""));
            System.out.println("\nOutput:\n" + output + "\n");
        }
    }

    public static String calc(String input) throws Exception {

        Data data = StringParser.parse(input);

        int firstOperand;
        int secondOperand;
        boolean isRomanNumerals = data.isRomanNumerals();

        if (isRomanNumerals) {
            firstOperand = RomanNumeral.romanToArabic(data.getFirstOperand());
            secondOperand = RomanNumeral.romanToArabic(data.getSecondOperand());
        } else {
            firstOperand = Integer.parseInt(data.getFirstOperand());
            secondOperand = Integer.parseInt(data.getSecondOperand());
        }

        int result;

        switch (data.getOperation()) {
            case "+" -> result = Calculator.addition(firstOperand, secondOperand);
            case "-" -> {
                result = Calculator.subtraction(firstOperand, secondOperand);
                if (isRomanNumerals && result < 1) {
                    throw new Exception("результаты работы калькулятора с римскими числами могут" +
                            " быть только положительные числа");
                }
            }
            case "/" -> {
                result = Calculator.division(firstOperand, secondOperand);
                if (isRomanNumerals && result < 1) {
                    throw new Exception("результаты работы калькулятора с римскими числами могут" +
                            " быть только положительные числа");
                }
            }
            default -> result = Calculator.multiply(firstOperand, secondOperand);
        }
        return String.valueOf(isRomanNumerals ? RomanNumeral.arabicToRoman(result) : result);
    }
}

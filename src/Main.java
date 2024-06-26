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

enum OperandType {
    ARABIC, ARABIC_INTEGER, ROMAN, INCORRECT;

    static String romanRegex = "^M*(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
    static String arabicRegex = "\\d+.?\\d+";
    static String arabicIntegerRegex = "\\d+";

    static OperandType getType(String input) {
        if (input.matches(romanRegex) && !input.equals("")) {
            return ROMAN;
        } else if (input.matches(arabicRegex)) {
            return ARABIC;
        } else if (input.matches(arabicIntegerRegex)) {
            return ARABIC_INTEGER;
        }
        return INCORRECT;
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

    static String operatorRegex = "[+/*-]";

    static Data parse(String input) throws Exception {
        String[] operands = input.split(operatorRegex);

        verificationOperation(input);
        boolean romanNumerals = isRomanNumerals(operands);

        int startPosition = operands[0].length();
        String operation = input.substring(startPosition, startPosition + 1);

        return new Data(operands[0], operands[1], operation, romanNumerals);
    }

    static boolean isRomanNumerals(String[] operands) throws Exception {
        if (operands.length != 2) {
            throw new Exception("строка не является математической операцией");
        }

        String firstOperand = operands[0];
        String secondOperand = operands[1];

        OperandType firstOperandType = OperandType.getType(firstOperand);
        OperandType secondOperandType = OperandType.getType(secondOperand);

        if (firstOperandType == OperandType.INCORRECT || secondOperandType == OperandType.INCORRECT) {
            throw new Exception("строка не является математической операцией");
        }

        if ((firstOperandType == OperandType.ARABIC || secondOperandType == OperandType.ARABIC)) {
            throw new Exception("калькулятор умеет работать только с целыми числами");
        }

        if (firstOperandType != secondOperandType) {
            throw new Exception("используются одновременно разные системы счисления");
        }

        if (firstOperandType == OperandType.ARABIC_INTEGER) {
            if ((Integer.parseInt(firstOperand) < 1 || Integer.parseInt(firstOperand) > 10) ||
                    (Integer.parseInt(secondOperand) < 1 || Integer.parseInt(secondOperand) > 10)) {
                throw new Exception("калькулятор должен принимать на вход арабские числа" +
                        " от 1 до 10 включительно");
            }
        }

        if (firstOperandType == OperandType.ROMAN) {
            if ((RomanNumeral.romanToArabic(firstOperand) > 10) || (RomanNumeral.romanToArabic(secondOperand) > 10)) {
                throw new Exception("калькулятор должен принимать на вход римские числа" +
                        " от 1 до 10 включительно");
            }
            return true;
        }
        return false;
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

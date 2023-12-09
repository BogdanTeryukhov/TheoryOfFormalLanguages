package Lab1;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StateMachine {
    public enum State{
        START, SYMBOL, NORMAL_BRACKET, REVERSED_BRACKET, COMMA
    }

    public static boolean isLetter(char symbol){
        return (symbol >= 97 && symbol <= 122); 
    }

    //конечный автомат для парсинга выражения
    public static Map<String, List<String>> parse(String line) throws InputArgumentException {
        State state = State.START;

        List<String> functions = new ArrayList<>();
        List<String> variables = new ArrayList<>();

        Map<String, List<String>> functionsToVariables = new LinkedHashMap<>();
        int reversedBracketsCounter = 1;
        char previousSymbol = ' ';
        int index = 0;

        for (Character character : line.toCharArray()) {
            if (state == State.START)
            {
                state = State.SYMBOL;
            }

            else if (state == State.SYMBOL)
            {
                if (character == 44){ // запятая
                    state = State.COMMA;
                } else if (character == 40) {
                    if (isLetter(previousSymbol)){
                        //System.out.println(previousSymbol + "-----" + functions);
                        if (functions.contains(String.valueOf(previousSymbol))){
                            functions.add(previousSymbol + "_" + index);
                        }else{
                            functions.add(String.valueOf(previousSymbol));
                        }
                        index++;
                        reversedBracketsCounter = 1;
                    }
                    state = State.NORMAL_BRACKET;
                } else if (character == 41) {
                    state = State.REVERSED_BRACKET;
                }
            }

            else if (state == State.NORMAL_BRACKET)
            {
                if (character == 41){
                    functionsToVariables.put(functions.get(functions.size() - reversedBracketsCounter), new ArrayList<>());
                    state = State.REVERSED_BRACKET;
                }else{
                    variables.add(String.valueOf(character));
                    functionsToVariables.put(functions.get(functions.size() - reversedBracketsCounter), new ArrayList<>());
                    functionsToVariables.get(functions.get(functions.size() - reversedBracketsCounter)).add(String.valueOf(character));//добавление последнего в "переменных"
                    state = State.SYMBOL;
                }
            }

            else if (state == State.COMMA)
            {
                if (functionsToVariables.containsKey(String.valueOf(character))){
                    variables.add(character + "_" + index);
                    functionsToVariables.get(functions.get(functions.size() - reversedBracketsCounter))
                            .add(character + "_" + index);
                }else{
                    variables.add(String.valueOf(character));
                    functionsToVariables.get(functions.get(functions.size() - reversedBracketsCounter))
                            .add(String.valueOf(character));
                }
                state = State.SYMBOL;
            }

            else if (state == State.REVERSED_BRACKET)
            {
                if (character == 44){
                    reversedBracketsCounter++;
                    state = State.COMMA;
                }
            }

            previousSymbol = character;
//            for (Map.Entry<String, List<String>> entry: functionsToVariables.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
        }
//        System.out.println("Functions: " + functions);
//        System.out.println("Variables: " + variables);

        return reversedMapsCreator(functionsToVariables);
    }

    public static String[] compositionCreator(Map<String, List<String>> map){
        StringBuilder mainBuilder = new StringBuilder();
        boolean isItFirstTime = true;
        List<String> toMultList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry: map.entrySet()) {
            StringBuilder stringBuilder = new StringBuilder();

            String key = entry.getKey().substring(0,1);
            int current = 0;
            for (int i = entry.getValue().size(); i >= 0; i--) {
                if (i == 0){
                    stringBuilder
                            .append(key)
                            .append(i);
                    toMultList.add(key
                            .concat(String.valueOf(i)));
                } else if (i == entry.getValue().size() && !isItFirstTime) {
                    List<String> proxyList = new ArrayList<>();
                    for (String s : toMultList) {
                        stringBuilder
                                .append(key)
                                .append(i)
                                .append(s)
                                .append("+");
                        proxyList.add(key
                                .concat(String.valueOf(i)).concat(s));
                    }
                    toMultList = proxyList;
                } else {
                    if (!isItFirstTime){
                        current++;
                    }
                    toMultList.add(key
                            .concat(String.valueOf(i))
                            .concat(String.valueOf(entry.getValue().get(current))));

                    stringBuilder
                            .append(key)
                            .append(i)
                            .append(entry.getValue().get(current))
                            .append("+");
                    current++;
                }
            }
            isItFirstTime = false;
            mainBuilder = stringBuilder;
        }

        return mainBuilder.toString().split("\\+");
    }

    public static Map<String, List<String>> reversedMapsCreator(Map<String, List<String>> map){
        Set<String> keySet = map.keySet();
        Map<String, List<String>> reversedMap = new LinkedHashMap<>();
        List<String> keyList1 = new ArrayList<>(keySet);
        Collections.reverse(keyList1);

        for (int i = 0; i < keySet.size(); i++) {
            String letter = keyList1.get(i);
            reversedMap.put(letter, map.get(letter));
        }
        return reversedMap;
    }

    public static List<String> declareFunHelper(Map<String, List<String>> map, PrintWriter writer){
        List<String> uniqueElements = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry: map.entrySet()) {
            String key = entry.getKey().substring(0,1);
            for (int i = entry.getValue().size(); i >= 0; i--) {
                uniqueElements.add(key + "" + i); //___
                writer.println("(declare-fun " + key + i + " () Int)"); //___
            }
        }
        return uniqueElements;
    }

    public static List<String> declareFunHelperForSecond(Map<String, List<String>> map, PrintWriter writer, List<String> uniqueElements){
        for (Map.Entry<String, List<String>> entry: map.entrySet()) {
            String key = entry.getKey().substring(0,1);
            for (int i = entry.getValue().size(); i >= 0; i--) {
                if (!uniqueElements.contains(key + "" + i)) {
                    writer.println("(declare-fun " + key + i + " () Int)"); //___
                    uniqueElements.add(key + "" + i); //___
                }
            }
        }
        return uniqueElements;
    }

    public static void firstAssertHelper(char[] firstMultiplier, PrintWriter writer){
        for (int i = 1; i < firstMultiplier.length; i += 2) {
            String str = String.valueOf(firstMultiplier[i - 1]).concat(String.valueOf(firstMultiplier[i]));
            writer.print(str + " ");
        }
    }

    public static List<String> freeCoefficientsMakerForSecondAssert(String[] strings){
        List<String> freeCoefficientsForStrings = new ArrayList<>();
        for (String str: strings) {
            if (str.charAt(str.length() - 1) == '0'){
                freeCoefficientsForStrings.add(str);
            }
        }
        return freeCoefficientsForStrings;
    }

    public static void secondAssertHelper(List<String> freeCoefficientsForStrings, PrintWriter writer){
        for (int i = 0; i < freeCoefficientsForStrings.size(); i++) {
            char[] proxy = freeCoefficientsForStrings.get(i).toCharArray();
            if (proxy.length == 2){
                writer.print(freeCoefficientsForStrings.get(i) + ") ");
            }
            else {
                for (int j = 1; j < proxy.length; j += 2) {
                    String string = String.valueOf(proxy[j - 1]).concat(String.valueOf(proxy[j]));
                    writer.print(string + " ");
                }
                if (i + 1 == freeCoefficientsForStrings.size() - 1){
                    writer.print(") ");
                }else{
                    writer.print(") (* ");
                }
            }
        }
    }

    public static void makeAssertionsForOneAndTwoVars(String[] strings1, String[] strings2,
                                                PrintWriter writer, List<String> finalUnique, int numberOfVars){
        //0_1 assert
        String coefficientWithX1 = strings1[0].substring(0,2);
        String coefficientWithX2 = strings2[0].substring(0,2);

        //0_1 assert
        writer.println("(assert (>= " + coefficientWithX1 + " " + coefficientWithX2 + "))");

        //0_2 assert
        writer.println("(assert (>= " + coefficientWithX1 + " " + coefficientWithX1 + "))");

        //1 assert при x
        char[] firstMultiplier1X = strings1[0].substring(0, strings1[0].length() - 1).toCharArray();
        char[] firstMultiplier2X = strings2[0].substring(0, strings2[0].length() - 1).toCharArray();
        writer.print("(assert (>= (* ");
        firstAssertHelper(firstMultiplier1X, writer);
        writer.print(") (* ");
        firstAssertHelper(firstMultiplier2X, writer);
        writer.println(")))");

        //1 assert при Y
        if (numberOfVars == 2){
            char[] firstMultiplier1Y = strings1[1].substring(0, strings1[1].length() - 1).toCharArray();
            char[] firstMultiplier2Y = strings2[1].substring(0, strings2[1].length() - 1).toCharArray();
            writer.print("(assert (>= (* ");
            firstAssertHelper(firstMultiplier1Y, writer);
            writer.print(") (* ");
            firstAssertHelper(firstMultiplier2Y, writer);
            writer.println(")))");
        }


        //2 assert (по свободному члену)
        List<String> freeCoefficientsForStrings1 = freeCoefficientsMakerForSecondAssert(strings1);
        List<String> freeCoefficientsForStrings2 = freeCoefficientsMakerForSecondAssert(strings2);
        writer.print("(assert (>= (+ (* ");
        secondAssertHelper(freeCoefficientsForStrings1, writer);
        writer.print("(+ (* ");
        secondAssertHelper(freeCoefficientsForStrings2, writer);
        writer.println("))");


        //3 assert
        if (numberOfVars == 2){
            char[] firstMultiplier1Y = strings1[1].substring(0, strings1[1].length() - 1).toCharArray();
            char[] firstMultiplier2Y = strings2[1].substring(0, strings2[1].length() - 1).toCharArray();

            writer.print("(assert (or (and (> (* ");
            firstAssertHelper(firstMultiplier1X, writer);
            writer.print(") (* ");
            firstAssertHelper(firstMultiplier2X, writer);
            writer.print(")) (> (* ");
            firstAssertHelper(firstMultiplier1Y, writer);
            writer.print(") (* ");
            firstAssertHelper(firstMultiplier2Y, writer);
            writer.print("))) (> (+ (* ");
        }else {
            writer.print("(assert (or (> (* ");
            firstAssertHelper(firstMultiplier1X, writer);
            writer.print(") (* ");
            firstAssertHelper(firstMultiplier2X, writer);
            writer.print(")) (> (+ (* ");
        }
        secondAssertHelper(freeCoefficientsForStrings1, writer);
        writer.print("(+ (* ");
        secondAssertHelper(freeCoefficientsForStrings2, writer);
        writer.println(")))");


        //4 assert
        writer.print(("(assert (and "));
        for (String string: finalUnique) {
            if (string.matches(".0")){
                writer.print("(>= " + string + " 0)");
            }else{
                writer.print("(>= " + string + " 1)");
            }
        }
        writer.println("))");

        //5 assert
        writer.print(("(assert (and "));
        int cursor = 0;
        String current = finalUnique.get(0).substring(0, 1);
        boolean firstTime = true;
        for (String string: finalUnique) {
            if (cursor == 0 || (!string.substring(0, 1).equals(current)) && firstTime){
                if (cursor == 0){
                    writer.print("(or ");
                }else {
                    writer.print(") (or ");
                    firstTime = false;
                }
            }
            if (string.matches(".0")){
                writer.print("(>= " + string + " 0)");
            }else{
                writer.print("(>= " + string + " 1)");
            }
            cursor++;
        }
        writer.println(")))");
    }

    public static boolean exceptionChecker(String[] strings1, String[] strings2){
        if((strings1[0].charAt(strings1[0].length() - 1) == 'x' && !(strings2[0].charAt(strings2[0].length() - 1) == 'x')) ||
                (!(strings1[0].charAt(strings1[0].length() - 1) == 'x') && strings2[0].charAt(strings2[0].length() - 1) == 'x')){
            return true;
        }else if ((strings1[1].charAt(strings1[1].length() - 1) == 'y' && !(strings2[1].charAt(strings2[1].length() - 1) == 'y')) ||
            (!(strings1[1].charAt(strings1[1].length() - 1) == 'y') && strings2[1].charAt(strings2[1].length() - 1) == 'y')) {
            return true;
        }
        return false;
    }

     public static boolean isItOnlyOneFunction(String[] strings1, String[] strings2){
        return (strings1.length <= 3 && strings2.length > 3) || (strings2.length <= 3 && strings1.length > 3);
     }

    public static boolean smtFileFormer(Map<String, List<String>> map1,
                                     Map<String, List<String>> map2,
                                     String[] strings1,
                                     String[] strings2)
            throws IOException {

        PrintWriter writer = new PrintWriter(new FileWriter(".\\smt-solver-temp.smt2", true));
        boolean ifUnsat = false;
        //start
        writer.println("(set-logic QF_NIA)");

        //declare
        List<String> uniqueElements = declareFunHelper(map1, writer);
        List<String> finalUnique = declareFunHelperForSecond(map2, writer, uniqueElements);

        //проверка на равенство количества переменных слева и справа
        if (exceptionChecker(strings1, strings2) || isItOnlyOneFunction(strings1,strings2)){
            ifUnsat = true;
        }

        int capacity = 0;
        if (finalUnique.get(0).matches(".2")){
            capacity = 2;
        } else if (finalUnique.get(0).matches(".1")) {
            capacity = 1;
        }


        if (capacity == 2){
            makeAssertionsForOneAndTwoVars(strings1, strings2, writer, finalUnique, capacity);
        } else if (capacity == 1) {
            makeAssertionsForOneAndTwoVars(strings1, strings2, writer, finalUnique, capacity);
        }else{
            //0_1 assert
            String coefficientWithX1 = strings1[0].substring(0,2);
            String coefficientWithX2 = strings2[0].substring(0,2);

            //0_1 assert
            writer.println("(assert (>= " + coefficientWithX1 + " " + coefficientWithX2 + "))");

            //0_2 assert
            writer.println("(assert (>= " + coefficientWithX1 + " " + coefficientWithX1 + "))");

            //2 assert (по свободному члену)
            List<String> freeCoefficientsForStrings1 = freeCoefficientsMakerForSecondAssert(strings1);
            List<String> freeCoefficientsForStrings2 = freeCoefficientsMakerForSecondAssert(strings2);
            writer.print("(assert (>= (+ (* ");
            secondAssertHelper(freeCoefficientsForStrings1, writer);
            writer.print("(+ (* ");
            secondAssertHelper(freeCoefficientsForStrings2, writer);
            writer.println("))");


            //3 assert
            writer.print("(assert (> (+ (* ");
            secondAssertHelper(freeCoefficientsForStrings1, writer);
            writer.print("(+ (* ");
            secondAssertHelper(freeCoefficientsForStrings2, writer);
            writer.println("))");

            //4 assert
            writer.print(("(assert (and "));
            for (String string: finalUnique) {
                if (string.matches(".0")){
                    writer.print("(>= " + string + " 0)");
                }else{
                    writer.print("(>= " + string + " 1)");
                }
            }
            writer.println("))");

            //5 assert
            writer.print(("(assert (and "));
            int cursor = 0;
            String current = finalUnique.get(0).substring(0, 1);
            boolean firstTime = true;
            for (String string: finalUnique) {
                if (cursor == 0 || (!string.substring(0, 1).equals(current)) && firstTime){
                    if (cursor == 0){
                        writer.print("(or ");
                    }else {
                        writer.print(") (or ");
                        firstTime = false;
                    }
                }
                if (string.matches(".0")){
                    writer.print("(>= " + string + " 0)");
                }else{
                    writer.print("(>= " + string + " 1)");
                }
                cursor++;
            }
            writer.println(")))");
        }
        //finish
        writer.println("(check-sat)");
        writer.print("(get-model)");
        writer.close();
        return ifUnsat;
    }
    public static void declaresToFile(BufferedReader declaresReader, BufferedWriter writer) throws IOException {
        String line = declaresReader.readLine();
        List<String> isContains = new ArrayList<>();
        while(line != null) {
            line = declaresReader.readLine();
            if (line == null){
                break;
            }
            if (line.matches("\\(declare-fun.\\S\\d.\\(\\).Int\\)")){
                if (!isContains.contains(line)){
                    isContains.add(line);
                    writer.write(line + "\n");
                }
            }
        }
    }

    public static void assertsToFile(BufferedReader assertsReader, BufferedWriter writer) throws IOException {
        String assertLine = assertsReader.readLine();
        while(assertLine != null) {
            assertLine = assertsReader.readLine();
            if (assertLine == null){
                break;
            }
            if (assertLine.matches("\\(assert.*")){
                writer.write(assertLine + "\n");
            }
        }
    }
    public static String smtResponse(List<Boolean> isNotEqualVars){
        if (isNotEqualVars.contains(true)){
            return "UNSATISFIABLE";
        }else{
            try (Context context = new Context()) {
                Solver solver = context.mkSimpleSolver();
                solver.fromFile("smt-solver.smt2");
                return solver.check().toString();
            }
        }
    }

    public static void main(String[] args)
            throws InputArgumentException, IOException {
        Scanner scanner = new Scanner(System.in);

        Path pathToFile = Path.of(".\\smt-solver-temp.smt2");
        if (Files.exists(pathToFile)){
            Files.delete(pathToFile);
        }

        List<Boolean> isNotEqualsVars = new ArrayList<>();
        while (true){
            String part1 = scanner.next();
            String part2 = scanner.next();

            Map<String, List<String>> resultOfParsing1 = parse(part1);
            Map<String, List<String>> resultOfParsing2 = parse(part2);

            String[] strings1 = compositionCreator(resultOfParsing1);
            String[] strings2 = compositionCreator(resultOfParsing2);

            //System.out.println(Arrays.toString(strings1));
            //System.out.println(Arrays.toString(strings2));

            isNotEqualsVars.add(smtFileFormer(resultOfParsing1, resultOfParsing2, strings1, strings2));

            System.out.println("Want to continue? Print y/n");
            String wantMore = scanner.next();
            if (wantMore.equals("n")){
                break;
            }
        }

        Path pathToWrite = Path.of(".\\smt-solver.smt2");
        if (Files.exists(pathToWrite)){
            Files.delete(pathToWrite);
        }

        BufferedReader declaresReader = new BufferedReader(new FileReader(pathToFile.toFile()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(pathToWrite.toFile(),true));
        writer.write("(set-logic QF_NIA)" + "\n");
        declaresToFile(declaresReader, writer);
        declaresReader.close();

        BufferedReader assertsReader = new BufferedReader(new FileReader(pathToFile.toFile()));
        assertsToFile(assertsReader, writer);
        assertsReader.close();

        writer.write("(check-sat)" + "\n");
        writer.write("(get-model)" + "\n");
        writer.close();

        Files.delete(pathToFile);
        System.out.println(smtResponse(isNotEqualsVars));
    }
}
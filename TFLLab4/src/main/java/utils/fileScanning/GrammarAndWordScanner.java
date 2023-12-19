package utils.fileScanning;

import utils.grammar.Grammar;
import utils.grammar.rule.Rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GrammarAndWordScanner {
    public static String word;

    public static String incWord;
    public static List<String> fromStrings = new ArrayList<>();
    public static List<String> orSplitter(String to){
        StringBuilder builder = new StringBuilder();
        List<String> toList = new ArrayList<>();
        char[] arr = to.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '|'){
                toList.add(builder.toString());
                builder.setLength(0);
            }
            else {
                builder.append(arr[i]);
            }
        }
        toList.add(builder.toString());
        return toList;
    }
    public static void rulesCreator(String line, Grammar grammar){
        String from = line.substring(0,line.indexOf('-'));
        List<String> to = orSplitter(line.substring(line.indexOf('>') + 1, line.length()));
        if (from.length() != 1){
            throw new RuntimeException("Is not a character");
        }
        if (fromStrings.contains(from)){
            grammar.getRulesSet().get(Grammar.getIndexOfParticularRule(from.charAt(0), grammar)).getTo().addAll(to);
        }
        else {
            fromStrings.add(from);
            grammar.getRulesSet().add(new Rule(from.charAt(0), to));
        }
    }

    public static Grammar grammarFill(String sPath) throws IOException {
        Path path = Path.of(sPath);
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
        Grammar grammar = new Grammar();

        String line = reader.readLine();
        StringBuilder builder = new StringBuilder();
        boolean isWord = false;
        boolean isInc = false;
        while (line != null){
            if (isWord || isInc){
                if (!line.equals("Redacted Word For Incremental Parsing:")){
                    builder.append(line);
                }
            }
            if (line.matches("^.*->.*$")){
                rulesCreator(line, grammar);
            }
            else if (line.equals("Word:")){
                isWord = true;
            }
            else if (line.equals("Redacted Word For Incremental Parsing:")){
                isInc = true;
                isWord = false;
                word = builder.append("$").toString();
                builder.setLength(0);
            }
            line = reader.readLine();
        }
        //word = builder.append("$").toString();
        incWord = builder.append("$").toString();
        reader.close();
        return grammar;
    }
}

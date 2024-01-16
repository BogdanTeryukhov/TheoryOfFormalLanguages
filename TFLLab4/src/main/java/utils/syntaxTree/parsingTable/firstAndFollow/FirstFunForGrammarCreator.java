package utils.syntaxTree.parsingTable.firstAndFollow;

import utils.grammar.Grammar;
import utils.grammar.rule.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstFunForGrammarCreator {

    public static Map<Character, List<String>> firstFunctionHashMap = new HashMap<>();

    public static List<String> removeDuplicates(List<String> list){
        List<String> noDuplicatesList = new ArrayList<>();
        for (String word: list) {
            if (!noDuplicatesList.contains(word)){
                noDuplicatesList.add(word);
            }
        }
        return noDuplicatesList;
    }

    public static void firstCreator(Rule rule){
        Character ch = rule.getFrom();
        List<String> values = new ArrayList<>();

        for (int i = 0; i < rule.getTo().size(); i++) {
            if (rule.getTo().get(i).equals("eps")){
                values.add("eps");
            }
            else {
                int index = 0;
                char firstChar = rule.getTo().get(i).charAt(index);
                if (isTerminal(firstChar)){
                    values.add(String.valueOf(firstChar));
                }
                else {
                    boolean fromLiteral = false;
                    while(firstFunctionHashMap.get(firstChar).contains("eps")){
                        values.addAll(firstFunctionHashMap.get(firstChar));
                        if (index + 1 == rule.getTo().get(i).length()){
                            break;
                        }
                        firstChar = rule.getTo().get(i).charAt(++index);
                        if (isTerminal(firstChar)){
                            values.add(String.valueOf(firstChar));
                            fromLiteral = true;
                            break;
                        }
                    }
                    if (!fromLiteral){
                        values.addAll(firstFunctionHashMap.get(firstChar));
                    }
                }
            }
        }
        values = removeDuplicates(values);
        values = values.stream().sorted((o1, o2) -> {
            if (o1.length() < o2.length()){
                return -1;
            } else if (o1.length() > o2.length()) {
                return 1;
            }
            return 0;
        }).toList();
        firstFunctionHashMap.put(ch, values);
    }

    public static boolean isTerminal(Character character){
        return character < 65 || character > 90;
    }
}

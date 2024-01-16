package utils.syntaxTree.parsingTable.firstAndFollow;

import utils.grammar.Grammar;
import utils.grammar.rule.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowFunForGrammarCreator {
    public static Map<Character, List<String>> followFunctionHashMap = new HashMap<>();

    public static List<String> removeDuplicates(List<String> list){
        if (list == null){
            return list;
        }
        List<String> noDuplicatesList = new ArrayList<>();
        for (String word: list) {
            if (!noDuplicatesList.contains(word)){
                noDuplicatesList.add(word);
            }
        }
        return noDuplicatesList;
    }

    public static List<String> removeEpsilons(List<String> list){
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals("eps")){
                result.add(list.get(i));
            }
        }
        return result;
    }

    public static String getCurrentCoincidence(Character from, List<String> list){
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.contains(String.valueOf(from))){
                return str;
            }
        }
        return null;
    }

    public static List<Integer> searchingForAllCoincidences(Character from, List<List<String>> list){
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> in = list.get(i);
            for (int j = 0; j < in.size(); j++) {
                String str = in.get(j);
                if (str.contains(String.valueOf(from))){
                    result.add(i);
                }
            }
        }
        return result;
    }

    public static void currentManipulation(Character from, Character currentFrom, String currentTo, Map<Character, List<String>> firstFunctionHashMap, boolean topRule){
        //System.out.println("From: " + from + " Current from: " + currentFrom + " Current to: " + currentTo);
        if (currentTo.indexOf(from) == currentTo.length() - 1){
            // возвращаем Follow Function от currentFrom
            //System.out.println("Equality!");
            if (from != currentFrom){
                if (followFunctionHashMap.get(from) == null){
                    followFunctionHashMap.put(from, followFunctionHashMap.get(currentFrom));
                }
                else {
                    followFunctionHashMap.get(from).addAll(followFunctionHashMap.get(currentFrom));
                }
            }
        }
        else {
            Character charAfterFrom = currentTo.charAt(currentTo.indexOf(from) + 1);
            if (isTerminal(charAfterFrom)){
                // если литерал, к from символу в мапе добавляем charAfterFrom
                //System.out.println("Literal!");
                if (followFunctionHashMap.get(from) == null){
                    List<String> proxy = new ArrayList<>();
                    proxy.add(String.valueOf(charAfterFrom));
                    followFunctionHashMap.put(from, proxy);
                }
                else {
                    followFunctionHashMap.get(from).add(String.valueOf(charAfterFrom));
                }
            }
            else { // если не литерал
                //System.out.println("Non literal!");
                List<String> values = new ArrayList<>();

                if (!firstFunctionHashMap.get(charAfterFrom).contains("eps")){
                    values.addAll(firstFunctionHashMap.get(charAfterFrom));
                }
                else {
                    while(firstFunctionHashMap.get(charAfterFrom).contains("eps")){
                        values.addAll(firstFunctionHashMap.get(charAfterFrom));
                        if (currentTo.indexOf(charAfterFrom) == currentTo.length() - 1){
                            //System.out.println("Current From:" + currentFrom);
                            values.addAll(followFunctionHashMap.get(currentFrom));
                            break;
                        }
                        charAfterFrom = currentTo.charAt(currentTo.indexOf(charAfterFrom) + 1);
                        if (isTerminal(charAfterFrom)){
                            values.add(String.valueOf(charAfterFrom));
                            break;
                        }
                        else {
                            values.addAll(firstFunctionHashMap.get(charAfterFrom));
                        }
                    }
                }
                values = removeEpsilons(values);
                if (followFunctionHashMap.get(from) == null){
                    followFunctionHashMap.put(from, values);
                }
                else {
                    followFunctionHashMap.get(from).addAll(values);
                }
            }
        }
        //удаление дубликатов
        followFunctionHashMap.put(from, removeDuplicates(followFunctionHashMap.get(from)));
    }

    public static void followCreator(Grammar grammar, Map<Character, List<String>> firstFunctionHashMap){
        List<Character> froms = grammar.fromList(grammar);
        List<List<String>> tos = grammar.toList(grammar);
        //System.out.println("Froms: " + froms);
        //System.out.println("Tos: " + tos);
        boolean topRule = true;

        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            char from = grammar.getRulesSet().get(i).getFrom();
            List<Integer> indexes = searchingForAllCoincidences(from, tos);
            if (topRule){
                List<String> proxy = new ArrayList<>();
                proxy.add("$");
                followFunctionHashMap.put(from, proxy);
                topRule = false;
            }
            for (int j = 0; j < indexes.size(); j++) {
                //определили правило, в котором встречается исходный нетерминал
                String currentTo = getCurrentCoincidence(from, tos.get(indexes.get(j)));
                Character currentFrom = froms.get(indexes.get(j));
                currentManipulation(from, currentFrom, currentTo, firstFunctionHashMap, topRule);
            }
        }
    }

    public static boolean isTerminal(Character character){
        return character < 65 || character > 90;
    }
}

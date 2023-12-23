package utils.grammar;

import lombok.*;
import utils.grammar.rule.Rule;
import utils.syntaxTree.parsingTable.firstAndFollow.FirstFunForGrammarCreator;
import utils.syntaxTree.parsingTable.firstAndFollow.FollowFunForGrammarCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Grammar {
    private List<Rule> rulesSet;

    public Grammar() {
        rulesSet = new ArrayList<>();
    }

    public static boolean isGrammarLeftRecursive(Grammar grammar){
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            Character left = grammar.getRulesSet().get(i).getFrom();
            List<String> right = grammar.getRulesSet().get(i).getTo();
            for (int j = 0; j < right.size(); j++) {
                if (right.get(j).charAt(0) == left){
                    return true;
                }
            }
        }
        return false;
    }

    public static void secondCheck(Grammar grammar){
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            Rule rule = grammar.getRulesSet().get(i);
            if (Grammar.findRecursiveRule(rule.getFrom(), rule.getTo())){
                continue;
            }
            else {
                for (int j = 0; j < rule.getTo().size(); j++) {
                    char[] arr = rule.getTo().get(j).toCharArray();
                    if (!(arr[arr.length - 1] == rule.getFrom())){
                        if (Grammar.hasRepeatsOnFirstAndFollowFunctions(FirstFunForGrammarCreator.firstFunctionHashMap, FollowFunForGrammarCreator.followFunctionHashMap, rule.getFrom())){
                            throw new RuntimeException("Grammar is not LL(1) !");
                        }
                    }
                }
            }
        }
    }

    public static boolean findRecursiveRule(Character getFrom, List<String> getTo){
        for (int i = 0; i < getTo.size(); i++) {
            char[] arr = getTo.get(i).toCharArray();
            if (arr[arr.length - 1] == getFrom){
                return true;
            }
        }
        return false;
    }

    public static boolean hasRepeatsOnFirstAndFollowFunctions(Map<Character, List<String>> firstFunctionHashMap, Map<Character, List<String>> followFunctionHashMap, Character toCheck){
        List<String> first = firstFunctionHashMap.get(toCheck);
        List<String> follow = followFunctionHashMap.get(toCheck);
        for (int i = 0; i < follow.size(); i++) {
            if (first.contains(follow.get(i))){
                return true;
            }
        }
        return false;
    }

    public static List<String> removeDuplicates(List<String> list){
        List<String> noDuplicatesList = new ArrayList<>();
        for (String word: list) {
            if (!noDuplicatesList.contains(word)){
                noDuplicatesList.add(word);
            }
        }
        return noDuplicatesList;
    }

    public static List<String> getTerminalsAlphabet(Grammar grammar){
        List<String> terminalsAlphabet = new ArrayList<>();
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            List<String> stringsOfCurrentRule = grammar.getRulesSet().get(i).getTo();
            for (int j = 0; j < stringsOfCurrentRule.size(); j++) {
                String currentString = stringsOfCurrentRule.get(j);
                if (!currentString.equals("eps")){
                    char[] arr = currentString.toCharArray();
                    for (int k = 0; k < arr.length; k++) {
                        if (isTerminal(arr[k])){
                            terminalsAlphabet.add(String.valueOf(arr[k]));
                        }
                    }
                }
            }
        }
        terminalsAlphabet.add("$");
        return removeDuplicates(terminalsAlphabet);
    }

    public static List<Character> getNonTerminalsAlphabet(Grammar grammar){
        List<Character> nonTerminalsAlphabet = new ArrayList<>();
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            Character charOfCurrentRule = grammar.getRulesSet().get(i).getFrom();
            nonTerminalsAlphabet.add(charOfCurrentRule);
        }
        return nonTerminalsAlphabet;
    }

    public static int getIndexOfParticularRule(Character character, Grammar grammar){
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            if (grammar.getRulesSet().get(i).getFrom() == character){
                return i;
            }
        }
        return -1;
    }

    public static Rule getParticularRule(Character character, Grammar grammar){
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            if (grammar.getRulesSet().get(i).getFrom() == character){
                return grammar.getRulesSet().get(i);
            }
        }
        return null;
    }

    public List<String> getByFrom(Character from, Grammar grammar){
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            if (grammar.getRulesSet().get(i).getFrom() == from){
                return grammar.getRulesSet().get(i).getTo();
            }
        }
        return null;
    }

    public List<Character> fromList(Grammar grammar){
        List<Character> froms = new ArrayList<>();
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            froms.add(grammar.getRulesSet().get(i).getFrom());
        }
        return froms;
    }

    public List<List<String>> toList(Grammar grammar){
        List<List<String>> froms = new ArrayList<>();
        for (int i = 0; i < grammar.getRulesSet().size(); i++) {
            froms.add(grammar.getRulesSet().get(i).getTo());
        }
        return froms;
    }
    public static boolean isTerminal(Character character){
        return character < 65 || character > 90;
    }
}

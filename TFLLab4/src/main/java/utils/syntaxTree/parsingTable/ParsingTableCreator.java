package utils.syntaxTree.parsingTable;

import utils.grammar.Grammar;
import utils.grammar.rule.Rule;
import utils.syntaxTree.parsingTable.table.ParsingTable;
import utils.syntaxTree.parsingTable.table.transition.Transition;

import java.util.List;
import java.util.Map;

public class ParsingTableCreator {

    public static ParsingTable parsingTable = new ParsingTable();

    public static void tableCreator(Map<Character, List<String>> firstFunctionHashMap, Map<Character, List<String>> followFunctionHashMap, Grammar grammar){
        List<Character> nonTerminals = Grammar.getNonTerminalsAlphabet(grammar);
        List<String> terminals = Grammar.getTerminalsAlphabet(grammar);

        for (int i = 0; i < nonTerminals.size(); i++) {
            Character currentNonTerminal = nonTerminals.get(i); // S
            List<String> currentNonTerminalFirstFunction = firstFunctionHashMap.get(currentNonTerminal); // [a]
            //System.out.println(currentNonTerminalFirstFunction);
            int index = 0;
            for (int j = 0; j < currentNonTerminalFirstFunction.size(); j++) {
                String currentChar = currentNonTerminalFirstFunction.get(j); //[a]
                //System.out.println("Current char: " + currentChar);
                if (currentChar.length() == 1){ //если литерал, а не эпсилон
                    //System.out.println("Current nonterminal: " + currentNonTerminal);
                    Rule global = Grammar.getParticularRule(currentNonTerminal, grammar); //[S->aABb]
                    Rule subRule;
                    if (global.getTo().size() == 1){
                        subRule = new Rule(currentNonTerminal, List.of(global.getTo().get(0)));
                    }
                    else{
                        subRule = new Rule(currentNonTerminal, List.of(global.getTo().get(index)));
                        if (global.getTo().get(index).contains(currentChar)){
                            index++;
                        }
                    }
                    parsingTable.getTransitions().add(new Transition(currentNonTerminal, currentChar, subRule));
                }
            }
            if (currentNonTerminalFirstFunction.contains("eps")){ //если есть эпсилон, нужно зайти в follow для текущего нетерминала
                List<String> currentNonTerminalFollowFunction = followFunctionHashMap.get(currentNonTerminal);
                for (int j = 0; j < currentNonTerminalFollowFunction.size(); j++) {
                    parsingTable.getTransitions().add(new Transition(currentNonTerminal, currentNonTerminalFollowFunction.get(j), new Rule(currentNonTerminal, List.of("eps"))));
                }
            }
        }
    }
}

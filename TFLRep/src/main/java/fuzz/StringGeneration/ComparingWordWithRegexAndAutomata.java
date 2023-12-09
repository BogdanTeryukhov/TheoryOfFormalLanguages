package fuzz.StringGeneration;

import fuzz.AutomatByRegex.Automat.NFA;
import fuzz.AutomatByRegex.Automat.utils.Transition;

import java.util.List;

public class ComparingWordWithRegexAndAutomata {
    public static boolean solution = false;
    public void waysChecking(int next, char[] arr, NFA nfa, int currentState){
        if (currentState == nfa.getAccepting().get(0) && next == arr.length){
            solution = true;
        }
        List<Transition> transitionList = NFA.fromCurrentStateTo(currentState, nfa);
        for (int i = 0; i < transitionList.size(); i++) {
            if (transitionList.get(i).getBy().equals("epsilon") || next == arr.length || transitionList.get(i).getBy().equals(String.valueOf(arr[next]))){
                if (transitionList.get(i).getBy().equals("epsilon")){
                    waysChecking(next, arr, nfa, transitionList.get(i).getTo());
                }
                else {
                    if (next == arr.length && currentState != nfa.getAccepting().get(0)){
                        solution = false;
                    }
                    else {
                        waysChecking(next + 1, arr, nfa, transitionList.get(i).getTo());
                    }
                }
            }
        }
    }
    public boolean isBelongToAutomata(NFA nfa, String word){
        int currentState = nfa.getInitial();
        char[] arr = word.toCharArray();
        int indexNext = 0;
        boolean verdict = false;

        waysChecking(indexNext, arr, nfa, currentState);

        verdict = solution;
        solution = false;
        return verdict;
    }
}

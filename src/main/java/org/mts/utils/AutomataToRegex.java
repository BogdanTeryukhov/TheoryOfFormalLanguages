package org.mts.utils;

import org.mts.utils.Automata.Automata;
import org.mts.utils.Automata.Transition;

import java.util.*;

public class AutomataToRegex {

    public static List<List<String>> result = new ArrayList<>();

    public static String removeEpsilons(String regex){
        return regex.replace("eps","");
    }

    public static List<String> getAllStatesFromAutomata(Automata automata){
        List<String> states = new ArrayList<>();
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (!states.contains(transition.getFrom()) || !states.contains(transition.getTo())){
                if (!states.contains(transition.getFrom())){
                    states.add(transition.getFrom());
                }
                else {
                    states.add(transition.getTo());
                }
            }
        }
        return states;
    }
    public static void addingExtraStartingAndAcceptingStates(Automata automata){
        automata.getTransitions().add(new Transition("0", "eps", automata.getStartingState()));
        automata.setStartingState("0");
        for (int i = 0; i < automata.getAcceptingStates().size(); i++) {
            automata.getTransitions().add(new Transition(automata.getAcceptingStates().get(i),"eps","1"));
        }
        automata.setAcceptingStates(List.of("1"));
    }

    public static List<List<String>> getAllPermutations(List<String> list, int k){
        for(int i = k; i < list.size(); i++){
            java.util.Collections.swap(list, i, k);
            getAllPermutations(list, k+1);
            java.util.Collections.swap(list, k, i);
        }
        if (k == list.size() - 1) {
            List<String> proxy = new ArrayList<>(List.copyOf(list));
            proxy.add("0");
            proxy.add("1");
            result.add(proxy);
        }
        return result;
    }

    // [Q1, Q2, Q3, Q4]
    public static String getMinRegex(Automata automata){
        List<String> allStates = getAllStatesFromAutomata(automata);
        allStates.remove("0");
        allStates.remove("1");
        List<List<String>> res = getAllPermutations(allStates, 0);

        //System.out.println(res.get(0));
        Automata duplicate = new Automata(automata);
        //System.out.println(res);
        String prevRegex = getRegexFromAutomata(duplicate, res.get(0));
        //System.out.println("Prev Regex: " + prevRegex);
        for (int i = 1; i < res.size(); i++) {
            //System.out.println("Res: " + res.get(i));
            Automata dupl = new Automata(automata);
            //System.out.println(dupl);
            //System.out.println("Array: " + res.get(i));
            String regex = getRegexFromAutomata(dupl, res.get(i));
            //System.out.println("Result Regex: " + regex);
            if (regex.length() < prevRegex.length()){
                prevRegex = regex;
            }
        }
        return prevRegex;
    }
    public static String getRegexFromAutomata(Automata automata, List<String> allStates){
        int i = 0;
        while (allStates.size() > 2){
            String currentState = allStates.get(i);
            List<Transition> currentStateTos = Automata.findTransitionsByParticularTo(automata,currentState);
            List<Transition> currentStateFroms = Automata.findTransitionsByParticularFrom(automata,currentState);
//            System.out.println("Current state: " + currentState);
//            System.out.println("Current tos: " + currentStateTos);
//            System.out.println("Current froms: " + currentStateFroms);
            for (Transition transitionTos: currentStateTos) {
                for (Transition transitionFroms: currentStateFroms) {
                    String overheadTransition = Automata.overheadTransition(automata, transitionTos.getFrom(), transitionFroms.getTo()) == null ? "" : Automata.overheadTransition(automata, transitionTos.getFrom(), transitionFroms.getTo());
                    //String overheadTransition = Automata.overheadRegex(Automata.overheadTransitionList(automata, transitionTos.getFrom(), transitionFroms.getTo())) == null ? "" : Automata.overheadRegex(Automata.overheadTransitionList(automata, transitionTos.getFrom(), transitionFroms.getTo()));
                    //String loopTransition = Automata.loopRegex(Automata.loopTransition(automata,currentState)) == null ? "" : Automata.loopRegex(Automata.loopTransition(automata,currentState));
                    String loopTransition = Automata.loopTransition(automata, currentState) == null ? "" : "(".concat(Automata.loopTransition(automata, currentState)).concat(")*");

                    String regex;
                    if (Objects.equals(overheadTransition, "") || Objects.equals(overheadTransition, "eps")){
                        regex = transitionTos.getBy()
                                .concat(loopTransition)
                                .concat(transitionFroms.getBy());
                    }
                    else{
                        regex = "(".concat(overheadTransition).concat("|(")
                                .concat(transitionTos.getBy())
                                .concat(loopTransition)
                                .concat(transitionFroms.getBy())
                                .concat("))");
                    }
                    regex = removeEpsilons(regex);

//                    System.out.println("Loop transition: " + loopTransition);
//                    System.out.println("Tos: " + transitionTos.getFrom() + " : " + transitionTos.getTo());
//                    System.out.println("Froms: " + transitionFroms.getFrom() + " : " + transitionFroms.getTo());
//                    System.out.println("Regex: " + regex);
                    if (Automata.ifTransitionHasAlreadyHere(automata, transitionTos.getFrom(), transitionFroms.getTo())){
                        int index = Automata.returnIndexOfCurrentTransition(automata, transitionTos.getFrom(), transitionFroms.getTo());
                        automata.getTransitions().remove(index);
                    }
                    automata.getTransitions().add(new Transition(transitionTos.getFrom(), regex, transitionFroms.getTo()));
                }
            }
            List<Integer> indexes = Automata.getAllTransitionsByParticularState(automata, currentState);
            for (Integer index : indexes) {
                automata.getTransitions().remove(index.intValue());
            }
            //System.out.println("Automata: " + automata + "\n");
            allStates.remove(currentState);
        }
        return automata.getTransitions().get(0).getBy();
    }
}

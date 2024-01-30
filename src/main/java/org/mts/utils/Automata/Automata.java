package org.mts.utils.Automata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
public class Automata {
    private String startingState;
    private List<String> acceptingStates;
    private List<Transition> transitions;

    public Automata() {
        acceptingStates = new ArrayList<>();
        transitions = new ArrayList<>();
    }

    public Automata(Automata automata){
        this.startingState = automata.startingState;
        this.acceptingStates = new ArrayList<>(automata.getAcceptingStates());
        this.transitions = new ArrayList<>(automata.getTransitions());
    }

    public static List<Transition> findTransitionsByParticularTo(Automata automata, String to){
        List<Transition> tos = new ArrayList<>();
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getTo().equals(to) && !transition.getFrom().equals(to)){
                tos.add(transition);
            }
        }
        return tos;
    }

    public static List<Transition> findTransitionsByParticularFrom(Automata automata, String from){
        List<Transition> froms = new ArrayList<>();
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(from) && !transition.getTo().equals(from)){
                froms.add(transition);
            }
        }
        return froms;
    }

    public static List<String> loopTransition(Automata automata, String state){
        List<String> loopList = new ArrayList<>();
        boolean firstIter = true;
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(state) && transition.getTo().equals(state)){
                if (firstIter){
                    loopList.add(transition.getBy());
                }
                else {
                    loopList.add(" | " + transition.getBy());
                }
            }
        }
        return loopList;
    }

    public static String loopRegex(List<String> loopList){
        if (loopList.size() == 0){
            return null;
        }
        if (loopList.size() == 1){
            return "(".concat(loopList.get(0)).concat(")*");
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < loopList.size(); i++) {
            if (i == 0){
                builder.append("(").append(loopList.get(i)).append("|");
            }
            else if (i == loopList.size() - 1){
                builder.append(loopList.get(i)).append(")");
            }
            else {
                builder.append(loopList.get(i)).append("|");
            }
        }
        builder.append("*");
        return builder.toString();
    }

    public static List<String> overheadTransitionList(Automata automata, String from, String to){
        List<String> overheadList = new ArrayList<>();
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(from) && transition.getTo().equals(to)){
                overheadList.add(transition.getBy());
            }
        }
        return overheadList;
    }

    public static String overheadTransition(Automata automata, String from, String to){
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(from) && transition.getTo().equals(to)){
                return transition.getBy();
            }
        }
        return null;
    }

    public static String overheadRegex(List<String> overheadList){
        if (overheadList.size() == 0){
            return null;
        }
        if (overheadList.size() == 1){
            return overheadList.get(0);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < overheadList.size(); i++) {
            if (i == 0){
                builder.append("(").append(overheadList.get(i)).append("|");
            }
            else if (i == overheadList.size() - 1){
                builder.append(overheadList.get(i)).append(")");
            }
            else {
                builder.append(overheadList.get(i)).append("|");
            }
        }
        return builder.toString();
    }

    public static boolean ifTransitionHasAlreadyHere(Automata automata, String from, String to){
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(from) && transition.getTo().equals(to)){
                return true;
            }
        }
        return false;
    }
    public static int returnIndexOfCurrentTransition(Automata automata, String from, String to){
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(from) && transition.getTo().equals(to)){
                return i;
            }
        }
        return -1;
    }

    public static List<Integer> getAllTransitionsByParticularState(Automata automata, String currentState){
        List<Integer> indexes = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(currentState) || transition.getTo().equals(currentState)){
                indexes.add(i - count);
                count++;
            }
        }
        return indexes;
    }
}
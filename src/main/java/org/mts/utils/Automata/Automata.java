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

    public static String loopTransition(Automata automata, String state){
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(transition.getTo())){
                return transition.getBy();
            }
        }
        return null;
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

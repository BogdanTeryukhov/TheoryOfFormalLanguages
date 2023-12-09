package fuzz.AutomatByRegex.Automat;

import fuzz.AutomatByRegex.Automat.utils.Transition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class NFA {
    private Set<Integer> states;
    private int initial;
    private List<Integer> accepting;
    private Set<String> alphabet;
    private List<Transition> transitions;

    public NFA() {
        states = new HashSet<>();
        accepting = new ArrayList<>();
        alphabet = new HashSet<>();
        transitions = new ArrayList<>();
    }


    public static List<Transition> fromCurrentStateTo(int currentState, NFA nfa){
        List<Transition> result = new ArrayList<>();
        for (Transition transition: nfa.getTransitions()) {
            if (transition.getFrom() == currentState){
                result.add(transition);
            }
        }
        return result;
    }

    public static void sortTransitions(NFA nfa){
        List<Transition> sorted = nfa.getTransitions().stream().sorted((o1, o2) -> {
            if (o1.getFrom() > o2.getFrom()){
                return 1;
            } else if (o2.getFrom() > o1.getFrom()) {
                return -1;
            }
            return 0;
        }).toList();
        nfa.setTransitions(sorted);
    }

    public static Transition findTransitionByFromAndTo(NFA nfa, int from, int to){
        for (Transition transition: nfa.getTransitions()) {
            if (transition.getFrom() == from && transition.getTo() == to){
                return transition;
            }
        }
        return null;
    }

    public static void addLostStates(NFA nfa){
        for (Transition transition: nfa.getTransitions()) {
            nfa.getStates().add(transition.getTo());
            nfa.getStates().add(transition.getFrom());
        }
    }

    public static void getLastState(NFA nfa){
        Object[] setDuplicates = nfa.getStates().toArray();
        for (int i = 0; i < nfa.getStates().size(); i++) {
            if (i == nfa.getStates().size() - 1){
                nfa.getAccepting().add((Integer) setDuplicates[i]);
            }
        }
    }

}

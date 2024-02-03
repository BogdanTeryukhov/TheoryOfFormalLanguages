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
        List<String> loopList = new ArrayList<>();
        boolean firstIter = true;
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            if (transition.getFrom().equals(state) && transition.getTo().equals(state)){
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

    public static String createGetTo(List<Transition> transitionList, Transition transition){
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(transition.getBy());

        boolean noDuplicates = true;
        for (int i = 0; i < transitionList.size(); i++) {
            Transition currentTransition = transitionList.get(i);
            if (transition.getFrom().equals(currentTransition.getFrom())
                    && transition.getTo().equals(currentTransition.getTo())){
                builder.append("|").append(currentTransition.getBy());
                noDuplicates = false;
            }
        }
        if (noDuplicates){
            return transition.getBy();
        }
        return builder.append(")").toString();
    }

    public static Automata destroyDuplicates(Automata automata){
        Automata noDuplAutomata = new Automata();
        noDuplAutomata.setStartingState(automata.getStartingState());
        noDuplAutomata.setAcceptingStates(automata.getAcceptingStates());

        List<Transition> uniqueList = new ArrayList<>();
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            boolean isInUniqueList = false;
            if (uniqueList.isEmpty()){
                noDuplAutomata.getTransitions().add(transition);
                uniqueList.add(transition);
                isInUniqueList = true;
            }
            else {
                for (int j = 0; j < uniqueList.size(); j++) {
                    Transition uniqueListTransition = uniqueList.get(j);
                    if (uniqueListTransition.getFrom().equals(transition.getFrom())
                            && uniqueListTransition.getTo().equals(transition.getTo())){
                        isInUniqueList = true;
                    }
                }
            }
            if (!isInUniqueList){
                noDuplAutomata.getTransitions().add(transition);
                uniqueList.add(transition);
            }
        }
        return noDuplAutomata;
    }
    public static Automata createUniqueTransitions(Automata automata){
        Automata resultAutomata = new Automata();
        resultAutomata.setStartingState(automata.getStartingState());
        resultAutomata.setAcceptingStates(automata.getAcceptingStates());
        for (int i = 0; i < automata.getTransitions().size(); i++) {
            Transition transition = automata.getTransitions().get(i);
            String result = createGetTo(automata.getTransitions().subList(i + 1, automata.getTransitions().size()),transition);
            resultAutomata.getTransitions().add(new Transition(transition.getFrom(), result, transition.getTo()));
        }

        //System.out.println(resultAutomata);

        Automata noDuplAutomata = destroyDuplicates(resultAutomata);

        //System.out.println(noDuplAutomata);

        return noDuplAutomata;
    }
}
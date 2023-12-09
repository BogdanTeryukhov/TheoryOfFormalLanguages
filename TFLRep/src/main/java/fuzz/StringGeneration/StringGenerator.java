package fuzz.StringGeneration;

import fuzz.AutomatByRegex.Automat.NFA;
import fuzz.StringGeneration.utils.StringMutations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class StringGenerator {

    public static int[][] reachabilityMatrix;

    public void reachabilityMatrixGeneration(NFA nfa){
        reachabilityMatrix = new int[nfa.getStates().size()][nfa.getStates().size()];
        for (int i = 0; i < nfa.getTransitions().size(); i++) {
            int currentFrom = nfa.getTransitions().get(i).getFrom();
            int currentTo = nfa.getTransitions().get(i).getTo();
            reachabilityMatrix[currentFrom][currentTo] = 1;
        }
    }

    public List<Integer> randomStatesSequence(NFA nfa){
        List<Integer> sequence = new ArrayList<>();
        sequence.add(nfa.getInitial());

        for (int i = 0; i < nfa.getStates().size(); i++) {
            List<Integer> reachabilityList = new ArrayList<>();
            for (int j = 0; j < nfa.getStates().size(); j++) {
                if (reachabilityMatrix[i][j] == 1){
                    reachabilityList.add(j);
                }
            }
            int randomNextState = getRandomReachableState(reachabilityList);
            i = randomNextState - 1;
            sequence.add(randomNextState);
            if (randomNextState == nfa.getAccepting().get(0)){
                break;
            }
        }
        return sequence;
    }

    public boolean doMutationsOrNot(){
        return new Random().nextInt(0,10) < 8;
    }

    public String mutations(String str){
        char[] arr = str.toCharArray();
        StringBuilder builder = new StringBuilder();

        if (doMutationsOrNot()){
            for (int i = 0; i < arr.length; i++) {
                int rand = new Random().nextInt(0,3);
                builder.append(arr[i]);
                StringMutations stringMutations = new StringMutations();
                switch (rand){
                    case 0:
                        stringMutations.lettersRepeat(builder);
                    case 1:
                        stringMutations.lettersSwap(builder, arr[i]);
                    case 2:
                        stringMutations.letterRemove(builder);
                }
            }
        }
        return builder.isEmpty() ? str : builder.toString();
    }

    public String stringGeneration(NFA nfa){
        reachabilityMatrixGeneration(nfa);
        List<Integer> statesSequence = randomStatesSequence(nfa);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statesSequence.size() - 1; i++) {
            String by = Objects.requireNonNull(NFA.findTransitionByFromAndTo(nfa, statesSequence.get(i), statesSequence.get(i + 1))).getBy();
            builder.append(Objects.equals(by, "epsilon") ? "" : by);
        }
        //builder = new StringBuilder(mutations(builder.toString()));
        return builder.toString();
    }


    public int getRandomReachableState(List<Integer> reachability){
        return reachability.get(new Random().nextInt(0, reachability.size()));
    }
}

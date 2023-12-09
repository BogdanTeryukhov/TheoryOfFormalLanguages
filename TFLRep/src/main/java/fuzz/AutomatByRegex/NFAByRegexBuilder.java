package fuzz.AutomatByRegex;

import fuzz.AutomatByRegex.Automat.NFA;
import fuzz.AutomatByRegex.Automat.utils.Transition;
import optimization.entity.Tree;
import optimization.entity.parse.Parser;

import java.util.ArrayList;
import java.util.List;

public class NFAByRegexBuilder {

    public static int counter = 0;
    public static List<Integer> endPoints = new ArrayList<>();
    public static boolean inORCase;
    public static boolean isNewBeginning = true;
    static int beginning = 0;

    public void clear(){
        inORCase = false;
        counter = 0;
        beginning = 0;
        endPoints.clear();
        isNewBeginning = true;
    }

    public void ifRootHasChildrenSymbols(Tree root, NFA automata){
        if (root.getType() == Tree.Type.OR){
            ORCaseProcessor(root, automata);
        } else if (root.getType() == Tree.Type.CONCAT) {
            ConcatCaseProcessor(root.getLeft(), automata);
            ConcatCaseProcessor(root.getRight(), automata);
        } else if (root.getType() == Tree.Type.ASTERISK) {
            AsteriskCaseBuilder(root.getLeft(), automata);
        }
    }

    public NFA processor(String regex){
        NFA automata = new NFA();
        Tree tree = Parser.parser(regex);
        automata.setInitial(counter);
        automata.getStates().add(counter);

        if (tree.getLeft().getValue() != null && (tree.getRight() == null || tree.getRight().getValue() != null)){
            ifRootHasChildrenSymbols(tree, automata);
        }
        else{
            convertation(tree, tree.getLeft(), automata);
            if (tree.getRight() != null){
                convertation(tree, tree.getRight(), automata);
            }
            NFA.addLostStates(automata);
        }
        NFA.getLastState(automata);
        NFA.sortTransitions(automata);
        clear();
        return automata;
    }

    public void convertation(Tree root, Tree tree, NFA automata){
        //System.out.println("Tree root: " + root.getType() + " Tree: " + tree.getType());
        if (tree.getLeft() == null){
            if (tree.getType() == Tree.Type.SYMBOL){
                ConcatCaseProcessor(tree,automata);
            }
        }
        else if (tree.getLeft().getValue() == null && tree.getRight() != null && tree.getRight().getValue() == null){
            convertation(tree, tree.getLeft(), automata);
            convertation(tree, tree.getRight(), automata);
        }
        else if (tree.getRight() == null || tree.getRight().getValue() == null){
            if (root.getType() == Tree.Type.OR || tree.getType() == Tree.Type.OR){
                if (tree.getType() == Tree.Type.ASTERISK){
                    AsteriskCaseBuilder(tree.getLeft(),automata);
                }
                else if (root.getType() == Tree.Type.ASTERISK) {
                    counter++;
                    ORCaseProcessor(tree,automata);
                    AsteriskFromOr(automata);
                }
                else {
                    inORCase = true;
                    ORCaseProcessor(tree, automata);
                }
            }
            else if (root.getType() == Tree.Type.CONCAT) {
                if (tree.getType() == Tree.Type.ASTERISK){
                    if (tree.getLeft().getType() == Tree.Type.OR){
                        convertation(tree, tree.getLeft(),automata);
                    }
                    else {
                        AsteriskCaseBuilder(tree.getLeft(), automata);
                    }

                }
                else if (tree.getType() == Tree.Type.SYMBOL) {
                    ConcatCaseProcessor(tree.getLeft(),automata);
                }
                else if (tree.getType() == Tree.Type.CONCAT){
                    ConcatCaseProcessor(tree.getLeft(),automata);
                }
            }
            if (tree.getRight() != null){
                convertation(tree, tree.getRight(), automata);
            }
        }
        else {
            if (tree.getLeft().getValue() == null){
                if (tree.getRight().getType() == Tree.Type.SYMBOL){
                    convertation(tree,tree.getLeft(),automata);
                    ConcatCaseProcessor(tree.getRight(), automata);
                }
            }
            else if (tree.getType() == Tree.Type.CONCAT){
                if (root.getType() == Tree.Type.OR){
                    ORCaseProcessor(tree,automata);
                }else {
                    ConcatCaseProcessor(tree.getLeft(),automata);
                }
                ConcatCaseProcessor(tree.getRight(),automata);
            }
            else if (tree.getType() == Tree.Type.OR) {
                if (root.getType() == Tree.Type.ASTERISK){
                    counter++;
                    ORCaseProcessor(tree, automata);
                    AsteriskFromOr(automata);
                }
                else {
                    ORCaseProcessor(tree, automata);
                }
            }
        }
    }

    public void ConcatCaseProcessor(Tree tree, NFA automata){
        automata.getStates().add(++counter); // 6
        automata.getAlphabet().add(tree.getValue());
        automata.getTransitions().add(new Transition(counter - 1, tree.getValue(), counter)); // 5 left 6
        if (inORCase){
            endPoints.remove(endPoints.size() - 1);
            endPoints.add(counter);
        }
        //System.out.println("From concat counter: " + counter);
    }

    public void ORCaseProcessor(Tree tree, NFA automata){
        //6
        String leftValue = tree.getLeft().getValue();
        //System.out.println(isNewBeginning);
        if (isNewBeginning){
            beginning = counter;
            isNewBeginning = false;
        }

        automata.getStates().add(++counter); // 1 (counter)
        automata.getTransitions().add(new Transition(beginning, "epsilon", counter)); // from 0 by epsilon to 1
        automata.getStates().add(++counter); // 2
        automata.getAlphabet().add(leftValue);
        automata.getTransitions().add(new Transition(counter - 1, leftValue, counter)); // from 1 by f to 2
        endPoints.add(counter);
        //System.out.println(endPoints);

        String rightValue;
        if (tree.getRight() != null && tree.getRight().getValue() != null){
            rightValue = tree.getRight().getValue();
            automata.getStates().add(++counter); // 3
            automata.getTransitions().add(new Transition(beginning, "epsilon", counter));
            automata.getStates().add(++counter);// 4
            automata.getAlphabet().add(rightValue);
            automata.getTransitions().add(new Transition(counter - 1, rightValue, counter));
            automata.getStates().add(++counter); //5
            automata.getTransitions().add(new Transition(counter - 1, "epsilon", counter));
            endPoints.add(counter - 1);
            //System.out.println("Endpoints: " + endPoints);
            inORCase = false;
            isNewBeginning = true;
            addEndPointsTransitions(automata);
            endPoints.clear();
        }
        //System.out.println("From ORCase counter: " + counter); //2 or 5
    }

    public void addEndPointsTransitions(NFA automata){
        for (int i = 0; i < endPoints.size() - 1; i++) {
            automata.getTransitions().add(new Transition(endPoints.get(i), "epsilon", counter));
        }
    }

    public void AsteriskCaseBuilder(Tree left, NFA automata){
        String symbol = left.getValue();
        int beginning = counter;

        automata.getStates().add(++counter); // 3
        automata.getTransitions().add(new Transition(counter - 1, "epsilon", counter));
        automata.getStates().add(++counter); //4
        automata.getAlphabet().add(symbol);
        automata.getTransitions().add(new Transition(counter - 1, symbol, counter));
        automata.getTransitions().add(new Transition(counter, "epsilon", counter - 1));
        automata.getStates().add(++counter); //5
        automata.getTransitions().add(new Transition(counter - 1, "epsilon", counter));
        automata.getTransitions().add(new Transition(beginning, "epsilon", counter));
        //System.out.println("From Aster counter: " + counter);
    }

    public void AsteriskFromOr(NFA automata){
        automata.getTransitions().add(new Transition(counter, "epsilon", counter - 5));
        //automata.getStates().add(counter - 6);
        automata.getTransitions().add(new Transition(counter - 6, "epsilon", counter - 5));
        automata.getStates().add(counter + 1);
        automata.getTransitions().add(new Transition(counter, "epsilon", counter + 1));
        automata.getTransitions().add(new Transition(counter - 6, "epsilon", counter + 1));
        counter++;
        //System.out.println("From asterOr counter: " + (counter + 1));
    }


    //доделать для вложенных

    public boolean isSymbol(char ch){
        return (ch >= 97 && ch <= 122) || (ch >= 48 && ch <= 57) || (ch >= 65 && ch <= 90);
    }
}

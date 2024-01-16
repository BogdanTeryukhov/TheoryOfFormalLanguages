package utils.syntaxTree.incrementalParsing;

import utils.grammar.rule.Rule;
import utils.syntaxTree.SyntaxTreeCreation;
import utils.syntaxTree.parsingTable.table.ParsingTable;
import utils.syntaxTree.tree.Tree;

import java.util.*;

import static utils.syntaxTree.SyntaxTreeCreation.*;

public class IncrementalParsing {

    private static Stack<String> stack = new Stack<>();

    public static Map<String,List<String>> incrementalTree = new LinkedHashMap<>();

    private static int currentIndexFromParsing = 0;

    private static int globalIndex = 0;

    private static boolean itsAMatch = true;

    private static boolean firstNonTerminal = true;

    public static void addStringInReverseOrderInImplementedAlgorithm(String str){
        if (isEpsilon(str)){
            stack.push(str);
        }
        else {
            char[] arr = str.toCharArray();
            for (int i = arr.length - 1; i >= 0; i--) {
                stack.push(String.valueOf(arr[i]));
            }
        }
    }

    public static void implementedProcessing(String str, char[] arr, int index, ParsingTable parsingTable, String root){
        //System.out.println("Root: " + root + " Str: " + str + " Proc str: " + arr[index]);
        //System.out.println("Stack: " + stack);
        if (stack.peek().equals("$")){
            return;
        }
        else if (isEpsilon(stack.peek())){
            stack.pop();
            if (reps.get(root) != null && reps.get(root) > 0){
                incrementalTree.put(root.concat(String.valueOf(reps.get(root))), List.of(str));
                reps.put(root, reps.get(root) + 1);
            }
            else {
                incrementalTree.put(root, List.of(str));
            }
            implementedProcessing(str, arr, index, parsingTable, root);
        }
        else {
            Character onTopOfStack = stack.peek().charAt(0);
            if (!isTerminal(onTopOfStack)){
                Rule rule = ParsingTable.findRuleByFromAndTo(onTopOfStack, String.valueOf(arr[index]), parsingTable);// B->a
                stack.pop();
                addStringInReverseOrderInImplementedAlgorithm(rule.getTo().get(0));
                root = String.valueOf(onTopOfStack);
                if (!rule.getTo().get(0).equals("eps")){
                    if (reps.get(rule.getFrom().toString()) != null && reps.get(rule.getFrom().toString()) > 0){
                        //System.out.println("Root: " + root + " Str: " + str);
                        List<String> pr = createCurrentNonterminalsList(rule.getTo().get(0));
                        List<String> duplicatedPr = new ArrayList<>();
                        int curInd = reps.get(rule.getFrom().toString()) + 1;
                        for (int i = 0; i < pr.size(); i++) {
                            if (pr.get(i).equals(rule.getFrom().toString())){
                                duplicatedPr.add(pr.get(i).concat(String.valueOf(curInd++)));
                            }
                            else {
                                duplicatedPr.add(pr.get(i));
                            }
                        }
                        incrementalTree.put(root.concat(String.valueOf(reps.get(root))), duplicatedPr);
                        reps.put(root, reps.get(root) + 1);
                    }
                    else {
                        List<String> pr = createCurrentNonterminalsList(rule.getTo().get(0));
                        List<String> duplicatedPr = new ArrayList<>();
                        int curInd = 1;
                        for (int i = 0; i < pr.size(); i++) {
                            if (pr.get(i).equals(rule.getFrom().toString())){
                                duplicatedPr.add(pr.get(i).concat(String.valueOf(curInd++)));
                            }
                            else {
                                duplicatedPr.add(pr.get(i));
                            }
                        }
                        System.out.println(root + " " + reps.get(root) + " " + duplicatedPr);
                        incrementalTree.put(root, duplicatedPr);
                        reps.put(root, reps.get(root) + 1);
                    }
                }
                implementedProcessing(stack.peek(), arr, index, parsingTable, root);
            }
            else {
                if (onTopOfStack == arr[index]){
                    currentIndexFromParsing++;
                    stack.pop();
                    implementedProcessing(stack.peek(), arr, index + 1, parsingTable, root);
                }
                else {
                    throw new RuntimeException("Word cant be parsed!");
                }
            }
        }
    }

    public static void step(Tree currentTree, String subWord, ParsingTable parsingTable){
        String currentValue = currentTree.getValue();
        int parsingIndex = 0;
        incrementalTree.put(currentValue, new ArrayList<>());

        for (int i = 0; i < currentTree.getNodesList().size(); i++) {
            Tree currentNode = currentTree.getNodesList().get(i);
            //System.out.println("Current node: " + currentNode.getValue());
            if (!isEpsilon(currentNode.getValue()) && isNonTerminal(currentNode.getValue())){
                incrementalTree.get(currentValue).add(currentNode.getValue());
                step(currentNode, subWord, parsingTable);
            }
            else if (!Objects.equals(currentNode.getValue(), String.valueOf(subWord.charAt(globalIndex)))){
                //System.out.println("here");
                if (currentValue.equals("eps")){
                    stack.push("eps");
                }
                else {
                    stack.push(currentValue.substring(0,1));
                }
                implementedProcessing(currentValue, subWord.substring(globalIndex).toCharArray(), parsingIndex, parsingTable, currentValue);
                globalIndex += currentIndexFromParsing;
                currentIndexFromParsing = 0;
            }
            else {
                //System.out.println(currentValue);
                //incrementalTree.forEach((key,val) -> System.out.println(key + " : " + val));
                incrementalTree.get(currentValue).add(currentNode.getValue());
                globalIndex++;
            }
        }
    }

    public static void incrementalParsingRealisation(Tree previousTree, String word, ParsingTable parsingTable){
        stack.push("$");
        String value = previousTree.getValue();
        try {
            step(previousTree, word, parsingTable);
        } catch (Exception e) {
            throw new RuntimeException("Word can`t be parsed!");
        }
        Tree incAlgorithmTree = SyntaxTreeCreation.createTree(incrementalTree, value, null);
        addIndexesOfReusedNodes(incAlgorithmTree, resultTree);
        Tree.drawTree(incAlgorithmTree, true, "");
    }

    public static void addIndexesOfReusedNodes(Tree incTree, Tree commonTree){

        for (int i = 0; i < incTree.getNodesList().size(); i++) {
            Tree currentInc = incTree.getNodesList().get(i);
            Tree currentCommon = commonTree.getNodesList().get(i);
            if (!isNonTerminal(currentInc.getValue())){
                if (currentInc.getValue().equals(currentCommon.getValue())){
                    incTree.setIndex(commonTree.getIndex());
                    currentInc.setIndex(currentCommon.getIndex());
                }
                else {
                    itsAMatch = false;
                }
                if (commonTree.getNodesList().size() == (i + 1)){
                    break;
                }
            }
            else {
                addIndexesOfReusedNodes(incTree.getNodesList().get(i), commonTree.getNodesList().get(i));
            }
        }
    }

    public static boolean isNonTerminal(String value){
        return value.charAt(0) >= 65 && value.charAt(0) <= 90;
    }
}

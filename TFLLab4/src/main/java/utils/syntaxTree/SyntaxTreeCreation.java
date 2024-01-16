package utils.syntaxTree;

import utils.grammar.rule.Rule;
import utils.syntaxTree.parsingTable.table.ParsingTable;
import utils.syntaxTree.tree.Tree;

import java.util.*;

public class SyntaxTreeCreation {
    private static Stack<String> stack = new Stack<>();
    public static Map<String, List<String>> tree = new LinkedHashMap<>();
    public static Tree resultTree = new Tree();
    private static int index = 1;
    public static Map<String, Integer> reps = new HashMap<>();

    public static List<String> createCurrentNonterminalsList(String str){
        List<String> list = new ArrayList<>();
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            list.add(String.valueOf(arr[i]));
        }
        return list;
    }

    public static Tree createTree(Map<String, List<String>> treeHashMap, String top, Tree prev){
        Tree root;
        if (prev != null){
            root = Tree.getTreeByValue(top, prev);
        }
        else {
            root = new Tree(top);
        }
        //System.out.println("Get top: " + top);
        for (int i = 0; i < treeHashMap.get(top).size(); i++) {
            String str = treeHashMap.get(top).get(i);
            root.getNodesList().add(new Tree(str));
            if (!isEpsilon(str) && !isTerminal(str.charAt(0))){
                createTree(treeHashMap, str, root);
            }
        }
        return root;
    }

    public static void addStringInReverseOrder(String str){
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

    public static void processing(String str, char[] arr, int index, ParsingTable parsingTable, String root){
        //System.out.println("Root: " + root + " Str: " + str + " Proc str: " + arr[index]);
        //System.out.println("Stack: " + stack);
        if (stack.peek().equals("$")){
            return;
        }
        else if (isEpsilon(stack.peek())){
            stack.pop();
            if (reps.get(root) != null){
                tree.put(root.concat(String.valueOf(reps.get(root) + 1)), List.of(str));
            }
            else {
                tree.put(root, List.of(str));
            }
            processing(str, arr, index, parsingTable, root);
        }
        else {
            Character onTopOfStack = stack.peek().charAt(0);
            if (!isTerminal(onTopOfStack)){
                Rule rule = ParsingTable.findRuleByFromAndTo(onTopOfStack, String.valueOf(arr[index]), parsingTable); // B->a
                stack.pop();
                addStringInReverseOrder(rule.getTo().get(0));
                root = String.valueOf(onTopOfStack);
                if (!rule.getTo().get(0).equals("eps")){
                    if (reps.get(rule.getFrom().toString()) != null){
                        //System.out.println("Root: " + root + " Str: " + str);
                        List<String> pr = createCurrentNonterminalsList(rule.getTo().get(0));
                        List<String> duplicatedPr = new ArrayList<>();
                        int curInd = reps.get(rule.getFrom().toString()) + 2;
                        for (int i = 0; i < pr.size(); i++) {
                            if (pr.get(i).equals(rule.getFrom().toString())){
                                duplicatedPr.add(pr.get(i).concat(String.valueOf(curInd++)));
                            }
                            else {
                                duplicatedPr.add(pr.get(i));
                            }
                        }
                        reps.put(root, reps.get(root) + 1);
                        tree.put(root.concat(String.valueOf(reps.get(root))), duplicatedPr);
                    }
                    else {
                        List<String> pr = createCurrentNonterminalsList(rule.getTo().get(0));
                        List<String> duplicatedPr = new ArrayList<>();
                        reps.put(root, 0);
                        int curInd = reps.get(rule.getFrom().toString()) + 1;
                        for (int i = 0; i < pr.size(); i++) {
                            if (pr.get(i).equals(rule.getFrom().toString())){
                                duplicatedPr.add(pr.get(i).concat(String.valueOf(curInd++)));
                            }
                            else {
                                duplicatedPr.add(pr.get(i));
                            }
                        }
                        tree.put(root, duplicatedPr);
                    }
                }
                processing(stack.peek(), arr, index, parsingTable, root);
            }
            else {
                if (onTopOfStack == arr[index]){
                    stack.pop();
                    processing(stack.peek(), arr, index + 1, parsingTable, root);
                }
                else {
                    throw new RuntimeException("Word cant be parsed!");
                }
            }
        }
    }

    public static void createHashMap(ParsingTable parsingTable, String word){
        Character character = parsingTable.getTransitions().get(0).getFrom();
        stack.push("$");
        stack.push(String.valueOf(character));
        char[] arr = word.toCharArray();
        int index = 0;
        try {
            processing(String.valueOf(character), arr, index, parsingTable, String.valueOf(character));
        }
        catch (Exception e){
                throw new RuntimeException("Word cant be parsed!");
        }
        //tree.forEach((key,value) -> System.out.println(key + " : " + value));
        resultTree = createTree(tree, String.valueOf(tree.entrySet().stream().findFirst().get().getKey()), null);
        addIndexesToNodes(resultTree);
        Tree.drawTree(resultTree, true, "");
    }

    public static void addIndexesToNodes(Tree resultTree){
        for (int i = 0; i < resultTree.getNodesList().size(); i++) {
            String str = resultTree.getNodesList().get(i).getValue();
            if (isEpsilon(str) || isTerminal(str.charAt(0))){
                resultTree.getNodesList().get(i).setIndex(index++);
            }
            else {
                resultTree.getNodesList().get(i).setIndex(index);
                addIndexesToNodes(resultTree.getNodesList().get(i));
            }
        }
    }

    public static boolean isTerminal(Character character){
        return character < 65 || character > 90;
    }

    public static boolean isEpsilon(String mayBeEpsilon){
        return mayBeEpsilon.equals("eps");
    }
}

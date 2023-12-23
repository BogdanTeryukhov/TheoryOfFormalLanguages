package utils.syntaxTree.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Tree {
    private String value;
    private int index;
    private List<Tree> nodesList;
    public Tree() {
        nodesList = new ArrayList<>();
    }

    public Tree(String value) {
        this.value = value;
        nodesList = new ArrayList<>();
    }

    public static Tree getTreeByValue(String value, Tree tree){
        for (int i = 0; i < tree.getNodesList().size(); i++) {
            if (tree.getNodesList().get(i).getValue().equals(value)){
                return tree.getNodesList().get(i);
            }
        }
        return null;
    }

    public static List<String> getRootChildren(Map<String, List<String>> stringListMap){
        int max = 0;
        String key = null;
        for (Map.Entry<String, List<String>> entry: stringListMap.entrySet()) {
            if (entry.getValue().size() > max){
                max = entry.getValue().size();
                key = entry.getKey();
            }
        }
        return stringListMap.get(key);
    }

    public static void drawTree(Tree tree, boolean isFirst, String prefix){
        if (isFirst){
            String value = tree.getValue();
            System.out.println(prefix + "└── " + value);
        }
        for (int i = 0; i < tree.getNodesList().size(); i++) {
            if (tree.getNodesList().get(i).getNodesList().isEmpty() && i != tree.getNodesList().size() - 1){
                System.out.println(prefix + "    ├── " + tree.getNodesList().get(i).getValue() + " " + (tree.getNodesList().get(i).getIndex() == 0 ? "" : tree.getNodesList().get(i).getIndex()));
            }
            else {
                System.out.println(prefix + "    └── " + tree.getNodesList().get(i).getValue() + " " + (tree.getNodesList().get(i).getIndex() == 0 ? "" : tree.getNodesList().get(i).getIndex()));
            }
            drawTree(tree.getNodesList().get(i), false, prefix + "    ");
        }
    }
    //"├── " : "└── "

    public static boolean isTerminal(Character character){
        return character < 65 || character > 90;
    }

    public static boolean isEpsilon(String mayBeEpsilon){
        return mayBeEpsilon.equals("eps");
    }
}

package optimization.utils;

import lombok.experimental.UtilityClass;
import optimization.entity.Tree;

@UtilityClass
public class SSNF {
    public Tree ssnf(Tree tree) {
        switch (tree.getType()) {
            case SYMBOL -> {
                return tree;
            }
            case OR -> {
                return new Tree(Tree.Type.OR, ssnf(tree.getLeft()),
                        ssnf(tree.getRight()));
            }
            case CONCAT -> {
                return new Tree(Tree.Type.CONCAT, ssnf(tree.getLeft()), ssnf(tree.getRight()));
            }
            case ASTERISK -> {
                return new Tree(Tree.Type.ASTERISK, ss(tree.getLeft()));
            }
            default -> throw new RuntimeException();
        }
    }
    public Tree ss(Tree tree) {
        switch (tree.getType()) {
            case SYMBOL -> {
                return tree;
            }
            case OR -> {
                return new Tree(Tree.Type.OR, ss(tree.getLeft()), ss(tree.getRight()));
            }
            case CONCAT -> {
                Tree treeChildLeft = tree.getLeft();
                Tree treeChildRight = tree.getRight();
                if (treeChildLeft.getType() == Tree.Type.ASTERISK
                        && treeChildRight.getType() == Tree.Type.ASTERISK) {
                    return new Tree(Tree.Type.OR, ss(tree.getLeft()), ss(tree.getRight()));
                }
                return new Tree(Tree.Type.CONCAT, ssnf(tree.getLeft()),
                        ssnf(tree.getRight()));
            }
            case ASTERISK -> {
                return ss(tree.getLeft());
            }
            default -> throw new RuntimeException();
        }
    }
}

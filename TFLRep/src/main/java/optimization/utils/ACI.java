package optimization.utils;

import lombok.experimental.UtilityClass;
import optimization.entity.Tree;

import java.util.ArrayList;
import java.util.Comparator;

@UtilityClass
public class ACI {
    public Tree normalizeAssociativity(Tree root) {
        if (root == null) {
            return null;
        }
        if (root.getType() == Tree.Type.OR) {
            while (root.getLeft() != null && root.getLeft().getType() == Tree.Type.OR) {
                Tree leftChild = root.getLeft();
                root.setLeft(leftChild.getRight());
                leftChild.setRight(root);
                root = leftChild;
            }
        }
        if (root.getType() == Tree.Type.CONCAT) {
            while (root.getLeft() != null && root.getLeft().getType() == Tree.Type.CONCAT) {
                Tree leftChild = root.getLeft();
                root.setLeft(leftChild.getRight());
                leftChild.setRight(root);
                root = leftChild;
            }
        }
        root.setLeft(normalizeAssociativity(root.getLeft()));
        root.setRight(normalizeAssociativity(root.getRight()));
        return root;
    }

    public Tree normalizeCommutativity(Tree root) {
        if (root == null) {
            return null;
        }
        if (root.getType() == Tree.Type.OR) {
            ArrayList<Tree> operands = collectOperands(root);
            operands.sort(Comparator.comparing(Tree::toString));
            return createTreeFromSortedOperands(operands);
        }
        root.setLeft(normalizeCommutativity(root.getLeft()));
        root.setRight(normalizeCommutativity(root.getRight()));
        return root;
    }

    public Tree normalizeIdempotency(Tree root) {
        if (root == null) {
            return null;
        }
        while (root.getType() == Tree.Type.OR && root.getRight().getType() == Tree.Type.OR) {
            Tree left = root.getLeft();
            Tree right = root.getRight();
            Tree rightLeft = right.getLeft();
            Tree rightRight = right.getRight();
            if (left.toString().equals(rightLeft.toString())) {
                root = right;
            } else if (left.toString().equals(rightRight.toString())) {
                root = right;
            } else {
                break;
            }
        }
        while (root.getType() == Tree.Type.OR && root.getLeft().getType() == Tree.Type.OR) {
            Tree left = root.getLeft();
            Tree right = root.getRight();
            Tree leftLeft = left.getLeft();
            Tree leftRight = left.getRight();
            if (right.toString().equals(leftLeft.toString())) {
                root = left;
            } else if (right.toString().equals(leftRight.toString())) {
                root = left;
            } else {
                break;
            }
        }
        if (root.getType() == Tree.Type.OR) {
            Tree left = root.getLeft();
            Tree right = root.getRight();
            if (left.toString().equals(right.toString())) {
                root = right;
            }
        }
        root.setLeft(normalizeIdempotency(root.getLeft()));
        root.setRight(normalizeIdempotency(root.getRight()));
        return root;
    }

    private ArrayList<Tree> collectOperands(Tree root) {
        ArrayList<Tree> operands = new ArrayList<>();
        if (root == null) {
            return null;
        }
        if (root.getType() == Tree.Type.OR) {
            if (root.getLeft().getType() != Tree.Type.OR) {
                operands.add(root.getLeft());
            } else {
                operands.addAll(collectOperands(root.getLeft()));
            }
            if (root.getRight().getType() != Tree.Type.OR) {
                operands.add(root.getRight());
            } else {
                operands.addAll(collectOperands(root.getRight()));
            }
        }
        return operands;
    }

    public Tree createTreeFromSortedOperands(ArrayList<Tree> operands) {
        if (operands.isEmpty()) {
            return null;
        }
        if (operands.size() == 2) {
            return new Tree(Tree.Type.OR, operands.get(0), operands.get(1));
        }
        Tree root = new Tree(Tree.Type.OR, operands.get(0));
        Tree current = root;
        for (int i = 1; i < operands.size() - 1; i++) {
            if (i == operands.size() - 2) {
                Tree temp = new Tree(Tree.Type.OR, operands.get(i), operands.get(i + 1));
                current.setRight(temp);
                current = temp;
                continue;
            }
            Tree temp = new Tree(Tree.Type.OR, operands.get(i));
            current.setRight(temp);
            current = temp;
        }
        return root;
    }
}

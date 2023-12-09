package optimization.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SerializationUtils;
import optimization.entity.Tree;

@UtilityClass
public class DSTR {
    public Tree dstrTree(Tree root) {
        if (root == null) {
            return null;
        }
        root = dstrLeft(root);
        root = dstrRight(root);
        root.setLeft(dstrTree(root.getLeft()));
        root.setRight(dstrTree(root.getRight()));
        return root;
    }

    private static Tree dstrRight(Tree root) {
        while (root.getType() == Tree.Type.OR &&
                root.getLeft().getType() == Tree.Type.CONCAT &&
                root.getRight().getType() == Tree.Type.OR &&
                root.getRight().getLeft().getType() == Tree.Type.CONCAT &&
                getLastConcat(root.getLeft()).toString()
                        .equals(getLastConcat(root.getRight().getLeft()).toString())) {
            root = normalizeOr(new Tree(
                    Tree.Type.OR,
                    new Tree(
                            Tree.Type.CONCAT,
                            normalizeOr(new Tree(
                                    Tree.Type.OR,
                                    getConcatNotLast(root.getLeft()),
                                    getConcatNotLast(root.getRight().getLeft()))),
                            getLastConcat(root.getLeft())),
                    root.getRight().getRight()));
        }
        if (root.getType() == Tree.Type.OR &&
                root.getLeft().getType() == Tree.Type.CONCAT &&
                root.getRight().getType() == Tree.Type.CONCAT &&
                getLastConcat(root.getLeft()).toString().equals(getLastConcat(root.getRight()).toString())) {
            root = new Tree(
                    Tree.Type.CONCAT,
                    normalizeOr(new Tree(
                            Tree.Type.OR,
                            getConcatNotLast(root.getLeft()),
                            getConcatNotLast(root.getRight()))),
                    getLastConcat(root.getLeft()));
        }
        root = ACI.normalizeCommutativity(SerializationUtils.clone(root));
        return root;
    }

    private Tree getLastConcat(Tree root) {
        Tree current = root;
        while (current.getType() == Tree.Type.CONCAT) {
            current = current.getRight();
        }
        return current;
    }

    private static Tree getConcatNotLast(Tree root) {
        if (root.getType() == Tree.Type.CONCAT && root.getRight().getType() != Tree.Type.CONCAT) {
            return root.getLeft();
        }
        Tree current = root;
        Tree newTree = new Tree(Tree.Type.CONCAT, root.getLeft());
        Tree currentNew = newTree;
        while (current.getType() == Tree.Type.CONCAT && current.getRight().getType() == Tree.Type.CONCAT) {
            if (current.getRight().getRight().getType() != Tree.Type.CONCAT) {
                currentNew.setRight(current.getRight().getLeft());
                break;
            }
            currentNew.setRight(new Tree(Tree.Type.CONCAT, current.getRight().getLeft()));
            currentNew = currentNew.getRight();
            current = current.getRight();
        }

        return newTree;
    }

    private static Tree dstrLeft(Tree root) {
        while (root.getType() == Tree.Type.OR &&
                root.getLeft().getType() == Tree.Type.CONCAT &&
                root.getRight().getType() == Tree.Type.OR &&
                root.getRight().getLeft().getType() == Tree.Type.CONCAT &&
                root.getLeft().getLeft().toString().equals(root.getRight().getLeft().getLeft().toString())) {
            root = normalizeOr(new Tree(
                    Tree.Type.OR,
                    root.getRight().getRight(),
                    new Tree(
                            Tree.Type.CONCAT,
                            root.getLeft().getLeft(),
                            normalizeOr(new Tree(
                                    Tree.Type.OR,
                                    root.getLeft().getRight(),
                                    root.getRight().getLeft().getRight())))));
        }
        if (root.getType() == Tree.Type.OR &&
                root.getLeft().getType() == Tree.Type.CONCAT &&
                root.getRight().getType() == Tree.Type.CONCAT &&
                root.getLeft().getLeft().toString().equals(root.getRight().getLeft().toString())) {
            root = new Tree(
                    Tree.Type.CONCAT,
                    root.getLeft().getLeft(),
                    normalizeOr(new Tree(
                            Tree.Type.OR,
                            root.getLeft().getRight(),
                            root.getRight().getRight()))
            );
        }
        root = ACI.normalizeCommutativity(SerializationUtils.clone(root));
        return root;
    }

    private Tree normalizeOr(Tree tree) {
        if (tree.getLeft().getType() == Tree.Type.OR &&
                tree.getRight().getType() != Tree.Type.OR) {
            Tree temp = tree.getLeft();
            tree.setLeft(tree.getRight());
            tree.setRight(temp);
        }
        return tree;
    }
}

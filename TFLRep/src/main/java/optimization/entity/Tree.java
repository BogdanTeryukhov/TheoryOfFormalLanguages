package optimization.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Tree implements Serializable {
    private final Type type;
    private String value;
    private Tree left;
    private Tree right;

    public Tree(Type type, Tree left) {
        this.type = type;
        this.left = left;
    }

    public Tree(Type type, Tree left, Tree right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public Tree(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String drawTree(Tree root) {
        return "\n" + printTree(root, "", false);
    }

    private static String printTree(Tree root, String prefix, boolean isLeft) {
        StringBuilder builder = new StringBuilder();
        if (root != null) {
            builder.append(prefix).append(isLeft ? "├── " : "└── ").append(root.getType())
                    .append(root.getValue() != null ? " " + root.getValue() : "").append("\n");
            String s1 = printTree(root.left, prefix + (isLeft ? "│   " : "    "), root.right != null);
            String s2 = printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
            builder.append(s1).append(s2);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        switch (type) {
            case OR -> {
                return "(" + left + "|" + right + ")";
            }
            case CONCAT -> {
                if (left.getType() == Type.OR &&
                        right.getType() != Type.OR) {
                    return "(" + left + ")" + right;
                }
                if (right.getType() == Type.OR &&
                        left.getType() != Type.OR) {
                    return left + "(" + right + ")";
                }
                return left + "" + right;
            }
            case SYMBOL -> {
                return value;
            }
            case ASTERISK -> {
                if (left.getType() == Type.SYMBOL) {
                    return left + "*";
                }
                return "(" + left + ")" + "*";
            }
            default -> {
                return "";
            }
        }
    }

    public enum Type {
        OR,
        CONCAT,
        ASTERISK,
        SYMBOL
    }
}

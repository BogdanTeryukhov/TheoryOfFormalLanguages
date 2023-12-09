package optimization.entity.parse;

import lombok.Getter;
import optimization.entity.Tree;

@Getter
public class ParseRegex{

    public Tree parseRegex(IterLexeme iterLexeme) {
        Tree parseOne = parseConcat(iterLexeme);
        Lexeme currentLexeme = iterLexeme.getCurrent();
        if (currentLexeme != null &&
                currentLexeme.getType() == Lexeme.LexemeType.OR) {
            iterLexeme.next();
            Tree parseTwo = parseRegex(iterLexeme);
            return new Tree(Tree.Type.OR, parseOne, parseTwo);
        }
        return parseOne;
    }

    public Tree parseConcat(IterLexeme iterLexeme) {
        Tree parseOne = parseIter(iterLexeme);
        Lexeme currentLexeme = iterLexeme.getCurrent();
        if (currentLexeme != null &&
                (currentLexeme.getType() == Lexeme.LexemeType.OPEN_BRACKET ||
                        currentLexeme.getType() == Lexeme.LexemeType.SYMBOL)) {
            Tree parseTwo = parseConcat(iterLexeme);
            return new Tree(Tree.Type.CONCAT, parseOne, parseTwo);
        }
        return parseOne;
    }

    public Tree parseIter(IterLexeme iterLexeme) {
        Tree parseOne = parseGroup(iterLexeme);
        if (iterLexeme.getCurrent() != null &&
                iterLexeme.getCurrent().getType() == Lexeme.LexemeType.ITERATION) {
            Tree tree = new Tree(Tree.Type.ASTERISK, parseOne);
            iterLexeme.next();
            while (iterLexeme.getCurrent() != null &&
                    iterLexeme.getCurrent().getType() == Lexeme.LexemeType.ITERATION) {
                iterLexeme.next();
                tree = new Tree(Tree.Type.ASTERISK, tree);
            }
            return tree;
        }
        return parseOne;
    }

    public Tree parseGroup(IterLexeme iterLexeme) {
        if (iterLexeme.getCurrent() != null &&
                iterLexeme.getCurrent().getType() == Lexeme.LexemeType.SYMBOL) {
            String value = iterLexeme.getCurrent().getValue();
            iterLexeme.next();
            return new Tree(Tree.Type.SYMBOL, value);
        }
        if (iterLexeme.getCurrent() != null &&
                iterLexeme.getCurrent().getType() == Lexeme.LexemeType.OPEN_BRACKET) {
            iterLexeme.next();
            Tree parseOne = parseRegex(iterLexeme);
            if (iterLexeme.getCurrent() == null ||
                    iterLexeme.getCurrent().getType() != Lexeme.LexemeType.CLOSE_BRACKET) {
                throw new RuntimeException();
            }
            iterLexeme.next();
            return parseOne;
        }
        throw new RuntimeException();
    }
}

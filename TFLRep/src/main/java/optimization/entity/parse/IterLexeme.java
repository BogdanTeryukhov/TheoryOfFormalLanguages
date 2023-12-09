package optimization.entity.parse;

import java.util.ArrayList;
import java.util.List;

public class IterLexeme {
    private final List<Lexeme> lexemes;
    private final int size;
    private int position;

    public IterLexeme(List<Lexeme> lexemes) {
        this.lexemes = new ArrayList<>(lexemes);
        this.position = 0;
        this.size = lexemes.size();
    }

    public Lexeme getCurrent() {
        if (position < size) {
            return lexemes.get(position);
        }
        return null;
    }

    public void next() {
        position++;
    }
}

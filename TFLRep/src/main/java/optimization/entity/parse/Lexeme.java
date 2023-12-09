package optimization.entity.parse;

import lombok.Getter;
import lombok.ToString;
import java.util.regex.Pattern;

@Getter
@ToString
public class Lexeme {
    private final String value;
    private LexemeType type;

    public Lexeme(String value) {
        this.value = value;
        setType(value);
    }

    private void setType(String value) {
        for (LexemeType lexemeType : LexemeType.values()) {
            if (isSymbolMatching(lexemeType.regex, value)) {
                this.type = lexemeType;
                return;
            }
        }
        throw new RuntimeException();
    }

    private boolean isSymbolMatching(String regex, String symbol) {
        return Pattern.compile(regex).matcher(symbol).find();
    }

    public enum LexemeType {
        SYMBOL("[a-zA-Z0-9]+"),
        OPEN_BRACKET("\\("),
        CLOSE_BRACKET("\\)"),
        ITERATION("\\*"),
        OR("\\|");

        @Getter
        private final String regex;

        LexemeType(String regex) {
            this.regex = regex;
        }
    }
}

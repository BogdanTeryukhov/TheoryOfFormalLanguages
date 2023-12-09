package optimization.entity.parse;

import lombok.experimental.UtilityClass;
import optimization.entity.Tree;
import optimization.entity.parse.IterLexeme;
import optimization.entity.parse.Lexeme;
import optimization.entity.parse.ParseRegex;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Parser {
    public Tree parser(String data) {
        return syntacticalAnalyze(lexicalAnalyze(data));
    }

    private IterLexeme lexicalAnalyze(String date) {
        List<Lexeme> lexemes = new ArrayList<>();
        for (int i = 0; i < date.length(); i++) {
            String c = String.valueOf(date.charAt(i));
            lexemes.add(new Lexeme(c));
        }
        return new IterLexeme(lexemes);
    }

    private Tree syntacticalAnalyze(IterLexeme lexemes) {
        return new ParseRegex().parseRegex(lexemes);
    }
}

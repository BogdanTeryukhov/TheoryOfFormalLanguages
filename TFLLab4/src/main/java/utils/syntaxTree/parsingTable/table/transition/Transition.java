package utils.syntaxTree.parsingTable.table.transition;

import lombok.*;
import utils.grammar.rule.Rule;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Transition {
    private Character from;
    private String to;
    private Rule by;
}

package utils.syntaxTree.parsingTable.table;

import lombok.*;
import utils.grammar.rule.Rule;
import utils.syntaxTree.parsingTable.table.transition.Transition;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ParsingTable {
    private List<Transition> transitions;
    public ParsingTable() {
       transitions = new ArrayList<>();
    }

    public static Rule findRuleByFromAndTo(Character from, String to, ParsingTable parsingTable){
        for (int i = 0; i < parsingTable.getTransitions().size(); i++) {
            Transition current = parsingTable.getTransitions().get(i);
            if (current.getFrom() == from && current.getTo().equals(to)){
                return current.getBy();
            }
        }
        return null;
    }
}

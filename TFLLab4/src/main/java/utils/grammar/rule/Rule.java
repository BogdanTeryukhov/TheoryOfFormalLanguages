package utils.grammar.rule;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Rule {
    private Character from;
    private List<String> to;

    public Rule() {
        to = new ArrayList<>();
    }
}

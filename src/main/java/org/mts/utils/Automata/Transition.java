package org.mts.utils.Automata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Transition {
    private String from;
    private String by;
    private String to;
}

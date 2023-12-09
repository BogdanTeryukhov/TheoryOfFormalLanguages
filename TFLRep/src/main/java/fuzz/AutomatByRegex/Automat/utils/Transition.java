package fuzz.AutomatByRegex.Automat.utils;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Transition {
    private int from;
    private String by;
    private int to;
}

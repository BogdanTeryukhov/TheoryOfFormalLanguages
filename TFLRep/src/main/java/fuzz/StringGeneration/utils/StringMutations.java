package fuzz.StringGeneration.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StringMutations {

    public String letterRemove(StringBuilder builder){
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public String lettersRepeat(StringBuilder builder){
        return builder.append(builder.charAt(builder.length() - 1)).toString();
    }

    public String lettersSwap(StringBuilder builder, char ch){
        char prev = builder.charAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1)
                .append(ch)
                .append(prev);
        return builder.toString();
    }
}

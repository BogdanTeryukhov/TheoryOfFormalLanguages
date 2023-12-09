package optimization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptimizationServiceTest {

    final OptimizationService optimizationService = new OptimizationService();


    List<DoOptimization> fillingArray(){
        List<DoOptimization> list = new ArrayList<>();
        list.add(new DoOptimization("(abc)***", "(abc)*"));
        list.add(new DoOptimization("((abc)*|(cde)*)*", "(abc|cde)*"));
        list.add(new DoOptimization("(abc(cde)*)*", "(abc(cde)*)*"));
        list.add(new DoOptimization("((abc)*(cde)*)*", "(abc|cde)*"));
        list.add(new DoOptimization("ac|bc", "(a|b)c"));
        list.add(new DoOptimization("ab|ac", "a(b|c)"));
        list.add(new DoOptimization("a*a|a*c", "a*(a|c)"));
        list.add(new DoOptimization("a*a|b|a*c", "a*(a|c)|b"));
        list.add(new DoOptimization("(((c|a(b|c)|b|a)|b)*|((acd)e)*)*", "(a|a(b|c|cde)|b|c)*"));
        list.add(new DoOptimization("(acde|a(b|c))", "a(b|c|cde)"));
        list.add(new DoOptimization("(acde|agz|acdf|ab|ac))", "a(b|c|cd(e|f)|gz)"));
        list.add(new DoOptimization("abcdefghij|abcdefghi|abcdefgh|abcdefg|abcdef|abcde|abcd|abc|ab|a",
                "a|a(b|b(c|c(d|d(e|e(f|f(g|g(h|h(i|ij))))))))"));
        list.add(new DoOptimization("abbba|adddda", "a(bbb|dddd)a"));
        list.add(new DoOptimization("abcdefg|bcdefg|cdefg|defg|efg|fg|g",
                "(((((ab|b)c|c)d|d)e|e)f|f)g|g"));

        return list;
    }

    @Test
    void optimization() {
        List<DoOptimization> list =  fillingArray();
        for (DoOptimization doOptimization : list) {
            String optimization = optimizationService.optimization(doOptimization.input);
            assertEquals(doOptimization.expected, optimization);
        }
    }

    @Getter
    @AllArgsConstructor
    static class DoOptimization {
        private String input;
        private String expected;
    }
}
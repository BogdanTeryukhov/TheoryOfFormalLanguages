package fuzz.AutomatByRegex;

import fuzz.AutomatByRegex.Automat.NFA;
import fuzz.StringGeneration.ComparingWordWithRegexAndAutomata;
import fuzz.RegexGeneration.RegexGenerator;
import fuzz.RegexGeneration.utils.RegexPattern;
import fuzz.StringGeneration.StringGenerator;
import fuzz.StringGeneration.utils.StringMutations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import optimization.OptimizationService;

class NFAByRegexBuilderTest {
    OptimizationService optimizationService;
    NFAByRegexBuilder nfaByRegexBuilder;
    RegexGenerator regexGenerator;

    StringMutations stringMutations;
    ComparingWordWithRegexAndAutomata comparingWordWithRegexAndAutomata;

    StringGenerator stringGenerator;

    @BeforeEach
    void setUp(){
        stringMutations = new StringMutations();
        comparingWordWithRegexAndAutomata = new ComparingWordWithRegexAndAutomata();
        stringGenerator = new StringGenerator();
        regexGenerator = new RegexGenerator();
        optimizationService = new OptimizationService();
        nfaByRegexBuilder = new NFAByRegexBuilder();
    }
    @Test
    void isSymbol() {
        Assertions.assertTrue(nfaByRegexBuilder.isSymbol('a'));
        Assertions.assertTrue(nfaByRegexBuilder.isSymbol('B'));
        Assertions.assertTrue(nfaByRegexBuilder.isSymbol('0'));
    }

    @Test
    void testingFailures(){
        String optimizedRegex = optimizationService.optimization("(b|p)*bqb(b|q)");
        NFA nfa = nfaByRegexBuilder.processor(optimizedRegex);
        //kaaoo kkmo koo

        String out = "bqbb";
        System.out.println(out);
        System.out.println(out.matches(optimizedRegex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, out));

        String mutated = "bb";
        System.out.println(mutated);
        System.out.println(mutated.matches(optimizedRegex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, mutated));
        //System.out.println(optimizedRegex);
        System.out.println(nfa);
    }


    @Test
    void oneBuildRegex(){
        //(f|g)q(f|k)(g|k)q  |  fqkkq
        for (int i = 0; i < 10; i++) {
            String regex = regexGenerator.generation(new RegexPattern(5, 4, 15));
            System.out.println("Start");
            for (int j = 0; j < 10; j++) {
                String optimizedRegex = optimizationService.optimization(regex);
                NFA nfa = nfaByRegexBuilder.processor(optimizedRegex);
                //kaaoo kkmo koo

                String out = stringGenerator.stringGeneration(nfa);
                System.out.println(out);
                System.out.println(out.matches(regex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, out));
                if (out.matches(regex) != comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, out)){
                    throw new RuntimeException();
                }

                String mutated = stringGenerator.mutations(out);
                System.out.println(mutated);
                System.out.println(mutated.matches(regex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa,mutated));
                if (mutated.matches(regex) != comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, mutated)){
                    throw new RuntimeException();
                }
                //System.out.println(optimizedRegex);
                System.out.println(nfa);
            }
            System.out.println("Finish");
        }

        //"k(a|k)*(m|o)o(b|m)"
    }

    @Test
    void processor() {
        NFA nfa1 = nfaByRegexBuilder.processor("k(a|k)*(m|o)o(b|m)"); // failed (k|y)(k|u)r
        System.out.println(nfa1);
    }
}
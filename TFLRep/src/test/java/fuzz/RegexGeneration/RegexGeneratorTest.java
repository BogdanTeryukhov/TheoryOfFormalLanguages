package fuzz.RegexGeneration;

import fuzz.AutomatByRegex.NFAByRegexBuilder;
import fuzz.RegexGeneration.utils.RegexPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import optimization.OptimizationService;

class RegexGeneratorTest {

    RegexGenerator regexGenerator;
    OptimizationService optimizationService;

    NFAByRegexBuilder nfaByRegexBuilder;

    @BeforeEach
    void setUp(){
        regexGenerator = new RegexGenerator();
        optimizationService = new OptimizationService();
        nfaByRegexBuilder = new NFAByRegexBuilder();
    }

    @Test
    void multipleGeneration(){
        for (int i = 0; i < 5; i++) {
            String regex = regexGenerator.generation(new RegexPattern(5,1,11));
            System.out.println(regex + " : " + optimizationService.optimization(regex));
        }
    }

    @Test
    void generation() {
        for (int i = 0; i < 10; i++) {
            String regex = regexGenerator.generation(new RegexPattern(5,1,4));
            //System.out.println(optimizationService.optimization("(r|(h|k)|fg*kl)t(t|j)*r*"));
            System.out.println(optimizationService.optimization("fg*kl|h|k|r)t(j|t)*r*"));
            //System.out.println(NFAByRegexBuilder.convertRegexToNFA(regex)); (fg*kl|h|k|r)t(j|t)*r*
        }
    }
}
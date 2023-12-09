package fuzz.StringGeneration;

import fuzz.StringGeneration.utils.StringMutations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringGeneratorTest {

    StringMutations stringMutations;

    StringGenerator stringGenerator;

    @BeforeEach
    void setUp(){
        stringGenerator = new StringGenerator();
        stringMutations = new StringMutations();
    }

    @Test
    void mutations() {
        String str = "essential";
        System.out.println(str);
        System.out.println(stringGenerator.mutations(str));
    }
}
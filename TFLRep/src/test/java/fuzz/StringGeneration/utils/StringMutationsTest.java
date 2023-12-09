package fuzz.StringGeneration.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringMutationsTest {

    StringMutations stringMutations;

    @BeforeEach
    void setUp(){
        stringMutations = new StringMutations();
    }

    @Test
    void lettersSwap() {
        String str = "essential";
        //System.out.println(stringMutations.lettersSwap(str));
    }

    @Test
    void lettersRepeat() {
        String str = "essential";
        StringBuilder builder = new StringBuilder();
        //System.out.println(stringMutations.lettersRepeat());
    }
}
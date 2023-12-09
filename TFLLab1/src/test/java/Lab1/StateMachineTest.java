package Lab1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.List;
import java.util.Map;

public class StateMachineTest {

    @Test
    public void parse() throws InputArgumentException {
        String line1 = "h(h(x,x),f)";
        String line2 = "g(p,f(x,x))";

        Map<String, List<String>> result1 = StateMachine.parse(line1);
        Map<String, List<String>> result2 = StateMachine.parse(line2);

        for (Map.Entry<String, List<String>> entry: result1.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        for (Map.Entry<String, List<String>> entry: result2.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Test
    public void isLetter() {
        Assert.assertTrue(StateMachine.isLetter('a'));
        Assert.assertTrue(StateMachine.isLetter('g'));
        Assert.assertFalse(StateMachine.isLetter('1'));
        Assert.assertFalse(StateMachine.isLetter('-'));
        Assert.assertFalse(StateMachine.isLetter(' '));
    }
}
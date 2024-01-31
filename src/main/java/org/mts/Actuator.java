package org.mts;


import org.mts.utils.Automata.Automata;
import org.mts.utils.Automata.Transition;
import org.mts.utils.AutomataToRegex;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Actuator {

    public static Automata automata = new Automata();

    public static List<String> fillAcceptingState(String line){
        List<String> acceptingStates = new ArrayList<>();
        char[] arr = line.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ','){
                acceptingStates.add(builder.toString().trim());
                builder.setLength(0);
            }
            else {
                builder.append(arr[i]);
            }
        }
        acceptingStates.add(builder.toString().trim());
        return acceptingStates;
    }

    public static Automata inputAutomata(String pathToFile) throws IOException {
        Path path = Path.of(pathToFile);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            boolean startingState = false;
            boolean acceptingStates = false;
            boolean transitions = false;
            while ((line = br.readLine()) != null) {
                if (transitions){
                    String from = line.substring(line.indexOf('(') + 1, line.indexOf(','));
                    String by = line.substring(line.indexOf(',') + 1, line.indexOf(')'));
                    String to = line.substring(line.indexOf('>') + 1).trim();
                    automata.getTransitions().add(new Transition(from,by,to));
                }
                else if (acceptingStates){
                    automata.setAcceptingStates(fillAcceptingState(line));
                    acceptingStates = false;
                }
                else if (startingState) {
                    automata.setStartingState(line);
                    startingState = false;
                }
                //--------------------------------
                if (line.equals("Starting State:")){
                    startingState = true;
                }
                else if (line.equals("Accepting States:")){
                    acceptingStates = true;
                }
                else if (line.equals("Transitions:")) {
                    transitions = true;
                }
            }
        }
        catch (IOException e){
            throw new IOException();
        }
        return automata;
    }
    public static void main(String[] args) throws IOException {
        automata = inputAutomata(args[0]);
        Automata uniqueAutomata = Automata.createUniqueTransitions(automata);
        AutomataToRegex.addingExtraStartingAndAcceptingStates(uniqueAutomata);
        String minRegex = AutomataToRegex.getMinRegex(uniqueAutomata);
        System.out.println("Min regex: " + minRegex);
    }
}
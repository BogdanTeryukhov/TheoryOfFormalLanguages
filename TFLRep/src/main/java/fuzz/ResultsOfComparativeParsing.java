package fuzz;

import fuzz.AutomatByRegex.Automat.NFA;
import fuzz.AutomatByRegex.NFAByRegexBuilder;
import fuzz.RegexGeneration.RegexGenerator;
import fuzz.RegexGeneration.utils.RegexPattern;
import fuzz.StringGeneration.ComparingWordWithRegexAndAutomata;
import fuzz.StringGeneration.StringGenerator;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;

import optimization.OptimizationService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResultsOfComparativeParsing {

    public RegexGenerator regexGenerator;
    public OptimizationService optimizationService;
    public NFAByRegexBuilder nfaByRegexBuilder;
    public StringGenerator stringGenerator;
    public ComparingWordWithRegexAndAutomata comparingWordWithRegexAndAutomata;

    public ResultsOfComparativeParsing() {
        regexGenerator = new RegexGenerator();
        optimizationService = new OptimizationService();
        nfaByRegexBuilder = new NFAByRegexBuilder();
        stringGenerator = new StringGenerator();
        comparingWordWithRegexAndAutomata = new ComparingWordWithRegexAndAutomata();
    }

    public List<Long> getDataFromJson() throws IOException, ParseException {
        //File file = new File("input.json");
        BufferedReader reader = new BufferedReader(new FileReader("input.json"));
        Object object = new JSONParser().parse(reader);
        JSONObject json = (JSONObject) object;

        List<Long> data = new ArrayList<>();
        data.add((Long) json.get("alphabet_size"));
        data.add((Long) json.get("stellar_height"));
        data.add((Long) json.get("maximum_number_of_letters"));
        return data;
    }

    public void results() throws IOException, ParseException {
        Path pathToWrite = Path.of(".\\results.txt");
        if (Files.exists(pathToWrite)){
            Files.delete(pathToWrite);
        }

        PrintWriter writer = new PrintWriter(new FileWriter(pathToWrite.toFile(), false));

        List<Long> data = getDataFromJson();
        long alphabetSize = data.get(0);
        long stellarHeight = data.get(1);
        long maximumNumberOfLetters = data.get(2);

        int counter = 1;
        for (int i = 0; i < 10; i++) {
            String regex = regexGenerator.generation(new RegexPattern(alphabetSize, stellarHeight, maximumNumberOfLetters));
            for (int j = 0; j < 10; j++) {
                writer.println(counter + " expression:");
                String optimizedRegex = optimizationService.optimization(regex);
                NFA nfa = nfaByRegexBuilder.processor(optimizedRegex);

                String out = stringGenerator.stringGeneration(nfa);
                //System.out.println(out);
                //System.out.println(out.matches(regex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, out));
                String mutated = stringGenerator.mutations(out);
                //System.out.println(mutated);
                //System.out.println(mutated.matches(regex) + " : " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa,mutated));
                writer.println("\t" + "Word: " + mutated);
                writer.println("\t" + "Regex inclusion: " + mutated.matches(regex));
                writer.println("\t" + "FSM inclusion: " + comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa,mutated));
                writer.println("\t" + "Equals?: " + (mutated.matches(regex) == comparingWordWithRegexAndAutomata.isBelongToAutomata(nfa, mutated)));
                writer.println("\n");
                counter++;
                //System.out.println(nfa);
            }
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException, ParseException {
        ResultsOfComparativeParsing results = new ResultsOfComparativeParsing();
        results.results();
    }
}

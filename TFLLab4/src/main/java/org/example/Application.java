package org.example;

import utils.fileScanning.GrammarAndWordScanner;
import utils.grammar.Grammar;
import utils.grammar.rule.Rule;
import utils.syntaxTree.SyntaxTreeCreation;
import utils.syntaxTree.incrementalParsing.IncrementalParsing;
import utils.syntaxTree.parsingTable.ParsingTableCreator;
import utils.syntaxTree.parsingTable.firstAndFollow.FirstFunForGrammarCreator;
import utils.syntaxTree.parsingTable.firstAndFollow.FollowFunForGrammarCreator;

import java.io.IOException;
import java.util.Arrays;

import static utils.fileScanning.GrammarAndWordScanner.grammarFill;
import static utils.fileScanning.GrammarAndWordScanner.word;

public class Application {

    public static void main(String[] args) throws IOException {
        Grammar grammar = GrammarAndWordScanner.grammarFill(args[0]); //args[0] - путь к файлу input.txt
        if (Grammar.isGrammarLeftRecursive(grammar)){
            throw new RuntimeException("Grammar is not LL(1) !");
        }

        for (int i = grammar.getRulesSet().size() - 1; i >= 0; i--) {
            FirstFunForGrammarCreator.firstCreator(grammar.getRulesSet().get(i));
        }
        //System.out.println("First");
        //FirstFunForGrammarCreator.firstFunctionHashMap.forEach((key,value)->System.out.println(key + " : " + value));
        FollowFunForGrammarCreator.followCreator(grammar, FirstFunForGrammarCreator.firstFunctionHashMap);

        //System.out.println("Follow");
        //FollowFunForGrammarCreator.followFunctionHashMap.forEach((key,value)->System.out.println(key + " : " + value));
        ParsingTableCreator.tableCreator(FirstFunForGrammarCreator.firstFunctionHashMap, FollowFunForGrammarCreator.followFunctionHashMap, grammar);

        System.out.println("LL(1) Parsing Tree: ");
        SyntaxTreeCreation.createHashMap(ParsingTableCreator.parsingTable, GrammarAndWordScanner.word);
        //SyntaxTreeCreation.tree.forEach((key, value) -> System.out.println(key + " : " + value));

        //System.out.println("Reps");
        //SyntaxTreeCreation.reps.forEach((key, value) -> System.out.println(key + " : " + value));

        System.out.println("\nIncremental LL(1) Parsing Tree");
        IncrementalParsing.incrementalParsingRealisation(SyntaxTreeCreation.resultTree, GrammarAndWordScanner.incWord, ParsingTableCreator.parsingTable);
        //IncrementalParsing.incrementalTree.forEach((key,value) -> System.out.println(key + " : " + value));
    }
}
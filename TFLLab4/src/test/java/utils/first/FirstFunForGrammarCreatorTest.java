package utils.first;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.fileScanning.GrammarAndWordScanner;
import utils.syntaxTree.SyntaxTreeCreation;
import utils.syntaxTree.incrementalParsing.IncrementalParsing;
import utils.syntaxTree.parsingTable.ParsingTableCreator;
import utils.syntaxTree.parsingTable.firstAndFollow.FirstFunForGrammarCreator;
import utils.syntaxTree.parsingTable.firstAndFollow.FollowFunForGrammarCreator;
import utils.grammar.Grammar;

import java.io.IOException;

class FirstFunForGrammarCreatorTest {

    //private final GrammarAndWordScanner grammarAndWordScanner = new GrammarAndWordScanner();

    @BeforeEach
    void setUp(){

    }

    @Test
    void firstCreator() throws IOException {
        Grammar grammar = GrammarAndWordScanner.grammarFill("C:\\Users\\andre\\IdeaProjects\\TFLLab4\\src\\main\\resources\\input.txt");
        System.out.println("Grammar");
        System.out.println(grammar);
        if (Grammar.isGrammarLeftRecursive(grammar)){
            throw new RuntimeException("Grammar is not LL(1) !");
        }

        System.out.println("First Functions Derivation");
        for (int i = grammar.getRulesSet().size() - 1; i >= 0; i--) {
            FirstFunForGrammarCreator.firstCreator(grammar.getRulesSet().get(i));
        }
        FirstFunForGrammarCreator.firstFunctionHashMap.forEach((key, value) -> System.out.println(key + " : " + value));
        System.out.println("Finish First Functions Derivations\n");

        FollowFunForGrammarCreator.followCreator(grammar, FirstFunForGrammarCreator.firstFunctionHashMap);
        System.out.println("Follow Functions Derivation");
        FollowFunForGrammarCreator.followFunctionHashMap.forEach((key, value) -> System.out.println(key + " : " + value));
        System.out.println("Follow Functions Derivations\n");

//        if (Grammar.hasRepeatsOnFirstAndFollowFunctions(FirstFunForGrammarCreator.firstFunctionHashMap, FollowFunForGrammarCreator.followFunctionHashMap)){
//            throw new RuntimeException("Grammar is not LL(1) !");
//        }
        System.out.println("Parsing Table: ");
        ParsingTableCreator.tableCreator(FirstFunForGrammarCreator.firstFunctionHashMap, FollowFunForGrammarCreator.followFunctionHashMap, grammar);
        System.out.println(ParsingTableCreator.parsingTable);

        System.out.println("Tree list: ");
        SyntaxTreeCreation.createHashMap(ParsingTableCreator.parsingTable, GrammarAndWordScanner.word);
        SyntaxTreeCreation.tree.forEach((key, value) -> System.out.println(key + " : " + value));

        System.out.println("\nIncremental Parsing");
        IncrementalParsing.incrementalParsingRealisation(SyntaxTreeCreation.resultTree, "baa", ParsingTableCreator.parsingTable);
        IncrementalParsing.incrementalTree.forEach((key,value) -> System.out.println(key + " : " + value));
        //SyntaxTreeCreation.createHashMap(ParsingTableCreator.parsingTable, "adcakdhk");
        //SyntaxTreeCreation.tree.forEach((key, value) -> System.out.println(key + " : " + value));
    }
}
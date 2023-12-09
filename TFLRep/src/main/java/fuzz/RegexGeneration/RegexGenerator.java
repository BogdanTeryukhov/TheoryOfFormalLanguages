package fuzz.RegexGeneration;

import fuzz.RegexGeneration.utils.RegexPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegexGenerator {
    private static final char[] alphabet =
            new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public char randomSymbolFromAlphabet(){
        return alphabet[new Random().nextInt(0,25)];
    }

    public char getRandomSymbol(List<Character> alphabet){
        return alphabet.get(new Random().nextInt(0, alphabet.size()));
    }

    public char getRandomNonRepeatedSymbol(List<Character> alphabet, char symbol){
        while (true){
            char ch = getRandomSymbol(alphabet);
            if (ch != symbol){
                return ch;
            }
        }
    }

    public List<Character> alphabetCreation(List<Character> alphabet, long size){
        for (int i = 0; i < size; i++) {
            while (true){
                char ch = randomSymbolFromAlphabet();
                if (!alphabet.contains(ch)){
                    alphabet.add(ch);
                    break;
                }
            }
        }
        return alphabet;
    }

    public int getNumber(boolean isFirstLoop){
        return isFirstLoop ? new Random().ints(1,3).findFirst().getAsInt()
                : new Random().ints(1,4).findFirst().getAsInt();
    }

    // доделать возможную вложенность
    public String generation(RegexPattern regexPattern){
        StringBuilder builder = new StringBuilder();
        List<Character> alphabet = new ArrayList<>();
        alphabet = alphabetCreation(alphabet, regexPattern.getAlphabetSize());

        int regexLettersNumber = 0;
        int numberOfStars = 0;
        boolean isFirstLoop = true;
        while (regexPattern.getMaxLettersNumber() - regexLettersNumber >= 0){
            //System.out.println("Max: " + regexPattern.getMaxLettersNumber() + " Current: " + regexLettersNumber);
            int rand = getNumber(isFirstLoop);
            //System.out.println("Rand: " + rand);
            isFirstLoop = regexPattern.getSsnf() == 0;
            if (rand == 1){
                //Или
                if (regexPattern.getMaxLettersNumber() - regexLettersNumber >= 2){
                    char symbol = getRandomSymbol(alphabet);
                    builder.append('(')
                            .append(symbol)
                            .append('|')
                            .append(getRandomNonRepeatedSymbol(alphabet, symbol))
                            .append(')');
                    regexLettersNumber += 2;
                }
                else {
                    break;
                }
            }
            else if (rand == 2){
                //Константа
                if (regexPattern.getMaxLettersNumber() - regexLettersNumber >= 1){
                    builder.append(getRandomSymbol(alphabet));
                    regexLettersNumber++;
                }
                else {
                    break;
                }
            }
            else if (rand == 3){
                //System.out.println("SSNF: " + regexPattern.getSsnf() + " Stars:" + numberOfStars);
                //Индексация
                if (regexPattern.getSsnf() - numberOfStars > 0){
                    builder.append('*');
                    numberOfStars++;
                    isFirstLoop = true;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        return builder.toString();
    }
}

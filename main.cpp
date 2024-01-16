#include <iostream>
#include <string>
#include <algorithm>
#include <sstream>
#include <fstream>
#include "grammar/hpp/grammar.hpp"

using namespace std;

Grammar grammar_creation(int step, char starting_symbol) {
    //Запись правил через консоль
    vector<string> rules_set;
    string rule;

    cout<<"Введите правила: "<<endl;
    while (cin >> rule){
        rules_set.push_back(rule);
    }

    if (step == 0) {
        return Grammar(starting_symbol, rules_set);
    }
    else {
        return Grammar(starting_symbol, rules_set, step);
    }
}

bool is_equals(string s1, string s2){
    for (int i = 0; i < s1.length(); ++i) {
        if (s1[i] != s2[i]){
            return false;
        }
    }
    return true;
}

int main(int argc, char ** argv){
    string s; // сюда будем класть считанные строки

    string input_word;
    int step;
    char starting_symbol;

    ifstream file(argv[1]); // файл из которого читаем (для линукс путь будет выглядеть по другому)

    bool is_input_word = false;
    bool is_step = false;
    bool is_starting_symbol = false;
    while(getline(file, s)){ // пока не достигнут конец файла класть очередную строку в переменную (s)
        if (is_starting_symbol){
            starting_symbol = s[0];
            is_starting_symbol = false;
        }
        else if (is_step){
            step = atoi(s.c_str());
            is_step = false;
        }
        else if (is_input_word){
            input_word = s;
            is_input_word = false;
        }


        if (is_equals("Input word:", s)){
            is_input_word = true;
        }
        else if (is_equals("Step:",s)){
            is_step = true;
        }
        else if (is_equals("Starting Symbol:",s)){
            is_starting_symbol = true;
        }
    }
    file.close(); // обязательно закрываем файл что бы не повредить его

    try {
        Grammar g = grammar_creation(step, starting_symbol);
        g.build_first();
        g.build_follow();
        g.build_parsing_table();

        string msg;
        cout<<input_word<<endl;
        bool flag = g.word_check(input_word, msg, argv[2]);
        flag ? cout << "word is in grammar!" << endl : cout << "error in word_check function: " << msg << endl;
    }
    catch (runtime_error e){
        cout << e.what() << endl;
    }
    cout << endl;
    return 0;
}

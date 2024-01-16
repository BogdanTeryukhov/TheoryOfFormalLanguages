#include <string>
#include <set>
#include <map>
#include <vector>

using namespace std;

struct Rule{
    char from = ' ';
    string to;
};

struct Node{
    char s;
    Node *parent = nullptr;
    Node(char symbol) : s(symbol){};
};

class Grammar {
public:
    Grammar(char starting_symbol, const vector<string> &rules_set, int drawing = -1);
    //вспомогательные функции для построения множеств first, follow, а также parsing table и проверок грамматики
    bool is_left_recursive(set<char> rec_nt);
    bool is_first_flag_true_or_false(char nterm, set<char> rec_nt);
    bool is_follow_flag_true_or_false(char from, string to);
    void parsing_table_in_creation(int rule_ind, char term, char nterm);

    void build_first();
    void build_follow();
    void build_parsing_table();
    bool word_check(string word, string &message, string path);

private:
    char start;
    set<char> terminals;
    set<char> nonterminals;

    const char epsilon = '#';
    const char end = '$';

    int drawing = -1;
    int cnt_draw = 0;

    // таблица синтаксического разбора
    map<char, map<char, vector<int>>> parsing_table;
    //правила
    vector<Rule> rules;
    vector<set<char>> add_first_for_rule;
    map<char, vector<int>> nt_to_rules;
    //множества first и follow
    map<char, set<char>> first;
    map<char, set<char>> follow;


    void firstForNTerm();
    set<char> firstForNTermRule(string to);
    void rules_creation(vector<string> rules);
    bool isTerminal(char elem);
    void tryPrintingStack(set<Node*> nodes, string path);
    void printStack(set<Node*> nodes, string path);
};

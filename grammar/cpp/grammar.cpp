#include "../hpp/grammar.hpp"

#include <algorithm>
#include <stdexcept>
#include <iostream>
#include <sstream>
#include <fstream>

Grammar::Grammar(char starting_symbol, const vector<string> &rules_set, int drawing) : start(starting_symbol), drawing(drawing){
    if (isTerminal(starting_symbol)){
        throw runtime_error("Стартовый символ должен быть [A-Z]");
    }
    rules_creation(rules_set);
}

bool Grammar::isTerminal(char elem){
    return !(elem >= 65 && elem <= 90);
}

void Grammar::rules_creation(vector<string> cur_rules){
    for (int i = 0; i < cur_rules.size(); ++i){
        string rule = cur_rules[i];
        //cout<<"Cur_rule: "<< rule<<endl;
        char left = rule[0];
        if (isTerminal(left) || rule[1] != '-' || rule[2] != '>'){
            throw runtime_error("Неверный формат правила");
        }
        string right(rule.begin() + 3, rule.end());
        nonterminals.insert(left);
        for (char & it : right){
            if (isTerminal(it)){
                terminals.insert(it);
            }
        }
        terminals.insert(end);
        terminals.erase(epsilon);
        rules.push_back({left, right});
        add_first_for_rule.push_back({});
        nt_to_rules[left].push_back(i);
    }
}

void Grammar::build_first(){
    firstForNTerm();
}


bool Grammar::is_first_flag_true_or_false(char nterm, set<char> rec_nt){
    bool flag = false;
    vector<int> cur_rules = nt_to_rules[nterm];
    for (int cur_rule : cur_rules){
        int proxy = first[nterm].size();
        set<char> proxy_set = firstForNTermRule(rules[cur_rule].to);
        if (proxy_set.find(nterm) != proxy_set.end()) {
            rec_nt.insert(nterm);
        }
        add_first_for_rule[cur_rule] = proxy_set;
        first[nterm].insert(proxy_set.begin(), proxy_set.end());
        if (first[nterm].size() != proxy){
            flag = true;
        }
    }
    return flag;
}

bool Grammar::is_left_recursive(set<char> rec_nt) {
    bool left_recursive = false;
    for (auto nterm : rec_nt) {
        if (first[nterm].find(epsilon) == first[nterm].end()) {
            left_recursive = true;
        }
    }
    return left_recursive;
}

void Grammar::firstForNTerm(){
    bool first_flag = true;
    set<char> rec_nt;
    while (first_flag){
        first_flag = false;
        for (auto nterm : nonterminals){
            first_flag = is_first_flag_true_or_false(nterm, rec_nt);
        }
    }
    if (is_left_recursive(rec_nt)){
        throw runtime_error("Леворекурсивная грамматика");
    }
}

set<char> Grammar::firstForNTermRule(string to){
    if (isTerminal(to[0])){
        if (to.size() > 1 && to[0] == epsilon){
            throw runtime_error("Ничего не должно быть рядом с пустым словом");
        }
        return {to[0]};
    }
    else{
        set<char> next_first = first[to[0]];
        next_first.insert(to[0]);
        if (to.size() == 1 || next_first.find(epsilon) == next_first.end()){
            return next_first;
        }
        else{
            next_first.erase(epsilon);
            to.erase(to.begin());
            set<char> next_next_first = firstForNTermRule(to);
            next_first.insert(next_next_first.begin(), next_next_first.end());
            return next_first;
        }
    }
}

bool Grammar::is_follow_flag_true_or_false(char from, string to) {
    bool flag = false;
    for (int j = 0; j < to.size(); ++j){
        if (!isTerminal(to[j])) {
            int x = follow[to[j]].size();
            if (j == to.size() - 1){
                follow[to[j]].insert(follow[from].begin(), follow[from].end());
            }
            else{
                set<char> next_first = firstForNTermRule(string(to.begin() + j + 1, to.end()));
                bool is_eps = next_first.find(epsilon) != next_first.end();
                next_first.erase(epsilon);
                follow[to[j]].insert(next_first.begin(), next_first.end());
                if (is_eps){
                    follow[to[j]].insert(follow[from].begin(), follow[from].end());
                }
            }
            if (follow[to[j]].size() != x){
                flag = true;
            }
        }
    }
    return flag;
}

void Grammar::build_follow(){
    follow[start] = {end};
    bool follow_flag = true;
    while (follow_flag){
        follow_flag = false;
        for (auto & rule : rules){
            char from = rule.from;
            string to = rule.to;
            follow_flag = is_follow_flag_true_or_false(from,to);
        }
    }
}

void Grammar::parsing_table_in_creation(int rule_ind, char term, char nterm) {
    if (add_first_for_rule[rule_ind].find(epsilon) != add_first_for_rule[rule_ind].end() ||
    add_first_for_rule[rule_ind].find(term) != add_first_for_rule[rule_ind].end() && follow[nterm].find(term) != follow[nterm].end()){
        parsing_table[nterm][term].push_back(rule_ind);
    }
}

void Grammar::build_parsing_table(){
    for (char nterm : nonterminals){
        parsing_table[nterm] = {};
        for (char term : terminals){
            parsing_table[nterm][term] = {};
            vector<int> nt_rules = nt_to_rules[nterm];
            for (int rule_ind : nt_rules){
                parsing_table_in_creation(rule_ind, term, nterm);
            }
        }
    }
}

bool Grammar::word_check(string word, string &message, string path){
    word += end;
    vector<Node *> nodes;

    Node *beg = new Node(start);
    beg->parent = new Node(end);
    nodes.push_back(beg);
    nodes.push_back(beg->parent);

    set<pair<Node *, int>> cur_nodes; // указатель на вершину с элементом и позиция на ленте
    cur_nodes.insert({beg, 0});

    int error_ind = 0;
    bool is_ok = false;

    int it = 0;
    while (!cur_nodes.empty())
    {
        it++;
        set<pair<Node *, int>> new_cur_nodes = cur_nodes;
        bool is_final = false;
        for (auto p_cur_node : cur_nodes)
        {
            char lenta_elem = word[p_cur_node.second];
            Node *cur_node = p_cur_node.first;

            if (isTerminal(cur_node->s))
            {
                if (lenta_elem == end && cur_node->s == end)
                {
                    is_final = true;
                    is_ok = true;
                    break;
                }
                if (lenta_elem == cur_node->s)
                {
                    new_cur_nodes.erase(p_cur_node);
                    int new_lenta_ind = p_cur_node.second + 1;
                    if (new_lenta_ind < word.size()) {
                        new_cur_nodes.insert({cur_node->parent, new_lenta_ind});
                        error_ind = max(error_ind, new_lenta_ind); // маскимальный достижимый индекс
                    }
                } else {
                    new_cur_nodes.erase(p_cur_node);
                }
            }
            else
            {
                // удаляем нетерминал и добавляем развертку нетерминала
                new_cur_nodes.erase(p_cur_node);
                vector<int> rules_ind = parsing_table[cur_node->s][lenta_elem];
                for (int i = 0; i < rules_ind.size(); ++i)
                {
                    Rule r = rules[rules_ind[i]];
                    Node *child = cur_node->parent;
                    for (int j = r.to.size() - 1; j >= 0; j--)
                    {
                        if (r.to[j] != epsilon)
                        {
                            Node *new_node = new Node(r.to[j]);
                            nodes.push_back(new_node);
                            new_node->parent = child;
                            child = new_node;
                        }
                    }
                    new_cur_nodes.insert({child, p_cur_node.second});

                    set<Node*> stack_for_print;
                    for (auto p : new_cur_nodes) {
                        stack_for_print.insert(p.first);
                    }
                    tryPrintingStack(stack_for_print, path);
                }
            }
        }
        if (is_final){
            break;
        }
        cur_nodes = new_cur_nodes;
    }

    for (int i = 0; i < nodes.size(); ++i){
        delete nodes[i];
    }

    if (!is_ok){
        message = "error in " + to_string(error_ind); // выводим самый дальний индекс, до которого дошли
    }

    return is_ok;
}

void Grammar::tryPrintingStack(set<Node*> nodes, string path){
    if (drawing < 0){
        return;
    }
    cnt_draw++;
    if (cnt_draw == drawing) {
        printStack(nodes, path);
    }
}

void Grammar::printStack(set<Node*> nodes, string path){
    stringstream ss;
    ss << "digraph G { " << endl;

    map<Node*, int> node_to_int;
    int mx_vex_num = 1;

    set<Node*> is_printed;

    while(!nodes.empty()) {
        set<Node*> new_nodes;
        for (auto node : nodes) {
            if (node_to_int.find(node) == node_to_int.end()) {
                node_to_int[node] = mx_vex_num;
                mx_vex_num++;
            }
            if (node_to_int.find(node->parent) == node_to_int.end()) {
                node_to_int[node->parent] = mx_vex_num;
                mx_vex_num++;
            }

            if (is_printed.find(node) == is_printed.end()) {
                ss << "\t" << node_to_int[node] << " [label = \"" << node->s << "\"];" << endl;
                if (node->parent) {
                    ss << "\t" << node_to_int[node] << " -> " << node_to_int[node->parent] << ";" << endl;
                    new_nodes.insert(node->parent);
                }
                is_printed.insert(node);
            }
        }
        nodes = new_nodes;
    }

    ss << "}" << endl;

    ofstream outFile;
    outFile.open(path);
    outFile << ss.rdbuf();
    outFile.close();
}


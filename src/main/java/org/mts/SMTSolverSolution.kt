package org.mts

import com.microsoft.z3.Context
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.*

object SMTSolverSolution {
    private val smtCreation = StringBuilder("")

    fun isCharInString(c: Char, str: String): Boolean {
        for (element in str) {
            if (element == c) {
                return true
            }
        }
        return false
    }

    fun spacesDelete(s: StringBuilder) {
        var i = 0
        while (i < s.length) {
            if (s[i] == ' ') {
                s.deleteCharAt(i)
                i--
            }
            ++i
        }
    }

    fun splitString(str: StringBuilder, delim: Char): List<String> {
        val res: MutableList<String> = ArrayList()
        var pos: Int
        var token: String
        while (str.indexOf(delim.toString()).also { pos = it } != -1) {
            token = str.substring(0, pos)
            res.add(token)
            str.delete(0, pos + 1)
        }
        res.add(str.toString())
        return res
    }

    fun whitesToBrackets(s: StringBuilder) {
        while (s[0] != '(') {
            s.deleteCharAt(0)
        }
        s.deleteCharAt(0)
        while (s[s.length - 1] != ')') {
            s.deleteCharAt(s.length - 1)
        }
        s.deleteCharAt(s.length - 1)
    }

    fun getVariables(varsStr: StringBuilder): Set<String> {
        spacesDelete(varsStr)
        whitesToBrackets(varsStr)
        val strings = splitString(varsStr, ',')
        val st: MutableSet<String> = HashSet()
        for (str in strings) {
            st.add(str)
        }
        return st
    }

    fun strParsing(str: StringBuilder, vars: Set<String>, coefficients: MutableSet<String>): Term? {
        if (str.isEmpty()) {
            return null
        }
        val name = StringBuilder()
        while (!isCharInString(str[0], "(),")) {
            name.append(str[0])
            str.deleteCharAt(0)
        }
        if (name.isEmpty()) {
            return null
        }
        val t = Term()
        t.value = name.toString()
        if (!vars.contains(t.value)) {
            t.f0 = Term()
            coefficients.add(t.value + "_0")
        }
        if (isCharInString(str[0], "),")) {
            return t
        }
        if (str[0] == '(') {
            str.deleteCharAt(0)
            t.f1 = strParsing(str, vars, coefficients)
            if (t.f1 != null) {
                coefficients.add(t.value + "_1")
            }
            if (str[0] == ',') {
                str.deleteCharAt(0)
                t.f2 = strParsing(str, vars, coefficients)
                if (t.f2 != null) {
                    coefficients.add(t.value + "_2")
                }
            }
            if (str[0] == ')') {
                str.deleteCharAt(0)
                return t
            }
        }
        return null
    }

    fun treeTraversal(t: Term?, currentExpr: String, mp: MutableMap<String?, String>) {
        var currentExpr = currentExpr
        if (t == null) {
            return
        }
        if (currentExpr == "") {
            currentExpr = "1"
        }
        if (t.f0 == null) {
            mp[t.value] = "(+ " + mp[t.value] + " " + currentExpr + ")"
        } else {
            mp["0"] = "(+ " + mp["0"] + " " + "(* " + currentExpr + " " + t.value + "_0" + ")" + ")"
            val tempExp = currentExpr
            currentExpr = "(* " + currentExpr + " " + t.value + "_1" + ")"
            treeTraversal(t.f1, currentExpr, mp)
            currentExpr = tempExp
            currentExpr = "(* " + currentExpr + " " + t.value + "_2" + ")"
            treeTraversal(t.f2, currentExpr, mp)
            currentExpr = tempExp
        }
    }

    fun coefSmt(mp: MutableMap<String?, String>, root: Term?, vars: Set<String>) {
        for (`var` in vars) {
            mp[`var`] = "0"
        }
        mp["0"] = "0"
        val currentExpr = ""
        treeTraversal(root, currentExpr, mp)
    }

    fun addCoefsToSmt(coefficients: Set<String>) {
        for (coefficient in coefficients) {
            smtCreation.append("(declare-fun ").append(coefficient).append(" () Int)\n")
        }
    }

    fun addAssertionsToSmt(coefficients: Set<String>, l: Map<String?, String>, r: Map<String?, String>) {
        for ((key, value) in l) {
            val pR = r[key]
            smtCreation.append("(assert (>= ").append(value).append(" ").append(pR).append("))\n")
        }
        smtCreation.append("(assert (or")
        for ((key, value) in l) {
            val pR = r[key]
            smtCreation.append(" (> ").append(value).append(" ").append(pR).append(")")
        }
        smtCreation.append("))\n")
        smtCreation.append("(assert (and")
        for (coefficient in coefficients) {
            smtCreation.append(" (>= ").append(coefficient)
            if (coefficient[coefficient.length - 1] == '0') {
                smtCreation.append(" 0)")
            } else {
                smtCreation.append(" 1)")
            }
        }
        smtCreation.append("))\n")
        val constToSmtStr: MutableMap<String, String> = HashMap()
        for (coefficient in coefficients) {
            val constName = coefficient.substring(0, coefficient.length - 2)
            constToSmtStr.putIfAbsent(constName, "(or")
            constToSmtStr.computeIfPresent(constName) { k: String?, v: String -> v + " (> " + coefficient + " " + if (coefficient[coefficient.length - 1] == '0') "0)" else "1)" }
        }
        smtCreation.append("(assert (and")
        for ((_, value) in constToSmtStr) {
            smtCreation.append(" ").append(value).append(")")
        }
        smtCreation.append("))\n")
    }

    fun smtResponse(): String {
        Context().use { context ->
            val solver = context.mkSimpleSolver()
            solver.fromFile("smt-solver.smt2")
            return solver.check().toString()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        val varsStrings = StringBuilder(scanner.nextLine())
        val vars = getVariables(varsStrings)
        val rules: MutableList<Rule> = ArrayList()
        val coefficients: MutableSet<String> = HashSet()
        while (scanner.hasNextLine()) {
            val newRule = Rule()
            val rulesStrings = StringBuilder(scanner.nextLine())
            spacesDelete(rulesStrings)
            val rule = splitString(rulesStrings, '=')
            newRule.left = strParsing(StringBuilder(rule[0]), vars, coefficients)
            newRule.right = strParsing(StringBuilder(rule[1]), vars, coefficients)
            rules.add(newRule)
        }
        smtCreation.append("(set-logic QF_NIA)\n")
        addCoefsToSmt(coefficients)
        smtCreation.append("\n")
        for (rule in rules) {
            val l: MutableMap<String?, String> = HashMap()
            val r: MutableMap<String?, String> = HashMap()
            coefSmt(l, rule.left, vars)
            coefSmt(r, rule.right, vars)
            addAssertionsToSmt(coefficients, l, r)
        }
        smtCreation.append("\n(check-sat)\n(get-model)\n")
        try {
            PrintWriter(FileOutputStream("smt-solver.smt2")).use { out -> out.println(smtCreation) }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        println(smtResponse())
    }

    internal class Rule {
        var left: Term? = null
        var right: Term? = null
    }

    class Term {
        var `value` = ""
        var f0: Term? = null
        var f1: Term? = null
        var f2: Term? = null
    }
}

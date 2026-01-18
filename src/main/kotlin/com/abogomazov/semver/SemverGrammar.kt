package com.abogomazov.com.abogomazov.semver

import com.abogomazov.com.abogomazov.bnf.parser.result.ParsingResult
import com.abogomazov.com.abogomazov.bnf.dsl.format
import com.abogomazov.com.abogomazov.bnf.parser.Parser

object SemverGrammar {
    enum class Sym {
        FullSemVer, Core, Major, Minor, Patch, PreRelease, Build, Alphanumeric, Numeric
    }

    private val grammar = format {
        symbol(Sym.FullSemVer) {
            oneOf(
                seq(Sym.Core.ref, "-".l, Sym.PreRelease.ref, "+".l, Sym.Build.ref),
                seq(Sym.Core.ref, "-".l, Sym.PreRelease.ref),
                seq(Sym.Core.ref, "+".l, Sym.Build.ref),
                Sym.Core.ref
            )
        }
        symbol(Sym.Core) { seq(Sym.Major.ref, ".".l, Sym.Minor.ref, ".".l, Sym.Patch.ref) }
        symbol(Sym.PreRelease) {
            oneOf(
                seq(Sym.Alphanumeric.ref, ".".l, Sym.PreRelease.ref),
                Sym.Alphanumeric.ref
            )
        }
        symbol(Sym.Build) {
            oneOf(
                seq(Sym.Alphanumeric.ref, ".".l, Sym.Build.ref),
                Sym.Alphanumeric.ref
            )
        }

        symbol(Sym.Alphanumeric) { "[0-9a-zA-Z-]+".r }
        symbol(Sym.Numeric) { "[0-9]+".r }

        symbol(Sym.Major) { Sym.Numeric.ref }
        symbol(Sym.Minor) { Sym.Numeric.ref }
        symbol(Sym.Patch) { Sym.Numeric.ref }
    }

    fun parse(input: String): ParsingResult {
        return Parser(grammar.rules).parse(Sym.FullSemVer, input)
    }
}
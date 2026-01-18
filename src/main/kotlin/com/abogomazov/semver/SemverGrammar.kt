package com.abogomazov.com.abogomazov.semver

import com.abogomazov.com.abogomazov.bnf.ParsingResult
import com.abogomazov.com.abogomazov.bnf.format

object SemverGrammar {
    enum class Sym {
        FullSemVer, Core, Major, Minor, Patch, PreRelease, Build, Alphanumeric, Numeric
    }

    private val grammar = format {
        symbol(Sym.FullSemVer) {
            oneOf(
                seq(Sym.Core.nt, "-".l, Sym.PreRelease.nt, "+".l, Sym.Build.nt),
                seq(Sym.Core.nt, "-".l, Sym.PreRelease.nt),
                seq(Sym.Core.nt, "+".l, Sym.Build.nt),
                Sym.Core.nt
            )
        }
        symbol(Sym.Core) { seq(Sym.Major.nt, ".".l, Sym.Minor.nt, ".".l, Sym.Patch.nt) }
        symbol(Sym.PreRelease) {
            oneOf(
                seq(Sym.Alphanumeric.nt, ".".l, Sym.PreRelease.nt),
                Sym.Alphanumeric.nt
            )
        }
        symbol(Sym.Build) {
            oneOf(
                seq(Sym.Alphanumeric.nt, ".".l, Sym.Build.nt),
                Sym.Alphanumeric.nt
            )
        }

        symbol(Sym.Alphanumeric) { "[0-9a-zA-Z-]+".r }
        symbol(Sym.Numeric) { "[0-9]+".r }

        symbol(Sym.Major) { Sym.Numeric.nt }
        symbol(Sym.Minor) { Sym.Numeric.nt }
        symbol(Sym.Patch) { Sym.Numeric.nt }
    }

    fun parse(input: String): ParsingResult {
        return grammar.parse(Sym.FullSemVer, input)
    }
}
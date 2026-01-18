package com.abogomazov

data class Semver(
    val major: Long,
    val minor: Long,
    val patch: Long,
    val preRelease: String? = null,
    val build: String? = null,
) {
    companion object {
        enum class Sym {
            FullSemVer, Core, Major, Minor, Patch, PreRelease, Build, Alphanumeric, Numeric
        }

        val grammar = bnf {
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

        fun parse(input: String): Semver {
            val result = grammar.parse(Sym.FullSemVer, input)

            return Semver(
                major = result.long(Sym.Major),
                minor = result.long(Sym.Minor),
                patch = result.long(Sym.Patch),
                preRelease = result.textOrNull(Sym.PreRelease),
                build = result.textOrNull(Sym.PreRelease),
            )
        }
    }
}

fun main() {
    val s = Semver.parse("10.2.3")
    println(s)
}

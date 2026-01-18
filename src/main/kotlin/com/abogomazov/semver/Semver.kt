package com.abogomazov.com.abogomazov.semver

import com.abogomazov.com.abogomazov.semver.SemverGrammar.Sym

data class Semver(
    val major: Long,
    val minor: Long,
    val patch: Long,
    val preRelease: String? = null,
    val build: String? = null,
) {
    companion object {
        fun parse(input: String): Semver =
            SemverGrammar.parse(input).mapTo(Semver::class) {
                Sym.Major to Semver::major
                Sym.Minor to Semver::minor
                Sym.Patch to Semver::patch
                Sym.PreRelease to Semver::preRelease
                Sym.Build to Semver::build
            }
    }
}
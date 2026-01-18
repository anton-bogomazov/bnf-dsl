package com.abogomazov.com.abogomazov.semver

fun main() {
    val s = Semver.parse("10.2.3-rc.1+hello-world")
    println(s)
}

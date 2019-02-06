object Versions {
    const val bintrayPlugin = "1.8.4"
    const val artifactoryPlugin = "4.9.0"

    const val kotlin = "1.3.20"
    const val dokka = "0.9.17"

    const val rxKotlin = "2.3.0"
    const val rxJava = "2.2.6"

    const val bukkit = "1.13.2-R0.1-SNAPSHOT"

    const val kotlinTest = "3.2.1"

    const val ktlint = "0.30.0"
}

object Dependencies {
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    const val bukkit = "org.bukkit:bukkit:${Versions.bukkit}"

    const val kotlinTest = "io.kotlintest:kotlintest-runner-junit5:${Versions.kotlinTest}"

    const val ktlint = "com.github.shyiko:ktlint:${Versions.ktlint}"
}

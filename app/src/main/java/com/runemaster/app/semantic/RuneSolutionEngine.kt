package com.runemaster.app.semantic

enum class RuneFunction {
    RESOURCE,
    COMMUNICATION,
    AGREEMENT,
    MOVEMENT,
    DIRECTION,
    RESULT,
    GROWTH,
    STABILITY,
    BOUNDARY,
    CLARITY,
    RELATIONSHIP,
    HARMONY,
    TRANSITION,
    ENDURANCE,
    PERSON,
    EMOTION,
    UNKNOWN
}

data class RuneRecommendation(
    val rune: String,
    val score: Int,
    val functions: Set<RuneFunction>,
    val reason: String
)

data class FormulaSuggestion(
    val type: String,
    val primary: RuneRecommendation?,
    val supporting: List<RuneRecommendation>,
    val explanation: String
)

data class SemanticSolution(
    val analysis: SemanticResult,
    val requiredFunctions: Set<RuneFunction>,
    val runes: List<RuneRecommendation>,
    val formulas: List<FormulaSuggestion>
)

object RuneSolutionEngine {

    private val runeFunctions = mapOf(
        "FEHU" to setOf(
            RuneFunction.RESOURCE,
            RuneFunction.RESULT
        ),

        "URUZ" to setOf(
            RuneFunction.ENDURANCE,
            RuneFunction.RESOURCE
        ),

        "THURISAZ" to setOf(
            RuneFunction.BOUNDARY
        ),

        "ANSUZ" to setOf(
            RuneFunction.COMMUNICATION,
            RuneFunction.CLARITY
        ),

        "RAIDHO" to setOf(
            RuneFunction.MOVEMENT,
            RuneFunction.DIRECTION
        ),

        "KENAZ" to setOf(
            RuneFunction.CLARITY
        ),

        "GEBO" to setOf(
            RuneFunction.AGREEMENT,
            RuneFunction.RELATIONSHIP
        ),

        "WUNJO" to setOf(
            RuneFunction.HARMONY
        ),

        "HAGALAZ" to setOf(
            RuneFunction.TRANSITION
        ),

        "NAUTHIZ" to setOf(
            RuneFunction.ENDURANCE
        ),

        "ISA" to setOf(
            RuneFunction.STABILITY
        ),

        "JERA" to setOf(
            RuneFunction.RESULT,
            RuneFunction.GROWTH
        ),

        "EIHWAZ" to setOf(
            RuneFunction.STABILITY,
            RuneFunction.ENDURANCE
        ),

        "PERTHRO" to setOf(
            RuneFunction.CLARITY
        ),

        "ALGIZ" to setOf(
            RuneFunction.BOUNDARY
        ),

        "SOWILO" to setOf(
            RuneFunction.DIRECTION,
            RuneFunction.RESULT
        ),

        "TIWAZ" to setOf(
            RuneFunction.DIRECTION,
            RuneFunction.MOVEMENT
        ),

        "BERKANO" to setOf(
            RuneFunction.GROWTH,
            RuneFunction.RELATIONSHIP
        ),

        "EHWAZ" to setOf(
            RuneFunction.MOVEMENT,
            RuneFunction.RELATIONSHIP
        ),

        "MANNAZ" to setOf(
            RuneFunction.PERSON,
            RuneFunction.COMMUNICATION
        ),

        "LAGUZ" to setOf(
            RuneFunction.EMOTION
        ),

        "INGWAZ" to setOf(
            RuneFunction.GROWTH,
            RuneFunction.STABILITY
        ),

        "DAGAZ" to setOf(
            RuneFunction.TRANSITION
        ),

        "OTHALA" to setOf(
            RuneFunction.STABILITY,
            RuneFunction.BOUNDARY
        )
    )

    fun solve(text: String): SemanticSolution {
        val analysis =
            SemanticEngine.analyze(text)

        val required =
            resolveFunctions(analysis)

        val ranked =
            runeFunctions.map { (rune, functions) ->

                val overlap =
                    functions.intersect(required)

                RuneRecommendation(
                    rune = rune,
                    score =
                        overlap.size * 10 +
                        if (
                            overlap.size >= 2
                        ) 4 else 0,
                    functions = overlap,
                    reason =
                        overlap.joinToString {
                            functionName(it)
                        }
                )
            }
            .filter { it.score > 0 }
            .sortedByDescending { it.score }

        return SemanticSolution(
            analysis = analysis,
            requiredFunctions = required,
            runes = ranked,
            formulas = createFormulas(ranked)
        )
    }

    private fun resolveFunctions(
        a: SemanticResult
    ): Set<RuneFunction> {

        val f = mutableSetOf<RuneFunction>()

        if (
            Domain.FINANCE in a.domains ||
            Domain.BUSINESS in a.domains
        ) {
            f += RuneFunction.RESOURCE
            f += RuneFunction.RESULT
        }

        if (
            Domain.FAMILY in a.domains ||
            Domain.RELATIONSHIP in a.domains ||
            Domain.LOVE in a.domains
        ) {
            f += RuneFunction.RELATIONSHIP
        }

        if (
            Domain.DOCUMENTS in a.domains ||
            Intent.AGREE in a.intents
        ) {
            f += RuneFunction.COMMUNICATION
            f += RuneFunction.AGREEMENT
        }

        if (
            ProblemType.CONFLICT in a.problems ||
            ProblemType.COMMUNICATION in a.problems
        ) {
            f += RuneFunction.COMMUNICATION
            f += RuneFunction.CLARITY
        }

        if (
            ProblemType.DELAY in a.problems ||
            ProblemType.STAGNATION in a.problems ||
            Intent.ACCELERATE in a.intents
        ) {
            f += RuneFunction.MOVEMENT
            f += RuneFunction.DIRECTION
        }

        if (
            ProblemType.UNCERTAINTY in a.problems ||
            Intent.CLARIFY in a.intents
        ) {
            f += RuneFunction.CLARITY
        }

        if (
            ProblemType.PRESSURE in a.problems ||
            Intent.PROTECT in a.intents
        ) {
            f += RuneFunction.BOUNDARY
        }

        if (
            Intent.DEVELOP in a.intents ||
            Intent.INCREASE in a.intents
        ) {
            f += RuneFunction.GROWTH
        }

        if (
            Intent.STABILIZE in a.intents ||
            Intent.KEEP in a.intents
        ) {
            f += RuneFunction.STABILITY
        }

        if (
            Intent.CHANGE in a.intents ||
            Intent.END in a.intents
        ) {
            f += RuneFunction.TRANSITION
        }

        if (
            Domain.EMOTIONAL in a.domains ||
            ProblemType.FEAR in a.problems
        ) {
            f += RuneFunction.EMOTION
            f += RuneFunction.PERSON
        }

        if (
            f.isEmpty()
        ) {
            f += RuneFunction.CLARITY
            f += RuneFunction.PERSON
        }

        return f
    }

    private fun createFormulas(
        runes: List<RuneRecommendation>
    ): List<FormulaSuggestion> {

        if (runes.isEmpty()) return emptyList()

        return listOf(
            smartFormula("КОМПАКТНЫЙ", runes, 3),
            smartFormula("СБАЛАНСИРОВАННЫЙ", runes, 5),
            smartFormula("РАСШИРЕННЫЙ", runes, 7)
        )
    }

    private fun smartFormula(
        title: String,
        runes: List<RuneRecommendation>,
        maxCount: Int
    ): FormulaSuggestion {

        val primary = runes.first()
        val chosen = mutableListOf(primary)
        val covered = primary.functions.toMutableSet()

        runes.drop(1).forEach { candidate ->
            if (chosen.size >= maxCount) return@forEach

            val addsSomething =
                candidate.functions.any { it !in covered }

            if (addsSomething) {
                chosen += candidate
                covered += candidate.functions
            }
        }

        if (chosen.size < maxCount) {
            runes.forEach { candidate ->
                if (
                    chosen.size < maxCount &&
                    candidate !in chosen
                ) {
                    chosen += candidate
                }
            }
        }

        return FormulaSuggestion(
            type = title,
            primary = primary,
            supporting = chosen.drop(1),
            explanation =
                "Главная руна закрывает центральные функции запроса. " +
                "Вспомогательные подобраны так, чтобы добавлять разные " +
                "функции и по возможности не дублировать друг друга."
        )
    }

    private fun functionName(
        f: RuneFunction
    ): String =
        when (f) {
            RuneFunction.RESOURCE ->
                "ресурс"
            RuneFunction.COMMUNICATION ->
                "коммуникация"
            RuneFunction.AGREEMENT ->
                "согласование"
            RuneFunction.MOVEMENT ->
                "движение процесса"
            RuneFunction.DIRECTION ->
                "направление"
            RuneFunction.RESULT ->
                "результат"
            RuneFunction.GROWTH ->
                "развитие"
            RuneFunction.STABILITY ->
                "устойчивость"
            RuneFunction.BOUNDARY ->
                "границы"
            RuneFunction.CLARITY ->
                "ясность"
            RuneFunction.RELATIONSHIP ->
                "взаимодействие"
            RuneFunction.HARMONY ->
                "гармонизация"
            RuneFunction.TRANSITION ->
                "переход"
            RuneFunction.ENDURANCE ->
                "стойкость"
            RuneFunction.PERSON ->
                "позиция человека"
            RuneFunction.EMOTION ->
                "эмоциональная составляющая"
            else ->
                "неопределённая функция"
        }
}

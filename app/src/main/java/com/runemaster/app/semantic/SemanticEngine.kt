package com.runemaster.app.semantic

enum class Domain {
    FAMILY,
    RELATIONSHIP,
    LOVE,
    FINANCE,
    WORK,
    CAREER,
    BUSINESS,
    DOCUMENTS,
    EDUCATION,
    HOME,
    SOCIAL,
    EMOTIONAL,
    CREATIVITY,
    TRAVEL,
    CHANGE,
    PROTECTION,
    LEGAL,
    UNKNOWN
}

enum class Intent {
    GET,
    FIND,
    INCREASE,
    DECREASE,
    KEEP,
    RESTORE,
    IMPROVE,
    END,
    REMOVE,
    PROTECT,
    CLARIFY,
    ACCELERATE,
    CHANGE,
    AGREE,
    COMMUNICATE,
    DEVELOP,
    STABILIZE,
    UNKNOWN
}

enum class ProblemType {
    LACK,
    LOSS,
    CONFLICT,
    DELAY,
    BLOCK,
    INSTABILITY,
    EXCESS,
    UNCERTAINTY,
    REJECTION,
    STAGNATION,
    SEPARATION,
    COMMUNICATION,
    FEAR,
    PRESSURE,
    FAILURE,
    CHANGE,
    UNKNOWN
}

data class IntentRelation(
    val intent: Intent,
    val phrase: String,
    val negated: Boolean,
    val nearbyEntities: Set<String>
)

data class SemanticResult(
    val original: String,
    val domains: Set<Domain>,
    val intents: Set<Intent>,
    val problems: Set<ProblemType>,
    val entities: Set<String>,
    val tokens: Set<String>,
    val negatedTokens: Set<String>,
    val intentRelations: List<IntentRelation> = emptyList(),
    val confidence: Float
)

object SemanticEngine {

    private val domains = mapOf(
        Domain.FAMILY to listOf(
            "семья","семей","муж","жена","супруг",
            "супруга","дети","ребенок","ребёнок",
            "сын","дочь","родители","мама","папа",
            "домочадцы","родственники"
        ),

        Domain.RELATIONSHIP to listOf(
            "отношения","отношен","партнер","партнёр",
            "парень","девушка","бывший","бывшая",
            "любимый","любимая","жених","невеста",
            "суженый","суженный"
        ),

        Domain.LOVE to listOf(
            "любовь","любви","влюб","роман",
            "личная жизнь","замуж","жениться",
            "жених","невеста","сужен"
        ),

        Domain.FINANCE to listOf(
            "деньги","денег","финанс","доход",
            "зарплат","долг","долги","кредит",
            "ипотек","прибыль","расход","накоп",
            "богат","бедност","безденеж","оплата"
        ),

        Domain.WORK to listOf(
            "работа","работе","работу","ваканси",
            "работодатель","начальник","начальница",
            "шеф","босс","коллег","увольнен",
            "увольнение","трудоустрой"
        ),

        Domain.CAREER to listOf(
            "карьер","повышен","должност",
            "службе","профессиональ","рост",
            "продвижен"
        ),

        Domain.BUSINESS to listOf(
            "бизнес","клиент","заказчик",
            "контрагент","покупатель","продаж",
            "сделка","прибыль","заказ","партнер",
            "партнёр","компания"
        ),

        Domain.DOCUMENTS to listOf(
            "договор","контракт","документ",
            "бумаг","соглашен","подпис",
            "оферт","допник","согласован"
        ),

        Domain.EDUCATION to listOf(
            "учеб","учёб","школ","институт",
            "университет","экзамен","курс",
            "диплом","знания","учиться"
        ),

        Domain.HOME to listOf(
            "дом","квартир","жилье","жильё",
            "недвижим","переезд","ремонт"
        ),

        Domain.SOCIAL to listOf(
            "друзья","друг","подруга","компания",
            "коллектив","окружение","общество"
        ),

        Domain.EMOTIONAL to listOf(
            "страшно","боюсь","тревог","стресс",
            "устал","устала","сил нет","апат",
            "злость","ревност","обида","одиноч"
        ),

        Domain.CREATIVITY to listOf(
            "творч","идея","вдохнов",
            "рисовать","писать","музык",
            "проект"
        ),

        Domain.TRAVEL to listOf(
            "дорога","поездк","путешеств",
            "ехать","лететь","поехать"
        ),

        Domain.CHANGE to listOf(
            "перемен","изменить","поменять",
            "новый этап","начать заново"
        ),

        Domain.PROTECTION to listOf(
            "защит","безопас","границ",
            "давление","угроз"
        ),

        Domain.LEGAL to listOf(
            "суд","юрист","адвокат","иск",
            "закон","право","юрид",
            "претенз","спор"
        )
    )

    private val intents = mapOf(
        Intent.GET to listOf(
            "получить","добиться","обрести"
        ),
        Intent.FIND to listOf(
            "найти","искать","ищу","встретить"
        ),
        Intent.INCREASE to listOf(
            "увеличить","больше","повысить",
            "рост","нарастить"
        ),
        Intent.DECREASE to listOf(
            "уменьшить","снизить","меньше"
        ),
        Intent.KEEP to listOf(
            "сохранить","удержать","не потерять"
        ),
        Intent.RESTORE to listOf(
            "вернуть","восстановить","возобновить"
        ),
        Intent.IMPROVE to listOf(
            "улучшить","наладить","исправить"
        ),
        Intent.END to listOf(
            "закончить","прекратить","завершить"
        ),
        Intent.REMOVE to listOf(
            "убрать","избавиться","устранить"
        ),
        Intent.PROTECT to listOf(
            "защитить","обезопасить","оградить"
        ),
        Intent.CLARIFY to listOf(
            "понять","выяснить","прояснить",
            "разобраться"
        ),
        Intent.ACCELERATE to listOf(
            "ускорить","быстрее","поторопить"
        ),
        Intent.CHANGE to listOf(
            "изменить","поменять","сменить"
        ),
        Intent.AGREE to listOf(
            "договориться","согласовать",
            "подписать","соглашение"
        ),
        Intent.COMMUNICATE to listOf(
            "поговорить","обсудить","переговоры",
            "общение","объяснить"
        ),
        Intent.DEVELOP to listOf(
            "развить","развивать","вырасти"
        ),
        Intent.STABILIZE to listOf(
            "стабилизировать","укрепить",
            "устойчивость","нормализовать"
        )
    )

    private val problems = mapOf(
        ProblemType.LACK to listOf(
            "нет","не хватает","мало",
            "недостаток","дефицит","без"
        ),

        ProblemType.LOSS to listOf(
            "потерял","потеряла","потеря",
            "лишился","лишилась","ушел",
            "ушёл","ушла"
        ),

        ProblemType.CONFLICT to listOf(
            "конфликт","ссор","руга",
            "скандал","война","вражд",
            "задолбал","задолбала","достал",
            "достала"
        ),

        ProblemType.DELAY to listOf(
            "медленно","тянут","затяг",
            "долго","завис","тормоз",
            "волокит","морозится","морозятся"
        ),

        ProblemType.BLOCK to listOf(
            "не получается","не могу",
            "препятств","мешает","блок",
            "не дают"
        ),

        ProblemType.INSTABILITY to listOf(
            "нестабиль","то есть то нет",
            "скачет","неустойчив"
        ),

        ProblemType.EXCESS to listOf(
            "слишком много","перерасход",
            "все трачу","всё трачу",
            "улетает","сливаю"
        ),

        ProblemType.UNCERTAINTY to listOf(
            "не знаю","не понимаю",
            "неясно","сомневаюсь","тупик",
            "запутал"
        ),

        ProblemType.REJECTION to listOf(
            "не хотят","отказ","отказыва",
            "не принимает","не принимают",
            "не соглас"
        ),

        ProblemType.STAGNATION to listOf(
            "застой","стоит на месте",
            "не двигается","болото"
        ),

        ProblemType.SEPARATION to listOf(
            "расстав","развод","разрыв",
            "разошлись"
        ),

        ProblemType.COMMUNICATION to listOf(
            "не понимаем","не слышит",
            "не слышат","молчит",
            "не разговариваем"
        ),

        ProblemType.FEAR to listOf(
            "боюсь","страх","страшно",
            "опасаюсь"
        ),

        ProblemType.PRESSURE to listOf(
            "давит","давление","принужд",
            "шантаж","угрож"
        ),

        ProblemType.FAILURE to listOf(
            "неудач","провал","сорвал",
            "не выходит"
        )
    )

    private val entityGroups = mapOf(
        "CONTRACT" to listOf(
            "договор","контракт","соглашение",
            "оферта","допник","допсоглашение"
        ),

        "CLIENT" to listOf(
            "клиент","заказчик","контрагент",
            "покупатель"
        ),

        "JOB" to listOf(
            "работа","вакансия","должность"
        ),

        "MONEY" to listOf(
            "деньги","доход","зарплата",
            "прибыль","накопления"
        ),

        "PARTNER" to listOf(
            "муж","жена","парень","девушка",
            "партнер","партнёр","жених",
            "невеста","суженый","суженный"
        ),

        "CHILD" to listOf(
            "сын","дочь","ребенок","ребёнок",
            "дети"
        ),

        "HOME" to listOf(
            "дом","квартира","жилье","жильё"
        )
    )

    private val slang = mapOf(
        "полная жопа" to "серьезная проблема",
        "полный пиздец" to "серьезная проблема",
        "капец" to "проблема",
        "хана" to "проблема",
        "задолбал" to "конфликт",
        "задолбала" to "конфликт",
        "достал" to "конфликт",
        "достала" to "конфликт",
        "морозится" to "затягивает",
        "морозятся" to "затягивают"
    )

    private val negations = setOf(
        "не","нет","никогда","ни"
    )

    fun analyze(source: String): SemanticResult {
        var normalized = normalize(source)

        slang.forEach { (from, to) ->
            normalized =
                normalized.replace(from, "$from $to")
        }

        val words = normalized
            .split(" ")
            .filter { it.isNotBlank() }

        val tokenSet = words.toSet()

        val negative = mutableSetOf<String>()

        words.forEachIndexed { index, word ->
            if (word in negations) {
                words
                    .drop(index + 1)
                    .take(3)
                    .forEach { negative += it }
            }
        }

        val detectedDomains =
            domains.filterValues {
                matches(normalized, tokenSet, it)
            }.keys

        val detectedIntents =
            intents.filterValues {
                matches(normalized, tokenSet, it)
            }.keys

        val detectedProblems =
            problems.filterValues {
                matches(normalized, tokenSet, it)
            }.keys

        val detectedEntities =
            entityGroups.filterValues {
                matches(normalized, tokenSet, it)
            }.keys

        val relations =
            detectIntentRelations(
                normalized,
                words
            )

        val positiveIntents =
            relations
                .filter { !it.negated }
                .map { it.intent }
                .toSet()

        val finalIntents =
            if (positiveIntents.isNotEmpty())
                positiveIntents
            else
                detectedIntents

        val evidence =
            detectedDomains.size +
            detectedIntents.size +
            detectedProblems.size +
            detectedEntities.size

        val confidence =
            (0.25f + evidence * 0.12f)
                .coerceAtMost(0.98f)

        return SemanticResult(
            original = source,
            domains =
                detectedDomains.ifEmpty {
                    setOf(Domain.UNKNOWN)
                },
            intents =
                finalIntents.ifEmpty {
                    setOf(Intent.UNKNOWN)
                },
            problems =
                detectedProblems.ifEmpty {
                    setOf(ProblemType.UNKNOWN)
                },
            entities = detectedEntities,
            tokens = tokenSet,
            negatedTokens = negative,
            intentRelations = relations,
            confidence = confidence
        )
    }

    private fun detectIntentRelations(
        text: String,
        words: List<String>
    ): List<IntentRelation> {

        val result =
            mutableListOf<IntentRelation>()

        intents.forEach { (intent, variants) ->

            variants.forEach { rawVariant ->

                val variant = normalize(rawVariant)

                var start = text.indexOf(variant)

                while (start >= 0) {

                    val before =
                        text.substring(
                            0,
                            start
                        )
                        .trim()
                        .split(" ")
                        .takeLast(4)

                    val negated =
                        before.any {
                            it in setOf(
                                "не",
                                "никогда",
                                "ни"
                            )
                        }

                    val from =
                        (start - 55)
                            .coerceAtLeast(0)

                    val to =
                        (start + variant.length + 55)
                            .coerceAtMost(text.length)

                    val context =
                        text.substring(from, to)

                    val nearby =
                        entityGroups
                            .filterValues {
                                entityVariants ->
                                entityVariants.any {
                                    ev ->
                                    context.contains(
                                        normalize(ev)
                                    )
                                }
                            }
                            .keys

                    result +=
                        IntentRelation(
                            intent = intent,
                            phrase = variant,
                            negated = negated,
                            nearbyEntities = nearby
                        )

                    start =
                        text.indexOf(
                            variant,
                            start + variant.length
                        )
                }
            }
        }

        return result
            .distinctBy {
                Triple(
                    it.intent,
                    it.phrase,
                    it.negated
                )
            }
    }

    private fun matches(
        text: String,
        tokens: Set<String>,
        variants: List<String>
    ): Boolean {
        return variants.any { variant ->
            val v = normalize(variant)

            text.contains(v) ||
                tokens.any { token ->
                    token.length >= 4 &&
                    v.length >= 4 &&
                    (
                        token.startsWith(
                            v.take(
                                minOf(5, v.length)
                            )
                        ) ||
                        v.startsWith(
                            token.take(
                                minOf(5, token.length)
                            )
                        )
                    )
                }
        }
    }

    private fun normalize(value: String): String =
        value
            .lowercase()
            .replace('ё','е')
            .replace(
                Regex("[^а-яa-z0-9\\s-]"),
                " "
            )
            .replace(Regex("\\s+"), " ")
            .trim()
}

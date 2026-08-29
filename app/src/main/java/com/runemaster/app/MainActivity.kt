package com.runemaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF080706)
private val Gold = Color(0xFFD6A94C)
private val GoldLight = Color(0xFFF5D982)
private val CardBg = Color(0xFF17130D)
private val Text = Color(0xFFF3E8CC)
private val Muted = Color(0xFFBBAE8B)

data class RuneInfo(
    val symbol: String,
    val name: String,
    val russian: String,
    val keywords: String,
    val upright: String,
    val reversed: String,
    val practice: String
)

private val runes = listOf(
    RuneInfo("ᚠ","FEHU","Феху","деньги, имущество, ресурсы, доход",
        "Движимое имущество, материальный ресурс, результат труда, обмен и способность распоряжаться имеющимся.",
        "В современных системах: задержка результата, потери, неверное обращение с ресурсами. Исторически система перевёрнутых значений не универсальна.",
        "Используется как символическая тема материальных ресурсов, результата труда и управления имеющимся."),

    RuneInfo("ᚢ","URUZ","Уруз","сила, энергия, здоровье, выносливость",
        "Сила, жизненность, выносливость, способность пройти изменение.",
        "Ослабление, истощение или неправильно направленная сила в современных гадательных системах.",
        "Применяется в практиках, где намерение связано с внутренним ресурсом, стойкостью и изменением."),

    RuneInfo("ᚦ","THURISAZ","Турисаз","границы, конфликт, защита, препятствие",
        "Сила противодействия, граница, конфликт, необходимость осторожного решения.",
        "Неудачно направленное противодействие, уязвимость или необдуманная конфронтация.",
        "Требует особенно чётко сформулированного намерения; не следует механически использовать её для любого запроса о защите."),

    RuneInfo("ᚨ","ANSUZ","Ансуз","общение, речь, знания, переговоры",
        "Речь, сообщение, понимание, совет, передача знания.",
        "Недопонимание, искажение сообщения, неудачная коммуникация.",
        "Подходит как символическая составляющая вопросов общения, обучения, собеседований и переговоров."),

    RuneInfo("ᚱ","RAIDHO","Райдо","дорога, движение, процесс, путешествие",
        "Путь, движение, последовательный процесс и согласованное направление.",
        "Задержка, неверный маршрут или нарушение последовательности.",
        "Используется для задач, которые важно провести от исходной точки к определённому результату."),

    RuneInfo("ᚲ","KENAZ","Кеназ","ясность, творчество, навык, понимание",
        "Свет, обнаружение, умение, ремесло, ясность и творческий процесс.",
        "Недостаток ясности, угасание интереса или невозможность увидеть решение.",
        "Применяется в задачах обучения, творчества, прояснения ситуации и развития навыка."),

    RuneInfo("ᚷ","GEBO","Гебо","отношения, партнёрство, обмен, подарок",
        "Дар, взаимность, обмен и отношения сторон.",
        "У руны нет естественного графического переворота; отдельное reversed-значение относится только к некоторым современным школам.",
        "Полезна как символ взаимности и сбалансированного взаимодействия."),

    RuneInfo("ᚹ","WUNJO","Вуньо","радость, гармония, удовлетворение",
        "Радость, согласие, принадлежность и удовлетворение.",
        "Разочарование, разлад или утрата чувства согласованности.",
        "Используется в практиках, ориентированных на гармонизацию и осознание желаемого состояния."),

    RuneInfo("ᚺ","HAGALAZ","Хагалаз","перемены, кризис, разрушение старого",
        "Град, внезапное нарушение привычного порядка, неконтролируемое изменение.",
        "Обычно отдельное перевёрнутое положение не используется.",
        "В современной практике рассматривается осторожно: как образ неизбежного изменения, а не универсальный способ «разрушить препятствие»."),

    RuneInfo("ᚾ","NAUTHIZ","Наутиз","нужда, ограничения, терпение",
        "Нужда, необходимость, ограничение и действие в условиях недостатка.",
        "В некоторых современных системах — усугубление ограничения или неверная оценка потребностей.",
        "Полезна прежде всего для анализа того, что действительно необходимо и какие ограничения присутствуют."),

    RuneInfo("ᛁ","ISA","Иса","остановка, пауза, концентрация",
        "Лёд, неподвижность, сохранение состояния, концентрация.",
        "Графически перевёрнутое положение не различается.",
        "Используется как образ остановки и фиксации; эффект зависит от контекста задачи."),

    RuneInfo("ᛃ","JERA","Йера","результат, цикл, урожай, время",
        "Год, урожай, закономерный результат последовательной работы.",
        "Графически отдельного перевёрнутого положения обычно нет.",
        "Подходит к задачам постепенного развития, где результат предполагает время и последовательные действия."),

    RuneInfo("ᛇ","EIHWAZ","Эйваз","устойчивость, переход, трансформация",
        "Тис, стойкость, ось перехода и способность выдерживать изменение.",
        "Отдельная reversed-трактовка не является общепринятой.",
        "В современных практиках применяется как символ устойчивости в процессе изменения."),

    RuneInfo("ᛈ","PERTHRO","Пертро","тайна, неизвестность, шанс",
        "Неизвестное, жребий, скрытый фактор и открывающаяся возможность.",
        "Скрытая информация, неопределённость, неудобное раскрытие неизвестного.",
        "Уместна прежде всего как тема неизвестных факторов и исследования ситуации."),

    RuneInfo("ᛉ","ALGIZ","Альгиз","защита, границы, осторожность",
        "Охрана, внимательность, границы и защищённая позиция.",
        "Уязвимость, ослабленные границы или недостаток осторожности.",
        "В современных практиках часто используется как символ границ и охраны."),

    RuneInfo("ᛊ","SOWILO","Соулу","успех, цель, энергия, ясность",
        "Солнце, целостность, направление к цели и ясность.",
        "У большинства школ отдельное перевёрнутое положение отсутствует.",
        "Используется как образ ясной цели, направленности и завершённости."),

    RuneInfo("ᛏ","TIWAZ","Тейваз","цель, воля, справедливость, победа",
        "Целенаправленность, принцип, ответственность и готовность действовать.",
        "Потеря направления, недостаток решимости или конфликт принципов.",
        "Может обозначать функцию направления и дисциплины в формуле."),

    RuneInfo("ᛒ","BERKANO","Беркана","семья, рост, забота, развитие",
        "Рост, рождение нового, забота и постепенное развитие.",
        "Затруднённое развитие, чрезмерная опека или проблемы роста.",
        "Применяется как символ выращивания и поддержания развивающегося процесса."),

    RuneInfo("ᛖ","EHWAZ","Эваз","движение, сотрудничество, изменения",
        "Движение благодаря взаимодействию, доверие и согласованная перемена.",
        "Несогласованность, задержка перемен или недостаток доверия.",
        "Подходит для задач совместного продвижения и перехода к новому состоянию."),

    RuneInfo("ᛗ","MANNAZ","Манназ","человек, общество, команда, самопознание",
        "Человек в отношениях с другими людьми, социальная среда и самопонимание.",
        "Изоляция, трудности взаимодействия или искажённое представление о себе.",
        "Используется для задач, где центральным фактором является сам человек и его взаимодействие с обществом."),

    RuneInfo("ᛚ","LAGUZ","Лагуз","интуиция, эмоции, поток, чувства",
        "Вода, течение, эмоциональность, интуитивное восприятие.",
        "Эмоциональная путаница, потеря направления или чрезмерная зависимость от ощущения.",
        "Применяется для исследования эмоционального процесса и способности адаптироваться."),

    RuneInfo("ᛜ","INGWAZ","Ингваз","созревание, потенциал, завершение",
        "Накопленный потенциал, созревание и переход к завершению.",
        "Графически отдельного перевёрнутого положения обычно не выделяют.",
        "Полезна как символ накопления ресурса перед завершением процесса."),

    RuneInfo("ᛞ","DAGAZ","Дагаз","прорыв, день, изменение, новый этап",
        "День, переломный момент, изменение состояния и новый этап.",
        "Отдельного перевёрнутого положения обычно нет.",
        "Применяется как образ перехода от одного состояния к другому."),

    RuneInfo("ᛟ","OTHALA","Отала","дом, семья, наследие, имущество",
        "Наследуемое имущество, дом, принадлежность, родовое и культурное наследие.",
        "В современных системах: проблема принадлежности, собственности или семейных границ.",
        "Подходит как символ дома, устойчивой основы, принадлежности и наследуемых ресурсов.")
)


data class ProblemConcept(
    val id: String,
    val title: String,
    val domain: String,
    val phrases: List<String>,
    val runeNames: List<String>,
    val explanation: String
)

data class DetectedProblem(
    val concept: ProblemConcept,
    val confidence: Int,
    val matchedWords: List<String>
)

data class AnalysisResult(
    val original: String,
    val problems: List<DetectedProblem>
)

private val problemDictionary = listOf(

    ProblemConcept(
        "money_lack",
        "Недостаток денег",
        "Финансы",
        listOf(
            "нет денег","мало денег","не хватает денег","без денег",
            "безденежье","денежная яма","финансовая яма","долги",
            "долгов","кредит","кредиты","нищий","бедность",
            "финансы поют романсы","с деньгами плохо",
            "проблемы с деньгами","денежные проблемы",
            "полная жопа с деньгами","жопа с деньгами"
        ),
        listOf("FEHU","JERA","SOWILO"),
        "Запрос связан с материальными ресурсами, их получением, сохранением или постепенным увеличением."
    ),

    ProblemConcept(
        "job_search",
        "Поиск работы",
        "Работа",
        listOf(
            "ищу работу","найти работу","поиск работы","новая работа",
            "нужна работа","нет работы","без работы","безработный",
            "безработная","трудоустроиться","устроиться на работу",
            "сменить работу","хочу другую работу","нормальную работу"
        ),
        listOf("RAIDHO","ANSUZ","FEHU","TIWAZ","JERA"),
        "Задача требует движения процесса, коммуникации с работодателями и материального результата."
    ),

    ProblemConcept(
        "career_growth",
        "Продвижение по работе",
        "Карьера",
        listOf(
            "карьерный рост","повышение","повысили","повысили бы",
            "продвижение по службе","повышение по службе",
            "повышение зарплаты","вырасти в должности",
            "новая должность","карьера","начальником",
            "хочу повышение","хочу должность"
        ),
        listOf("TIWAZ","SOWILO","ANSUZ","JERA"),
        "Выделяются целенаправленность, проявление компетентности, коммуникация и последовательное получение результата."
    ),

    ProblemConcept(
        "boss_conflict",
        "Конфликт с руководством",
        "Работа",
        listOf(
            "начальник достал","начальница достала","шеф достал",
            "босс достал","начальник задолбал","начальница задолбала",
            "начальник сжирает","начальница сжирает",
            "конфликт с начальником","конфликт с начальницей",
            "проблемы с начальством","давит начальник","давит начальница",
            "токсичный начальник","токсичная начальница"
        ),
        listOf("ANSUZ","ALGIZ","TIWAZ","MANNAZ"),
        "В запросе важны коммуникация, личные границы, социальное взаимодействие и ясная позиция."
    ),

    ProblemConcept(
        "family_conflict",
        "Конфликты в семье",
        "Семья",
        listOf(
            "проблемы в семье","семейные проблемы","ссоры в семье",
            "ругаемся","постоянно ругаемся","скандалы","скандал",
            "дома ругаются","дома ругаемся","конфликт в семье",
            "семья разваливается","семья рушится",
            "дома полный капец","дома полная жопа",
            "в семье полная жопа","жопа в семье"
        ),
        listOf("GEBO","ANSUZ","WUNJO","BERKANO","OTHALA"),
        "Для этой задачи важнее не одно слово «семья», а функции взаимности, общения, гармонизации и устойчивой семейной основы."
    ),

    ProblemConcept(
        "relationship_conflict",
        "Проблемы в отношениях",
        "Отношения",
        listOf(
            "проблемы в отношениях","ссоры с мужем","ссоры с женой",
            "ругаемся с мужем","ругаемся с женой","парень отдалился",
            "девушка отдалилась","муж отдалился","жена отдалилась",
            "отношения рушатся","отношения разваливаются",
            "не понимаем друг друга","нет взаимопонимания",
            "с мужиком полная жопа","с мужем полная жопа",
            "с женой полная жопа","в отношениях полная жопа"
        ),
        listOf("GEBO","ANSUZ","WUNJO","EHWAZ"),
        "Основными функциями становятся взаимность, коммуникация, согласованность и желаемое качество взаимодействия."
    ),

    ProblemConcept(
        "find_partner",
        "Поиск партнёра",
        "Отношения",
        listOf(
            "найти жениха","встретить жениха","ищу жениха",
            "найти суженого","встретить суженого","суженый",
            "суженный","будущий муж","найти мужа","встретить мужа",
            "найти парня","встретить мужчину","найти мужчину",
            "найти девушку","встретить девушку","найти жену",
            "встретить жену","хочу замуж","выйти замуж",
            "хочу жениться","найти любовь","встретить любовь",
            "вторая половина","найти вторую половину",
            "устроить личную жизнь","хочу отношения"
        ),
        listOf("GEBO","EHWAZ","WUNJO","RAIDHO"),
        "Запрос трактуется как создание возможностей для взаимного знакомства и развития партнёрского взаимодействия, а не как гарантия появления конкретного человека."
    ),

    ProblemConcept(
        "breakup",
        "Расставание",
        "Отношения",
        listOf(
            "расстались","расставание","ушел муж","ушёл муж",
            "ушла жена","бросил парень","бросила девушка",
            "меня бросили","разрыв отношений","развод",
            "хочу пережить расставание","после развода"
        ),
        listOf("DAGAZ","EIHWAZ","MANNAZ","WUNJO"),
        "Здесь выделяется переход к новому состоянию, устойчивость в период перемены и возвращение внимания к собственной позиции."
    ),

    ProblemConcept(
        "home_stability",
        "Дом и семейная устойчивость",
        "Семья",
        listOf(
            "укрепить семью","сохранить семью","семейное благополучие",
            "мир в семье","гармония в семье","счастье в семье",
            "уют дома","домашний уют","защитить дом","семейный очаг"
        ),
        listOf("OTHALA","BERKANO","GEBO","WUNJO"),
        "Запрос связан с домом, принадлежностью, поддержкой развития отношений и взаимностью."
    ),

    ProblemConcept(
        "business",
        "Бизнес и собственное дело",
        "Финансы",
        listOf(
            "бизнес","свой бизнес","свое дело","своё дело",
            "клиенты","нет клиентов","найти клиентов","продажи",
            "увеличить продажи","прибыль","доход бизнеса",
            "развить бизнес","развитие бизнеса"
        ),
        listOf("FEHU","JERA","ANSUZ","RAIDHO","SOWILO"),
        "Задача разделяется на ресурс, устойчивый цикл результата, коммуникацию с людьми и движение процесса."
    ),

    ProblemConcept(
        "study",
        "Учёба и знания",
        "Обучение",
        listOf(
            "учеба","учёба","экзамен","экзамены","сдать экзамен",
            "обучение","учиться","знания","память","университет",
            "институт","школа","курс","курсы"
        ),
        listOf("ANSUZ","KENAZ","JERA","TIWAZ"),
        "Основными функциями здесь являются получение знания, понимание, систематическая работа и направленность."
    ),

    ProblemConcept(
        "uncertainty",
        "Неясность ситуации",
        "Состояние",
        listOf(
            "не знаю что делать","не понимаю что делать","в тупике",
            "запутался","запуталась","ничего не понимаю",
            "не могу решить","сложно выбрать","нужна ясность",
            "не понимаю ситуацию"
        ),
        listOf("KENAZ","PERTHRO","ANSUZ","MANNAZ"),
        "Сначала полезнее исследовать неизвестные факторы и сформировать ясное представление о ситуации, а уже затем выбирать направление действия."
    )
)

private fun normalizeText(value: String): String {
    return value
        .lowercase()
        .replace('ё', 'е')
        .replace(Regex("[^а-яa-z0-9\\s-]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun stemRu(word: String): String {
    val w = normalizeText(word)
    val endings = listOf(
        "иями","ями","ами","его","ого","ему","ому","ыми","ими",
        "ией","ей","ой","ий","ый","ая","яя","ое","ее","ам","ям",
        "ах","ях","ом","ем","ов","ев","ую","юю","а","я","ы","и",
        "у","ю","е","о"
    )
    for (e in endings) {
        if (w.length > e.length + 3 && w.endsWith(e)) {
            return w.dropLast(e.length)
        }
    }
    return w
}

private fun phraseScore(text: String, phrase: String): Int {
    val t = normalizeText(text)
    val p = normalizeText(phrase)

    if (t.contains(p)) return 100

    val textWords = t.split(" ").filter { it.length > 2 }.map(::stemRu).toSet()
    val phraseWords = p.split(" ").filter { it.length > 2 }.map(::stemRu)

    if (phraseWords.isEmpty()) return 0

    val hits = phraseWords.count { pw ->
        textWords.any { tw ->
            tw == pw || (tw.length >= 5 && pw.length >= 5 &&
                (tw.startsWith(pw.take(5)) || pw.startsWith(tw.take(5))))
        }
    }

    return ((hits.toFloat() / phraseWords.size) * 80).toInt()
}

private fun analyzeRequest(text: String): AnalysisResult {
    val normalized = normalizeText(text)

    val detected = problemDictionary.mapNotNull { concept ->
        val scored = concept.phrases
            .map { it to phraseScore(normalized, it) }
            .filter { it.second >= 55 }
            .sortedByDescending { it.second }

        if (scored.isEmpty()) null
        else DetectedProblem(
            concept = concept,
            confidence = scored.first().second,
            matchedWords = scored.take(3).map { it.first }
        )
    }.sortedWith(
        compareByDescending<DetectedProblem> { it.confidence }
            .thenBy { it.concept.domain }
    )

    return AnalysisResult(text, detected)
}

private fun recommendedRunes(result: AnalysisResult): List<Pair<RuneInfo, Int>> {
    val scores = mutableMapOf<String, Int>()

    result.problems.forEachIndexed { index, detected ->
        detected.concept.runeNames.forEachIndexed { runeIndex, runeName ->
            val weight = when (runeIndex) {
                0 -> 6
                1 -> 4
                2 -> 3
                else -> 2
            }
            scores[runeName] = (scores[runeName] ?: 0) + weight +
                    if (index == 0) 2 else 0
        }
    }

    return scores.entries
        .sortedByDescending { it.value }
        .mapNotNull { e ->
            runes.find { it.name == e.key }?.let { it to e.value }
        }
        .take(7)
}

sealed class Screen {
    data object Home : Screen()
    data object Runes : Screen()
    data class RuneDetail(val rune: RuneInfo) : Screen()
    data class Analysis(val text: String) : Screen()
    data class Placeholder(val title: String, val description: String) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Gold,
                    secondary = GoldLight,
                    background = Bg,
                    surface = CardBg,
                    onBackground = Text,
                    onSurface = Text
                )
            ) {
                RuneMasterApp()
            }
        }
    }
}

@Composable
fun RuneMasterApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    BackHandler(screen !is Screen.Home) {
        screen = Screen.Home
    }

    when (val current = screen) {
        Screen.Home -> HomeScreen { screen = it }
        Screen.Runes -> RuneListScreen(
            onBack = { screen = Screen.Home },
            onRune = { screen = Screen.RuneDetail(it) }
        )
        is Screen.RuneDetail -> RuneDetailScreen(
            current.rune,
            onBack = { screen = Screen.Runes }
        )
        is Screen.Analysis -> AnalysisScreen(
            text = current.text,
            onBack = { screen = Screen.Home },
            onRune = { screen = Screen.RuneDetail(it) }
        )
        is Screen.Placeholder -> PlaceholderScreen(
            current.title,
            current.description,
            onBack = { screen = Screen.Home }
        )
    }
}

@Composable
private fun AppHeader(title: String, subtitle: String? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("ᛉ", color = GoldLight, fontSize = 46.sp)
        Text(
            title,
            color = GoldLight,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )
        subtitle?.let {
            Text(it, color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HomeScreen(navigate: (Screen) -> Unit) {
    var query by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            AppHeader("РУНОЛОГ", "Старший футарк • рабочий справочник")
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Опишите ситуацию") },
                placeholder = {
                    Text("Например: проблемы с работой и деньгами")
                },
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotBlank()) navigate(Screen.Runes)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = Gold.copy(alpha = .45f),
                    cursorColor = Gold,
                    focusedLabelColor = Gold
                )
            )

            Spacer(Modifier.height(10.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigate(
                        if (query.isBlank()) Screen.Runes
                        else Screen.Analysis(query)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                )
            ) {
                Text("ПОДОБРАТЬ ПРАКТИКУ", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(18.dp))

            HomeTile("ᚠ", "24 РУНЫ", "Все руны Старшего футарка") {
                navigate(Screen.Runes)
            }

            HomeTile("ᛃ", "ДИАГНОСТИКА", "Расклады и интерпретация") {
                navigate(Screen.Placeholder(
                    "ДИАГНОСТИКА",
                    "Здесь появятся интерактивные расклады, случайное извлечение рун, положения и журнал интерпретаций."
                ))
            }

            HomeTile("ᛉ", "СТАВЫ", "Каталог формул и подбор") {
                navigate(Screen.Placeholder(
                    "СТАВЫ",
                    "Раздел предназначен для каталога, объяснения функций каждой руны и автоматического подбора по задаче."
                ))
            }

            HomeTile("ᚷ", "КОНСТРУКТОР", "Главная и вспомогательные руны") {
                navigate(Screen.Placeholder(
                    "КОНСТРУКТОР",
                    "Здесь будет графический редактор формул с главной руной и функциональными вспомогательными позициями."
                ))
            }

            HomeTile("ᚲ", "СВЕЧИ", "Свечные практики") {
                navigate(Screen.Placeholder(
                    "СВЕЧИ",
                    "Раздел будет содержать отдельный безопасный мастер свечной практики: цель, символы, проведение и завершение."
                ))
            }

            HomeTile("ᚨ", "ЖУРНАЛ", "История работы") {
                navigate(Screen.Placeholder(
                    "ЖУРНАЛ",
                    "Здесь будут сохраняться локальные записи о запросах, формулах и наблюдениях."
                ))
            }

            HomeTile("ᛟ", "ИСТОЧНИКИ", "История и современные традиции") {
                navigate(Screen.Placeholder(
                    "ИСТОЧНИКИ",
                    "Исторические сведения будут отделены от современных эзотерических интерпретаций и авторских методов."
                ))
            }
        }
    }
}

@Composable
private fun HomeTile(
    symbol: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, color = GoldLight, fontSize = 34.sp, modifier = Modifier.width(58.dp))
            Column {
                Text(title, color = GoldLight, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun RuneListScreen(
    onBack: () -> Unit,
    onRune: (RuneInfo) -> Unit
) {
    var search by remember { mutableStateOf("") }

    val filtered = remember(search) {
        if (search.isBlank()) runes
        else {
            val q = search.lowercase().replace('ё','е')
            runes.filter {
                listOf(
                    it.name,
                    it.russian,
                    it.keywords,
                    it.upright,
                    it.practice
                ).any { value ->
                    value.lowercase().replace('ё','е').contains(q)
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("‹ ГЛАВНАЯ", color = Gold)
        }

        AppHeader("24 РУНЫ", "Нажмите на карточку для полного описания")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Поиск: деньги, семья, дорога...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            )
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(filtered) { rune ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onRune(rune) },
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            rune.symbol,
                            color = GoldLight,
                            fontSize = 43.sp,
                            modifier = Modifier.width(67.dp)
                        )
                        Column {
                            Text(
                                "${rune.name} • ${rune.russian}",
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(rune.keywords, color = Muted, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuneDetailScreen(rune: RuneInfo, onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(true) }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(18.dp),
        contentPadding = PaddingValues(bottom = 35.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text("‹ 24 РУНЫ", color = Gold)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(rune.symbol, color = GoldLight, fontSize = 92.sp)
                    Text(
                        rune.name,
                        color = GoldLight,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(rune.russian, color = Gold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        rune.keywords,
                        color = Muted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Нажмите на карточку",
                        color = Gold.copy(alpha = .6f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 13.dp)
                    )
                }
            }

            AnimatedVisibility(expanded) {
                Column {
                    InfoBlock("ПРЯМОЕ ПОЛОЖЕНИЕ", rune.upright)
                    InfoBlock("ПЕРЕВЁРНУТОЕ ПОЛОЖЕНИЕ", rune.reversed)
                    InfoBlock("ПРИМЕНЕНИЕ", rune.practice)
                    InfoBlock(
                        "ВАЖНО О ТРАДИЦИИ",
                        "Мантические значения, перевёрнутые позиции и магические применения относятся преимущественно к современным системам работы с рунами. Они не должны автоматически выдаваться за единое историческое учение эпохи Старшего футарка."
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(title: String, body: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            title,
            color = Gold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            body,
            color = Text,
            lineHeight = 22.sp
        )
    }
}


@Composable
private fun AnalysisScreen(
    text: String,
    onBack: () -> Unit,
    onRune: (RuneInfo) -> Unit
) {
    val result = remember(text) { analyzeRequest(text) }
    val recommendations = remember(result) { recommendedRunes(result) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text("‹ ГЛАВНАЯ", color = Gold)
            }

            AppHeader("АНАЛИЗ ЗАПРОСА", "Проблема → функции → подходящие руны")

            Spacer(Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "ИСХОДНЫЙ ЗАПРОС",
                        color = Gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(7.dp))
                    Text("«$text»", color = Text, lineHeight = 22.sp)
                }
            }

            Spacer(Modifier.height(18.dp))

            if (result.problems.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Запрос пока не распознан достаточно точно.",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Попробуйте назвать сферу и желаемый результат, например: «хочу найти новую работу», «постоянные ссоры в семье и не хватает денег» или «хочу встретить мужчину для серьёзных отношений». Словарь будет постепенно расширяться.",
                            color = Text,
                            lineHeight = 21.sp
                        )
                    }
                }
            } else {
                Text(
                    "ОБНАРУЖЕННЫЕ ЗАДАЧИ",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(8.dp))

                result.problems.forEach { detected ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                detected.concept.title,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                detected.concept.domain,
                                color = Gold,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(7.dp))
                            Text(
                                detected.concept.explanation,
                                color = Text,
                                lineHeight = 20.sp,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Совпадение: ${detected.confidence}%",
                                color = Muted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "ПОДБОР РУН",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    "Рейтинг учитывает все распознанные задачи одновременно. Нажмите на руну, чтобы открыть её карточку.",
                    color = Muted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(9.dp))

                recommendations.forEachIndexed { index, pair ->
                    val rune = pair.first
                    val roles = result.problems
                        .filter { rune.name in it.concept.runeNames }
                        .joinToString(" • ") { it.concept.title }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { onRune(rune) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (index == 0)
                                Color(0xFF251C0D) else CardBg
                        )
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                rune.symbol,
                                color = GoldLight,
                                fontSize = 45.sp,
                                modifier = Modifier.width(66.dp)
                            )

                            Column(Modifier.weight(1f)) {
                                Text(
                                    if (index == 0)
                                        "${rune.name} • ОСНОВНОЙ КАНДИДАТ"
                                    else rune.name,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    rune.russian,
                                    color = Gold,
                                    fontSize = 12.sp
                                )

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    roles,
                                    color = Text,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF131B17)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(17.dp)) {
                        Text(
                            "КАК ИНТЕРПРЕТИРОВАТЬ РЕЗУЛЬТАТ",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "Первая руна является наиболее подходящим кандидатом на центральную функцию только по текущей модели запроса. Остальные руны не должны автоматически наноситься все вместе. Следующий модуль конструктора будет проверять функции, совместимость и избыточность формулы перед созданием става.",
                            color = Text,
                            lineHeight = 20.sp,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("‹ ГЛАВНАЯ", color = Gold)
        }

        Spacer(Modifier.height(30.dp))

        AppHeader(title)

        Spacer(Modifier.height(30.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Text(
                description,
                modifier = Modifier.padding(22.dp),
                color = Text,
                lineHeight = 23.sp
            )
        }
    }
}

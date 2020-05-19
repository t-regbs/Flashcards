package flashcards

import java.io.File
import java.lang.StringBuilder
import kotlin.random.Random

data class Card(val term: String, val definition: String, var errorCount: Int )

const val FILEPATH = "/C:/Users/Timilehin/Documents/Kotlin/StoreFlashCards/"
val cards: MutableList<Card> = mutableListOf()
val logMessages = mutableListOf<String>()

fun main(args: Array<String>) {
    cards.clear()
    importFromCommandLineArgs(args)
    start()
    exportFromCommandLineArgs(args)
}

fun importFromCommandLineArgs(args: Array<String>) {
    var name: String
    if (!args.isNullOrEmpty()) {
        if (args[0] == "-import") {
            name = args[1]
            import(name)
        } else if (args.size > 2 && args[2] == "-import") {
            name = args[3]
            import(name)
        }
    }
}

fun exportFromCommandLineArgs(args: Array<String>) {
    var name: String
    if (!args.isNullOrEmpty()) {
        if (args[0] == "-export") {
            name = args[1]
            export(name)
        } else if (args.size > 2 && args[2] == "-export") {
            name = args[3]
            export(name)
        }
    }
}

fun start() {
    val startText = "Input the action (add, remove, import, export, ask, exit, log," +
            " hardest card, reset stats):"
    println(startText)
    logMessages.add(startText)
    when(readLine()) {
        "add" -> {
            logMessages.add("add")
            addCard()
        }
        "remove" -> {
            logMessages.add("remove")
            removeCard()
        }
        "import" -> {
            logMessages.add("import")
            import()
        }
        "export" -> {
            logMessages.add("export")
            export()
        }
        "ask" -> {
            logMessages.add("ask")
            ask()
        }
        "exit" -> {
            logMessages.add("exit")
            exit()
        }
        "log" -> {
            logMessages.add("log")
            showLog()
        }
        "hardest card" -> {
            logMessages.add("hardest card")
            showHardest()
        }
        "reset stats" -> {
            logMessages.add("reset stats")
            reset()
        }
    }
}

fun reset() {
    for (card in cards) {
        card.errorCount = 0
    }
    println("Card statistics has been reset.")
    logMessages.add("Card statistics has been reset.")
    start()
}

fun showHardest() {
    if (cards.all { it.errorCount == 0 }) {
        println("There are no cards with errors")
        logMessages.add("There are no cards with errors")
    } else {
        var maxErrors = cards.elementAt(0).errorCount
        for (card in cards) {
            if (card.errorCount > maxErrors ) {
                maxErrors = card.errorCount
            }
        }
        val maxCards = cards.filter { it.errorCount == maxErrors }
        var maxString = ""
        for (item in maxCards) {
            maxString += "\"${item.term}\""
            maxString += if (maxCards.size > 1) ", " else "."
        }
        if (maxCards.size > 1) {
            println("The hardest cards are $maxString You have $maxErrors errors answering them.")
            logMessages.add("The hardest cards are $maxString You have $maxErrors errors answering them.")
        } else {
            println("The hardest card is $maxString You have $maxErrors errors answering it.")
            logMessages.add("The hardest card is $maxString You have $maxErrors errors answering it.")
        }
    }
    start()
}

fun showLog() {
    println("File name:")
    logMessages.add("File name:")
    val name = readLine().toString()
    logMessages.add(name)
    val file = File(name)
    file.createNewFile()
    val logSb = StringBuilder()
    for (msg in logMessages) logSb.appendln(msg)
    file.writeText(logSb.toString())
    println("The log has been saved.")
    logMessages.add("The log has been saved.")
    start()
}

fun ask() {
    val askText = "How many times to ask?"
    println(askText)
    logMessages.add(askText)
    val times = readLine()!!.toInt()
    logMessages.add(times.toString())
    for (i in 0 until times) {
        val random = Random.nextInt(cards.size)
        val randomCard = cards.elementAt(random)
        val prtDefText = "Print the definition of \"${randomCard.term}\":"
        println(prtDefText)
        logMessages.add(prtDefText)
        val ans = readLine().toString()
        logMessages.add(ans)
        if (randomCard.definition == ans) {
            println("Correct answer.")
            logMessages.add("Correct answer.")
        } else if (randomCard.definition != ans && cards.any { it.definition == ans }){
            val correct = cards.filter { it.definition == ans }
            val wrongAnsLong = "Wrong answer, The correct one is \"${randomCard.definition}\"," +
                    "you've just written the definition of" +
                    " \"${correct[0].term}\""
            println(wrongAnsLong)
            logMessages.add(wrongAnsLong)
            randomCard.errorCount++
        } else{
            val wrongAnsShort = "Wrong answer. The correct one is \"${randomCard.definition}\"."
            println(wrongAnsShort)
            logMessages.add(wrongAnsShort)
            randomCard.errorCount++
        }
    }
    start()
}

fun exit() {
    println("Bye bye!")
    logMessages.add("Bye bye!")
}

fun addCard() {
    println("The card:")
    logMessages.add("The card:")
    var term = readLine().toString()
    logMessages.add(term)
    if (cards.any { it.term == term }) {
        val alreadyExists = "The card \"$term\" already exists."
        println(alreadyExists)
        logMessages.add(alreadyExists)
        start()
        return
    }
    val defText = "The definition of the card:"
    println(defText)
    logMessages.add(defText)
    val definition = readLine().toString()
    logMessages.add(definition)
    if (cards.any { it.definition == definition }) {
        val alreadyExists = "The definition \"$definition\" already exists."
        println(alreadyExists)
        logMessages.add(alreadyExists)
        start()
        return
    }
    cards.add(Card(term, definition, 0))
    val pairMessage = "The pair (\"$term\":\"$definition\") has been added."
    println(pairMessage)
    logMessages.add(pairMessage)
    start()
}

fun import(name: String = "") {
    var fileName: String
    if (name == "") {
        println("File name:")
        logMessages.add("File name:")
        fileName = readLine().toString()
        logMessages.add(fileName)
    } else {
        fileName = name
    }
    val file = File("$FILEPATH$fileName")
    if (file.exists()) {
        val lines = file.readLines()
        for (line in lines) {
            val det = line.split("|")
            cards.add(Card(det[0], det[1], det[2].toInt()))
        }
        println("${lines.size} cards have been loaded.")
        logMessages.add("${lines.size} cards have been loaded.")
        if (name == "") start()
    } else {
        println("File not found.")
        logMessages.add("File not found.")
        start()
    }
}

fun export(name: String = "") {
    var fileName: String
    if (name == "") {
        println("File name:")
        logMessages.add("File name:")
        fileName = readLine().toString()
        logMessages.add(fileName)
    } else {
        fileName = name
    }
    val file = File("$FILEPATH$fileName")
    file.createNewFile()
    val cardSb = StringBuilder()
    for (card in cards) {
        cardSb.apply {
            append(card.term)
            append("|")
            append(card.definition)
            append("|")
            append(card.errorCount.toString())
            appendln()
        }
    }
    file.writeText(cardSb.toString())
    println("${cards.size} cards have been saved.")
    logMessages.add("${cards.size} cards have been saved.")
    if (name == "") start()
}

fun removeCard() {
    println("The card:")
    logMessages.add("The card:")
    val term = readLine().toString()
    logMessages.add(term)
    if (cards.any { it.term == term }) {
        cards.removeAll { it.term == term }
        println("The card has been removed.")
        logMessages.add("The card has been removed.")
        start()
    } else {
        println("Can't remove \"$term\": there is no such card.")
        logMessages.add("Can't remove \"$term\": there is no such card.")
        start()
    }
}


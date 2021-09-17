package manager.task.common.utils

import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

private val LS = System.lineSeparator()
private const val SL = "*SL*"
private const val AUTO_FIT = "%AUTO_FIT%"

fun <T> printTableOf(content: Iterable<T>): PrintTable<T> {
    return PrintTable(content = content, size = StreamSupport.stream(content.spliterator(), false).count().toInt())
}

fun <T> printTableOf(content: List<T>): PrintTable<T> {
    return PrintTable(content = content, size = content.size)
}

fun <T> printTableOf(content: Stream<T>): PrintTable<T> {
    val temp = content.collect(Collectors.toList())
    return PrintTable(content = temp, size = temp.size)
}

class PrintTable<T>(
    private var name: String = "PrintTableKotlin<Object>",
    val content: Iterable<T> = mutableListOf(),
    val size: Int = 0,
    private val columns: MutableList<Column<Any>> = mutableListOf()
) {

    fun name(tableName: String): PrintTable<T> {
        name = tableName
        return this
    }

    /**
     * @param name - имя колонки.
     * @param getValue - ссылка на Getter для каждого объекта,
     * что бы получить нужное значения для каждой ячейки в колонке.
     * @param cellColours - условия для покраски значения в ячейки
     * (применяется каждый [CellColour] к каждой ячейке).
     */
    fun addColumn(
        name : String = "",
        pattern: String = AUTO_FIT,
        cellColours: List<CellColour<T>> = mutableListOf(),
        getValue: (T) -> Any?
    ): PrintTable<T>  {
        columns.add(Column(name, pattern, getValue as (Any) -> Any,size, cellColours as List<CellColour<Any>>))
        return this
    }

    private fun buildHeader(): String {
        val rsl = StringBuilder()
        for (column in columns) {
            rsl.append("| ")
            val alignSpaceNum: Int =
                if (column.pattern == AUTO_FIT) column.maxCellWidth - column.name.length else column.pattern.toInt() - column.name.length
            if (alignSpaceNum > 0) rsl.append(repeatChar(' ', alignSpaceNum))
            rsl.append(column.name).append(' ')
        }
        rsl.append('|')
        return rsl.toString()
    }

    private fun buildLine(lineIndex: Int): String {
        val rsl = StringBuilder()
        for (column in columns) {
            val cellContent: String = column.content[lineIndex]
            rsl.append("| ")
            val cellContentLength =
                if (column.isCellColorized[lineIndex]) cellContent.length - 9 else cellContent.length
            val alignSpaceNum: Int =
                if (column.pattern == AUTO_FIT) column.maxCellWidth - cellContentLength else column.pattern.toInt() - cellContentLength
            if (alignSpaceNum > 0) rsl.append(repeatChar(' ', alignSpaceNum))
            rsl.append(cellContent).append(' ')
        }
        rsl.append('|')
        return rsl.toString()
    }


    override fun toString(): String {
        val rsl = StringBuilder()
        columns.forEach { it.buildAllCellsContent(content as Iterable<Any>) }
        val header = buildHeader()
        rsl.append(SL).append(LS)
        rsl.append(header).append(LS)
        rsl.append(SL).append(LS)
        for (i in 0 until size) { rsl.append(buildLine(i)).append(LS) }
        rsl.append(SL).append(LS)
        val splitLine = "+" + repeatChar('-', header.length - 2) + "+"
        return rsl.toString().replace(SL, splitLine)
    }

    fun print() {
        val size = if (content is Collection<*>) "" + (content as Collection<T>).size else ""
        val sizeLabel = if (size.isEmpty()) "" else " (table size: $size)"
        val rsl = StringJoiner(System.lineSeparator())
            .add(name + sizeLabel)
            .add(this.toString())
        println(rsl)
    }

    private fun repeatChar(c: Char, times: Int): String {
        val rsl = StringBuilder()
        for (i in 0 until times) {
            rsl.append(c)
        }
        return rsl.toString()
    }


    class Column<T>(
        val name: String,
        val pattern: String,
        val getValue: (T) -> Any,
        contentSize : Int,
        private val cellColours: List<CellColour<T>>
    ) {
        val content: MutableList<String> = MutableList(contentSize) { "" }
        val isCellColorized: MutableList<Boolean> = MutableList(contentSize) { false }
        var maxCellWidth = 0

        fun buildAllCellsContent(tableContent: Iterable<T>) {
            for ((i, each) in tableContent.withIndex()) {
                var cellValue = "" + getValue.invoke(each)
                val cellValueWidth = cellValue.length
                for (cc in cellColours) {
                    if (cc.check.test(each)) {
                        cellValue = Colour.colour(cellValue, cc.colour)
                        isCellColorized[i] = true
                    }
                }
                content[i] = cellValue
                if (cellValueWidth > maxCellWidth) maxCellWidth = cellValueWidth
            }
        }

    }

    class CellColour<T>(val colour: Colour, val check: Predicate<T>)

    /**
     * смещение текста за 1 colour(...) -> 9 chars
     * все цвета дают одинаковое смещение.
     */
    enum class Colour(private val txtColour: String) {
        BLACK(TEXT_BLACK), 
        RED(TEXT_RED),
        GREEN(TEXT_GREEN), 
        YELLOW(TEXT_YELLOW),
        BLUE(TEXT_BLUE),
        PURPLE(TEXT_PURPLE),
        CYAN(TEXT_CYAN),
        WHITE(TEXT_WHITE), 
        DEFAULT(TEXT_RESET);

        companion object {
            fun colour(txt: Any, colour: Colour): String {
                return colour.txtColour + txt + TEXT_RESET
            }
        }

    }


}
/* Colours for text */
const val  TEXT_RESET = "\u001B[0m"
const val  TEXT_BLACK = "\u001B[30m"
const val  TEXT_RED = "\u001B[31m"
const val  TEXT_GREEN = "\u001B[32m"
const val  TEXT_YELLOW = "\u001B[33m"
const val  TEXT_BLUE = "\u001B[34m"
const val  TEXT_PURPLE = "\u001B[35m"
const val  TEXT_CYAN = "\u001B[36m"
const val TEXT_WHITE = "\u001B[37m"

private const val  BACKGROUND_BLACK = "\u001B[40m"
private const val  BACKGROUND_RED = "\u001B[41m"
private const val  BACKGROUND_GREEN = "\u001B[42m"
private const val  BACKGROUND_YELLOW = "\u001B[43m"
private const val  BACKGROUND_BLUE = "\u001B[44m"
private const val  BACKGROUND_MAGENTA = "\u001B[45m"
private const val  BACKGROUND_CYAN = "\u001B[46m"
private const val  BACKGROUND_WHITE = "\u001B[47m"
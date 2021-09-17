package manager.task.common

import manager.task.common.WS.Paths.path
import manager.task.common.utils.PrintTable.CellColour
import manager.task.common.utils.PrintTable.Colour
import manager.task.common.utils.Utils
import manager.task.common.utils.printTableOf

fun commonPath() {
    path<Unit>("printPathConfig", false) {
        val authTrueGreen = CellColour<WS.Paths.PathKeeper>(Colour.GREEN) { it.isNeedAuth }
        val authFalseRed = CellColour<WS.Paths.PathKeeper>(Colour.RED) { !it.isNeedAuth }
        val auths = listOf(authTrueGreen, authFalseRed)
        printTableOf(WS.Paths.paths.values)
            .name("path config")
            .addColumn("PATH") { it.path }
            .addColumn("AUTH", cellColours = auths) { it.isNeedAuth }
            .addColumn("PARAMETER TYPE") { it.expectedParamType.typeName }
            .print()
    }

    path<Unit>("printRequests", false) {
        printTableOf(WS.Requests.requests.values)
            .name("path config")
            .addColumn("activityId") { it.activityId }
            .addColumn("income") { it.income }
            .addColumn("incomeTime") { Utils.localDateTimeFormatter.format(it.incomeTime) }
            .addColumn("outcome") { it.outcome ?: "null" }
            .addColumn("outcomeTime") { if (it.outcomeTime != null) Utils.localDateTimeFormatter.format(it.outcomeTime) else "null" }
            .print()
    }
}
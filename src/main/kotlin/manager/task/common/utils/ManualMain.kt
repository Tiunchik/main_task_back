package manager.task.common.utils

import manager.task.common.WS
import manager.task.common.log
import manager.task.domains.user.User

class ManualMain
private val log = log<ManualMain>()

fun main() {
//    val t = gson.toJson(WS.Income("c/user", "admin", "1234-5678-9012", 0, ""))
//    val t = gson.toJson(WS.Outcome("1234-5678-9012", 0, true, ""))

    val src = """
        {
        "reqId":"1234-5678-9012",
        "path":"c/user",
        "auth":"admin",
        "body":{
        "reqId":"1234-5678-9012",
        "path":"c/user",
        "auth":"admin",
        "body":""

        }
    """.trimIndent()

    val rsl = Utils.fromJson<WS.Income>(src)
    log.info("rsl", rsl)
    val k = Utils.gson.toJson(User(0, "", ""))

}

open class Parent
class Child : Parent()

fun funcExample(parent : Parent) : Parent {
    return Child()
}

fun show() {
    funcExample(Child())
}



class FI {

    init {
        println("FI init")
    }

    companion object TY {
        init {
            println("TY init")
        }
    }
}

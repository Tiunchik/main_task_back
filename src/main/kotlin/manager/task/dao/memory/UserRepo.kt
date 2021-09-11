package manager.task.dao.memory

import manager.task.WsEndpoint
import manager.task.models.User

class UserRepo {
    private val userStore = mutableListOf<User>()

    @WsEndpoint
    fun userCreate(user: User): Any {
        // TODO : сделать парсинг в параметров для wsMsg.
        userStore += user
        return ""
    }

//    fun userRead(lines: List<String>): Any {
//        val searchParam = lines[1]
//        userStore.forEach { if (it.matchAnyField(searchParam)) return it }
//        return ""
//    }
}

private fun User.matchAnyField(search: Any): Boolean {
    if (search is String) {
        if (nick.contains(search)) return true
        if (login.contains(search)) return true
        if (password.contains(search)) return true
    }
    return false
}
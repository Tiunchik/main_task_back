package manager.task.paths

import manager.task.WS.Paths.path
import manager.task.models.User

fun userControllerPaths() {

    path<User>("testUser") {
        println("it work! $it")
    }
    path<User>(path = "testUserAuth", auth = false) {
        println("it work! $it")
    }
}
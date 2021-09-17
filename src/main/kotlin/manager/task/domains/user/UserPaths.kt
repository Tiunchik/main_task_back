package manager.task.domains.user

import manager.task.common.WS.Paths.path
import manager.task.common.WS.Paths.pathCtx

fun userControllerPaths() {

    pathCtx<Unit>("currentUser") { _, ctx -> authService.getCurrentUser("") }

    path<User>("createUser") { userRepo create it }
    path<Long>("getUser") { userRepo get it }
    path<User>("updateUser") { userRepo update it }
    path<User>("deleteUser") { userRepo delete it }
}
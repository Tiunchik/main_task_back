package manager.task.paths

import manager.task.WS.Paths.path
import manager.task.WS.Paths.pathCtx
import manager.task.models.User

fun userControllerPaths() {

    path<Int>("testPrimitive") { id ->
        println("testPrimitive param = $id")
    }
    path<Unit>("testUnit") {
        println("Unit param")
    }

    path<User>("testUser") { user ->
        println("it work! $user")
        authService
    }
    path<User>(path = "testUserAuth", auth = false) {
        println("it work! $it")
    }
    pathCtx<Pair<User, String>>("testUserCtx", false) { user, ctx ->
        println("activityId:${ctx.activityId}")
        println("currentUser:${ctx.currentUser}")
        println("pair:$user")
    }

    // TODO : UNIT is temp -> wait for no param path
    path<Unit>("currentUser") { authService.getCurrentUser("") }

    path<User>("createUser") { userRepo create it }
    path<Long>("getUser") { userRepo get it }
    path<User>("updateUser") { userRepo update it }
    path<User>("deleteUser") { userRepo delete it }
}

 class ValueWrapper<T>{
     var value : T = TODO()
 }
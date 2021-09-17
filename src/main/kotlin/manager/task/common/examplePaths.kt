package manager.task.common

import manager.task.common.WS.Paths.path
import manager.task.common.WS.Paths.pathCtx
import manager.task.domains.user.User


fun examplePaths() {

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
    /* auth + использование currentUser -> ConfigException */
    pathCtx<Pair<User, String>>("testUserCtxPair", false) { user, ctx ->
        println("activityId:${ctx.activityId}")
        println("currentUser:${ctx.currentUser}")
        println("pair:$user")
    }

    pathCtx<User>("testUserCtx") { user, ctx ->
        println("activityId:${ctx.activityId}")
        println("currentUser:${ctx.currentUser}")
        println("pair:$user")
    }

}
package manager.task.microservices

import manager.task.domains.user.User

class AuthService {
    fun getCurrentUser(incomeAuth: String): User {
        // TODO : make Real auth impl - > wait for real auth
        return User(777, incomeAuth, "authUser")
    }

    fun checkAuth(auth: String): Boolean {
        // TODO : make Real auth impl - > wait for real auth
        if (auth == "admin" || auth == "test") return true
        return isAuthVerified(auth)
    }

    private fun isAuthVerified(auth: String): Boolean {
        // TODO
        return true
    }

}
package manager.task.dao.memory

import manager.task.models.User

class UserRepo {
    private val userStore = mutableListOf<User>()

    /* create */
    infix fun create(user: User) {
        userStore.add(user)
    }
    /* read */
    infix fun get(email: String) = userStore.find { it.email == email }
    infix fun get(id: Long) = userStore.find { it.id == id }
    /* update */
    infix fun update(user: User) {
        userStore[user.id.toInt()].apply {
            this.email = user.email
            this.password = user.password
        }
    }
    /* delete */
    infix fun delete(user: User) {
        userStore.removeIf {  it.id == user.id}
    }
    infix fun delete(id: Long) {
        userStore.removeIf {  it.id == id}
    }
    infix fun delete(email: String) {
        userStore.removeIf {  it.email == email}
    }

}

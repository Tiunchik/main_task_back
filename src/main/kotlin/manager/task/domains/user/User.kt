package manager.task.domains.user

data class User(
    val id: Long,
    var email: String,
    var password: String,
)
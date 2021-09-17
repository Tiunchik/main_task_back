package manager.task.models

data class User(
    val id: Long,
    var email: String,
    var password: String,
)
package manager.task.configs

data class PsqlConfig(
    val url : String = "",
    val login : String = "",
    val password : String = "",
    val jdbcProvider : String = "",
)
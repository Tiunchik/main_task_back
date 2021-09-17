package manager.task.common

class ConfigurationException(message: String) : RuntimeException(message)
class PathIsNotSupported(path: String) : RuntimeException("Path:\"$path\" is not supported")
class JsonParseException(message: String, e: Throwable? = null) : RuntimeException(message, e)

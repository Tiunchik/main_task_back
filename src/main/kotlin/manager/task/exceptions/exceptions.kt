package manager.task.exceptions

class ConfigurationException(message: String) : RuntimeException(message)
// TODO : перенести сюда сообщение и принимать параметр path
class PathIsNotSupported(path: String) : RuntimeException("Path:\"$path\" is not supported")
class JsonParseException(message: String, e : Throwable? = null) : RuntimeException(message, e)

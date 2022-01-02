package plugin.thirdlife.types

class LifeException(message: String) : Exception(message)
class CacheException(message: String) : Exception(message)
class PermissionException: Exception("You do not have permission to do this")
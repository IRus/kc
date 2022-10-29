package pkg

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

public suspend fun main() {
    val ls = LoggingSystem(
        level = LoggingLevel.ERROR,
        appender = MutexAppender(),
        format = KomodoLoggingFormat(),
    )

    val logger = ls.getLogger("Hello")

    logger.error { f("Hello", t = RuntimeException("World")) }
}

public interface KomodoLogger<F> {
    public val name: LoggerName
    public val level: LoggingLevel
    public val appender: Appender
    public val format: F
}

public data class LoggerName(
    public val name: String,
)

public suspend inline fun <F> KomodoLogger<F>.error(
    message: context(LoggerName, LoggingLevel) F.() -> String,
) {
    if (level <= LoggingLevel.ERROR) {
        appender.append(message(name, LoggingLevel.ERROR, format))
    }
}

public interface LoggingFormatContext {
    public val name: String
    public val level: LoggingLevel
}

public class KomodoLoggingFormat {
    context(LoggerName, LoggingLevel)
    @Suppress("NOTHING_TO_INLINE")
    public inline fun f(
        message: String,
        t: Throwable? = null,
    ): String {
        return "${this@LoggerName.name} ${this@LoggingLevel.name}: $message ${t?.message}\n"
    }
}

public enum class LoggingLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    OFF,
}

public interface Appender {
    public suspend fun append(message: String)
}

public class MutexAppender : Appender {
    private val mutex = Mutex()
    override suspend fun append(message: String) {
        mutex.withLock {
            val ba = message.toByteArray()
            System.out.write(ba, 0, ba.size)
        }
    }
}

public class LoggingSystem<F>(
    private val level: LoggingLevel,
    private val appender: Appender,
    private val format: F,
) {
    public fun getLogger(name: String): KomodoLogger<F> {
        return DefaultKomodoLogger(
            name = LoggerName(name),
            level = level,
            appender = appender,
            format = format,
        )
    }
}

public class DefaultKomodoLogger<F>(
    override val name: LoggerName,
    override val level: LoggingLevel,
    override val appender: Appender,
    public override val format: F,
) : KomodoLogger<F>



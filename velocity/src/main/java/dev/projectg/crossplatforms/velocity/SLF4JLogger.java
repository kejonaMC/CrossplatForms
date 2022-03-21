package dev.projectg.crossplatforms.velocity;

import dev.projectg.crossplatforms.Logger;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class SLF4JLogger extends Logger {

    @Nonnull
    private final org.slf4j.Logger logger;

    private boolean debug = false; // SLF4J doesn't really have an easy way to set debug

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void severe(String message) {
        logger.error(message);
    }

    @Override
    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        } else if (debug) {
            logger.info(message);
        }
    }

    @Override
    public boolean isDebug() {
        return debug || logger.isDebugEnabled();
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}

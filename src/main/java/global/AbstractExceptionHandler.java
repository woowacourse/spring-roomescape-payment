package global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.RoomescapeApplication;

@RestControllerAdvice
public abstract class AbstractExceptionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(RoomescapeApplication.class);

    protected void logError(Exception exception) {
        logger.error("Error occur {}", exception);
    }
}

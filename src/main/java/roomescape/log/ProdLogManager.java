package roomescape.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public class ProdLogManager implements LogManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void logInfo(Exception e) {
        logger.info(e.getMessage());
    }

    @Override
    public void logInfo(String message, Exception e) {
        logger.info(message, e);
    }
}

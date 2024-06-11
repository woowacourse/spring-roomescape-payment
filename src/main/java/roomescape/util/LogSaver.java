package roomescape.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogSaver {

    private final Logger log;
    private final ObjectMapper objectMapper;

    public LogSaver(ObjectMapper objectMapper) {
        this.log = LoggerFactory.getLogger(getClass());
        this.objectMapper = objectMapper;
    }

    public void logInfo(final String message, final Object object) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(object);
        log.info("[{}] {}", message, json);
    }
}

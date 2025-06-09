package roomescape.common.log.message.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import roomescape.common.log.message.LogMessageProvider;
import roomescape.common.log.message.RequestInfo;

@Component
@RequiredArgsConstructor
public class JsonLogMessageProvider implements LogMessageProvider {

    private static final String REQUEST_MESSAGE_FORMAT = "REQUEST%n%s";
    private static final String DEFAULT_REQUEST_MESSAGE = "Failed to write request log";

    private static final String RESPONSE_MESSAGE_FORMAT = "RESPONSE%n%s";
    private static final String DEFAULT_RESPONSE_MESSAGE = "Failed to write response log";

    private static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = createPrettyPrinter();
    private final ObjectMapper objectMapper;

    private static DefaultPrettyPrinter createPrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(new DefaultIndenter("  ", System.lineSeparator()));
        return printer;
    }

    @Override
    public String getRequestLog(
            final RequestInfo requestInfo,
            final Map<String, Object> handlerArguments
    ) {
        final RequestLogEntry requestLogEntry = RequestLogEntry.createWithHandlerArgumentMap(
                requestInfo,
                handlerArguments
        );
        try {
            return String.format(
                    REQUEST_MESSAGE_FORMAT,
                    objectMapper.writer(DEFAULT_PRETTY_PRINTER)
                            .writeValueAsString(requestLogEntry)
            );
        } catch (JsonProcessingException e) {
            return DEFAULT_REQUEST_MESSAGE;
        }
    }

    @Override
    public String getResponseLog(
            final RequestInfo requestInfo,
            final ResponseEntity<?> response
    ) {
        final ResponseLogEntry responseLogEntry = new ResponseLogEntry(
                requestInfo,
                response
        );
        try {
            return String.format(
                    RESPONSE_MESSAGE_FORMAT,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseLogEntry)
            );
        } catch (JsonProcessingException e) {
            return DEFAULT_RESPONSE_MESSAGE;
        }
    }
}

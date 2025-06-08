package roomescape.common.log.message.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import roomescape.common.log.context.RequestContext;
import roomescape.common.log.message.LogMessageProvider;

@Component
@RequiredArgsConstructor
public class JsonLogMessageProvider implements LogMessageProvider {

    private static final String REQUEST_MESSAGE_FORMAT = "REQUEST%n%s";
    private static final String RESPONSE_MESSAGE_FORMAT = "RESPONSE%n%s";
    private static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = createPrettyPrinter();
    private final ObjectMapper objectMapper;

    private static DefaultPrettyPrinter createPrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(new DefaultIndenter("  ", System.lineSeparator()));
        return printer;
    }

    @Override
    public String getRequestLog(
            final RequestContext requestContext,
            final Map<String, Object> handlerArguments
    ) {
        final RequestLogEntry requestLogEntry = RequestLogEntry.createWithHandlerArgumentMap(
                requestContext,
                handlerArguments
        );
        try {
            return String.format(
                    REQUEST_MESSAGE_FORMAT,
                    objectMapper.writer(DEFAULT_PRETTY_PRINTER)
                            .writeValueAsString(requestLogEntry)
            );
        } catch (JsonProcessingException e) {
            return "Failed to write request log";
        }
    }

    @Override
    public String getResponseLog(
            final RequestContext requestContext,
            final ResponseEntity<?> response
    ) {
        final ResponseLogEntry responseLogEntry = new ResponseLogEntry(
                requestContext,
                response
        );
        try {
            return String.format(
                    RESPONSE_MESSAGE_FORMAT,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseLogEntry)
            );
        } catch (JsonProcessingException e) {
            return "Failed to write response log";
        }
    }
}

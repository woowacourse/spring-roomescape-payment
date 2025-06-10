package roomescape.common.log.message.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.common.log.entry.ErrorLogEntry;
import roomescape.common.log.entry.RequestLogEntry;
import roomescape.common.log.entry.ResponseLogEntry;
import roomescape.common.log.message.LogMessageProvider;

@Component
@RequiredArgsConstructor
public class JsonLogMessageProvider implements LogMessageProvider {

    private static final String REQUEST_MESSAGE_FORMAT = "REQUEST%n%s";
    private static final String DEFAULT_REQUEST_MESSAGE = "Failed to write request log";

    private static final String RESPONSE_MESSAGE_FORMAT = "RESPONSE%n%s";
    private static final String DEFAULT_RESPONSE_MESSAGE = "Failed to write response log";

    private static final String ERROR_MESSAGE_FORMAT = "ERROR%n%s";
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to write error log";

    private static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = createPrettyPrinter();

    private final ObjectMapper objectMapper;

    private static DefaultPrettyPrinter createPrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(new DefaultIndenter("  ", System.lineSeparator()));
        return printer;
    }

    @Override
    public String getRequestLog(final RequestLogEntry requestLogEntry) {
        return formatLogMessage(
                REQUEST_MESSAGE_FORMAT,
                requestLogEntry,
                DEFAULT_REQUEST_MESSAGE
        );
    }

    @Override
    public String getResponseLog(final ResponseLogEntry responseLogEntry) {
        return formatLogMessage(
                RESPONSE_MESSAGE_FORMAT,
                responseLogEntry,
                DEFAULT_RESPONSE_MESSAGE
        );
    }

    @Override
    public String getErrorLog(final ErrorLogEntry errorLogEntry) {
        return formatLogMessage(
                ERROR_MESSAGE_FORMAT,
                errorLogEntry,
                DEFAULT_ERROR_MESSAGE
        );
    }

    private String formatLogMessage(final String format, final Object logEntry, String defaultMessage) {
        try {
            return String.format(
                    format,
                    objectMapper.writer(DEFAULT_PRETTY_PRINTER).writeValueAsString(logEntry)
            );
        } catch (JsonProcessingException e) {
            return defaultMessage;
        }
    }
}

package roomescape.common.log.message.json;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import roomescape.common.log.message.RequestInfo;

record RequestLogEntry(
        RequestInfo requestInfo,
        List<HandlerArgument> handlerArguments
) {

    private static final List<Predicate<Class<?>>> JSON_UNSERIALIZABLE_CLASSES = List.of(
            HttpServletRequest.class::isAssignableFrom,
            HttpServletResponse.class::isAssignableFrom
    );

    static RequestLogEntry createWithHandlerArgumentMap(
            final RequestInfo requestInfo,
            final Map<String, Object> handlerArguments
    ) {
        return new RequestLogEntry(
                requestInfo,
                handlerArguments.entrySet().stream()
                        .filter(entry -> isJsonSerializable(entry.getValue()))
                        .map(HandlerArgument::fromArgumentEntry)
                        .toList()
        );
    }

    private static boolean isJsonSerializable(final Object argument) {
        return JSON_UNSERIALIZABLE_CLASSES.stream()
                .noneMatch(predicate -> predicate.test(argument.getClass()));
    }

    private record HandlerArgument(
            String name,
            Object value
    ) {
        private static HandlerArgument fromArgumentEntry(final Entry<String, Object> argumentEntry) {
            final String name = formatName(argumentEntry.getKey(), argumentEntry.getValue());
            final Object value = argumentEntry.getValue();

            return new HandlerArgument(name, value);
        }

        private static String formatName(final String name, final Object value) {
            return String.format("%s %s", value.getClass().getSimpleName(), name);
        }
    }
}

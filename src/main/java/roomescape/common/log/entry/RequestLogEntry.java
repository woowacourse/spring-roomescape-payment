package roomescape.common.log.entry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import roomescape.common.log.context.RequestContext;

public record RequestLogEntry(
        RequestContext requestContext,
        String handlerName,
        List<HandlerArgument> handlerArguments
) {

    private static final List<Predicate<Class<?>>> IGNORE_CLASSES = List.of(
            HttpServletRequest.class::isAssignableFrom,
            HttpServletResponse.class::isAssignableFrom
    );

    public static RequestLogEntry createWithHandlerArguments(
            final RequestContext requestContext,
            final String handlerName,
            final Map<String, Object> handlerArguments
    ) {
        return new RequestLogEntry(
                requestContext,
                handlerName,
                handlerArguments.entrySet().stream()
                        .filter(entry -> isAssignableType(entry.getValue()))
                        .map(HandlerArgument::fromArgumentEntry)
                        .toList()
        );
    }

    private static boolean isAssignableType(final Object argument) {
        return IGNORE_CLASSES.stream()
                .noneMatch(predicate -> predicate.test(argument.getClass()));
    }

    private record HandlerArgument(
            Class<?> clazz,
            String name,
            Object value
    ) {
        private static HandlerArgument fromArgumentEntry(final Entry<String, Object> argumentEntry) {
            final String name = argumentEntry.getKey();
            final Object value = argumentEntry.getValue();

            return new HandlerArgument(value.getClass(), name, value);
        }
    }
}

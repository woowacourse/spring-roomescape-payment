package roomescape.reservation.application.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class TestEventPublisher implements ApplicationEventPublisher {

    private final List<Object> publishedEvents = new ArrayList<>();

    @Override
    public void publishEvent(final Object event) {
        publishedEvents.add(event);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        publishedEvents.add(event);
    }

    public <T> List<T> getEventsOfType(Class<T> eventType) {
        return publishedEvents.stream()
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .collect(Collectors.toList());
    }

    public void clear() {
        publishedEvents.clear();
    }

    public boolean hasEvent(Class<?> eventType) {
        return publishedEvents.stream()
                .anyMatch(eventType::isInstance);
    }
}

package roomescape.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void raise(Object event) {
        publisher.publishEvent(event);
    }
}

package roomescape.common.log.context;

import java.util.UUID;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class RequestIdProvider {

    private final UUID id = UUID.randomUUID();
}

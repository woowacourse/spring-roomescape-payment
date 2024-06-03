package roomescape.service.reservation.pay;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdemPotencyKeyGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}

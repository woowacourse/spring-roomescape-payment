package roomescape.payment.util;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdempotencyKeyGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}

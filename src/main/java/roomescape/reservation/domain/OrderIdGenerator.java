package roomescape.reservation.domain;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class OrderIdGenerator {

    private static final String PREFIX = "ROOMESCAPE_ORDER_";
    private static final Random random = new Random();
    private static final Encoder ENCODER = Base64.getEncoder();
    private static final int LENGTH = 20;

    public static String generateOrderId() {
        double randomValue = random.nextDouble();

        String base64 = ENCODER.encodeToString(
                String.valueOf(randomValue).getBytes()
        );

        String suffix = base64.length() > LENGTH ? base64.substring(0, LENGTH) : base64;

        return PREFIX + suffix;
    }
}

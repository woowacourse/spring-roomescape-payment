package fixture;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import roomescape.member.entity.RoleType;
import roomescape.payment.entity.Payment;

public class PaymentFixture {

    public static Payment create(String paymentKey, String orderId, Long amount, String paymentType) {
        return new Payment(paymentKey, orderId, amount, paymentType);
    }

    public static Payment createDefault() {
        return create(
                createRandomString(10),
                createRandomString(10),
                1000L,
                "NORMAL"
        );
    }

    public static List<Payment> createDefaultList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createDefault())
                .collect(Collectors.toList());
    }

    private static String createRandomString(int length) {
        return java.util.UUID.randomUUID().toString().substring(0, length);
    }
}

package roomescape.fixture;

import roomescape.payment.model.Payment;
import roomescape.theme.model.Theme;

import java.util.ArrayList;
import java.util.List;

public class PaymentFixture {

    public static Payment getOne() {
        return new Payment("abcde", "qwer", 1000L);
    }

    public static List<Payment> get(int count) {
        final List<Payment> payments = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            payments.add(new Payment(
                   "abcd",
                    "qwer",
                    Integer.toUnsignedLong(i))
            );
        }

        return payments;
    }
}

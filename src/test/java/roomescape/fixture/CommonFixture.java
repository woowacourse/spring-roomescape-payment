package roomescape.fixture;

import java.time.LocalDate;
import java.time.LocalTime;

public class CommonFixture {
    public static final String NAME = "mangcho";
    public static final String EMAIL = "mangcho@woowa.net";
    public static final String PASSWORD = "password";
    public static final LocalTime now = LocalTime.now().withNano(0);
    public static final LocalDate today = LocalDate.now();
    public static final LocalDate tomorrow = LocalDate.now().plusDays(1);
    public static final Long amount = 1000L;
    public static final String orderId = "hello";
    public static final String paymentKey = "";
    public static final String paymentType = "";
}

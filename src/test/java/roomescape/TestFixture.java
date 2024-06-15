package roomescape;

import roomescape.member.domain.Email;
import roomescape.member.domain.Member;
import roomescape.member.domain.Name;
import roomescape.payment.application.ProductPayRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static roomescape.member.domain.Role.ADMIN;
import static roomescape.member.domain.Role.USER;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

public class TestFixture {
    public static final String MIA_NAME = "미아";
    public static final String MIA_EMAIL = "testmia@gmail.com";
    public static final LocalDate MIA_RESERVATION_DATE = LocalDate.of(2030, 4, 18);
    public static final LocalTime MIA_RESERVATION_TIME = LocalTime.of(15, 0);

    public static final String TOMMY_NAME = "토미";
    public static final String TOMMY_EMAIL = "testtommy@gmail.com";
    public static final LocalDate TOMMY_RESERVATION_DATE = LocalDate.of(2030, 5, 19);
    public static final LocalTime TOMMY_RESERVATION_TIME = LocalTime.of(15, 0);

    public static final String ADMIN_NAME = "어드민";
    public static final String ADMIN_EMAIL = "admin@gmail.com";

    public static final String WOOTECO_THEME_NAME = "레벨2 탈출";
    public static final String WOOTECO_THEME_DESCRIPTION = "우테코 레벨2를 탈출하는 내용입니다.";
    public static final String THEME_THUMBNAIL = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg";
    public static final String HORROR_THEME_NAME = "호러";
    public static final String HORROR_THEME_DESCRIPTION = "매우 무섭습니다.";

    public static final String TEST_ERROR_MESSAGE = "ERROR MESSAGE";
    public static final String TEST_PASSWORD = "test password";

    public static Member USER_MIA() {
        return new Member(MIA_NAME, MIA_EMAIL, TEST_PASSWORD, USER);
    }

    public static Member USER_MIA(Long id) {
        return new Member(id, new Name(MIA_NAME), new Email(MIA_EMAIL), TEST_PASSWORD, USER);
    }

    public static Member USER_TOMMY() {
        return new Member(TOMMY_NAME, TOMMY_EMAIL, TEST_PASSWORD, USER);
    }

    public static Member USER_ADMIN() {
        return new Member(ADMIN_NAME, ADMIN_EMAIL, TEST_PASSWORD, ADMIN);
    }

    public static Member USER_ADMIN(Long id) {
        return new Member(id, new Name(ADMIN_NAME), new Email(ADMIN_EMAIL), TEST_PASSWORD, ADMIN);
    }

    public static Reservation MIA_RESERVATION(Long id, ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        return new Reservation(id, member, MIA_RESERVATION_DATE, time, theme, status);
    }

    public static Reservation MIA_RESERVATION() {
        return MIA_RESERVATION(new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), USER_MIA(), BOOKING);
    }

    public static Reservation MIA_RESERVATION(ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        return new Reservation(member, MIA_RESERVATION_DATE, time, theme, status);
    }

    public static Reservation TOMMY_RESERVATION() {
        return new Reservation(USER_TOMMY(), TOMMY_RESERVATION_DATE, new ReservationTime(TOMMY_RESERVATION_TIME),
                WOOTECO_THEME(), BOOKING);
    }

    public static Reservation TOMMY_RESERVATION(ReservationTime time, Theme theme, Member member, ReservationStatus status) {
        return new Reservation(member, TOMMY_RESERVATION_DATE, time, theme, status);
    }

    public static Theme WOOTECO_THEME() {
        return new Theme(WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    public static Theme WOOTECO_THEME(Long id) {
        return new Theme(id, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    public static Theme HORROR_THEME() {
        return new Theme(HORROR_THEME_NAME, HORROR_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    public static Theme HORROR_THEME(Long id) {
        return new Theme(id, HORROR_THEME_NAME, HORROR_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    public static ProductPayRequest PRODUCT_PAY_REQUEST() {
        return new ProductPayRequest("key", "orderId", BigDecimal.valueOf(1000L), "none");
    }

    public static ProductPayRequest PRODUCT_PAY_REQUEST(String key, String orderId) {
        return new ProductPayRequest(key, orderId, BigDecimal.valueOf(1000L), "none");
    }

    public static Payment SIMPLE_PAYMENT(String paymentKey, String orderId, PaymentProduct paymentProduct) {
        return new Payment(paymentKey, orderId, BigDecimal.valueOf(1000L), paymentProduct);
    }
}

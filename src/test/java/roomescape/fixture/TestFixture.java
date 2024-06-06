package roomescape.fixture;

import java.time.LocalDate;
import java.util.List;

import roomescape.domain.member.Member;
import roomescape.domain.member.Name;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;

public class TestFixture {

    // member, reservationTime, theme

    public static final List<Member> MEMBERS = List.of(
            new Member(1L, new Name("제우스"), "zeus@woowa.com", "1q2w3e4r", Role.ADMIN),
            new Member(2L, new Name("냥인"), "cutehuman@woowa.com", "password", Role.MEMBER)
    );

    public static final List<ReservationTime> TIMES = List.of(
            new ReservationTime(1L, "10:00"),
            new ReservationTime(2L, "22:00")
    );

    public static final List<Theme> THEMES = List.of(
            new Theme(1L, "테마1", "테마설명1", "thumbnail_1.jpg"),
            new Theme(2L, "테마2", "테마설명2", "thumbnail_2.jpg")
    );

    public static final List<Reservation> RESERVATIONS = List.of(
            new Reservation(1L, memberFixture(1), LocalDate.parse("2024-06-01"), timeFixture(1), themeFixture(1), ReservationStatus.RESERVED),
            new Reservation(2L, memberFixture(2), LocalDate.parse("2024-06-01"), timeFixture(2), themeFixture(2), ReservationStatus.WAITING)
    );

    public static final String ADMIN_NAME = "냥인";
    public static final String ADMIN_EMAIL = "nyangin@email.com";
    public static final String ADMIN_PASSWORD = "1234";
    public static final String MEMBER_TENNY_NAME = "테니";
    public static final String MEMBER_TENNY_EMAIL = "tenny@email.com";
    public static final String MEMBER_MIA_NAME = "미아";
    public static final String MEMBER_MIA_EMAIL = "mia@email.com";
    public static final String MEMBER_BROWN_NAME= "브라운";
    public static final String MEMBER_BROWN_EMAIL = "brown@email.com";
    public static final String MEMBER_PASSWORD = "1234";

    public static final LocalDate DATE_MAY_EIGHTH = LocalDate.parse("2034-05-08");
    public static final LocalDate DATE_MAY_NINTH = LocalDate.parse("2034-05-09");

    public static final String START_AT_SIX = "18:00";
    public static final String START_AT_SEVEN = "19:00";

    public static final String THEME_HORROR_NAME = "호러";
    public static final String THEME_HORROR_DESCRIPTION = "매우 무섭습니다.";
    public static final String THEME_HORROR_THUMBNAIL = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg";

    public static final String THEME_DETECTIVE_NAME = "추리";
    public static final String THEME_DETECTIVE_DESCRIPTION = "매우 어렵습니다.";
    public static final String THEME_DETECTIVE_THUMBNAIL = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg";

    public static final String PAYMENT_KEY = "tgen_20240513184816ZSAZ9";
    public static final String ORDER_ID = "MC4wNDYzMzA0OTc2MDgy";
    public static final Long AMOUNT = 1000L;

    private TestFixture() {
    }

    public static Member ADMIN() {
        return new Member(new Name(ADMIN_NAME), ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
    }

    public static Member ADMIN(final Long id) {
        return new Member(id, new Name(ADMIN_NAME), ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
    }

    public static Member MEMBER_TENNY() {
        return new Member(new Name(MEMBER_TENNY_NAME), MEMBER_TENNY_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
    }

    public static Member MEMBER_TENNY(final Long id) {
        return new Member(id, new Name(MEMBER_TENNY_NAME), MEMBER_TENNY_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
    }

    public static Member MEMBER_MIA() {
        return new Member(new Name(MEMBER_MIA_NAME), MEMBER_MIA_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
    }

    public static Member MEMBER_BROWN() {
        return new Member(new Name(MEMBER_BROWN_NAME), MEMBER_BROWN_EMAIL, MEMBER_PASSWORD, Role.MEMBER);
    }

    public static Member memberFixture(int id) {
        return MEMBERS.get(id - 1);
    }

    public static ReservationTime RESERVATION_TIME_SIX() {
        return new ReservationTime(START_AT_SIX);
    }

    public static ReservationTime RESERVATION_TIME_SIX(final Long id) {
        return new ReservationTime(id, START_AT_SIX);
    }

    public static ReservationTime RESERVATION_TIME_SEVEN() {
        return new ReservationTime(START_AT_SEVEN);
    }

    public static ReservationTime RESERVATION_TIME_SEVEN(final Long id) {
        return new ReservationTime(id, START_AT_SEVEN);
    }

    public static ReservationTime timeFixture(int id) {
        return TIMES.get(id - 1);
    }

    public static Theme THEME_HORROR() {
        return new Theme(THEME_HORROR_NAME, THEME_HORROR_DESCRIPTION, THEME_HORROR_THUMBNAIL);
    }

    public static Theme THEME_HORROR(final Long id) {
        return new Theme(id, THEME_HORROR_NAME, THEME_HORROR_DESCRIPTION, THEME_HORROR_THUMBNAIL);
    }

    public static Theme THEME_DETECTIVE() {
        return new Theme(THEME_DETECTIVE_NAME, THEME_DETECTIVE_DESCRIPTION, THEME_DETECTIVE_THUMBNAIL);
    }

    public static Theme THEME_DETECTIVE(final Long id) {
        return new Theme(id, THEME_DETECTIVE_NAME, THEME_DETECTIVE_DESCRIPTION, THEME_DETECTIVE_THUMBNAIL);
    }

    public static Theme themeFixture(int id) {
        return THEMES.get(id - 1);
    }

    public static Reservation reservationFixture(int id) {
        return RESERVATIONS.get(id - 1);
    }
}

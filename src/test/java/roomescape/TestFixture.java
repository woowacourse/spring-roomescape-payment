package roomescape;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.domain.Password;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public class TestFixture {

    public static final LocalDate DEFAULT_DATE = LocalDate.now().plusDays(1);

    public static Member createAdminMember() {
        return createAdminMember("관리자", "admin@email.com", "password");
    }

    public static Member createDefaultMember_1() {
        return createMember("회원1", "test1@email.com", "1234");
    }

    public static Member createMember(String name, String email, String password) {
        return Member.builder()
                .name(name)
                .role(MemberRole.MEMBER)
                .email(email)
                .password(Password.createForMember(password))
                .build();
    }

    public static Member createAdminMember(String name, String email, String password) {
        return Member.builder()
                .name(name)
                .role(MemberRole.ADMIN)
                .email(email)
                .password(Password.createForMember(password))
                .build();
    }

    public static ReservationTime createTimeAt_10() {
        return ReservationTime.from(LocalTime.of(10,0));
    }

    public static ReservationTime createTimeAt(LocalTime time) {
        return ReservationTime.from(time);
    }

    public static Theme createDefaultTheme() {
        return Theme.of("테마1", "테마1입니다.", "테마1썸네일.jpg");
    }

    public static Theme createThemeByName(String name) {
        return Theme.of(name, name+"설명 입니다.", name+".jpg");
    }

    public static Reservation createReservation_1() {
        Member member = createMember("멍구1", "test1@email.com", "1234");
        ReservationTime time = createTimeAt(LocalTime.of(10, 0));
        Theme theme = createThemeByName("테마1");
        return createReservationOf(member, DEFAULT_DATE, time, theme);
    }

    public static Reservation createReservation_2() {
        Member member = createMember("멍구2", "test2@email.com", "1234");
        ReservationTime time = createTimeAt(LocalTime.of(11, 0));
        Theme theme = createThemeByName("테마2");
        return createReservationOf(member, DEFAULT_DATE, time, theme);
    }

    public static Reservation createReservationOf(Member member, LocalDate date, ReservationTime time, Theme theme) {
        return Reservation.builder()
                .member(member)
                .roomEscapeInformation(RoomEscapeInformation.builder()
                        .id(null)
                        .date(date)
                        .time(time)
                        .theme(theme)
                        .build())
                .build();
    }

    public static Claims createClaims(final Member member) {
        return Jwts.claims()
                .subject(member.getId().toString())
                .build();
    }

}

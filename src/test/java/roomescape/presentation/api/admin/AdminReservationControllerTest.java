package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.fixture.Fixture;
import roomescape.infra.payment.PaymentClient;
import roomescape.infra.payment.PaymentResponse;
import roomescape.presentation.BaseControllerTest;
import roomescape.presentation.dto.request.AdminReservationWebRequest;

class AdminReservationControllerTest extends BaseControllerTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @SpyBean
    private PaymentClient paymentClient;

    @Test
    @DisplayName("조건에 맞는 예약들(예약 대기 제외)을 조회하고 성공할 경우 200을 반환한다.")
    void getReservationsByConditions() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        Member member1 = memberRepository.save(new Member("ex2@gmail.com", "password", "유저2", Role.USER));
        reservationRepository.save(new Reservation(new ReservationDetail(date, reservationTime, theme), member));
        waitingRepository.save(new Waiting(new ReservationDetail(date, reservationTime, theme), member1));

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("date", hasItems("2024-04-09"))
                .body("member.id", hasItems(2))
                .body("time.id", hasItems(1))
                .body("theme.id", hasItems(1));
    }

    @Nested
    @DisplayName("예약을 생성하는 경우")
    class AddReservation {

        @Test
        @DisplayName("성공할 경우 201을 반환한다.")
        void addAdminReservation() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            // given
            BDDMockito.doReturn(new PaymentResponse("DONE", "123"))
                    .when(paymentClient).confirmPayment(any());

            ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
            Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

            AdminReservationWebRequest request = new AdminReservationWebRequest(
                    LocalDate.of(2024, 6, 22),
                    reservationTime.getId(),
                    theme.getId(),
                    "test-paymentKey",
                    "test-orderId",
                    1L,
                    "test-paymentType",
                    admin.getId()
            );

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("date", equalTo("2024-06-22"))
                    .body("member.name", equalTo("어드민"))
                    .body("time.startAt", equalTo("11:00"))
                    .body("theme.name", equalTo("테마 이름"));
        }

        @Test
        @DisplayName("어드민 권한이 아닐 경우 403을 반환한다.")
        void addAdminReservationFailWhenNotAdmin() {
            Member user = memberRepository.save(Fixture.MEMBER_USER);
            String token = tokenProvider.createToken(user.getId().toString());

            AdminReservationWebRequest request = new AdminReservationWebRequest(
                    LocalDate.of(2024, 6, 22),
                    1L,
                    1L,
                    null,
                    null,
                    null,
                    null,
                    1L
            );

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Test
    @DisplayName("예약을 삭제하고 성공할 경우 204를 반환한다.")
    void deleteReservationById() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);
        Reservation savedReservation = reservationRepository.save(new Reservation(detail, member));

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/admin/reservations/" + savedReservation.getId())
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}

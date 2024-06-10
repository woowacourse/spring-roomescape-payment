package roomescape.web.acceptance;

import static org.hamcrest.Matchers.is;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.support.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.support.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.support.fixture.MemberFixture.MEMBER_SOLAR;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.ThemeFixture.THEME_DATABASE;
import static roomescape.support.fixture.ThemeFixture.THEME_JAVA;
import static roomescape.support.fixture.TimeFixture.ONE_PM;
import static roomescape.support.fixture.TimeFixture.THREE_PM;
import static roomescape.support.fixture.TimeFixture.TWO_PM;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;
import roomescape.support.mock.FakePayment;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberReservationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    Member jazz;
    Member bri;
    Member solar;
    Member sun;

    ReservationTime onePm;
    ReservationTime twoPm;
    ReservationTime threePm;

    Theme bed;
    Theme java;
    Theme database;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jazz = memberRepository.save(MEMBER_JAZZ.create());
        bri = memberRepository.save(MEMBER_BRI.create());
        solar = memberRepository.save(MEMBER_SOLAR.create());
        sun = memberRepository.save(MEMBER_SUN.create());

        onePm = timeRepository.save(ONE_PM.create());
        twoPm = timeRepository.save(TWO_PM.create());
        threePm = timeRepository.save(THREE_PM.create());

        bed = themeRepository.save(THEME_BED.create());
        java = themeRepository.save(THEME_JAVA.create());
        database = themeRepository.save(THEME_DATABASE.create());
    }

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("멤버가 예약을 생성하는데 성공하면 응답과 201 상태 코드를 반환한다.")
    @Test
    void return_201_when_create_reservation_member() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String memberToken = jwtProvider.encode(jazz);

        UserReservationRequest reservationCreate = new UserReservationRequest(tomorrow, 1L, 1L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .contentType(ContentType.JSON)
                .body(reservationCreate)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("멤버가 예약 결제에 성공하면 응답과 200 상태 코드를 반환한다.")
    @Test
    void return_201_when_success_payment() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        reservationRepository.save(reservation(jazz, java, tomorrow, onePm, Status.RESERVED));
        String memberToken = jwtProvider.encode(jazz);

        ReservationPaymentRequest request = new ReservationPaymentRequest(1L,
                FakePayment.AMOUNT, FakePayment.ORDER_ID, FakePayment.PAYMENT_KEY);

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations/payment")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("멤버가 자신의 예약을 조회하면 응답과 200 상태 코드를 반환한다.")
    void return_200_when_find_reservations_member() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String memberToken = jwtProvider.encode(jazz);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .contentType(ContentType.JSON)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @DisplayName("멤버가 자신의 대기중인 예약을 삭제하는데 성공하면 응답과 204 상태코드를 반환한다.")
    @Test
    void return_204_when_delete_waiting_reservation() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String memberToken = jwtProvider.encode(jazz);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        Reservation waiting = reservationRepository.save(reservation(jazz, bed, tomorrow, onePm, WAITING));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .when().delete("/waitings/" + waiting.getId())
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("멤버가 자신의 예약을 삭제하는데 성공하면 응답과 204 상태코드를 반환한다.")
    @Test
    void return_204_when_delete_reservation() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String memberToken = jwtProvider.encode(jazz);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        Reservation reservation = reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, onePm, WAITING));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }
}

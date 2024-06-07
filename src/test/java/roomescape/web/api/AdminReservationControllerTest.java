package roomescape.web.api;

import static org.hamcrest.Matchers.is;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.fixture.MemberFixture.MEMBER_SOLAR;
import static roomescape.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.fixture.ThemeFixture.THEME_BED;
import static roomescape.fixture.ThemeFixture.THEME_DATABASE;
import static roomescape.fixture.ThemeFixture.THEME_JAVA;
import static roomescape.fixture.TimeFixture.ONE_PM;
import static roomescape.fixture.TimeFixture.THREE_PM;
import static roomescape.fixture.TimeFixture.TWO_PM;

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
import roomescape.application.dto.request.reservation.ReservationRequest;
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

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminReservationControllerTest {

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

    @DisplayName("어드민이 예약을 생성하는데 성공하면 응답과 201 상태 코드를 반환한다.")
    @Test
    void return_201_when_create_reservation_admin() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String adminToken = jwtProvider.encode(solar);

        ReservationRequest reservationCreate = new ReservationRequest(tomorrow, 1L, 1L, 1L);

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .body(reservationCreate)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("어드민이 모든 예약을 조회하면 응답과 200 상태 코드를 반환한다.")
    void return_200_when_find_reservations_member() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String adminToken = jwtProvider.encode(solar);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @Test
    @DisplayName("어드민이 기간 범위, 회원, 테마와 일치하는 예약을 검색하면 응답과 200 상태 코드를 반환한다.")
    void return_200_when_search_reservations_admin() {
        String today = LocalDate.now().toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String adminToken = jwtProvider.encode(solar);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .when().get("/admin/reservations/search?from=" + today + "&to=" + tomorrow + "&memberId=" + sun.getId()
                        + "&themeId=" + bed.getId())
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("어드민이 대기중인 예약 목록을 검색하면 응답과 200 상태 코드를 반환한다.")
    void return_200_when_waiting_reservations_admin() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String adminToken = jwtProvider.encode(solar);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, onePm, WAITING));
        reservationRepository.save(reservation(jazz, bed, tomorrow, threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));
        reservationRepository.save(reservation(bri, database, tomorrow, threePm, WAITING));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @DisplayName("어드민이 대기중인 예약을 삭제하는데 성공하면 응답과 204 상태코드를 반환한다.")
    @Test
    void return_204_when_delete_waiting_reservation() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String adminToken = jwtProvider.encode(solar);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        Reservation waiting = reservationRepository.save(reservation(jazz, bed, tomorrow, onePm, WAITING));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/waitings/" + waiting.getId())
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민이 예약을 삭제하는데 성공하면 응답과 204 상태코드를 반환한다.")
    @Test
    void return_204_when_delete_reservation() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        String adminToken = jwtProvider.encode(solar);

        reservationRepository.save(reservation(sun, bed, tomorrow, onePm, RESERVED));
        Reservation reservation = reservationRepository.save(reservation(jazz, bed, tomorrow, twoPm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, tomorrow, onePm, WAITING));
        reservationRepository.save(reservation(solar, database, tomorrow, threePm, RESERVED));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }
}

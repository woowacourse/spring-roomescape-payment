package roomescape.api;

import static org.hamcrest.Matchers.is;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;
import static roomescape.test.fixture.DateFixture.TODAY;
import static roomescape.test.fixture.DateFixture.YESTERDAY;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.member.repository.MemberRepository;
import roomescape.mvc.payment.repository.PaymentRepository;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.repository.ReservationRepository;
import roomescape.mvc.reservation.request.AdminReservationRequest;
import roomescape.mvc.reservation.request.ReservationCreationRequest;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.theme.repository.ThemeRepository;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.time.repository.ReservationTimeRepository;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.repository.WaitingRepository;
import roomescape.test.stub.PaymentClientStub;
import roomescape.utility.JwtTokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(value = {PaymentClientStub.class})
class ReservationApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository timeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private PaymentRepository paymentRepository;

    @AfterEach
    void setup() {
        reservationRepository.deleteAll();
        waitingRepository.deleteAll();
        paymentRepository.deleteAll();
        memberRepository.deleteAll();
        timeRepository.deleteAll();
        themeRepository.deleteAll();
    }

    @DisplayName("모든 예약을 조회할 수 있다.")
    @Test
    void canFindAllReservations() {
        // given
        Member admin = memberRepository.save(
                Member.createWithoutId(Role.ADMIN, "관리자", "admin@email.com", "qwer1234!"));
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원", "member@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member));
        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(TODAY, time, theme, member));
        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, member));

        AccessTokenContent tokenContent = new AccessTokenContent(admin.getId(), admin.getRole(), admin.getName());
        String accessToken = tokenProvider.createAccessToken(tokenContent);

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(3));
    }

    @DisplayName("필터를 통해 예약을 검색할 수 있다.")
    @Test
    void canSearchReservationsByFilter() {
        // given
        Member admin = memberRepository.save(
                Member.createWithoutId(Role.ADMIN, "관리자", "admin@email.com", "qwer1234!"));
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원", "member@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member));
        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(TODAY, time, theme, member));
        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, member));

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", member.getId());
        params.put("themeId", theme.getId());
        params.put("from", TODAY.toString());
        params.put("to", NEXT_DAY.toString());

        String accessToken = tokenProvider.createAccessToken(
                new AccessTokenContent(admin.getId(), admin.getRole(), admin.getName()));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .queryParams(params)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2));
    }

    @DisplayName("특정 회원의 예약을 예약 상태와 함께 조회할 수 있다.")
    @Test
    void canFindAllReservationStateByMember() {
        // given
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원1", "member1@email.com", "qwer1234!"));
        Member otherMember = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원2", "member2@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member));
        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(TODAY, time, theme, member));

        reservationRepository.save(Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, otherMember));
        waitingRepository.save(Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member));

        String accessToken = tokenProvider.createAccessToken(
                new AccessTokenContent(member.getId(), member.getRole(), member.getName()));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .when().get("/reservations/state")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("reservationResponses.size()", is(2))
                .body("waitingWithRankResponses.size()", is(1));
    }

    @DisplayName("자신의 예약을 추가할 수 있다.")
    @Test
    void canAddReservation() {
        // given
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원1", "member1@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        String accessToken = tokenProvider.createAccessToken(
                new AccessTokenContent(member.getId(), member.getRole(), member.getName()));

        ReservationCreationRequest creationContent =
                new ReservationCreationRequest(theme.getId(), NEXT_DAY, time.getId(),
                        "asfqwe123!", "setqerwe123!", "NORMAL", 10000L);

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .body(creationContent)
                .when().post("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("관리자는 다른 사람의 예약을 추가할 수 있다.")
    @Test
    void canAddReservationByAdmin() {
        // given
        Member admin = memberRepository.save(
                Member.createWithoutId(Role.ADMIN, "관리자", "admin@email.com", "qwer1234!"));
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원1", "member1@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        String accessToken = tokenProvider.createAccessToken(
                new AccessTokenContent(admin.getId(), admin.getRole(), admin.getName()));

        AdminReservationRequest creationContent =
                new AdminReservationRequest(member.getId(), theme.getId(), NEXT_DAY, time.getId());

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .body(creationContent)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("ID를 통해 예약을 제거할 수 있다.")
    @Test
    void canDeleteReservationById() {
        // given
        Member admin = memberRepository.save(
                Member.createWithoutId(Role.ADMIN, "관리자", "admin@email.com", "qwer1234!"));
        Member member = memberRepository.save(
                Member.createWithoutId(Role.GENERAL, "회원", "member@email.com", "qwer1234!"));
        ReservationTime time = timeRepository.save(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(
                Theme.createWithoutId("테마", "설명", "섬네일"));

        Reservation reservation = reservationRepository.save(
                Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member));

        String accessToken = tokenProvider.createAccessToken(
                new AccessTokenContent(admin.getId(), admin.getRole(), admin.getName()));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .cookie("access", accessToken)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}

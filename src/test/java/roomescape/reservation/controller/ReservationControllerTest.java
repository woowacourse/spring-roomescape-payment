package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.domain.CanceledPayment;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.CanceledPaymentRepository;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationControllerTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CanceledPaymentRepository canceledPaymentRepository;

    @MockBean
    private PaymentClient paymentClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("처음으로 등록하는 예약의 id는 1이다.")
    void firstPost() {
        String accessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        LocalTime time = LocalTime.of(17, 30);
        LocalDate date = LocalDate.now().plusDays(1L);

        reservationTimeRepository.save(new ReservationTime(time));
        themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));

        Map<String, String> reservationParams = Map.of(
                "date", date.toString(),
                "timeId", "1",
                "themeId", "1",
                "paymentKey", "pk",
                "orderId", "oi",
                "amount", "1000",
                "paymentType", "DEFAULT"
        );

        when(paymentClient.confirmPayment(any(PaymentRequest.class))).thenReturn(
                new PaymentResponse("pk", "oi", OffsetDateTime.of(date, time, ZoneOffset.ofHours(9)), 1000L));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .header("Cookie", accessTokenCookie)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1))
                .header("Location", "/reservations/1");
    }

    @Test
    @DisplayName("대기중인 예약을 취소한다.")
    void cancelWaiting() {
        // given
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        String accessTokenCookie = getAccessTokenCookieByLogin("email@email.com", "password");

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member1 = memberRepository.save(new Member("name1", "email1r@email.com", "password", Role.MEMBER));

        // when
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(1), reservationTime, theme, member1,
                ReservationStatus.CONFIRMED));
        Reservation waiting = reservationRepository.save(
                new Reservation(LocalDate.now().plusDays(1), reservationTime, theme, member,
                        ReservationStatus.WAITING));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessTokenCookie)
                .when().delete("/reservations/waiting/{id}", waiting.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("회원은 자신이 아닌 다른 회원의 예약을 취소할 수 없다.")
    void cantCancelOtherMembersWaiting() {
        // given
        Member confirmedMember = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        String accessTokenCookie = getAccessTokenCookieByLogin("email@email.com", "password");

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member waitingMember = memberRepository.save(new Member("name1", "email1r@email.com", "password", Role.MEMBER));

        // when
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(1), reservationTime, theme, confirmedMember,
                ReservationStatus.CONFIRMED));
        Reservation waiting = reservationRepository.save(
                new Reservation(LocalDate.now().plusDays(1), reservationTime, theme, waitingMember,
                        ReservationStatus.WAITING));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessTokenCookie)
                .when().delete("/reservations/waiting/{id}", waiting.getId())
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("관리자 권한이 있으면 전체 예약정보를 조회할 수 있다.")
    void readEmptyReservations() {
        // given
        String accessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        // when
        reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, member, ReservationStatus.CONFIRMED));
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(1), reservationTime, theme, member,
                ReservationStatus.CONFIRMED));
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(2), reservationTime, theme, member,
                ReservationStatus.CONFIRMED));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.reservations.size()", is(3));
    }

    @Test
    @DisplayName("예약 취소는 관리자만 할 수 있다.")
    void canRemoveMyReservation() {
        // given
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        String accessTokenCookie = getAccessTokenCookieByLogin(member.getEmail(), member.getPassword());

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Reservation reservation = reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, member, ReservationStatus.CONFIRMED));

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessTokenCookie)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("관리자가 대기중인 예약을 거절한다.")
    void denyWaiting() {
        // given
        String adminTokenCookie = getAdminAccessTokenCookieByLogin("admin@email.com", "password");

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member confirmedMember = memberRepository.save(new Member("name1", "email@email.com", "password", Role.MEMBER));
        Member waitingMember = memberRepository.save(new Member("name1", "email1@email.com", "password", Role.MEMBER));

        reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, confirmedMember, ReservationStatus.CONFIRMED));
        Reservation waiting = reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, waitingMember, ReservationStatus.WAITING));

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", adminTokenCookie)
                .when().post("/reservations/waiting/{id}/deny", waiting.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("본인의 예약이 아니면 예약 정보를 삭제할 수 없으며 403 Forbidden 을 Response 받는다.")
    void canRemoveAnotherReservation() {
        // given
        Member member = memberRepository.save(new Member("name", "member1@email.com", "password", Role.MEMBER));
        String accessTokenCookie = getAccessTokenCookieByLogin(member.getEmail(), member.getPassword());

        Member anotherMember = memberRepository.save(new Member("name1", "member2@email.com", "password", Role.MEMBER));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));

        Reservation reservation = reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, anotherMember, ReservationStatus.CONFIRMED));

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessTokenCookie)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("본인의 예약이 아니더라도 관리자 권한이 있으면 예약 정보를 삭제할 수 있다.")
    void readReservationsSizeAfterPostAndDelete() {
        // given
        Member member = memberRepository.save(new Member("name", "admin@admin.com", "password", Role.ADMIN));
        String accessTokenCookie = getAccessTokenCookieByLogin(member.getEmail(), member.getPassword());

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member anotherMember = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        Reservation reservation = reservationRepository.save(
                new Reservation(LocalDate.now(), reservationTime, theme, anotherMember, ReservationStatus.CONFIRMED));

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessTokenCookie)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }

    @ParameterizedTest
    @MethodSource("requestValidateSource")
    @DisplayName("예약 생성 시, 요청 값에 공백 또는 null이 포함되어 있으면 400 에러를 발생한다.")
    void validateBlankRequest(Map<String, String> invalidRequestBody) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(invalidRequestBody)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    private static Stream<Map<String, String>> requestValidateSource() {
        return Stream.of(
                Map.of("timeId", "1",
                        "themeId", "1"),

                Map.of("date", LocalDate.now().plusDays(1L).toString(),
                        "themeId", "1"),

                Map.of("date", LocalDate.now().plusDays(1L).toString(),
                        "timeId", "1"),

                Map.of("date", " ",
                        "timeId", "1",
                        "themeId", "1"),

                Map.of("date", LocalDate.now().plusDays(1L).toString(),
                        "timeId", " ",
                        "themeId", "1"),

                Map.of("date", LocalDate.now().plusDays(1L).toString(),
                        "timeId", "1",
                        "themeId", " ")
        );
    }

    @Test
    @DisplayName("예약 생성 시, 정수 요청 데이터에 문자가 입력되어오면 400 에러를 발생한다.")
    void validateRequestDataFormat() {
        Map<String, String> invalidTypeRequestBody = Map.of(
                "date", LocalDate.now().plusDays(1L).toString(),
                "timeId", "1",
                "themeId", "한글"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(invalidTypeRequestBody)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @ParameterizedTest
    @DisplayName("모든 예약 / 대기 중인 예약 / 현재 로그인된 회원의 예약 및 대기를 조회한다.")
    @CsvSource(value = {"/reservations, reservations, 2", "/reservations/waiting, reservations, 1",
            "/reservations-mine, myReservationResponses, 3"}, delimiter = ',')
    void getAllReservations(String requestURI, String responseFieldName, int expectedSize) {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        ReservationTime time1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(18, 30)));
        ReservationTime time2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(19, 30)));

        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        String accessToken = getAccessTokenCookieByLogin("email@email.com", "password");

        // when : 예약은 2개, 예약 대기는 1개 조회되어야 한다.
        reservationRepository.save(new Reservation(date, time, theme, member, ReservationStatus.CONFIRMED));
        reservationRepository.save(
                new Reservation(date, time1, theme, member, ReservationStatus.CONFIRMED_PAYMENT_REQUIRED));
        reservationRepository.save(new Reservation(date, time2, theme, member, ReservationStatus.WAITING));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessToken)
                .when().get(requestURI)
                .then().log().all()
                .statusCode(200)
                .body("data." + responseFieldName + ".size()", is(expectedSize));
    }

    @Test
    @DisplayName("예약을 삭제할 때, 승인되었으나 결제 대기중인 예약은 결제 취소 없이 바로 삭제한다.")
    void removeNotPaidReservation() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        String accessToken = getAdminAccessTokenCookieByLogin("admin@email.com", "password");

        // when
        Reservation saved = reservationRepository.save(new Reservation(date, time, theme,
                memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER)),
                ReservationStatus.CONFIRMED_PAYMENT_REQUIRED));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessToken)
                .when().delete("/reservations/{id}", saved.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("이미 결제가 된 예약은 삭제 후 결제 취소를 요청한다.")
    void removePaidReservation() {
        // given
        String accessToken = getAdminAccessTokenCookieByLogin("admin@email.com", "password");
        LocalDate date = LocalDate.now().plusDays(1);
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(17, 30)));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        Reservation saved = reservationRepository.save(
                new Reservation(date, time, theme, member, ReservationStatus.CONFIRMED));
        Payment savedPayment = paymentRepository.save(
                new Payment("pk", "oi", 1000L, saved, OffsetDateTime.now().minusHours(1L)));

        // when
        when(paymentClient.cancelPayment(any(PaymentCancelRequest.class)))
                .thenReturn(new PaymentCancelResponse("pk", "고객 요청", savedPayment.getTotalAmount(),
                        OffsetDateTime.now()));

        // then
        RestAssured.given().log().all()
                .port(port)
                .header("Cookie", accessToken)
                .when().delete("/reservations/{id}", saved.getId())
                .then().log().all()
                .statusCode(204);

    }

    @Test
    @DisplayName("예약을 추가할 때, 결제 승인 이후에 예외가 발생하면 결제를 취소한 뒤 결제 취소 테이블에 취소 정보를 저장한다.")
    void saveReservationWithCancelPayment() {
        // given
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(1L);
        LocalDate date = localDateTime.toLocalDate();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(localDateTime.toLocalTime()));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        String accessToken = getAccessTokenCookieByLogin(member.getEmail(), member.getPassword());

        // when : 이전 날짜의 예약을 추가하여 결제 승인 이후 DB 저장 과정에서 예외를 발생시킨다.
        String paymentKey = "pk";
        OffsetDateTime canceledAt = OffsetDateTime.now().plusHours(1L);
        when(paymentClient.confirmPayment(any(PaymentRequest.class)))
                .thenReturn(new PaymentResponse(paymentKey, "oi", canceledAt.minusHours(1L), 1000L));

        when(paymentClient.cancelPayment(any(PaymentCancelRequest.class)))
                .thenReturn(new PaymentCancelResponse(paymentKey, "고객 요청", 1000L, canceledAt));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .header("Cookie", accessToken)
                .body(new ReservationRequest(date, time.getId(), theme.getId(), "pk", "oi", 1000L, "DEFAULT"))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);

        // then
        Optional<CanceledPayment> canceledPaymentOptional = canceledPaymentRepository.findByPaymentKey(paymentKey);
        assertThat(canceledPaymentOptional).isNotNull();
        assertThat(canceledPaymentOptional.get().getCanceledAt()).isEqualTo(canceledAt);
        assertThat(canceledPaymentOptional.get().getCancelReason()).isEqualTo("고객 요청");
        assertThat(canceledPaymentOptional.get().getCancelAmount()).isEqualTo(1000L);
        assertThat(canceledPaymentOptional.get().getApprovedAt()).isEqualTo(canceledAt.minusHours(1L));

    }

    @DisplayName("테마만을 이용하여 예약을 조회한다.")
    @ParameterizedTest(name = "테마 ID={0}로 조회 시 {1}개의 예약이 조회된다.")
    @CsvSource(value = {"1/4", "2/3"}, delimiter = '/')
    @Sql({"/truncate.sql", "/test_search_data.sql"})
    void searchByTheme(String themeId, int expectedCount) {
        RestAssured.given().log().all()
                .port(port)
                .param("themeId", themeId)
                .param("memberId", "")
                .param("dateFrom", "")
                .param("dateTo", "")
                .header("cookie", getAdminAccessTokenCookieByLogin("admin@email.com", "password"))
                .when().get("/reservations/search")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.reservations.size()", is(expectedCount));
    }

    @DisplayName("시작 날짜만을 이용하여 예약을 조회한다.")
    @ParameterizedTest(name = "오늘 날짜보다 {0}일 전인 날짜를 시작 날짜로 조회 시 {1}개의 예약이 조회된다.")
    @CsvSource(value = {"1/1", "7/7"}, delimiter = '/')
    @Sql({"/truncate.sql", "/test_search_data.sql"})
    void searchByFromDate(int minusDays, int expectedCount) {
        RestAssured.given().log().all()
                .port(port)
                .param("themeId", "")
                .param("memberId", "")
                .param("dateFrom", LocalDate.now().minusDays(minusDays).toString())
                .param("dateTo", "")
                .header("cookie", getAdminAccessTokenCookieByLogin("admin@email.com", "password"))
                .when().get("/reservations/search")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.reservations.size()", is(expectedCount));
    }

    @DisplayName("종료 날짜만을 이용하여 예약을 조회한다..")
    @ParameterizedTest(name = "오늘 날짜보다 {0}일 전인 날짜를 종료 날짜로 조회 시 {1}개의 예약이 조회된다.")
    @CsvSource(value = {"1/7", "3/5", "7/1"}, delimiter = '/')
    @Sql({"/truncate.sql", "/test_search_data.sql"})
    void searchByToDate(int minusDays, int expectedCount) {
        RestAssured.given().log().all()
                .port(port)
                .param("themeId", "")
                .param("memberId", "")
                .param("dateFrom", "")
                .param("dateTo", LocalDate.now().minusDays(minusDays).toString())
                .header("cookie", getAdminAccessTokenCookieByLogin("admin@email.com", "password"))
                .when().get("/reservations/search")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.reservations.size()", is(expectedCount));
    }

    private String getAccessTokenCookieByLogin(final String email, final String password) {
        Map<String, String> loginParams = Map.of(
                "email", email,
                "password", password
        );

        String accessToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(loginParams)
                .when().post("/login")
                .then().log().all().extract().cookie("accessToken");

        return "accessToken=" + accessToken;
    }

    private String getAdminAccessTokenCookieByLogin(final String email, final String password) {
        memberRepository.save(new Member("이름", email, password, Role.ADMIN));

        Map<String, String> loginParams = Map.of(
                "email", email,
                "password", password
        );

        String accessToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(loginParams)
                .when().post("/login")
                .then().log().all().extract().cookie("accessToken");

        return "accessToken=" + accessToken;
    }
}

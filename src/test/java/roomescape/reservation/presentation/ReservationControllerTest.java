package roomescape.reservation.presentation;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.DatabaseCleaner;
import roomescape.TestConfig;
import roomescape.member.presentation.fixture.MemberFixture;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.fixture.ReservationFixture;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
class ReservationControllerTest {
    private final DatabaseCleaner databaseCleaner;
    private final ReservationFixture reservationFixture = new ReservationFixture();
    private final MemberFixture memberFixture = new MemberFixture();

    private RequestSpecification spec;

    @LocalServerPort
    int port;

    @Autowired
    ReservationControllerTest(final DatabaseCleaner databaseCleaner) {
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        databaseCleaner.clear();
        databaseCleaner.setUserInfo();

        this.spec = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("예약 추가 테스트")
    void createReservationTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        final ReservationRequest reservation = reservationFixture.createReservationRequest(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL"
        );

        // when - then
        RestAssured.given(this.spec).log().all()
                .filter(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date")
                                        .description("예약 날짜 (형식: yyyy-MM-dd)"),
                                fieldWithPath("themeId")
                                        .description("테마 식별자"),
                                fieldWithPath("timeId")
                                        .description("예약 시간 식별자"),
                                fieldWithPath("paymentKey")
                                        .description("외부 결제 API가 발급한 키값"),
                                fieldWithPath("orderId")
                                        .description("랜덤으로 생성한 식별자"),
                                fieldWithPath("amount")
                                        .description("결제 금액"),
                                fieldWithPath("paymentType")
                                        .description("결제 유형")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("식별자"),
                                fieldWithPath("date")
                                        .description("예약 날짜 (형식: yyyy-MM-dd)"),
                                fieldWithPath("member.id")
                                        .description("멤버 식별자"),
                                fieldWithPath("member.name")
                                        .description("사용자 이름"),
                                fieldWithPath("theme.id")
                                        .description("테마 식별자"),
                                fieldWithPath("theme.name")
                                        .description("테마 이름"),
                                fieldWithPath("theme.description")
                                        .description("테마 시나리오 설명"),
                                fieldWithPath("theme.thumbnail")
                                        .description("썸네일 이미지 주소"),
                                fieldWithPath("time.id")
                                        .description("예약 시간 식별자"),
                                fieldWithPath("time.startAt")
                                        .description("예약 시간 (형식: HH:mm:ss)")
                        )))
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("지나간 날짜와 시간에 대한 예약 생성은 불가능하다.")
    void createReservationIsPastDateExceptionTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        final ReservationRequest reservation = reservationFixture.createReservationRequest(
                LocalDate.of(2024, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL"
        );

        // when - then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("중복된 일시의 예약은 불가능하다.")
    void createReservationIsDuplicateDateExceptionTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        final ReservationRequest reservation = reservationFixture.createReservationRequest(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL"
        );

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        // then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간 ID를 이용하여 예약할 수 없다.")
    void createReservationInvalidTimeIdExceptionTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        final ReservationRequest reservation = reservationFixture.createReservationRequest(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL"
        );

        // when - then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("존재하지 않는 테마 ID를 이용하여 예약할 수 없다.")
    void createReservationInvalidThemeIdExceptionTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        final ReservationRequest reservation = reservationFixture.createReservationRequest(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL"
        );

        // when - then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 삭제 테스트")
    void deleteReservationTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        reservationFixture.createReservation(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL",
                cookies
        );

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        // then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @DisplayName("예약 조회 테스트")
    void reservationPageTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        reservationFixture.createReservation(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL",
                cookies
        );

        // when-then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("유저 예약 조회 테스트")
    void getUserReservationsTest() {
        // given
        final Map<String, String> adminCookies = memberFixture.loginAdmin();
        final Map<String, String> userCookies = memberFixture.loginUser();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), adminCookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                adminCookies
        );

        reservationFixture.createReservation(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL",
                adminCookies
        );

        reservationFixture.createReservation(
                LocalDate.of(2025, 8, 5),
                1L,
                1L,
                "test",
                "testOrderId",
                1000,
                "NORMAL",
                userCookies
        );

        reservationFixture.createWaiting(LocalDate.of(2025, 8, 12), 1L, 1L, adminCookies);

        // when - then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }
}

package roomescape.core.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.dto.theme.ThemeRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;
import roomescape.utils.ThemeRequestGenerator;

@AcceptanceTest
class AdminControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private String accessToken;

    @Autowired
    private TestFixture testFixture;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("관리자 페이지로 이동한다.")
    void moveToAdminPage() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 관리 페이지로 이동한다.")
    void moveToReservationManagePage() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약을 삭제할 수 있다.")
    void deleteReservationByAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("예약 대기 삭제 시, 삭제를 시도한 사용자가 어드민이 아니라면 예외가 발생한다.")
    void deleteReservationByNonAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("시간 관리 페이지로 이동한다.")
    void moveToTimeManagePage() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "10:89"})
    @DisplayName("시간 생성 시, startAt 값의 형식이 올바르지 않으면 예외가 발생한다.")
    void validateTimeCreateWithEmpty(final String startAt) {
        ReservationTimeRequest request = new ReservationTimeRequest(startAt);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("시간 생성 시, startAt 값이 중복되면 예외가 발생한다.")
    void validateTimeDuplicated() {
        ReservationTimeRequest request = new ReservationTimeRequest("10:00");

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("시간 삭제 시, 해당 시간을 참조하는 예약이 있으면 예외가 발생한다.")
    void validateTimeDelete() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/admin/times/1")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("시간 삭제 시, 해당 시간을 참조하는 예약이 없으면 삭제된다.")
    void deleteTime() {
        testFixture.persistReservationTimeAfterMinute(2);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("테마 관리 페이지로 이동한다.")
    void moveToThemeManagePage() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("테마 생성 시, name 값이 올바르지 않으면 예외가 발생한다.")
    void validateThemeWithNameEmpty(final String name) {
        ThemeRequest request = new ThemeRequest(name, "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("테마 생성 시, description 값이 올바르지 않으면 예외가 발생한다.")
    void validateThemeWithDescriptionEmpty(final String description) {
        ThemeRequest request = new ThemeRequest("레벨2 탈출", description,
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(400);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @DisplayName("테마 생성 시, thumbnail 값이 올바르지 않으면 예외가 발생한다.")
    void validateThemeWithThumbnailEmpty(final String thumbnail) {
        ThemeRequest request = new ThemeRequest("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.", thumbnail);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("테마 생성 시, name 값이 중복이면 예외가 발생한다.")
    void validateThemeWithDuplicatedName() {
        ThemeRequest request = new ThemeRequest("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("테마 삭제 시, 해당 테마를 참조하는 예약이 있으면 예외가 발생한다.")
    void validateThemeDelete() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/admin/themes/1")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("테마 삭제 시, 해당 테마를 참조하는 예약이 없으면 테마가 삭제된다.")
    void deleteTheme() {
        ThemeRequestGenerator.generateWithName("테마 2");

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/admin/themes/2")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("예약자를 지정해서 예약을 생성할 수 있다.")
    void createReservationAsAdmin() {
        ReservationRequest request = new ReservationRequest(1L, TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 대기 관리 페이지로 이동한다.")
    void moveToWaitingManagePage() {
        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약 대기를 삭제할 수 있다.")
    void deleteWaitingByAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .when().delete("/admin/waitings/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("예약 대기 삭제 시, 삭제를 시도한 사용자가 어드민이 아니라면 예외가 발생한다.")
    void deleteWaitingByNonAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/admin/waitings/1")
                .then().log().all()
                .statusCode(401);
    }
}

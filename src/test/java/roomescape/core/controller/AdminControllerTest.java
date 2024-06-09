package roomescape.core.controller;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.dto.theme.ThemeRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
import roomescape.utils.TestFixture;
import roomescape.utils.ThemeRequestGenerator;

@AcceptanceTest
class AdminControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private String accessToken;

    private RequestSpecification specification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        specification = DocumentHelper.specification(restDocumentation);

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("관리자 페이지로 이동한다.")
    void moveToAdminPage() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("admin-view"))
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 관리 페이지로 이동한다.")
    void moveToReservationManagePage() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("admin-reservation-view"))
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약을 삭제할 수 있다.")
    void deleteReservationByAdmin() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .filter(document("admin-reservation-delete", pathParameters(
                        parameterWithName("id").description("취소하려는 예약 ID"))))
                .when().delete("/admin/reservations/{id}", 1L)
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
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("admin-time-view"))
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("시간을 생성할 수 있다.")
    void createReservationTime() {
        ReservationTimeRequest request = new ReservationTimeRequest("12:00");

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .filter(document("admin-time-create",
                        requestFields(fieldWithPath("startAt").description("시간 값")),
                        responseFields(fieldWithPath("id").description("시간 ID"),
                                fieldWithPath("startAt").description("시간 값"))))
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);
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
    @DisplayName("사용자가 어드민이면 시간을 삭제할 수 있다.")
    void deleteTimeByAdmin() {
        databaseCleaner.executeTruncate();
        testFixture.persistAdmin();
        testFixture.persistReservationTimeAfterMinute(1);

        RestAssured.given(this.specification).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .filter(document("admin-time-delete", pathParameters(
                        parameterWithName("id").description("취소하려는 시간 ID"))))
                .when().delete("/admin/times/{id}", 1L)
                .then().log().all()
                .statusCode(204);
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
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("admin-theme-view"))
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("테마를 생성할 수 있다.")
    void createTheme() {
        ThemeRequest request = new ThemeRequest("테마 0", "테마 0 설명.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .filter(document("admin-theme-create",
                        requestFields(fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 이미지")),
                        responseFields(fieldWithPath("id").description("테마 ID"),
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 이미지"))))
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);
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
    @DisplayName("사용자가 어드민이면 테마를 삭제할 수 있다.")
    void deleteThemeByAdmin() {
        databaseCleaner.executeTruncate();
        testFixture.persistAdmin();
        testFixture.persistTheme("테마 0");

        RestAssured.given(this.specification).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .filter(document("admin-theme-delete", pathParameters(
                        parameterWithName("id").description("취소하려는 예약 ID"))))
                .when().delete("/admin/themes/{id}", 1L)
                .then().log().all()
                .statusCode(204);
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

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .filter(document("admin-reservation-create",
                        requestFields(fieldWithPath("memberId").description("예약할 사용자"),
                                fieldWithPath("date").description("예약할 날짜"),
                                fieldWithPath("timeId").description("예약할 시간"),
                                fieldWithPath("themeId").description("예약 대기할 테마"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 ID")),
                        responseFields(fieldWithPath("id").description("예약 ID"),
                                fieldWithPath("date").description("예약된 날짜"),
                                fieldWithPath("member").description("예약한 사용자"),
                                fieldWithPath("member.id").description("예약한 사용자 ID"),
                                fieldWithPath("member.name").description("예약한 사용자 이름"),
                                fieldWithPath("time").description("예약된 시간"),
                                fieldWithPath("time.id").description("예약된 시간 ID"),
                                fieldWithPath("time.startAt").description("예약된 시간 값"),
                                fieldWithPath("theme").description("예약된 테마"),
                                fieldWithPath("theme.id").description("예약된 테마 ID"),
                                fieldWithPath("theme.name").description("예약된 테마 이름"),
                                fieldWithPath("theme.description").description("예약된 테마 설명"),
                                fieldWithPath("theme.thumbnail").description("예약된 테마 이미지"))))
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 대기 관리 페이지로 이동한다.")
    void moveToWaitingManagePage() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("admin-waiting-view"))
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약 대기를 삭제할 수 있다.")
    void deleteWaitingByAdmin() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .filter(document("admin-waiting-delete", pathParameters(
                        parameterWithName("id").description("취소하려는 예약 ID"))))
                .when().delete("/admin/waitings/{id}", 1L)
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

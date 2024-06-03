package roomescape.core.controller;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
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

    @Autowired
    private TestFixture testFixture;

    private String accessToken;
    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();
        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("사용자가 어드민이면 시간을 생성할 수 있다.")
    void createReservationTimeAsAdmin() {
        ReservationTimeRequest request = new ReservationTimeRequest("10:00");

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .accept("application/json")
                .filter(document("admin/times/post/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        requestFields(
                                fieldWithPath("startAt").description("생성할 방탈출 시작 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성한 시간의 id"),
                                fieldWithPath("startAt").description("생성한 방탈출 시작 시간")
                        )))
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("사용자가 어드민이면 테마를 생성할 수 있다.")
    void createThemeAsAdmin() {
        ThemeRequest request = new ThemeRequest("theme name", "theme description",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .accept("application/json")
                .filter(document("admin/themes/post/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        requestFields(
                                fieldWithPath("name").description("생성할 테마의 이름"),
                                fieldWithPath("description").description("생성할 테마의 설명"),
                                fieldWithPath("thumbnail").description("생성할 테마의 썸네일")
                        ),
                        responseFields(
                                fieldWithPath("id").description("생성한 테마의 id"),
                                fieldWithPath("name").description("생성한 테마의 이름"),
                                fieldWithPath("description").description("생성한 테마의 설명"),
                                fieldWithPath("thumbnail").description("생성한 테마의 썸네일")
                        )))
                .body(request)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약자를 지정해서 예약을 생성할 수 있다.")
    void createReservationAsAdmin() {
        ReservationRequest request = new ReservationRequest(1L, TOMORROW, 1L, 1L);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .accept("application/json")
                .filter(document("admin/reservations/post",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        requestFields(
                                fieldWithPath("memberId").description("예약할 사용자 id"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("themeId").description("예약 테마 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 id"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("member.*").description("예약자 정보"),
                                fieldWithPath("time.*").description("예약 시간 정보"),
                                fieldWithPath("theme.*").description("예약 테마 정보")
                        )))
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약 시간을 삭제할 수 있다.")
    void deleteReservationTimeByAdmin() {
        testFixture.deleteAllReservation();
        RestAssured.given(spec).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .accept(ContentType.JSON)
                .filter(document("admin/times/delete",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 예약 시간의 id"))))
                .when().delete("/admin/times/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("사용자가 어드민이면 테마를 삭제할 수 있다.")
    void deleteThemeByAdmin() {
        testFixture.deleteAllReservation();
        RestAssured.given(spec).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .accept(ContentType.JSON)
                .filter(document("admin/themes/delete/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 예약 대기의 id"))))
                .when().delete("/admin/themes/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약을 삭제할 수 있다.")
    void deleteReservationByAdmin() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("admin/reservations/delete/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 예약의 id"))))
                .when().delete("/admin/reservations/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("사용자가 어드민이면 예약 대기를 삭제할 수 있다.")
    void deleteWaitingByAdmin() {
        RestAssured.given(spec).log().all()
                .cookies("token", AccessTokenGenerator.adminTokenGenerate())
                .accept(ContentType.JSON)
                .filter(document("admin/waitings/delete/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("어드민 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 예약 대기의 id"))))
                .when().delete("/admin/waitings/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("예약 삭제 시, 삭제를 시도한 사용자가 어드민이 아니라면 예외가 발생한다.")
    void deleteReservationByNonAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(401);
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
    @DisplayName("예약 대기 삭제 시, 삭제를 시도한 사용자가 어드민이 아니라면 예외가 발생한다.")
    void deleteWaitingByNonAdmin() {
        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/admin/waitings/1")
                .then().log().all()
                .statusCode(401);
    }
}

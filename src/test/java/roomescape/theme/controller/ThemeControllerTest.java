package roomescape.theme.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.service.PaymentService;
import roomescape.theme.repository.ThemeRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ThemeRepository themeRepository;

    @DisplayName("테마를 추가한다.")
    @Test
    void postThemes() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "테마1");
        params.put("description", "테마1");
        params.put("thumbnail", "www.m.com");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("테마를 가져온다.")
    @Test
    void getThemes() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "테마1");
        params.put("description", "테마1");
        params.put("thumbnail", "www.m.com");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("테마를 삭제한다.")
    @Test
    void deleteThemes() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테마1");
        params.put("description", "테마1");
        params.put("thumbnail", "www.m.com");

        int themeId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().extract().path("id");

        RestAssured.given().log().all()
                .when().delete("/themes/" + themeId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("존재하지 않는 테마를 삭제할 수 없다.")
    @Test
    void deleteThemesWithNonExistsThemeId() {
        int notFoundStatusCode = 404;

        RestAssured.given().log().all()
                .when().delete("/themes/0")
                .then().log().all()
                .statusCode(notFoundStatusCode);
    }

    @DisplayName("테마가 사용 중이면 삭제할 수 없다.")
    @Test
    void deleteThemesWhenUsing() {
        int themeId = addTheme(Map.of("name", "테마1", "description", "테마1", "thumbnail", "www.m.com"));
        int timeId = addReservationTime("10:00");
        addReservation(timeId, themeId);
        int conflictStatusCode = 409;

        RestAssured.given().log().all()
                .when().delete("/themes/" + themeId)
                .then().log().all()
                .statusCode(conflictStatusCode);
    }

    @DisplayName("인기 테마를 가져온다.")
    @Test
    void getPopularThemes() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    private void addReservation(int timeId, int themeId) {
        String tokenValue = getAdminLoginTokenValue();

        Map<String, Object> reservationParams = Map.of(
                "date", LocalDate.now().plusDays(1L),
                "timeId", timeId,
                "themeId", themeId,
                "paymentKey", "paymentKey",
                "orderId", "orderId",
                "amount", 1_000L
        );

        doNothing().when(paymentService)
                .confirm(any(PaymentRequest.class));

        RestAssured.given().log().all()
                .cookie("token", tokenValue)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then();
    }

    private String getAdminLoginTokenValue() {
        Map<String, String> adminLoginParams = Map.of("email", "admin@woowa.com", "password", "12341234");
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminLoginParams)
                .when().post("/login")
                .then()
                .extract().cookie("token");
    }

    private int addReservationTime(final String timeValue) {
        Map<String, String> timeParams = Map.of("startAt", timeValue);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(timeParams)
                .when().post("/times")
                .then().extract().path("id");
    }

    private int addTheme(final Map<String, Object> themeParams) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(themeParams)
                .when().post("/themes")
                .then().extract().path("id");
    }
}

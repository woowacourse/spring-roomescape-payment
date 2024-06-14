package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import roomescape.controller.config.ControllerTestSupport;
import roomescape.domain.payment.PaymentResponse;
import roomescape.domain.payment.PaymentStatus;
import roomescape.service.dto.PaymentConfirmRequest;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.UserReservationResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

class ReservationControllerTest extends ControllerTestSupport {

    String userReservationId;
    String adminReservationId;
    int adminReservationSize;
    int userReservationSize;

    @Test
    @DisplayName("예약 목록 조회")
    void showReservation() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("응답 배열"),
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("[].date").type(JsonFieldType.STRING).description("추가 날짜"),
                fieldWithPath("[].member").type(JsonFieldType.OBJECT).description("회원"),
                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("회원 - 키"),
                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("회원 - 이름"),
                fieldWithPath("[].member.role").type(JsonFieldType.STRING).description("회원 - 역할"),
                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("시간"),
                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("시간 - 키"),
                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("시간 - 시작 시간"),
                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마"),
                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 - 키"),
                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 - 이름"),
                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 - 설명"),
                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING).description("테마 - 썸네일"),
                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 혹은 대기 상태"));
        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 추가")
    void saveReservation() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰"));
        RequestFieldsSnippet requestFields = requestFields(
                fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 키"),
                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 키"),
                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 키"));
        ResponseFieldsSnippet responseFields = responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("키"),
                fieldWithPath("date").type(JsonFieldType.STRING).description("추가 날짜"),
                fieldWithPath("member").type(JsonFieldType.OBJECT).description("회원"),
                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 - 키"),
                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원 - 이름"),
                fieldWithPath("member.role").type(JsonFieldType.STRING).description("회원 - 역할"),
                fieldWithPath("time").type(JsonFieldType.OBJECT).description("시간"),
                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 - 키"),
                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시간 - 시작 시간"),
                fieldWithPath("theme").type(JsonFieldType.OBJECT).description("테마"),
                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 - 키"),
                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 - 이름"),
                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 - 설명"),
                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 - 썸네일"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 혹은 대기 상태"));

        Map<String, Object> params = Map.of(
                "memberId", 1L,
                "date", "9999-09-09",
                "timeId", 1L,
                "themeId", 1L);

        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies, requestFields, responseFields))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(params)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 삭제")
    void deleteReservation() {
        RequestCookiesSnippet requestCookies = requestCookies(
                cookieWithName("token").description("회원 인증 토큰 (어드민이어야 합니다)"));
        Map<String, Object> params = Map.of(
                "memberId", 1L,
                "date", "9999-09-09",
                "timeId", 1L,
                "themeId", 1L,
                "amount", 1000,
                "orderId", "orderId",
                "paymentKey", "paymentKey");
        String createdId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(params)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201).extract().header("location").split("/")[2];

        RestAssured.given(specification).log().all()
                .filter(makeDocumentFilter(requestCookies))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/reservations/" + createdId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("어드민의 예약 CRUD")
    @TestFactory
    Stream<DynamicTest> dynamicAdminTestsFromCollection() {
        return Stream.of(
                dynamicTest("예약을 목록을 조회한다.", () -> {
                    adminReservationSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/admin/reservations")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("예약을 추가한다.", () -> {
                    Map<String, Object> params = Map.of(
                            "memberId", 1L,
                            "date", "2025-10-06",
                            "timeId", 1L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    adminReservationId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(params)
                            .when().post("/admin/reservations")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("존재하지 않는 시간으로 예약을 추가할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "memberId", 1L,
                            "date", "2025-10-05",
                            "timeId", 100L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(params)
                            .when().post("/admin/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("존재하지 않는 테마로 예약을 추가할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "memberId", 1L,
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 100L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(params)
                            .when().post("/admin/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("존재하지 않는 회원으로 예약을 추가할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "memberId", 100L,
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(params)
                            .when().post("/admin/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("예약 목록을 조회한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/admin/reservations")
                            .then().log().all()
                            .statusCode(200).body("size()", is(adminReservationSize + 1));
                }),
                dynamicTest("예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/reservations/" + adminReservationId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("이미 삭제된 예약을 삭제시도하면 statusCode가 400이다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/reservations/" + adminReservationId)
                            .then().log().all()
                            .statusCode(400);
                })
        );
    }

    @DisplayName("유저의 예약 생성")
    @TestFactory
    Stream<DynamicTest> dynamicUserTestsFromCollection() {
        mockPaymentConfirm(1000, "orderId", "paymentKey");
        return Stream.of(
                dynamicTest("내 예약 목록을 조회한다.", () -> {
                    userReservationSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .when().get("/reservations-mine?date=" + "2025-10-05")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("예약을 추가한다.", () -> {
                    Map<String, Object> params = Map.of(
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    userReservationId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .body(params)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("내 예약 목록을 조회하면 사이즈가 1증가한다.", () -> {
                    UserReservationResponse userReservationResponse = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .when().get("/reservations-mine?date=" + "2025-10-05")
                            .then().log().all()
                            .statusCode(200)
                            .body("size()", is(userReservationSize + 1))
                            .extract().as(UserReservationResponse[].class)[0];

                    assertAll(
                            () -> assertThat(userReservationResponse.theme()).isEqualTo("이름1"),
                            () -> assertThat(userReservationResponse.date()).isEqualTo("2025-10-05"),
                            () -> assertThat(userReservationResponse.time()).isEqualTo(LocalTime.of(9, 0, 0)),
                            () -> assertThat(userReservationResponse.status()).isEqualTo(ReservationStatus.BOOKED.getValue())
                    );
                }),
                dynamicTest("존재하지 않는 시간으로 예약을 추가할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "date", "2025-10-05",
                            "timeId", 100L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .body(params)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("존재하지 않는 테마로 예약을 추가할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "memberId", 1L,
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 100L,
                            "amount", 1000,
                            "orderId", "orderId",
                            "paymentKey", "paymentKey");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .body(params)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("어드민은 예약을 삭제할 수 있다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/reservations/" + userReservationId)
                            .then().log().all()
                            .statusCode(204);
                })
        );
    }

    @DisplayName("예약 대기 생성")
    @TestFactory
    Stream<DynamicTest> dynamicWaitTestsFromCollection() {
        mockPaymentConfirm(1000, "orderId111", "paymentKey111");
        return Stream.of(
                dynamicTest("내 예약 목록을 조회한다.", () -> {
                    userReservationSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .when().get("/reservations-mine?date=" + "2025-10-05")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("예약을 추가한다.", () -> {
                    Map<String, Object> params = Map.of(
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 1L,
                            "amount", 1000,
                            "orderId", "orderId111",
                            "paymentKey", "paymentKey111");

                    userReservationId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .body(params)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("예약이 없으면 예약 상태로 간다.", () -> {
                    UserReservationResponse userReservationResponse = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .when().get("/reservations-mine?date=" + "2025-10-05")
                            .then().log().all()
                            .statusCode(200)
                            .body("size()", is(userReservationSize + 1))
                            .extract().as(UserReservationResponse[].class)[0];

                    assertAll(
                            () -> assertThat(userReservationResponse.theme()).isEqualTo("이름1"),
                            () -> assertThat(userReservationResponse.date()).isEqualTo("2025-10-05"),
                            () -> assertThat(userReservationResponse.time()).isEqualTo(LocalTime.of(9, 0, 0)),
                            () -> assertThat(userReservationResponse.status()).isEqualTo(ReservationStatus.BOOKED.getValue())
                    );
                }),
                dynamicTest("한사람이 중복 예약을 생성할 수 없다.", () -> {
                    Map<String, Object> params = Map.of(
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 1L);

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", USER_TOKEN)
                            .body(params)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("어드민이 예약 대기를 추가한다.", () -> {
                    Map<String, Object> params = Map.of(
                            "date", "2025-10-05",
                            "timeId", 1L,
                            "themeId", 1L);

                    adminReservationId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(params)
                            .when().post("/reservations/waiting")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[3];
                }),
                dynamicTest("예약이 있으면 예약 대기 상태로 간다.", () -> {
                    UserReservationResponse[] responses = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/reservations-mine?date=" + "2025-10-05")
                            .then().log().all()
                            .statusCode(200)
                            .extract().as(UserReservationResponse[].class);

                    UserReservationResponse userReservationResponse = Arrays.stream(responses)
                            .filter(response -> response.id() == Long.parseLong(adminReservationId))
                            .findAny().get();

                    assertAll(
                            () -> assertThat(userReservationResponse.status()).isEqualTo(ReservationStatus.WAIT.getValue()),
                            () -> assertThat(userReservationResponse.rank()).isEqualTo(1L)
                    );
                }),
                dynamicTest("자신의 예약 대기를 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/reservations/waiting/" + adminReservationId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("어드민은 유저의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/reservations/" + userReservationId)
                            .then().log().all()
                            .statusCode(204);
                })
        );
    }

    private void mockPaymentConfirm(int amount, String orderId, String paymentKey) {
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(amount, orderId, paymentKey);
        PaymentResponse paymentResponse = new PaymentResponse(
                "mId",
                paymentKey,
                orderId,
                PaymentStatus.DONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                amount);
        doReturn(paymentResponse)
                .when(paymentClient)
                .confirmPayment(paymentRequest);
    }
}

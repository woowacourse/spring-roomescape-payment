package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.snippet.Attributes.key;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.auth.token.TokenProvider;
import roomescape.member.model.MemberRole;
import roomescape.reservation.dto.*;
import roomescape.reservation.service.PaymentService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(RestDocumentationExtension.class)
class AdminReservationControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @LocalServerPort
    int randomServerPort;

    @MockBean
    PaymentService paymentService;

    private RequestSpecification spec;

    @BeforeEach
    public void initReservation(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = randomServerPort;
        this.spec = new RequestSpecBuilder()
                .setPort(randomServerPort)
                .addFilter(document("{class-name}/{method-name}"))
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("(관리자) - 사용자 아이디를 포함하여 예약 정보를 저장한다.")
    @Test
    void saveReservationForAdminTest() {
        Mockito.when(paymentService.requestTossPayment(any())).thenReturn(null);

        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                3L,
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000L
        );

        RestAssured.given(spec).log().all()
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("date").attributes(key("type").value("date")).description("예약날짜입니다."),
                                fieldWithPath("memberId").description("멤버 아이디입니다."),
                                fieldWithPath("timeId").description("예약시간 아이디입니다."),
                                fieldWithPath("themeId").description("테마 아이디입니다."),
                                fieldWithPath("paymentKey").description("페이먼트 키입니다."),
                                fieldWithPath("orderId").description("주문 아이디입니다."),
                                fieldWithPath("amount").description("결제 금액입니다."))
                ))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(17));
    }

    @DisplayName("관리자가 아닌 클라이언트가 회원 아이디를 포함하여 예약 정보를 저장하려고 하면 에러 코드가 응답된다.")
    @Test
    void saveReservationIncludeMemberIdWhoNotAdminTest() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                null,
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000L
        );

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationRequest)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 정보를 삭제한다.")
    @Test
    void deleteReservationTest() {
        RestAssured.given(spec).log().all()
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);

        final List<ReservationResponse> reservations = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationResponse.class);

        assertThat(reservations.size()).isEqualTo(15);
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 정보를 삭제하려고 하면 에러 코드가 응답된다.")
    @Test
    void deleteReservationWhoNotAdminTest() {
        RestAssured.given(spec).log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 시간 정보를 저장한다.")
    @Test
    void saveReservationTimeTest() {
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(LocalTime.of(12, 15));

        RestAssured.given(spec).log().all()
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("startAt").attributes(key("tpye").value("time")).description("시작 시간입니다."))
                ))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201)
                .body("id", is(9));
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 시간 정보를 저장하려고 하면 예외를 발생시킨다.")
    @Test
    void saveReservationTimeWhoNotAdminTest() {
        final SaveReservationTimeRequest saveReservationTimeRequest = new SaveReservationTimeRequest(LocalTime.of(12, 15));

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("예약 시간 정보를 삭제한다.")
    @Test
    void deleteReservationTimeTest() {
        // 예약 시간 정보 삭제
        RestAssured.given(spec).log().all()
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(204);

        // 예약 시간 정보 조회
        final List<ReservationTimeResponse> reservationTimes = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ReservationTimeResponse.class);

        assertThat(reservationTimes.size()).isEqualTo(7);
    }

    @DisplayName("관리자가 아닌 클라이언트가 예약 시간 정보를 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void deleteReservationTimeWhoNotAdminTest() {
        RestAssured.given(spec).log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/times/2")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("테마 정보를 저장한다.")
    @Test
    void saveThemeTest() {
        final SaveThemeRequest saveThemeRequest = new SaveThemeRequest(
                "즐거운 방방탈출~",
                "방방방! 탈탈탈!",
                "방방 사진"
        );

        RestAssured.given(spec).log().all()
//                terminal test 오류
//                .filter(document("{class-name}/{method-name}",
//                        requestFields(
//                                fieldWithPath("name").description("테마명 입니다."))))
//                                fieldWithPath("description").description("테마 설명 아이디입니다."),
//                                fieldWithPath("thumbnail").description("테마 썸네일입니다."))
//                ))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveThemeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201)
                .body("id", is(16));
    }

    @DisplayName("관리자가 아닌 클라이언트가 테마 정보를 저장하려고 하면 예외를 발생시킨다.")
    @Test
    void saveThemeWhoNotAdminTest() {
        final SaveThemeRequest saveThemeRequest = new SaveThemeRequest(
                "즐거운 방방탈출~",
                "방방방! 탈탈탈!",
                "방방 사진"
        );

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveThemeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    @DisplayName("테마 정보를 삭제한다.")
    @Test
    void deleteThemeTest() {
        // 예약 시간 정보 삭제
        RestAssured.given(spec).log().all()
                .cookie("token", createAdminAccessToken())
                .when().delete("/admin/themes/7")
                .then().log().all()
                .statusCode(204);

        // 예약 시간 정보 조회
        final List<ThemeResponse> themes = RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .when().get("/themes")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", ThemeResponse.class);

        assertThat(themes.size()).isEqualTo(14);
    }

    @DisplayName("관리자가 아닌 클라이언트가 테마 정보를 삭제하려고 하면 예외를 발생시킨다.")
    @Test
    void deleteThemeWhoNotAdminTest() {
        // 예약 시간 정보 삭제
        RestAssured.given(spec).log().all()
                .cookie("token", createUserAccessToken())
                .when().delete("/admin/themes/7")
                .then().log().all()
                .statusCode(403)
                .body("message", is("유효하지 않은 권한 요청입니다."));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}

package roomescape.reservation.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.snippet.Attributes.key;

import java.time.LocalDate;

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
import roomescape.reservation.dto.SaveReservationRequest;
import roomescape.reservation.service.PaymentService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(RestDocumentationExtension.class)
class ReservationControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @LocalServerPort
    int randomServerPort;

    @MockBean
    private PaymentService paymentService;

    private RequestSpecification spec;

    @BeforeEach
    public void initReservation(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = randomServerPort;
        Mockito.when(paymentService.requestTossPayment(any())).thenReturn(null);
        this.spec = new RequestSpecBuilder()
                .setPort(randomServerPort)
                .addFilter(document("{class-name}/{method-name}"))
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("전체 예약 정보를 조회한다.")
    @Test
    void getReservationsTest() {
        RestAssured.given(spec).log().all()
                .cookie("token", createUserAccessToken())
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(16));
    }

    @DisplayName("예약 정보를 저장한다.")
    @Test
    void saveReservationTest() {
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
                .filter(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("date").attributes(key("type").value("date")).description("예약날짜입니다.").optional(),
                                fieldWithPath("memberId").description("멤버 아이디입니다.").optional(),
                                fieldWithPath("timeId").description("예약시간 아이디입니다."),
                                fieldWithPath("themeId").description("테마 아이디입니다."),
                                fieldWithPath("paymentKey").description("페이먼트 키입니다."),
                                fieldWithPath("orderId").description("주문 아이디입니다."),
                                fieldWithPath("amount").description("결제 금액입니다."))
                ))
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(17));
    }

    @DisplayName("존재하지 않는 예약 시간을 포함한 예약 저장 요청을 하면 400코드가 응답된다.")
    @Test
    void saveReservationWithNoExistReservationTime() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                null,
                80L,
                1L,
                "paymentKey",
                "orderId",
                1000L
        );

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("해당 id의 예약 시간이 존재하지 않습니다."));
    }

    @DisplayName("현재 날짜보다 이전 날짜의 예약을 저장하려고 요청하면 400코드가 응답된다.")
    @Test
    void saveReservationWithReservationDateAndTimeBeforeNow() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().minusDays(1),
                null,
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000L
        );

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("현재 날짜보다 이전 날짜를 예약할 수 없습니다."));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}

package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.exception.BadRequestException;
import roomescape.model.Reservation;
import roomescape.request.AdminReservationRequest;
import roomescape.request.ReservationRequest;
import roomescape.service.PaymentService;
import roomescape.service.fixture.PaymentFixture;
import roomescape.service.fixture.ReservationRequestBuilder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ReservationControllerTest extends AbstractControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @MockBean
    private PaymentService paymentService;


    @DisplayName("예약을 조회한다.")
    @Test
    void should_get_reservations() {
        RestDocumentationFilter description = document("reservation-success-get", responseFields(
                        fieldWithPath("[].id").description("예약 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("예약한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].date").description("등록한 예약 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("[].time").description("등록한 예약 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].time.id").description("예약 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].time.startAt").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme").description("등록한 예약 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200).extract();
    }

    @DisplayName("예약을 검색한다.")
    @Test
    void should_search_reservations() {
        RestDocumentationFilter description = document("admin-reservations-success-get",
                requiredCookie,
                queryParameters(
                        parameterWithName("themeId").description("검색할 테마 Id"),
                        parameterWithName("memberId").description("검색할 맴버 Id"),
                        parameterWithName("dateFrom").description("시작 예약 날짜"),
                        parameterWithName("dateTo").description("종료 예약 날짜")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("예약한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].date").description("등록한 예약 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("[].time").description("등록한 예약 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].time.id").description("예약 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].time.startAt").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme").description("등록한 예약 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("[].theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("[].theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .cookie("token", getAdminCookie())
                .when().get("/admin/reservations?themeId=1&memberId=1&dateFrom=2024-05-05&dateTo=2024-05-10")
                .then().log().all()
                .statusCode(200).extract();
    }

    @DisplayName("사용자가 예약을 추가할 수 있다.")
    @Test
    void should_insert_reservation_when_member_request() {
        doReturn(PaymentFixture.GENERAL.getPayment()).when(paymentService).confirmReservationPayments(any(ReservationRequest.class), any(Reservation.class));
        RestDocumentationFilter description = document("reservations-success-post",
                requiredCookie,
                requestFields(
                        fieldWithPath("date").description("예약 할 날짜"),
                        fieldWithPath("themeId").description("예약 할 테마 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("timeId").description("예약 할 시간 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("orderId").description("결제 주문 Id").type(JsonFieldType.STRING),
                        fieldWithPath("paymentKey").description("결제 Key").type(JsonFieldType.STRING),
                        fieldWithPath("amount").description("결제 금액").type(JsonFieldType.NUMBER)
                ),
                responseFields(
                        fieldWithPath("id").description("예약 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("name").description("예약한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("date").description("등록한 예약 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("time").description("등록한 예약 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("time.id").description("예약 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("time.startAt").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("theme").description("등록한 예약 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("생성된 예약 Id가 reservation/`id` 형태로 응답")
                )
        );
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L,
                "asdfsdf", "dfadf", 1999999);

        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("token", getMemberCookie())
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/7");
    }

    @DisplayName("관리자가 예약을 추가할 수 있다.")
    @Test
    void should_insert_reservation_when_admin_request() {
        AdminReservationRequest request = new AdminReservationRequest(
                LocalDate.of(2030, 8, 5), 10L, 6L, 1L);
        RestDocumentationFilter description = document("admin-reservations-success-post",
                requiredCookie,
                requestFields(
                        fieldWithPath("date").description("예약 할 날짜"),
                        fieldWithPath("themeId").description("예약 할 테마 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("timeId").description("예약 할 시간 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("memberId").description("예약 할 회원 Id").type(JsonFieldType.NUMBER)
                ),
                responseFields(
                        fieldWithPath("id").description("예약 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("name").description("예약한 회원 이름").type(JsonFieldType.STRING),
                        fieldWithPath("date").description("등록한 예약 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("time").description("등록한 예약 시간").type(JsonFieldType.OBJECT),
                        fieldWithPath("time.id").description("예약 시간 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("time.startAt").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("theme").description("등록한 예약 테마").type(JsonFieldType.OBJECT),
                        fieldWithPath("theme.id").description("테마 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("theme.name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("theme.description").description("테마 상세 설명").type(JsonFieldType.STRING),
                        fieldWithPath("theme.thumbnail").description("테마 이미지 URL").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("생성된 예약 Id가 reservation/`id` 형태로 응답")
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .body(request)
                .cookie("token", getAdminCookie())
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/7");
    }

    @DisplayName("존재하는 예약이라면 예약을 삭제할 수 있다.")
    @Test
    void should_delete_reservation_when_reservation_exist() {
        RestDocumentationFilter description = document("reservations-success-delete",
                pathParameters(
                        parameterWithName("id").description("삭제할 예약 id")
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().delete("/reservations/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("컨트롤러에 JdbcTemplate 필드가 존재하지 않는다.")
    @Test
    void should_not_exist_JdbcTemplate_field() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        AssertionsForClassTypes.assertThat(isJdbcTemplateInjected).isFalse();
    }

    @DisplayName("로그인 정보에 따른 예약 내역을 조회한다.")
    @Test
    void should_find_member_reservation() {
        RestDocumentationFilter description = document("reservations-mine-success-get",
                requiredCookie,
                responseFields(
                        fieldWithPath("[].id").description("예약 id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].theme").description("예약한 테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].date").description("등록한 예약 날짜").type(JsonFieldType.STRING),
                        fieldWithPath("[].time").description("등록한 예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].status").description("예약 상태").type(JsonFieldType.STRING),
                        fieldWithPath("[].time").description("예약 시간").type(JsonFieldType.STRING),
                        fieldWithPath("[].amount").description("결제 금액").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].paymentKey").description("결제 paymentKey").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .cookie("token", getMemberCookie())
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("예약등록시 예외가 발생할 경우 결제가 진행되지 않는다")
    @TestFactory
    Stream<DynamicTest> should_not_confirm_when_reservation_exception() {
        doNothing().when(paymentService).confirmReservationPayments(any(ReservationRequest.class), any(Reservation.class));
        ReservationRequest request = ReservationRequestBuilder.builder().date(LocalDate.now().minusDays(1)).build();
        return Stream.of(
                dynamicTest("예약 등록 시 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", getAdminCookie())
                            .body(request)
                            .cookie("token", getMemberCookie())
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("결제 서비스가 한 번도 호출되지 않는다.", () -> {
                    verify(paymentService, never()).confirmReservationPayments(any(ReservationRequest.class), any(Reservation.class));
                }));
    }

    @DisplayName("예약등록시 예외가 발생할 경우 결제가 진행되지 않는다")
    @TestFactory
    Stream<DynamicTest> should_not_reserved_when_payment_exception() {
        doThrow(new BadRequestException("결제 오류 발생")).when(paymentService).confirmReservationPayments(any(ReservationRequest.class), any(Reservation.class));
        ReservationRequest request = ReservationRequestBuilder.builder().build();
        return Stream.of(
                dynamicTest("예약 등록 시 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", getAdminCookie())
                            .body(request)
                            .when().post("/reservations")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("예약이 등록되지 않는다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", getMemberCookie())
                            .when().get("/reservations")
                            .then().log().all()
                            .body("size()", is(6));
                }));
    }
}

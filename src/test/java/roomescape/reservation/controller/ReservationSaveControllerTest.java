package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import roomescape.common.config.IntegrationTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.client.PaymentConfirmClient;
import roomescape.reservation.controller.dto.request.ReservationPaymentSaveRequest;
import roomescape.reservation.controller.dto.request.ReservationSaveRequest;

@AutoConfigureMockMvc
class ReservationSaveControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentConfirmClient paymentConfirmClient;

    @DisplayName("회원이 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveMemberReservation() throws Exception {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationPaymentSaveRequest reservationPaymentSaveRequest = new ReservationPaymentSaveRequest(
                LocalDate.now(), 1L, 1L, "paymentKey", "orderId", 1000L
        );

        doNothing().when(paymentConfirmClient).confirmPayment(any());

        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie(CookieUtils.TOKEN_KEY, getMemberToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPaymentSaveRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"))
                .andDo(document("reservations/save"));
    }

    @DisplayName("이미 동일한 예약을 한 회원이 중복된 예약을 시도하면 400 에러를 반환한다.")
    @Test
    void failWhenSameMemberReserves() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        ReservationSaveRequest adminRequest = new ReservationSaveRequest(1L, LocalDate.now(), 1L, 1L, null, null);

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(adminRequest))
                .accept(ContentType.JSON)
                .filter(RestAssuredRestDocumentation.document("/reservations/save/fail/duplicated"))
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 예약이 완료된 예약을 시도한다면 400 에러를 반환한다.")
    @Test
    void failWhenTryToMakeReservationAlreadyReserved() throws JsonProcessingException {
        saveMemberAsKaki();
        saveMemberAsAnna();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        ReservationSaveRequest adminRequest = new ReservationSaveRequest(2L, LocalDate.now(), 1L, 1L, null, null);

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(adminRequest))
                .accept(ContentType.JSON)
                .filter(RestAssuredRestDocumentation.document("/reservations/save/fail/already-reserved"))
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

package roomescape.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.stub.StubTokenProvider;
import roomescape.common.CleanUp;
import roomescape.config.AuthServiceTestConfig;
import roomescape.fixture.db.MemberDbFixture;
import roomescape.fixture.db.ReservationDateTimeDbFixture;
import roomescape.fixture.db.ThemeDbFixture;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.service.TossPaymentService;
import roomescape.reservation.controller.exception.ReservationExceptionHandler;
import roomescape.reservation.controller.request.ReservePaymentRequest;
import roomescape.reservation.domain.ReservationDateTime;

@Import({AuthServiceTestConfig.class, ReservationExceptionHandler.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReservationApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CleanUp cleanUp;

    @Autowired
    private ThemeDbFixture themeDbFixture;

    @Autowired
    private MemberDbFixture memberDbFixture;

    @Autowired
    private ReservationDateTimeDbFixture reservationDateTimeDbFixture;

    @MockitoBean
    private TossPaymentService tossPaymentService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        cleanUp.all();
        memberDbFixture.유저1_생성();
    }

    @Test
    void 방탈출_예약을_생성한다() throws JsonProcessingException {
        Long themeId = themeDbFixture.공포().getId();
        ReservationDateTime reservationDateTime = reservationDateTimeDbFixture.내일_열시();
        Long timeId = reservationDateTime.getReservationTime().getId();

        given(tossPaymentService.confirmPayment(any()))
                .willReturn(mock(PaymentResponse.class));

        ReservePaymentRequest request = ReservePaymentRequest.builder()
                .date(reservationDateTime.getDate())
                .timeId(timeId)
                .themeId(themeId)
                .paymentKey("paymentKey")
                .orderId("orderId")
                .amount(10000L)
                .paymentType("NORMAL")
                .build();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", StubTokenProvider.USER_STUB_TOKEN)
                .body(objectMapper.writeValueAsString(request))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        verify(tossPaymentService, times(1)).confirmPayment(any());
    }

    @Test
    void 예약_삭제시_존재하지_않는_예약이면_예외를_응답한다() {
        RestAssured.given()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void 예약_생성_시_null_값을_허용하지_않는다() {
        Long themeId = themeDbFixture.공포().getId();
        ReservationDateTime reservationDateTime = reservationDateTimeDbFixture.내일_열시();
        Long timeId = reservationDateTime.getReservationTime().getId();
        String dateTime = formatDateTime(reservationDateTime.getDate());

        HashMap<String, Object> request = new HashMap<>();
        request.put("themeId", themeId);
        request.put("timeId", timeId);
        request.put("date", dateTime);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", StubTokenProvider.USER_STUB_TOKEN)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 과거_시간으로_예약을_하면_예외를_반환한다() throws JsonProcessingException {
        Long themeId = themeDbFixture.공포().getId();
        ReservationDateTime reservationDateTime = reservationDateTimeDbFixture._7일전_열시();
        Long timeId = reservationDateTime.getReservationTime().getId();

        ReservePaymentRequest request = ReservePaymentRequest.builder()
                .date(reservationDateTime.getDate())
                .timeId(timeId)
                .themeId(themeId)
                .paymentKey("paymentKey")
                .orderId("orderId")
                .amount(10000L)
                .paymentType("NORMAL")
                .build();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", StubTokenProvider.USER_STUB_TOKEN)
                .body(objectMapper.writeValueAsString(request))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(422);
    }

    private String formatDateTime(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }
}

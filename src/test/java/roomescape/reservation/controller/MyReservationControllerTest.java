package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.fixture.DateFixture;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.model.SpringBootTestBase;
import roomescape.paymenthistory.PaymentType;
import roomescape.paymenthistory.dto.PaymentResponse;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.dto.ReservationCreateRequest;

@AutoConfigureMockMvc
class MyReservationControllerTest extends SpringBootTestBase {

    @MockBean
    private TossPaymentHistoryService tossPaymentHistoryService;

    @DisplayName("로그인한 사용자의 예약 및 예약 대기 목록을 읽을 수 있다.")
    @Test
    void findMyReservationsWaitings() {
        Cookies loginMemberCookies = restAssuredTemplate.makeUserCookie(MemberFixture.MEMBER_BROWN);
        Cookies adminCookies = restAssuredTemplate.makeUserCookie(MemberFixture.MEMBER_ADMIN);

        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), adminCookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), adminCookies).id();

        ReservationCreateRequest reservationParams = new ReservationCreateRequest(DateFixture.TOMORROW_DATE, timeId,
                themeId,
                "paymentKey", "orderId",
                10000, PaymentType.NORMAL);
        restAssuredTemplate.create(reservationParams, loginMemberCookies);

        given(tossPaymentHistoryService.findPaymentHistory(1))
                .willReturn(new PaymentResponse("paymentKey", 214000));

        int size = RestAssured.given().log().all()
                .cookies(loginMemberCookies)
                .when().get("/my/reservaitons")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getInt("size()");

        assertThat(size).isEqualTo(1);
    }
}

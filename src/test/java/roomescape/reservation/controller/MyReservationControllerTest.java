package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_BROWN;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.member.domain.Member;
import roomescape.model.SpringBootTestBase;
import roomescape.paymenthistory.PaymentType;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.waiting.dto.WaitingCreateRequest;

@AutoConfigureMockMvc
class MyReservationControllerTest extends SpringBootTestBase {


    @MockBean
    private TossPaymentHistoryService tossPaymentHistoryService;

    @DisplayName("로그인한 사용자의 예약 및 예약 대기 목록을 읽을 수 있다.")
    @Test
    void findMyReservationsWaitings() {

        Cookies adminCookies = restAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), adminCookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), adminCookies).id();

        doNothing().when(tossPaymentHistoryService).approvePayment(any());
        // 예약 추가
        Member reservationMember = MEMBER_BRI;
        Cookies reservationMemberCookies = restAssuredTemplate.makeUserCookie(reservationMember);
        ReservationCreateRequest reservationParams =
                new ReservationCreateRequest(date, timeId, themeId, "paymentKey", "orderId", 10000, PaymentType.NORMAL);
        restAssuredTemplate.create(reservationParams, reservationMemberCookies);

        // 로그인 유저 대기 추가
        Member loginMember = MEMBER_BROWN;
        Cookies loginMemberCookies = restAssuredTemplate.makeUserCookie(loginMember);
        WaitingCreateRequest waitingParams = new WaitingCreateRequest(date, timeId, themeId);
        restAssuredTemplate.create(waitingParams, loginMemberCookies);

        // 로그인 유저 예약 추가
        reservationParams = new ReservationCreateRequest(date.plusDays(1), timeId, themeId, "paymentKey", "orderId",
                10000, PaymentType.NORMAL);
        restAssuredTemplate.create(reservationParams, loginMemberCookies);

        // 나의 예약 목록 조회
        int size = RestAssured.given().log().all()
                .cookies(loginMemberCookies)
                .when().get("/my/reservaitons")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getInt("size()");

        assertThat(size).isEqualTo(2);
    }
}

package roomescape.member.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import roomescape.auth.domain.Token;
import roomescape.auth.provider.CookieProvider;
import roomescape.model.IntegrationTest;
import roomescape.registration.dto.PaymentResponse;
import roomescape.registration.dto.RegistrationInfoDto;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.equalTo;

class MemberIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("가입한 회원들의 이름들을 가져올 수 있다.")
    void memberList() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/admin/members")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원의 예약 내역들을 가져올 수 있다.")
    void memberReservationList() {
        int memberId = 1;
        RegistrationInfoDto firstReservation = firstMemberFirstReservation();
        Token token = tokenProvider.getAccessToken(memberId);
        ResponseCookie cookie = CookieProvider.setCookieFrom(token);

        RestAssured.given().log().all()
                .cookie(cookie.toString())
                .contentType(ContentType.JSON)
                .when().get("/member/registrations")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].id", equalTo(memberId))
                .body("[0].themeName", equalTo(firstReservation.themeName()))
                .body("[0].date", equalTo(firstReservation.date().toString()))
                .body("[0].time", equalTo(firstReservation.time().toString()))
                .body("[0].status", equalTo(firstReservation.status()));
    }

    private RegistrationInfoDto firstMemberFirstReservation() {
        return new RegistrationInfoDto(
                1,
                "polla",
                LocalDate.parse("2024-04-30"),
                LocalTime.parse("15:40"),
                "예약",
                PaymentResponse.getPaymentResponseForNotPaidReservation()
        );
    }
}

package roomescape.fixture;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.auth.dto.LoginRequest;
import roomescape.member.domain.Member;
import roomescape.paymenthistory.domain.TossPaymentRestClient;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.TimeCreateRequest;
import roomescape.time.dto.TimeResponse;
import roomescape.waiting.dto.WaitingCreateRequest;
import roomescape.waiting.dto.WaitingResponse;

@TestComponent
@ExtendWith(MockitoExtension.class)
public class RestAssuredTemplate {

    @MockBean
    private TossPaymentRestClient tossPaymentRestClient;

    public Cookies makeUserCookie(Member member) {
        LoginRequest request = new LoginRequest(member.getEmail(), member.getPassword());

        return given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().detailedCookies();
    }

    public ThemeResponse create(ThemeCreateRequest params, Cookies cookies) {
        return given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", ThemeResponse.class);
    }

    public TimeResponse create(TimeCreateRequest params, Cookies cookies) {
        return given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", TimeResponse.class);
    }

    public ReservationResponse create(ReservationCreateRequest params, Cookies cookies) {
        doNothing().when(tossPaymentRestClient).approvePayment(any());

        return given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", ReservationResponse.class);
    }

    public static ReservationResponse create(AdminReservationCreateRequest params, Cookies cookies) {
        return RestAssured.given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", ReservationResponse.class);
    }

    public WaitingResponse create(WaitingCreateRequest params, Cookies cookies) {
        return given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", WaitingResponse.class);
    }
}

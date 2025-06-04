package roomescape.presentation.api.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.infrastructure.security.JwtProvider;
import roomescape.presentation.support.methodresolver.AuthInfoArgumentResolver;
import roomescape.testconfig.TestConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ReservationControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthInfoArgumentResolver authInfoArgumentResolver;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 예약결제_토스서버오류() {
        // given
        final String requestBody = """
                    {
                        "date": "2024-07-01",
                        "timeId": 2,
                        "themeId": 1,
                        "paymentKey": "pk_test_123456789",
                        "orderId": "ORDER-123456",
                        "amount": 10000,
                        "paymentType": "NORMAL"
                    }
                """;

        // when
        final var response = RestAssured.given()
                .cookie("token", jwtProvider.issue(TestConfig.TESTER.memberId()))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/reservations")
                .then()
                .extract()
                .response();

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(400);
            assertThat(response.asString()).isEqualTo("{\"message\":\"존재하지 않는 결제 정보입니다\"}");
        });
    }
}

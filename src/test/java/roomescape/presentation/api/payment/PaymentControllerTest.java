package roomescape.presentation.api.payment;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class PaymentControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 결제요청_정상등록() {
        // given
        final String requestBody = """
                    {
                        "amount": 10000,
                        "orderId": "ORDER-123456"
                    }
                """;

        // when
        final var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/payments/validation")
                .then()
                .extract();

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(201);
        });
    }
}

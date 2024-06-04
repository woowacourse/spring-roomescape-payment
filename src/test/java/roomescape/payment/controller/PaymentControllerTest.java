package roomescape.payment.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.auth.token.TokenProvider;
import roomescape.member.model.MemberRole;
import roomescape.payment.dto.SavePaymentCredentialRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(value = "classpath:test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class PaymentControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @LocalServerPort
    private int randomServerPort;

    @BeforeEach
    public void setUp() {
        RestAssured.port = randomServerPort;
    }

    @DisplayName("결제 신용 정보를 저장한다.")
    @Test
    void saveCredentials() {
        final SavePaymentCredentialRequest request = new SavePaymentCredentialRequest("orderId", 1000L);

        RestAssured.given().log().all()
                .cookie("token", createAdminAccessToken())
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/payments/credentials")
                .then().log().all()
                .statusCode(200);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}

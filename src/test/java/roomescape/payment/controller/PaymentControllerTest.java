package roomescape.payment.controller;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.member.model.MemberRole;
import roomescape.payment.dto.SavePaymentCredentialRequest;

class PaymentControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("결제 신용 정보를 저장한다.")
    @Test
    void saveCredentials() {
        final SavePaymentCredentialRequest request = new SavePaymentCredentialRequest("orderId", 1000L);

        RestAssured.given(spec).log().all()
                .filter(document("save-payment-credentials"))
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

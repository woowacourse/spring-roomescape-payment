package roomescape.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.infrastructure.PaymentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PaymentApiTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 결제를_생성한다() {
        // given
        Map<String, Object> body = Map.of(
                "orderId", "orderId1",
                "amount", 1000L
        );
        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/payments")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void 중복_결제_생성이면_400에러가_발생한다() {
        // given
        paymentRepository.save(Payment.restore("id", "orderId1", "paymentKey", 1000L, PaymentStatus.IN_PROGRESS, null));
        Map<String, Object> body = Map.of(
                "orderId", "orderId1",
                "amount", 1000L
        );
        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/payments")
                .then().log().all()
                .statusCode(400);
    }
}

package roomescape.integration.helper;

import static roomescape.integration.helper.RestAssuredRequestUtils.postWithToken;

import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.util.Map;

public class ReservationTestHelper {

    public static void addReservation(Map<String, Object> body, RequestSpecification spec) {
        postWithToken("/admin/reservations", body, spec, AuthTokenExtractor.extractAdminToken()).then().statusCode(201);
    }

    public static void addWaiting(Map<String, Object> body, RequestSpecification spec) {
        postWithToken("/waitings", body, spec, AuthTokenExtractor.extractAdminToken()).then().statusCode(201);
    }

    public static Long addReservationAndGetId(Map<String, Object> body, RequestSpecification spec) {
        return postWithToken("/admin/reservations", body, spec, AuthTokenExtractor.extractAdminToken())
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    public static Map<String, Object> createReservationWithoutPaymentBody(Long memberId, Long themeId, String date) {
        return Map.of(
                "memberId", memberId,
                "themeId", themeId,
                "timeId", 1L,
                "date", date
        );
    }

    public static Map<String, Object> createReservationWithoutPaymentBody(Long memberId, Long themeId) {
        return Map.of(
                "memberId", memberId,
                "themeId", themeId,
                "timeId", 1L,
                "date", LocalDate.now().plusDays(1).toString()
        );
    }

    public static Map<String, Object> createReservationWithPaymentBody(Long memberId, Long themeId, String date) {
        return Map.of(
                "memberId", memberId,
                "themeId", themeId,
                "timeId", 1L,
                "date", date,
                "paymentKey", "pay_test_key",
                "amount", 15000,
                "orderId", "order-1234"
        );
    }

    public static Map<String, Object> createReservationWithPaymentBody(Long memberId, Long themeId) {
        return Map.of(
                "memberId", memberId,
                "themeId", themeId,
                "timeId", 1L,
                "date", LocalDate.now().plusDays(1).toString(),
                "paymentKey", "pay_test_key",
                "amount", 15000,
                "orderId", "order-1234"
        );
    }
}

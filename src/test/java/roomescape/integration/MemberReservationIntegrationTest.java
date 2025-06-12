package roomescape.integration;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.integration.helper.AuthTokenExtractor.extractMember1Token;
import static roomescape.integration.helper.AuthTokenExtractor.extractMember2Token;
import static roomescape.integration.helper.AuthTokenExtractor.specWithLoginMember1;
import static roomescape.integration.helper.AuthTokenExtractor.specWithLoginMember2;
import static roomescape.integration.helper.DateUtils.getToday;
import static roomescape.integration.helper.DateUtils.getTomorrow;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendDeleteWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithToken;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithTokenAndFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.MEMBER_RESERVATION_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.RESERVATION_MINE_RESPONSE_LIST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.RESERVATION_RESPONSE_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.WAITING_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.WAITING_RESPONSE_FIELDS;

import io.restassured.filter.Filter;
import io.restassured.response.Response;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;
import roomescape.integration.MemberReservationIntegrationTest.FakeClientConfig;

@Import(FakeClientConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberReservationIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "member-reservation";

    @Nested
    @DisplayName("회원 예약 API")
    class MemberReservationApi {

        @Test
        @DisplayName("예약 생성 API")
        void createReservation() {
            String member1Token = extractMember1Token();

            Map<String, Object> reservationBody = Map.of(
                    "themeId", 1,
                    "date", getTomorrow().toString(),
                    "timeId", 1,
                    "paymentKey", "test-payment-key",
                    "orderId", "test-order-id",
                    "amount", 30000
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create-reservation",
                    requestFields(MEMBER_RESERVATION_REQUEST_FIELDS),
                    responseFields(RESERVATION_RESPONSE_FIELDS)
            );

            sendPostWithTokenAndFilter("/reservations", reservationBody, specWithLoginMember1(spec), member1Token, filter)
                    .then().statusCode(201);
        }

        @Test
        @DisplayName("예약 대기 생성 API")
        void createWaiting() {
            String member1Token = extractMember1Token();

            Map<String, Object> waitingBody = Map.of(
                    "themeId", 1,
                    "date", getToday().plusDays(2).toString(),
                    "timeId", 1
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create-waiting",
                    requestFields(WAITING_REQUEST_FIELDS),
                    responseFields(WAITING_RESPONSE_FIELDS)
            );

            sendPostWithTokenAndFilter("/reservations/waitings", waitingBody, specWithLoginMember1(spec), member1Token, filter)
                    .then().statusCode(201);
        }

        @Test
        @DisplayName("예약 대기 삭제 API")
        void deleteWaiting() {
            String member1Token = extractMember1Token();

            Map<String, Object> waitingBody = Map.of(
                    "themeId", 1,
                    "date", getToday().plusDays(2).toString(),
                    "timeId", 1
            );

            Response postResponse = sendPostWithToken("/reservations/waitings", waitingBody, specWithLoginMember2(spec), member1Token);
            int id = postResponse.then().extract().jsonPath().getInt("id");

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete-waiting",
                    pathParameters(parameterWithName("id").description("예약 대기 ID"))
            );

            sendDeleteWithTokenAndFilter("/reservations/waitings/{id}", specWithLoginMember1(spec), member1Token, filter, id)
                    .then().statusCode(204);
        }

        @Test
        @DisplayName("나의 예약/대기 목록 조회 API")
        void getMyReservations() {
            String member2Token = extractMember2Token();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all-my-reservations",
                    responseFields(RESERVATION_MINE_RESPONSE_LIST_FIELDS)
            );

            sendGetWithTokenAndFilter("/reservations/me", specWithLoginMember2(spec), member2Token, filter)
                    .then().statusCode(200);
        }
    }

    @TestConfiguration
    static class FakeClientConfig {

        @Bean
        @Primary
        public PaymentClient paymentClient() {
            return request -> new TossPaymentResponse(
                    "fake_key",
                    request.orderId(),
                    request.amount(),
                    OffsetDateTime.now(ZoneOffset.UTC)
            );
        }
    }
}

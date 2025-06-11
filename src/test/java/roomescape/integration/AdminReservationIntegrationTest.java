package roomescape.integration;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.integration.helper.AuthTokenExtractor.extractAdminToken;
import static roomescape.integration.helper.AuthTokenExtractor.specWithLoginAdmin;
import static roomescape.integration.helper.DateUtils.getToday;
import static roomescape.integration.helper.DateUtils.getTomorrow;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendDeleteWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilterAndQueryParams;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithTokenAndFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.ADMIN_RESERVATION_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.RESERVATION_RESPONSE_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.RESERVATION_RESPONSE_LIST_FIELDS;

import io.restassured.filter.Filter;
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
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;
import roomescape.integration.AdminReservationIntegrationTest.FakeClientConfig;

@Import(FakeClientConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminReservationIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "admin-reservation";

    @Nested
    @DisplayName("관리자 예약 API")
    class AdminReservationApi {

        @Test
        @DisplayName("관리자 예약 생성 API")
        void createReservation() {
            String adminToken = extractAdminToken();

            Map<String, Object> body = Map.of(
                    "memberId", 1,
                    "themeId", 1,
                    "date", getTomorrow().toString(),
                    "timeId", 1
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create-reservation",
                    requestFields(ADMIN_RESERVATION_REQUEST_FIELDS),
                    responseFields(RESERVATION_RESPONSE_FIELDS)
            );

            sendPostWithTokenAndFilter("/admin/reservations", body, specWithLoginAdmin(spec), adminToken, filter)
                    .then().statusCode(201);
        }

        @Test
        @DisplayName("관리자 예약 조건 조회 API")
        void filterReservations() {
            String adminToken = extractAdminToken();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "filter-reservations",
                    responseFields(RESERVATION_RESPONSE_LIST_FIELDS)
            );

            Map<String, String> queryParams = Map.of(
                    "memberId", "2",
                    "themeId", "1",
                    "dateFrom", getToday().minusDays(2).toString(),
                    "dateTo",getToday().plusDays(2).toString()
            );

            sendGetWithTokenAndFilterAndQueryParams("/admin/reservations", specWithLoginAdmin(spec), adminToken, filter, queryParams)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("예약 대기 목록 조회 API")
        void getWaitings() {
            String adminToken = extractAdminToken();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all-waitings",
                    responseFields(RESERVATION_RESPONSE_LIST_FIELDS)
            );

            sendGetWithTokenAndFilter("/admin/waitings", specWithLoginAdmin(spec), adminToken, filter)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("예약 대기 삭제 API")
        void deleteWaiting() {
            String adminToken = extractAdminToken();

            int waitingId = 1;

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete-waiting",
                    pathParameters(parameterWithName("id").description("예약 대기 ID"))
            );

            sendDeleteWithTokenAndFilter("/admin/waitings/{id}", specWithLoginAdmin(spec), adminToken, filter, waitingId)
                    .then().statusCode(204);
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

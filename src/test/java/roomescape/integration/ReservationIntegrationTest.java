package roomescape.integration;


import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendDeleteWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.Reservation.RESERVATION_RESPONSE_LIST_FIELDS;

import io.restassured.filter.Filter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import roomescape.integration.ReservationIntegrationTest.FakeClientConfig;

@Import(FakeClientConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "reservation";

    @Nested
    @DisplayName("예약 API")
    class ReservationApi {

        @Test
        @DisplayName("전체 예약 목록 조회 API")
        void getReservations() {
            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all",
                    responseFields(RESERVATION_RESPONSE_LIST_FIELDS)
            );

            sendGetWithFilter("/reservations", spec, filter)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("예약 삭제 API")
        void deleteReservation() {
            int reservationId = 1;

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete",
                    pathParameters(parameterWithName("id").description("예약 ID"))
            );

            sendDeleteWithFilter("/reservations/{id}", spec, filter, reservationId)
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

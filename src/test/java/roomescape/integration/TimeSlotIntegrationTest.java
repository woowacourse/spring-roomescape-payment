package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendDeleteWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPost;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.TimeSlot.TIME_SLOT_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.TimeSlot.TIME_SLOT_RESPONSE_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.TimeSlot.TIME_SLOT_RESPONSE_LIST_FIELDS;

import io.restassured.filter.Filter;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TimeSlotIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "time-slot";

    @Nested
    @DisplayName("예약 시간 API")
    class TimeSlotApi {

        @Test
        @DisplayName("예약 시간 목록 조회 API")
        void getTimeSlots() {
            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all",
                    responseFields(TIME_SLOT_RESPONSE_LIST_FIELDS)
            );

            sendGetWithFilter("/times", spec, filter)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("예약 시간 생성 API")
        void createTimeSlot() {
            Map<String, String> body = Map.of("startAt", "10:30");

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create",
                    requestFields(TIME_SLOT_REQUEST_FIELDS),
                    responseFields(TIME_SLOT_RESPONSE_FIELDS)
            );

            sendPostWithFilter("/times", body, spec, filter)
                    .then().statusCode(201)
                    .body("startAt", is("10:30:00"));
        }

        @Test
        @DisplayName("예약 시간 삭제 API")
        void deleteTimeSlot() {
            Map<String, String> body = Map.of("startAt", "11:30");

            Response postResponse = sendPost("/times", body, spec);
            int id = postResponse.then().extract().jsonPath().getInt("id");

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete",
                    pathParameters(parameterWithName("id").description("예약 시간 ID")));

            sendDeleteWithFilter("/times/{id}", spec, filter, id)
                    .then().statusCode(204);
        }
    }
}

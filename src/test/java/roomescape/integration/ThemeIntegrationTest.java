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
import static roomescape.integration.helper.RestDocsFieldSnippets.Theme.POPULAR_THEME_RESPONSE_LIST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Theme.THEME_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Theme.THEME_RESPONSE_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Theme.THEME_RESPONSE_LIST_FIELDS;

import io.restassured.filter.Filter;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ThemeIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "theme";

    @Nested
    @DisplayName("테마 API")
    class ThemeApi {

        @Test
        @DisplayName("테마 목록 조회 API")
        void getThemes() {
            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all",
                    responseFields(THEME_RESPONSE_LIST_FIELDS)
            );

            sendGetWithFilter("/themes", spec, filter)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("인기 테마 목록 조회 API")
        void getPopularThemes() {
            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all-popular",
                    responseFields(POPULAR_THEME_RESPONSE_LIST_FIELDS)
            );

            sendGetWithFilter("/themes/popular-themes", spec, filter)
                    .then().statusCode(200);
        }


        @Test
        @DisplayName("테마 생성 API")
        void createTheme() {
            Map<String, String> body = Map.of(
                    "name", "생성 테스트용 테마 이름",
                    "description", "생성 테스트용 테마 설명",
                    "thumbnail", "http://example.com/create.jpg"
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create",
                    requestFields(THEME_REQUEST_FIELDS),
                    responseFields(THEME_RESPONSE_FIELDS)
            );

            sendPostWithFilter("/themes", body, spec, filter)
                    .then().statusCode(201)
                    .body("name", is("생성 테스트용 테마 이름"));
        }

        @Test
        @DisplayName("테마 삭제 API")
        void deleteTheme() {
            Map<String, String> body = Map.of(
                    "name", "삭제 테스트용 테마",
                    "description", "삭제 테스트용 설명",
                    "thumbnail", "http://example.com/delete.jpg"
            );

            Response postResponse = sendPost("/themes", body, spec);
            int id = postResponse.then().extract().jsonPath().getInt("id");

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete",
                    pathParameters(parameterWithName("id").description("테마 ID")));

            sendDeleteWithFilter("/themes/{id}", spec, filter, id)
                    .then().statusCode(204);
        }
    }
}

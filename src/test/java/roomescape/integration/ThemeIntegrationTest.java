package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.deleteWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.getWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.postWithFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPost;

import io.restassured.filter.Filter;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

class ThemeIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "theme";

    private static final List<FieldDescriptor> THEME_RESPONSES_FIELDS = List.of(
            fieldWithPath("[].id").description("테마 ID"),
            fieldWithPath("[].name").description("테마 이름"),
            fieldWithPath("[].description").description("테마 설명"),
            fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
    );

    private static final List<FieldDescriptor> POPULAR_THEME_RESPONSES_FIELDS = List.of(
            fieldWithPath("[].name").description("테마 이름"),
            fieldWithPath("[].description").description("테마 설명"),
            fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
    );

    private static final List<FieldDescriptor> THEME_RESPONSE_FIELDS = List.of(
            fieldWithPath("id").description("테마 ID"),
            fieldWithPath("name").description("테마 이름"),
            fieldWithPath("description").description("테마 설명"),
            fieldWithPath("thumbnail").description("테마 썸네일 URL")
    );

    private static final List<FieldDescriptor> THEME_FIELDS = List.of(
            fieldWithPath("name").description("테마 이름"),
            fieldWithPath("description").description("테마 설명"),
            fieldWithPath("thumbnail").description("테마 썸네일 URL")
    );

    @Nested
    @DisplayName("테마 API")
    class ThemeApi {

        @Test
        @DisplayName("테마 목록 조회 API")
        void getThemes() {
            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "find-all",
                    responseFields(THEME_RESPONSES_FIELDS));

            getWithFilter("/themes", spec, filter)
                    .then().statusCode(200);
        }

        @Test
        @DisplayName("테마 생성 API")
        void createTheme() {
            Map<String, String> body = Map.of(
                    "name", "미스터리룸",
                    "description", "미스터리한 분위기의 테마입니다.",
                    "thumbnail", "http://example.com/image.jpg"
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "create",
                    requestFields(THEME_FIELDS),
                    responseFields(THEME_RESPONSE_FIELDS));

            postWithFilter("/themes", body, spec, filter)
                    .then().statusCode(201)
                    .body("name", is("미스터리룸"));
        }

        @Test
        @DisplayName("테마 삭제 API")
        void deleteTheme() {
            Map<String, String> body = Map.of(
                    "name", "삭제용 테마",
                    "description", "삭제 테스트용 설명",
                    "thumbnail", "http://example.com/delete.jpg"
            );

            Response postResponse = sendPost("/themes", body, spec);
            int id = postResponse.then().extract().jsonPath().getInt("id");

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "delete",
                    pathParameters(parameterWithName("id").description("테마 ID")));

            deleteWithFilter("/themes/{id}", spec, filter, id)
                    .then().statusCode(204);
        }
    }
}

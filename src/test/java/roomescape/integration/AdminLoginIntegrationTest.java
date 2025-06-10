package roomescape.integration;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.integration.helper.AuthTokenExtractor.extractAdminToken;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithFilter;

import io.restassured.filter.Filter;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.FieldDescriptor;

class AdminLoginIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "admin-login";

    private static final List<FieldDescriptor> ADMIN_LOGIN_REQUEST_FIELDS = List.of(
            fieldWithPath("email").description("어드민 이메일"),
            fieldWithPath("password").description("어드민 비밀번호")
    );

    private static final List<FieldDescriptor> CHECK_ADMIN_LOGIN_RESPONSE_FIELDS = List.of(
            fieldWithPath("name").description("어드민 이름")
    );

    @Nested
    @DisplayName("어드민 로그인 API")
    class AdminLoginApi {

        @Test
        @DisplayName("로그인 API")
        void login() {
            Map<String, String> body = Map.of(
                    "email", "admin@email.com",
                    "password", "password"
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "login",
                    requestFields(ADMIN_LOGIN_REQUEST_FIELDS)
            );

            sendPostWithFilter("/admin/login", body, spec, filter)
                    .then().statusCode(200)
                    .header(HttpHeaders.SET_COOKIE, Matchers.containsString("token="));
        }

        @Test
        @DisplayName("로그인 확인 API")
        void checkLogin() {
            String token = extractAdminToken();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "check",
                        responseFields(CHECK_ADMIN_LOGIN_RESPONSE_FIELDS)
            );

            sendGetWithTokenAndFilter("/admin/login/check", spec, token, filter)
                    .then().statusCode(200);
        }
    }
}

package roomescape.integration;

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.integration.helper.AuthTokenExtractor.extractAdminToken;
import static roomescape.integration.helper.AuthTokenExtractor.specWithLoginAdmin;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.Auth.ADMIN_LOGIN_REQUEST_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Auth.CHECK_ADMIN_LOGIN_RESPONSE_FIELDS;

import io.restassured.filter.Filter;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class AdminLoginIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "admin-login";

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
            String adminToken = extractAdminToken();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "login-check",
                        responseFields(CHECK_ADMIN_LOGIN_RESPONSE_FIELDS)
            );

            sendGetWithTokenAndFilter("/admin/login/check", specWithLoginAdmin(spec), adminToken, filter)
                    .then().statusCode(200);
        }
    }
}

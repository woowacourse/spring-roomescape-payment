package roomescape.integration;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.integration.helper.AuthTokenExtractor.extractAdminToken;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.getWithFilterAndToken;
import static roomescape.integration.helper.RestAssuredRequestUtils.postWithFilter;

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

            postWithFilter("/admin/login", body, spec,
                    createDocumentFilter(DOCS_BASE_DIR, "login",
                            requestFields(
                                    fieldWithPath("email").description("어드민 이메일"),
                                    fieldWithPath("password").description("어드민 비밀번호")
                            )))
                    .then().statusCode(200)
                    .header(HttpHeaders.SET_COOKIE, Matchers.containsString("token="));
        }

        @Test
        @DisplayName("로그인 확인 API")
        void checkLogin() {
            String token = extractAdminToken();

            getWithFilterAndToken("/admin/login/check", spec, token,
                    createDocumentFilter(DOCS_BASE_DIR, "check",
                            responseFields(fieldWithPath("name").description("어드민 이름"))))
                    .then().statusCode(200);
        }
    }
}

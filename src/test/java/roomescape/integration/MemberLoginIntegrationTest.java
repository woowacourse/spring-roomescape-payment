package roomescape.integration;


import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.integration.helper.AuthTokenExtractor.extractMember1Token;
import static roomescape.integration.helper.AuthTokenExtractor.specWithLoginMember1;
import static roomescape.integration.helper.DocsFilterFactory.createDocumentFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendGetWithTokenAndFilter;
import static roomescape.integration.helper.RestAssuredRequestUtils.sendPostWithFilter;
import static roomescape.integration.helper.RestDocsFieldSnippets.Auth.CHECK_MEMBER_LOGIN_RESPONSE_FIELDS;
import static roomescape.integration.helper.RestDocsFieldSnippets.Auth.MEMBER_LOGIN_REQUEST_FIELDS;

import io.restassured.filter.Filter;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class MemberLoginIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "member-login";

    @Nested
    @DisplayName("멤버 로그인 API")
    class AdminLoginApi {

        @Test
        @DisplayName("로그인 API")
        void login() {
            Map<String, String> loginBody = Map.of(
                    "email", "member1@email.com",
                    "password", "password"
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "login",
                    requestFields(MEMBER_LOGIN_REQUEST_FIELDS)
            );

            sendPostWithFilter("/login", loginBody, spec, filter)
                    .then().statusCode(200)
                    .header(HttpHeaders.SET_COOKIE, Matchers.containsString("token="));
        }

        @Test
        @DisplayName("로그인 확인 API")
        void checkLogin() {
            String member1Token = extractMember1Token();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "login-check",
                    responseFields(CHECK_MEMBER_LOGIN_RESPONSE_FIELDS)
            );

            sendGetWithTokenAndFilter("/login/check", specWithLoginMember1(spec), member1Token, filter)
                    .then().statusCode(200);
        }
    }
}

package roomescape.integration;


import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.integration.helper.AuthTokenExtractor.extractMemberToken;
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

class MemberLoginIntegrationTest extends IntegrationTest {

    private static final String DOCS_BASE_DIR = "member-login";

    private static final List<FieldDescriptor> MEMBER_LOGIN_REQUEST_FIELDS = List.of(
            fieldWithPath("email").description("멤버 이메일"),
            fieldWithPath("password").description("멤버 비밀번호")
    );

    private static final List<FieldDescriptor> CHECK_MEMBER_LOGIN_RESPONSE_FIELDS = List.of(
            fieldWithPath("name").description("멤버 이름")
    );

    @Nested
    @DisplayName("멤버 로그인 API")
    class AdminLoginApi {

        @Test
        @DisplayName("로그인 API")
        void login() {
            Map<String, String> body = Map.of(
                    "email", "member1@email.com",
                    "password", "password"
            );

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "login",
                    requestFields(MEMBER_LOGIN_REQUEST_FIELDS)
            );

            sendPostWithFilter("/login", body, spec, filter)
                    .then().statusCode(200)
                    .header(HttpHeaders.SET_COOKIE, Matchers.containsString("token="));
        }

        @Test
        @DisplayName("로그인 확인 API")
        void checkLogin() {
            String token = extractMemberToken();

            Filter filter = createDocumentFilter(DOCS_BASE_DIR, "check",
                    responseFields(CHECK_MEMBER_LOGIN_RESPONSE_FIELDS)
            );

            sendGetWithTokenAndFilter("/login/check", spec, token, filter)
                    .then().statusCode(200);
        }
    }
}

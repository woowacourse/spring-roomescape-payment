package roomescape.presentation.rest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Nested
    @DisplayName("사용자를 추가한다.")
    class CreateUser extends RestDocsTestBase {

        @Test
        @DisplayName("id를 포함한 멤버와 CREATED를 응답한다")
        void createUser() {
            Map<String, String> requestBody = Map.of(
                    "email", "razel@email.com",
                    "password", "razel1234",
                    "name", "라젤"
            );

            RestAssured.given(spec).filter(createUser_Document()).log().all()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when().post("/users")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("name", Matchers.equalTo("라젤"));
        }

        RestDocumentationFilter createUser_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("email").description("저장할 사용자의 이메일(ID)"),
                    fieldWithPath("password").description("저장할 사용자의 비밀번호(PW)"),
                    fieldWithPath("name").description("저장할 사용자의 이름(ID)")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("사용자의 ID"),
                    fieldWithPath("name").description("사용자의 이름(ID)")
            };

            return document(
                    "user-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("모든 사용자를 조회한다.")
    class ReadAllUsers extends RestDocsTestBase {

        @Test
        @DisplayName("모든 사용자를 정상적으로 조회한다.")
        void readAllUsers() {
            String token = getAdminToken();

            RestAssured.given(spec).filter(readAllUsers_Document()).log().all()
                    .cookie("token", token)
                    .when().get("/admin/users")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());
        }

        RestDocumentationFilter readAllUsers_Document() {
            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("[]").description("조회된 사용자 목록"),
                    fieldWithPath("[].id").description("사용자의 ID"),
                    fieldWithPath("[].name").description("사용자의 이름(ID)")
            };

            return document(
                    "user-find-all",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }
    }
}

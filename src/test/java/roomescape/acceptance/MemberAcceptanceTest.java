package roomescape.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;
import static roomescape.fixture.MemberFixture.MEMBER_PK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.member.MemberService;

class MemberAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("사용자가 회원가입한다.")
    void register() {
        String request = """
                {
                    "name": "아루",
                    "email": "aru@test.com",
                    "password": "12341234"
                }
                """;

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("name").description("이름"),
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("ID"),
                fieldWithPath("name").description("이름")
        };

        RestDocumentationFilter docsFilter = document(
                "member-register",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .when().post("/members")
                .then().log().all()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입할 수 없다.")
    void duplicatedEmailTest() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String request = """
                {
                    "name": "아루",
                    "email": "member@test.com",
                    "password": "12341234"
                }
                """;

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("name").description("이름"),
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호")
        };

        RestDocumentationFilter docsFilter = document(
                "member-register-duplicated-email",
                preprocessRequest(prettyPrint()),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .when().post("/members")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @Test
    @DisplayName("관리자가 모든 회원을 조회한다.")
    void findAllMembers() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        fixture.registerMember(MEMBER_PK.registerRequest());

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].name").description("이름")
        };

        RestDocumentationFilter docsFilter = document(
                "member-find-all",
                requestCookies(cookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .cookie("token", fixture.getAdminToken())
                .filter(docsFilter)
                .when().get("/members")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("size()", equalTo(2));
    }
}

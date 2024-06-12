package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.member.dto.response.MemberResponse;

class MemberControllerTest extends ControllerTest {

    @Test
    @DisplayName("사용자가 회원가입한다.")
    void register() {
        BDDMockito.given(memberService.register(any(MemberRegisterRequest.class)))
                .willReturn(new MemberResponse(1L, "아루"));

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

        RestDocumentationResultHandler handler = document(
                "member-register",
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입할 수 없다.")
    void duplicatedEmailTest() {
        BDDMockito.given(memberService.register(any(MemberRegisterRequest.class)))
                .willThrow(new IllegalArgumentException("이미 가입된 이메일입니다."));

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

        RestDocumentationResultHandler handler = document(
                "member-register-duplicated-email",
                preprocessRequest(prettyPrint()),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .apply(handler)
                .statusCode(400);
    }

    @Test
    @DisplayName("관리자가 모든 회원을 조회한다.")
    void findAllMembers() {
        List<MemberResponse> response = List.of(
                new MemberResponse(1L, "아루"),
                new MemberResponse(2L, "비밥")
        );
        BDDMockito.given(memberService.findAll())
                .willReturn(response);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].name").description("이름")
        };

        RestDocumentationResultHandler handler = document(
                "member-find-all",
                requestCookies(cookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .cookie("token", "admin-token")
                .when().get("/members")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }
}

package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.member.dto.response.MemberResponse;

class MemberDocsTest extends RestDocsTest {

    @Test
    @DisplayName("새로운 회원을 추가한다.")
    void postSuccess() {
        MemberResponse response = new MemberResponse(1L, "wiib");

        doReturn(response)
                .when(memberService)
                .register(any());

        MemberRegisterRequest request = new MemberRegisterRequest("wiib", "test@test.com", "12341234");

        restDocs
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/member/post/success",
                        requestFields(
                                fieldWithPath("name.name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email.address").type(JsonFieldType.STRING).description("이메일 주소"),
                                fieldWithPath("password.password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름")
                        )));
    }

    @Test
    @DisplayName("회원 추가를 실패한다.")
    void postFail() {
        doThrow(new IllegalArgumentException("errorMessage"))
                .when(memberService)
                .register(any());

        MemberRegisterRequest request = new MemberRegisterRequest("wiib", "test@test.com", "12341234");

        restDocs
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/member/post/fail"));
    }

    @Test
    @DisplayName("관리자가 전체 회원을 조회한다.")
    void getAllSuccess() {
        List<MemberResponse> responses = List.of(
                new MemberResponse(1L, "admin"),
                new MemberResponse(2L, "wiib")
        );

        doReturn(responses)
                .when(memberService)
                .findAll();

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().get("/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/member/get/all/success",
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("회원 이름")
                        )));
    }

    @Test
    @DisplayName("회원이 전체 회원을 조회하면 실패한다.")
    void getAllFail() {
        List<MemberResponse> responses = List.of(
                new MemberResponse(1L, "admin"),
                new MemberResponse(2L, "wiib")
        );

        doReturn(responses)
                .when(memberService)
                .findAll();

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/members")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/member/get/all/fail"));
    }
}

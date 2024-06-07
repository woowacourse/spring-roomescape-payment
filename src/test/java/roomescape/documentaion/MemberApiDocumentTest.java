package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.member.application.MemberService;
import roomescape.member.dto.request.MemberJoinRequest;
import roomescape.member.presentation.MemberController;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.MIA_EMAIL;
import static roomescape.TestFixture.MIA_NAME;
import static roomescape.TestFixture.TEST_PASSWORD;
import static roomescape.TestFixture.USER_ADMIN;
import static roomescape.TestFixture.USER_MIA;

class MemberApiDocumentTest extends DocumentTest {
    private final MemberService memberService = Mockito.mock(MemberService.class);

    @Test
    @DisplayName("사용자 목록 조회 API")
    void findAll() throws Exception {
        BDDMockito.given(memberService.findAll())
                .willReturn(List.of(USER_MIA(1L)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "member-find",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("사용자 식별자"),
                                        fieldWithPath("[].email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("사용자 이름")
                                )
                        )
                );
    }

    @Test
    @DisplayName("일반 사용자 회원 가입 API")
    void postMember() throws Exception {
        MemberJoinRequest request = new MemberJoinRequest(MIA_EMAIL, TEST_PASSWORD, MIA_NAME);

        BDDMockito.given(memberService.create(any()))
                .willReturn(USER_MIA(1L));

        performMemberCreateDocumentTest(request, "/members/join", "member-create");
    }

    @Test
    @DisplayName("어드민 회원 가입 API")
    void postAdminMember() throws Exception {
        MemberJoinRequest request = new MemberJoinRequest(MIA_EMAIL, TEST_PASSWORD, MIA_NAME);

        BDDMockito.given(memberService.create(any()))
                .willReturn(USER_ADMIN(1L));

        performMemberCreateDocumentTest(request, "/members/join/admin", "member-create-admin");
    }

    private void performMemberCreateDocumentTest(MemberJoinRequest request, String path, String identifier) throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                identifier,
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 식별자"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름")
                                )
                        )
                );
    }

    @Override
    protected Object initController() {
        return new MemberController(memberService);
    }
}

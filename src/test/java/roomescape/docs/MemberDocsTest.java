package roomescape.docs;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.util.ApiDocumentUtils.getDocumentResponse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import roomescape.config.TestWebMvcConfig;
import roomescape.member.controller.MemberController;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;
import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;

@AutoConfigureRestDocs
@Import(TestWebMvcConfig.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class MemberDocsTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private MemberService memberService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MemberController(memberService))
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .addInterceptors(new TestAdminInterceptor())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void findAllMember() throws Exception {
        var response = new MemberResponse(1L, "홍길동", "example@example.com", "password", "MEMBER");

        when(memberService.findAllMember())
                .thenReturn(List.of(response));

        ResultActions result = mockMvc.perform(
                get("/members")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("회원 전체 조회",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("[].email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("[].password").type(JsonFieldType.STRING).description("회원 비밀번호"),
                                fieldWithPath("[].role").type(JsonFieldType.STRING).description("회원 역할")
                        )));
    }

    @Test
    void signup() throws Exception {
        var request = new MemberRequest("example@example.com", "password", "홍길동");

        ResultActions result = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("회원 가입",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름")
                        )));
    }
}

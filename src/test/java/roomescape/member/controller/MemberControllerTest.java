package roomescape.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.config.AdminAuthBaseTest;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberResponses;
import roomescape.member.service.MemberService;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class MemberControllerTest extends AdminAuthBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입 API")
    void signup() throws Exception {
        // given
        MemberRequest request = new MemberRequest("test@example.com", "password123!", "홍길동");
        doNothing().when(memberService).save(any(MemberRequest.class));

        // when && then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("member/singup",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("회원 이름")
                        )
                ));
    }

    @Test
    @DisplayName("모든 회원 조회 API")
    void findAllMember() throws Exception {
        // given
        List<MemberResponse> memberList = List.of(
                new MemberResponse(1L, "사용자1"),
                new MemberResponse(2L, "사용자2")
        );
        MemberResponses responses = new MemberResponses(memberList);

        given(memberService.findAllMember()).willReturn(responses);

        // when && then
        mockMvc.perform(addAuthCookie(get("/admin/members")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].id").value(1L))
                .andExpect(jsonPath("$.members[0].name").value("사용자1"))
                .andExpect(jsonPath("$.members[1].id").value(2L))
                .andExpect(jsonPath("$.members[1].name").value("사용자2"))
                .andDo(document("member/find-all",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("members[].id").description("회원 ID"),
                                fieldWithPath("members[].name").description("회원 이름")
                        )
                ));
    }
}

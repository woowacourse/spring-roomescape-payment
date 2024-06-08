package roomescape.controller.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.FindMemberResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.MemberService;

@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @DisplayName("전체 멤버 조회")
    @Test
    void findAll() throws Exception {
        given(memberService.findAll())
            .willReturn(List.of(
                new FindMemberResponse(1L, "트레"),
                new FindMemberResponse(2L, "호돌"),
                new FindMemberResponse(3L, "콜리")
            ));

        mockMvc.perform(get("/members"))
            .andDo(print())
            .andDo(document("members/findAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isOk());
    }
}

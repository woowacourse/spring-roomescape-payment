package roomescape.controller.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;

@AutoConfigureRestDocs
@WebMvcTest(LogoutController.class)
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .cookie(new Cookie("token", "user-token-example")))
            .andDo(print())
            .andDo(document("logout/logout",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isOk());
    }
}

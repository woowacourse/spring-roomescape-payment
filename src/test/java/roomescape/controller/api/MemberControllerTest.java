package roomescape.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.AuthAdminInterceptor;
import roomescape.controller.AuthArgumentResolver;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.auth.SignUpRequestDto;
import roomescape.dto.member.MemberResponseDto;
import roomescape.dto.member.MemberSignupResponseDto;
import roomescape.service.command.MemberCommandService;
import roomescape.service.query.MemberQueryService;
import roomescape.util.JwtTokenProvider;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    MemberQueryService memberQueryService;

    @MockitoBean
    MemberCommandService memberCommandService;

    @MockitoBean
    AuthArgumentResolver authArgumentResolver;

    @MockitoBean
    AuthAdminInterceptor authAdminInterceptor;

    String loginToken;

    @BeforeEach
    void setUp() {
        loginToken = jwtTokenProvider.createToken(
                new Member(1L, "가이온", "hello@woowa.com", Role.ADMIN, "password"));

        when(authArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new roomescape.dto.auth.LoginInfo(new Member(1L, "가이온", "hello@woowa.com", Role.ADMIN, "password")));
        when(authAdminInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void registerMember() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("가이온", "hello1@woowa.com", "password");
        when(memberCommandService.registerMember(signUpRequestDto)).thenReturn(new MemberSignupResponseDto(1L, "가이온", "hello1@woowa.com"));
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("회원가입 된 멤버를 가져올 수 있다.")
    @Test
    void findMembers() throws Exception {
        when(memberQueryService.findAllMembers()).thenReturn(List.of(new MemberResponseDto(1L, "가이온", "hello@woowa.com", Role.USER)));
        mockMvc.perform(get("/members")
                .cookie(new Cookie("token", loginToken)))
                .andExpect(status().isOk());
    }
}

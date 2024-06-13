package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.controller.TokenCookieManager;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.service.AuthService;
import roomescape.config.handler.AuthenticationArgumentResolver;
import roomescape.waiting.controller.WaitingController;
import roomescape.waiting.dto.WaitingRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.service.WaitingService;

@WebMvcTest(controllers = WaitingController.class)
@ExtendWith(RestDocumentationExtension.class)
class WaitingApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private AuthenticationArgumentResolver authenticationArgumentResolver;
    @MockBean
    private AuthService authService;
    @MockBean
    private TokenCookieManager tokenCookieManager;
    @MockBean
    private WaitingService waitingService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("예약대기를 생성한다.")
    @Test
    void createWaitingTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");
        WaitingRequest request = new WaitingRequest(LocalDate.of(2020, 12, 12), 1L, 1L);
        WaitingResponse response = new WaitingResponse(1L, "testMember", "우테코 탈출 2", LocalDate.of(2020, 12, 12),
                LocalTime.of(12, 12));

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);
        given(waitingService.createWaiting(any(), any()))
                .willReturn(response);

        ResultActions result = mockMvc.perform(post("/waitings")
                .cookie(cookie)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("waitings/create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("themeId").description("테마의 id"),
                                fieldWithPath("timeId").description("시간의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약대기의 id"),
                                fieldWithPath("memberName").description("멤버의 이름"),
                                fieldWithPath("themeName").description("테마의 이름"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("startAt").description("시간")
                        )
                ));

    }

    @DisplayName("예약대기을 삭제한다.")
    @Test
    void deleteTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);
        doNothing().when(waitingService).deleteWaiting(any());

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/waitings/{id}", 1L)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent())
                .andDo(
                        document("waitings/delete",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(parameterWithName("id").description("예약대기의 id"))
                        ));
    }
}

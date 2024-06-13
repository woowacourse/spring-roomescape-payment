package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.config.handler.AdminAuthorizationInterceptor;
import roomescape.config.handler.AuthenticationArgumentResolver;
import roomescape.waiting.controller.AdminWaitingController;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.service.WaitingService;

@WebMvcTest(controllers = AdminWaitingController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                WebMvcConfigurer.class,
                                AuthenticationArgumentResolver.class,
                                AdminAuthorizationInterceptor.class})
        })
@ExtendWith(RestDocumentationExtension.class)
class AdminWaitingApiTest {
    private static final WaitingResponse RESPONSE1 = new WaitingResponse(1L, "testMember", "우테코 탈출 2",
            LocalDate.of(2020, 12, 12),
            LocalTime.of(12, 12));
    private static final WaitingResponse RESPONSE2 = new WaitingResponse(2L, "testMember2", "우테코 탈출 3",
            LocalDate.of(2020, 11, 11),
            LocalTime.of(11, 11));

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WaitingService waitingService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("관리자가 예약대기를 찾는다")
    @Test
    void findWaitingsTest() throws Exception {
        List<WaitingResponse> responses = List.of(RESPONSE1, RESPONSE2);

        given(waitingService.findWaitings())
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/admin/waitings")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(
                        document("admin/find-waitings",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("[].id").description("예약대기의 id"),
                                        fieldWithPath("[].memberName").description("멤버의 이름"),
                                        fieldWithPath("[].themeName").description("테마의 이름"),
                                        fieldWithPath("[].date").description("날짜"),
                                        fieldWithPath("[].startAt").description("시간")
                                )
                        ));
    }

    @DisplayName("관리자가 예약대기을 삭제한다.")
    @Test
    void deleteTest() throws Exception {
        doNothing().when(waitingService).deleteWaiting(any());
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/waitings/{id}",1L)
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

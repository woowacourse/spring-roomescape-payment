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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import roomescape.time.controller.TimeController;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.TimeCreateRequest;
import roomescape.time.dto.TimeResponse;
import roomescape.time.service.TimeService;

@WebMvcTest(controllers = TimeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                WebMvcConfigurer.class,
                                AuthenticationArgumentResolver.class,
                                AdminAuthorizationInterceptor.class})
        })
@ExtendWith(RestDocumentationExtension.class)
class TimeApiTest {
    private static final LocalTime TIME1 = LocalTime.of(11, 11);
    private static final LocalTime TIME2 = LocalTime.of(12, 12);
    private static final TimeResponse RESPONSE1 = new TimeResponse(1L, TIME1);
    private static final TimeResponse RESPONSE2 = new TimeResponse(2L, TIME2);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private TimeService timeService;


    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("시간을 찾는다")
    @Test
    void findTimesTest() throws Exception {
        List<TimeResponse> responses = List.of(RESPONSE1, RESPONSE2);
        given(timeService.findTimes())
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/times"));

        result.andExpect(status().isOk())
                .andDo(document("times/findTimes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("시간의 id"),
                                fieldWithPath("[].startAt").description("시간")
                        )
                ));
    }

    @DisplayName("예약 가능 여부와 함께 시간 찾기")
    @Test
    void findAvailableTimesTest() throws Exception {
        AvailableTimeResponse response1 = new AvailableTimeResponse(RESPONSE1, true);
        AvailableTimeResponse response2 = new AvailableTimeResponse(RESPONSE2, false);
        List<AvailableTimeResponse> responses = List.of(response1, response2);

        given(timeService.findAvailableTimes(any(), any()))
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/times/available")
                .param("date", "2024-12-12")
                .param("themeId", "1")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("times/available",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("date").description("날짜"),
                                parameterWithName("themeId").description("테마 id")
                        ),
                        responseFields(
                                fieldWithPath("[].time.id").description("시간의 id"),
                                fieldWithPath("[].time.startAt").description("시간"),
                                fieldWithPath("[].alreadyBooked").description("해당 시간에 예약 존재 여부")
                        )
                ));
    }

    @DisplayName("시간을 생성한다")
    @Test
    void createTimeTest() throws Exception {
        TimeCreateRequest request = new TimeCreateRequest(TIME1);

        given(timeService.createTime(any()))
                .willReturn(RESPONSE1);

        ResultActions result = mockMvc.perform(post("/times")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(
                        document("times/create",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("startAt").description("시간")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("시간의 id"),
                                        fieldWithPath("startAt").description("시간")
                                )
                        )
                );
    }

    @DisplayName("시간을 삭제한다.")
    @Test
    void deleteTest() throws Exception {
        doNothing().when(timeService).deleteTime(any());
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/times/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent())
                .andDo(
                        document("times/delete",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(parameterWithName("id").description("시간의 id"))
                        ));
    }
}

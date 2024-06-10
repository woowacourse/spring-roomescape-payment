package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.application.ReservationTimeService;
import roomescape.application.dto.response.time.AvailableReservationTimeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.web.api.MemberReservationTimeController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(MemberReservationTimeController.class)
class MemberReservationTimeSliceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationTimeService timeService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final LocalTime time1 = LocalTime.of(10, 0);
    private final LocalTime time2 = LocalTime.of(11, 0);

    @DisplayName("모든 시간 조회")
    @Test
    void findAllTimes() throws Exception {
        List<ReservationTimeResponse> response = List.of(
                new ReservationTimeResponse(1L, time1),
                new ReservationTimeResponse(2L, time2)
        );

        given(timeService.findAllReservationTime()).willReturn(response);

        ResultActions result = mockMvc.perform(get("/times"));

        result.andExpect(status().isOk())
                .andDo(document("/times/findAllTimes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시작 시간 (HH:mm)")
                        ))
                );
    }

    @DisplayName("특정 날짜와 테마에 대한 가능한 시간 조회")
    @Test
    void findAllAvailableTimes() throws Exception {
        List<AvailableReservationTimeResponse> response = List.of(
                new AvailableReservationTimeResponse(1L, time1, true),
                new AvailableReservationTimeResponse(2L, time2, false)
        );

        given(timeService.findAllAvailableReservationTime(any(), any())).willReturn(response);

        ResultActions result = mockMvc.perform(get("/times/available")
                .param("date", "2024-06-09")
                .param("themeId", "1"));

        result.andExpect(status().isOk())
                .andDo(document("/times/findAllAvailableTimes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("date").description("날짜"),
                                parameterWithName("themeId").description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("[].alreadyBooked").type(JsonFieldType.BOOLEAN).description("예약 여부")
                        ))
                );
    }
}

package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.application.ReservationTimeService;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.web.api.AdminReservationTimeController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(AdminReservationTimeController.class)
public class AdminReservationTimeControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationTimeService timeService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @DisplayName("예약 시간 저장")
    @Test
    void saveReservationTime() throws Exception {
        LocalTime time3 = LocalTime.of(15, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(time3);
        ReservationTimeResponse response = new ReservationTimeResponse(3L, time3);

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(timeService.saveReservationTime(any())).willReturn(response);

        ResultActions result = mockMvc.perform(post("/admin/times")
                .header(HttpHeaders.COOKIE, "token=adminToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(request)));

        result.andExpect(status().isCreated())
                .andDo(document("/admin/saveReservationTime",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        requestFields(
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간 (HH:mm)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록된 리소스 URI")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간 (HH:mm)")
                        ))
                );
    }

    @DisplayName("예약 시간 삭제")
    @Test
    void deleteReservationTime() throws Exception {
        Long timeId = 1L;

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);

        ResultActions result = mockMvc.perform(delete("/admin/times/{idTime}", timeId)
                .header(HttpHeaders.COOKIE, "token=adminToken"));

        result.andExpect(status().isNoContent())
                .andDo(document("/admin/deleteReservationTime",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idTime").description("예약 시간 아이디")
                        ))
                );
    }
}

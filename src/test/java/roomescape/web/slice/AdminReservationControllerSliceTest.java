package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
import roomescape.application.CancelService;
import roomescape.application.ReservationService;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.response.member.MemberResponse;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.reservation.Status;
import roomescape.web.api.AdminReservationController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(AdminReservationController.class)
public class AdminReservationControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private CancelService cancelService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    private final ReservationTimeResponse time1 = new ReservationTimeResponse(1L, LocalTime.of(11, 0));
    private final ReservationTimeResponse time2 = new ReservationTimeResponse(2L, LocalTime.of(21, 0));
    private final ThemeResponse theme1 = new ThemeResponse(1L, "테마1 이름", "설명", "썸네일.jpg");
    private final ThemeResponse theme2 = new ThemeResponse(2L, "테마2 이름", "설명", "썸네일.jpg");
    private final MemberResponse member1 = new MemberResponse(1L, "재즈");
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);
    private final LocalDate yesterday = LocalDate.now().minusDays(1);

    @DisplayName("예약 생성")
    @Test
    void makeReservation() throws Exception {
        ReservationRequest request = new ReservationRequest(tomorrow, 1L, 1L, 1L);
        ReservationResponse response = new ReservationResponse(1L, tomorrow, time1, theme1, member1, Status.RESERVED
        );

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(reservationService.saveReservationByAdmin(any())).willReturn(response);

        ResultActions result = mockMvc.perform(post("/admin/reservations")
                .header(HttpHeaders.COOKIE, "token=adminToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andDo(document("/admin/makeReservation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록된 리소스 URI")
                        )
                ));
    }

    @DisplayName("예약 확정된 모든 예약 조회")
    @Test
    void findAllReservations() throws Exception {
        List<ReservationResponse> responseList = List.of(
                new ReservationResponse(1L, yesterday, time1, theme1, member1, Status.RESERVED),
                new ReservationResponse(2L, tomorrow, time2, theme2, member1, Status.RESERVED)
        );

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(reservationService.findAllReservationsWithReserved()).willReturn(responseList);

        ResultActions result = mockMvc.perform(get("/admin/reservations")
                .header(HttpHeaders.COOKIE, "token=adminToken")
        );

        result.andExpect(status().isOk())
                .andDo(document("/admin/findAllReservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ));
    }

    @DisplayName("예약 검색")
    @Test
    void searchAllReservations() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);
        Long memberId = 1L;
        Long themeId = 1L;

        List<ReservationResponse> responseList = List.of(
                new ReservationResponse(1L, yesterday, time1, theme1, member1, Status.RESERVED)
        );

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(reservationService.findAllReservationByConditions(any())).willReturn(responseList);

        ResultActions result = mockMvc.perform(get("/admin/reservations/search")
                .header(HttpHeaders.COOKIE, "token=adminToken")
                .param("from", startDate.toString())
                .param("to", endDate.toString())
                .param("memberId", memberId.toString())
                .param("themeId", themeId.toString()));

        result.andExpect(status().isOk())
                .andDo(document("/admin/searchAllReservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        queryParameters(
                                parameterWithName("from").description("검색 시작 날짜"),
                                parameterWithName("to").description("검색 끝 날짜"),
                                parameterWithName("memberId").description("회원 아이디"),
                                parameterWithName("themeId").description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ));
    }

    @DisplayName("대기 목록 조회")
    @Test
    void findAllWaitings() throws Exception {
        List<ReservationResponse> responseList = List.of(
                new ReservationResponse(3L, yesterday, time1, theme1, member1, Status.WAITING),
                new ReservationResponse(4L, tomorrow, time2, theme2, member1, Status.WAITING)
        );

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(reservationService.findAllWaitings()).willReturn(responseList);

        ResultActions result = mockMvc.perform(get("/admin/waitings")
                .header(HttpHeaders.COOKIE, "token=adminToken"));

        result.andExpect(status().isOk())
                .andDo(document("/admin/findAllWaitings",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ));
    }

    @DisplayName("대기 취소")
    @Test
    void cancelWaiting() throws Exception {
        Long waitingId = 1L;

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);

        ResultActions result = mockMvc.perform(delete("/admin/waitings/{idWaiting}", waitingId)
                .header(HttpHeaders.COOKIE, "token=adminToken"));

        result.andExpect(status().isOk())
                .andDo(document("/admin/cancelWaiting",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idWaiting").description("예약 아이디")
                        )
                ));
    }

    @DisplayName("예약 취소")
    @Test
    void cancelReservation() throws Exception {
        Long reservationId = 1L;

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);

        ResultActions result = mockMvc.perform(delete("/admin/reservations/{idReservation}", reservationId)
                .header(HttpHeaders.COOKIE, "token=adminToken"));

        result.andExpect(status().isNoContent())
                .andDo(document("/admin/cancelReservation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idReservation").description("예약 아이디")
                        )
                ));
    }
}

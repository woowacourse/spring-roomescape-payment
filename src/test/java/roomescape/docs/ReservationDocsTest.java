package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.util.ApiDocumentUtils.getDocumentResponse;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import roomescape.config.TestWebMvcConfig;
import roomescape.docs.support.SnippetSupport;
import roomescape.member.dto.MemberResponse;
import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

@AutoConfigureRestDocs
@Import(TestWebMvcConfig.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class ReservationDocsTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ReservationService reservationService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReservationController(reservationService))
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .addInterceptors(new TestAdminInterceptor())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void postReservation() throws Exception {
        var request = new ReservationRequest(LocalDate.now().plusDays(1L), 1L, 1L, "payKey", "orderId", 1000L);
        var response = new ReservationResponse(1L,
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalDate.now().atStartOfDay().toLocalTime()),
                new ThemeResponse(1L, "테마명", "테마 설명", "thumbnail.jpg"),
                new MemberResponse(1L, "홍길동", "example@example.com", "password", "MEMBER")
        );

        when(reservationService.saveReservation(any(), any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document(SnippetSupport.snippet(this, "예약 생성"),
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 아이디"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약시간 아이디"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("member.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                fieldWithPath("member.password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
                                fieldWithPath("member.role").type(JsonFieldType.STRING).description("사용자 권한")
                        )
                ));
    }

    @Test
    void deleteReservation() throws Exception {
        Long reservationId = 1L;

        ResultActions result = mockMvc.perform(
                delete("/reservations/{id}", reservationId));

        result.andExpect(status().isNoContent())
                .andDo(document(SnippetSupport.snippet(this, "예약 삭제"),
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("예약 아이디")
                        )
                ));
    }

    @Test
    void findMyReservations() throws Exception {
        var response = List.of(
                new MyReservationResponse(
                        1L,
                        "테마명",
                        LocalDate.now().plusDays(1),
                        LocalDate.now().atStartOfDay().toLocalTime(),
                        "예약 상태",
                        "paymentKey",
                        1000L
                )
        );

        when(reservationService.findMyReservations(any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                get("/reservations/mine")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document(SnippetSupport.snippet(this, "내 예약 조회"),
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("[].time").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태"),
                                fieldWithPath("[].paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("결제 금액")
                        )
                ));
    }

    @Test
    void findReservationsByCriteria() throws Exception {
        var response = List.of(
                new ReservationResponse(1L,
                        LocalDate.now().plusDays(1),
                        new ReservationTimeResponse(1L, LocalDate.now().atStartOfDay().toLocalTime()),
                        new ThemeResponse(1L, "테마명", "테마 설명", "thumbnail.jpg"),
                        new MemberResponse(1L, "홍길동", "example@example.com", "password", "MEMBER")
                ));

        when(reservationService.findReservationsByCriteria(any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                get("/reservations")
                        .param("themeId", "1")
                        .param("memberId", "1")
                        .param("dateFrom", LocalDate.now().toString())
                        .param("dateTo", LocalDate.now().plusDays(7).toString())
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document(SnippetSupport.snippet(this, "예약 조회(조건 검색)"),
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("themeId").description("테마 아이디"),
                                parameterWithName("memberId").description("사용자 아이디"),
                                parameterWithName("dateFrom").description("시작 날짜"),
                                parameterWithName("dateTo").description("종료 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("날짜"),
                                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약시간 아이디"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일"),
                                fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("사용자 아이디"),
                                fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("[].member.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                fieldWithPath("[].member.password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
                                fieldWithPath("[].member.role").type(JsonFieldType.STRING).description("사용자 권한")
                        )
                ));
    }

    @Test
    void findAllAvailableTimes() throws Exception {
        var response = List.of(
                new AvailableReservationTimeResponse(
                        1L,
                        LocalDate.now().atStartOfDay().toLocalTime(),
                        false
                ));

        when(reservationService.findAllReservationTime(any(), any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                get("/reservations/times")
                        .param("date", LocalDate.now().toString())
                        .param("themeId", "1")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document(SnippetSupport.snippet(this, "예약 가능한 시간 조회"),
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("date").description("예약 날짜"),
                                parameterWithName("themeId").description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약시간 아이디"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
                                fieldWithPath("[].alreadyBooked").type(JsonFieldType.BOOLEAN).description("이미 예약된 시간 "
                                        + "여부")
                        )
                ));
    }
}

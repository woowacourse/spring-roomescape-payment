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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.util.ApiDocumentUtils.getDocumentResponse;

import java.time.LocalDate;

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
import roomescape.member.dto.MemberResponse;
import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;
import roomescape.reservation.controller.AdminReservationController;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

@AutoConfigureRestDocs
@Import(TestWebMvcConfig.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class AdminReservationDocsTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ReservationService reservationService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AdminReservationController(reservationService))
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .addInterceptors(new TestAdminInterceptor())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void saveReservation() throws Exception {
        var request = new AdminReservationRequest(LocalDate.now().plusDays(1L), 1L, 1L, 1L);
        var response = new ReservationResponse(1L,
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalDate.now().atStartOfDay().toLocalTime()),
                new ThemeResponse(1L, "테마명", "테마 설명", "thumbnail.jpg"),
                new MemberResponse(1L, "홍길동", "example@example.com", "password", "MEMBER")
        );

        when(reservationService.saveAdminReservation(any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("어드민 예약 생성",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd 형식)"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 ID"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd 형식)"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 ID"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 이미지 URL"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("member.email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("member.password").type(JsonFieldType.STRING).description("회원 비밀번호"),
                                fieldWithPath("member.role").type(JsonFieldType.STRING).description("회원 역할")
                        )));
    }

    @Test
    void cancelReservation() throws Exception {
        Long reservationId = 1L;

        ResultActions result = mockMvc.perform(
                delete("/admin/reservations/{id}", reservationId));

        result.andExpect(status().isNoContent())
                .andDo(document("어드민 예약 취소",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("예약 아이디")
                        )
                ));
    }
}

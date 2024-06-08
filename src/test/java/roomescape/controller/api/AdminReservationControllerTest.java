package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.CreateReservationRequest;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.FindReservationResponse;
import roomescape.controller.dto.FindReservationStandbyResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.AdminReservationService;

@AutoConfigureRestDocs
@WebMvcTest(AdminReservationController.class)
class AdminReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminReservationService adminReservationService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @BeforeEach
    void setUp() {
        given(checkRoleInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);
    }

    @DisplayName("어드민 예약 저장")
    @Test
    void save() throws Exception {
        given(adminReservationService.reserve(any(), any(), any(), any()))
            .willReturn(new CreateReservationResponse(
                1L,
                "트레",
                LocalDate.parse("2060-01-01"),
                LocalTime.parse("10:00"),
                "테마"
            ));

        String request = objectMapper.writeValueAsString(
            new CreateReservationRequest(1L, LocalDate.parse("2060-01-01"), 1L, 1L));

        mockMvc.perform(post("/admin/reservations")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("admin/reservations/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("memberId").description("회원 ID"),
                    fieldWithPath("date").description("이용일"),
                    fieldWithPath("timeId").description("예약 시간 ID"),
                    fieldWithPath("themeId").description("테마 ID")
                ),
                responseFields(
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("memberName").description("예약을 요청한 회원명"),
                    fieldWithPath("date").description("이용일"),
                    fieldWithPath("time").description("이용 시간"),
                    fieldWithPath("themeName").description("예약 테마명")
                )
            ))
            .andExpect(status().isCreated());
    }

    @DisplayName("어드민 예약 삭제")
    @Test
    void delete() throws Exception {
        doNothing()
            .when(adminReservationService)
            .deleteById(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/reservations/{id}", 1L))
            .andDo(print())
            .andDo(document("admin/reservations/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("예약 ID")
                )
            ))
            .andExpect(status().isNoContent());
    }

    @DisplayName("어드민 전체 예약 조회")
    @Test
    void findAll() throws Exception {
        given(adminReservationService.findAllReserved())
            .willReturn(List.of(
                new FindReservationResponse(
                    1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "우테코 탈출하기"),
                new FindReservationResponse(
                    2L, "호돌", LocalDate.parse("2060-01-02"), LocalTime.parse("11:00"), "루터회관 탈출하기")
            ));

        mockMvc.perform(get("/admin/reservations"))
            .andDo(print())
            .andDo(document("admin/reservations/findAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("예약 ID"),
                    fieldWithPath("[].memberName").description("예약을 요청한 회원명"),
                    fieldWithPath("[].date").description("이용일"),
                    fieldWithPath("[].time").description("이용 시간"),
                    fieldWithPath("[].themeName").description("예약 테마명")
                )
            ))
            .andExpect(status().isOk());
    }

    @DisplayName("어드민 전체 예약대기 조회")
    @Test
    void findAllStandby() throws Exception {
        given(adminReservationService.findAllStandby())
            .willReturn(List.of(
                new FindReservationStandbyResponse(
                    3L, "트레", "공포의 방탈출", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00")),
                new FindReservationStandbyResponse(
                    4L, "호돌", "신나는 방탈출", LocalDate.parse("2060-01-02"), LocalTime.parse("11:00"))
            ));

        mockMvc.perform(get("/admin/reservations/standby"))
            .andDo(print())
            .andDo(document("admin/reservations/findAllStandby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("예약 ID"),
                    fieldWithPath("[].name").description("예약한 회원명"),
                    fieldWithPath("[].theme").description("테마 이름"),
                    fieldWithPath("[].date").description("이용일"),
                    fieldWithPath("[].startAt").description("이용 시간")
                )
            ))
            .andExpect(status().isOk());
    }

    @DisplayName("어드민 예약대기 삭제")
    @Test
    void deleteStandby() throws Exception {
        doNothing()
            .when(adminReservationService)
            .deleteStandby(any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/reservations/standby/{reservationId}", 1L))
            .andDo(print())
            .andDo(document("admin/reservations/deleteStandby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("reservationId").description("예약 ID")
                )
            ))
            .andExpect(status().isNoContent());
    }

    @DisplayName("어드민 필터로 예약 조회")
    @Test
    void find() throws Exception {
        given(adminReservationService.findAllByFilter(any(), any(), any(), any()))
            .willReturn(List.of(
                new FindReservationResponse(
                    1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "우테코 탈출하기"),
                new FindReservationResponse(
                    2L, "트레", LocalDate.parse("2060-01-02"), LocalTime.parse("11:00"), "우테코 탈출하기"),
                new FindReservationResponse(
                    3L, "트레", LocalDate.parse("2060-01-03"), LocalTime.parse("12:00"), "우테코 탈출하기")
            ));

        mockMvc.perform(get("/admin/reservations/search")
                .param("themeId", "1")
                .param("memberId", "1")
                .param("dateFrom", "2060-01-01")
                .param("dateTo", "2060-01-03"))
            .andDo(print())
            .andDo(document("admin/reservations/find",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("themeId").description("검색할 테마 ID"),
                    parameterWithName("memberId").description("검색할 회원 ID"),
                    parameterWithName("dateFrom").description("검색 날짜 시작일"),
                    parameterWithName("dateTo").description("검색 날짜 종료일")
                ),
                responseFields(
                    fieldWithPath("[].id").description("예약 ID"),
                    fieldWithPath("[].memberName").description("예약한 회원명"),
                    fieldWithPath("[].date").description("이용일"),
                    fieldWithPath("[].time").description("이용 시간"),
                    fieldWithPath("[].themeName").description("테마 이름")
                )
            ))
            .andExpect(status().isOk());
    }
}

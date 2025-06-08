package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.config.AdminAuthBaseTest;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class AdminReservationControllerTest extends AdminAuthBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("관리자 예약 추가 테스트 데이터")
    void saveReservationTestData() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        long timeId = 1L;
        long themeId = 1L;
        long memberId = 1L;

        AdminReservationRequest request = new AdminReservationRequest(date, timeId, themeId, memberId);

        ReservationResponse response = new ReservationResponse(
                1L, date,
                new ReservationTimeResponse(timeId, LocalTime.of(13, 0)),
                new ThemeResponse(themeId, "방탈출 테마1", "테마1 설명", "theme1.jpg"),
                new MemberResponse(memberId, "테스트 사용자")
        );

        given(reservationService.saveAdminReservation(any(AdminReservationRequest.class))).willReturn(response);

        // when && then
        mockMvc.perform(addAuthCookie(post("/admin/reservations"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.date").value("2024-01-01"))
                .andExpect(jsonPath("$.time.id").value(timeId))
                .andExpect(jsonPath("$.time.startAt").value("13:00"))
                .andExpect(jsonPath("$.theme.id").value(themeId))
                .andExpect(jsonPath("$.theme.name").value("방탈출 테마1"))
                .andExpect(jsonPath("$.theme.description").value("테마1 설명"))
                .andExpect(jsonPath("$.theme.thumbnail").value("theme1.jpg"))
                .andExpect(jsonPath("$.member.id").value(memberId))
                .andExpect(jsonPath("$.member.name").value("테스트 사용자"))
                .andDo(document("reservation/admin/reservation-save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("memberId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 ID"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 ID"),
                                fieldWithPath("time.startAt").description("시작 시간"),
                                fieldWithPath("theme.id").description("테마 ID"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 썸네일 이미지"),
                                fieldWithPath("member.id").description("회원 ID"),
                                fieldWithPath("member.name").description("회원 이름")
                        )
                ));
    }

    @Test
    @DisplayName("관리자 예약 취소 테스트 데이터")
    void cancelReservationTestData() throws Exception {
        // given
        Long reservationId = 1L;

        doNothing().when(reservationService).deleteReservation(anyLong());

        // when && then
        mockMvc.perform(addAuthCookie(delete("/admin/reservations/{reservationId}", reservationId)))
                .andExpect(status().isNoContent())
                .andDo(document("reservation/admin/reservation-delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("reservationId").description("삭제할 예약 ID")
                        )
                ));
    }
}

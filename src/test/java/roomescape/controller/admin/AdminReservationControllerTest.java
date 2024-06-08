package roomescape.controller.admin;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.ReservationConditionRequest;
import roomescape.service.dto.request.ReservationSaveRequest;
import roomescape.service.dto.response.*;
import roomescape.service.reservation.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AdminReservationController.class)
public class AdminReservationControllerTest extends RestDocsTestSupport {

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("어드민의 예약 생성")
    public void saveReservation_201() throws Exception {
        //given
        ReservationSaveRequest request = new ReservationSaveRequest(
                1L,
                LocalDate.now().plusDays(1),
                1L,
                1L
        );

        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "user", "USER"),
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0, 0)),
                new ThemeResponse(1L, "테마1", "테마 설명", "섬네일 링크")
        );

        Mockito.when(reservationService.saveReservation(any()))
                .thenReturn(response);

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.member.id").value(1L))
                .andExpect(jsonPath("$.time.id").value(1L))
                .andExpect(jsonPath("$.theme.id").value(1L))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("memberId")
                                        .type(NUMBER)
                                        .description("예약 회원 아이디")
                                        .attributes(constraints("1이상의 양수입니다.")),
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("오늘 이후의 날짜만 가능합니다.")),
                                fieldWithPath("timeId")
                                        .type(NUMBER)
                                        .description("예약 시간 아이디")
                                        .attributes(constraints("예약날짜가 오늘이라면 현재 이후의 시간 아이디만 가능합니다.")),
                                fieldWithPath("themeId")
                                        .type(NUMBER)
                                        .description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("예약 아이디")
                                        .attributes(constraints("양수의 예약 아이디입니다")),
                                fieldWithPath("member")
                                        .type(MemberResponse.class)
                                        .description("예약한 회원 정보"),
                                fieldWithPath("member.id")
                                        .type(NUMBER)
                                        .description("회원 id"),
                                fieldWithPath("member.name")
                                        .type(STRING)
                                        .description("회원 이름"),
                                fieldWithPath("member.role")
                                        .type(STRING)
                                        .description("회원 권한")
                                        .attributes(constraints( "ADMIN: 어드민, USER : 사용자")),
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("예약된 날짜(오늘 이후만 가능)")),
                                fieldWithPath("time")
                                        .type(ReservationTimeResponse.class)
                                        .description("예약된 시간 정보"),
                                fieldWithPath("time.id")
                                        .type(NUMBER)
                                        .description("예약 시간 id"),
                                fieldWithPath("time.startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간"),
                                fieldWithPath("theme")
                                        .type(ThemeResponse.class)
                                        .description("예약된 테마 정보"),
                                fieldWithPath("theme.id")
                                        .type(NUMBER)
                                        .description("테마 id"),
                                fieldWithPath("theme.name")
                                        .type(STRING)
                                        .description("테마 이름"),
                                fieldWithPath("theme.description")
                                        .type(STRING)
                                        .description("테마 설명"),
                                fieldWithPath("theme.thumbnail")
                                        .type(STRING)
                                        .description("테마 썸네일 링크")
                        )
                ));
    }

    @Test
    @DisplayName("어드민의 예약 필터링 조회")
    public void getReservations() throws Exception{

        //given
        ReservationConditionRequest request = new ReservationConditionRequest(
                1L,
                1L,
                LocalDate.now().minusWeeks(1),
                LocalDate.now()
        );

        ReservationResponses response = new ReservationResponses(
                List.of(
                    new ReservationResponse(
                            1L, 
                            new MemberResponse(1L, "user", "USER"), 
                            LocalDate.now().minusDays(1), 
                            new ReservationTimeResponse(1L, LocalTime.of(9, 0, 0)), 
                            new ThemeResponse(1L, "테마1", "테마 설명", "섬네일 링크")
                    ),
                    new ReservationResponse(
                            2L, 
                            new MemberResponse(1L, "user", "USER"), 
                            LocalDate.now().minusDays(2), 
                            new ReservationTimeResponse(2L, LocalTime.of(10, 0, 0)), 
                            new ThemeResponse(2L, "테마2", "테마 설명", "섬네일 링크")
                    )
                )
        );

        Mockito.when(reservationService.findReservationsByCondition(any()))
                .thenReturn(response);

        mockMvc.perform(get("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                        .param("themeId", request.themeId().toString())
                        .param("memberId", request.memberId().toString())
                        .param("dateTo", request.dateTo().toString())
                        .param("dateFrom", request.dateFrom().toString())
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("themeId")
                                        .description("테마 아이디")
                                        .optional()
                                        .attributes(constraints("1이상의 양수입니다.")),
                                parameterWithName("memberId")
                                        .description("멤버 아이디")
                                        .optional()
                                        .attributes(constraints("1이상의 양수입니다.")),
                                parameterWithName("dateTo")
                                        .description("예약 조회 시작날짜")
                                        .optional(),
                                parameterWithName("dateFrom")
                                        .description("예약 조회 종료 날짜")
                                        .optional()
                                        .attributes(constraints("조회 시작날짜보다 이후의 날짜여야 합니다."))
                        ),
                        responseFields(
                                fieldWithPath("reservationResponses")
                                        .type(ARRAY)
                                        .description("필터링된 예약 목록"),
                                fieldWithPath("reservationResponses[].id")
                                        .type(NUMBER)
                                        .description("예약 아이디")
                                        .attributes(constraints("양수의 예약 아이디입니다")),
                                fieldWithPath("reservationResponses[].member")
                                        .type(MemberResponse.class)
                                        .description("예약한 회원 정보"),
                                fieldWithPath("reservationResponses[].member.id")
                                        .type(NUMBER)
                                        .description("회원 id"),
                                fieldWithPath("reservationResponses[].member.name")
                                        .type(STRING)
                                        .description("회원 이름"),
                                fieldWithPath("reservationResponses[].member.role")
                                        .type(STRING)
                                        .description("회원 권한")
                                        .attributes(constraints( "ADMIN: 어드민, USER : 사용자")),
                                fieldWithPath("reservationResponses[].date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("예약된 날짜(오늘 이후만 가능)")),
                                fieldWithPath("reservationResponses[].time")
                                        .type(ReservationTimeResponse.class)
                                        .description("예약된 시간 정보"),
                                fieldWithPath("reservationResponses[].time.id")
                                        .type(NUMBER)
                                        .description("예약 시간 id"),
                                fieldWithPath("reservationResponses[].time.startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간"),
                                fieldWithPath("reservationResponses[].theme")
                                        .type(ThemeResponses.class)
                                        .description("예약된 테마 정보"),
                                fieldWithPath("reservationResponses[].theme.id")
                                        .type(NUMBER)
                                        .description("테마 id"),
                                fieldWithPath("reservationResponses[].theme.name")
                                        .type(STRING)
                                        .description("테마 이름"),
                                fieldWithPath("reservationResponses[].theme.description")
                                        .type(STRING)
                                        .description("테마 설명"),
                                fieldWithPath("reservationResponses[].theme.thumbnail")
                                        .type(STRING)
                                        .description("테마 썸네일 링크")
                        )
                ));

    }


    @Test
    @DisplayName("어드민의 예약 삭제")
    public void deleteReservation_204() throws Exception {
        //given
        mockMvc.perform(delete("/admin/reservations/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }


}


package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.config.handler.AdminAuthorizationInterceptor;
import roomescape.config.handler.AuthenticationArgumentResolver;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.controller.AdminReservationController;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationCreateService;
import roomescape.reservation.service.ReservationFindService;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.TimeResponse;

@WebMvcTest(controllers = AdminReservationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                WebMvcConfigurer.class,
                                AuthenticationArgumentResolver.class,
                                AdminAuthorizationInterceptor.class})
        })
@ExtendWith(RestDocumentationExtension.class)
class AdminReservationApiTest {
    private static final ReservationResponse RESPONSE1 = new ReservationResponse(
            1L, new MemberResponse(1L, "브라운"),
            LocalDate.of(2024, 8, 15),
            new TimeResponse(1L, LocalTime.of(19, 0)),
            new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    private static final ReservationResponse RESPONSE2 = new ReservationResponse(
            2L, new MemberResponse(2L, "브리"),
            LocalDate.of(2024, 8, 20),
            new TimeResponse(1L, LocalTime.of(19, 0)),
            new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ReservationFindService reservationFindService;
    @MockBean
    private ReservationCreateService reservationCreateService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("예약을 찾는다")
    @Test
    void findReservationsTest() throws Exception {
        List<ReservationResponse> responses = List.of(RESPONSE1, RESPONSE2);

        given(reservationFindService.findReservations(any()))
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/admin/reservations"));

        result.andExpect(status().isOk())
                .andDo(document("admin/find-reservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("예약의 id"),
                                fieldWithPath("[].member.id").description("멤버의 id"),
                                fieldWithPath("[].member.name").description("멤버의 이름"),
                                fieldWithPath("[].date").description("날짜"),
                                fieldWithPath("[].time.id").description("시간의 id"),
                                fieldWithPath("[].time.startAt").description("시간"),
                                fieldWithPath("[].theme.id").description("테마의 id"),
                                fieldWithPath("[].theme.name").description("테마의 이름"),
                                fieldWithPath("[].theme.description").description("테마의 설명"),
                                fieldWithPath("[].theme.thumbnail").description("테마의 썸네일")
                        )
                ));
    }

    @DisplayName("관리자 권힌으로 예약을 생성한다.")
    @Test
    void createReservationTest() throws Exception {
        AdminReservationCreateRequest adminReservationCreateRequest = new AdminReservationCreateRequest(
                1L,
                LocalDate.of(2999, 12, 12),
                1L,
                1L);

        given(reservationCreateService.createReservation(any()))
                .willReturn(RESPONSE1);

        ResultActions result = mockMvc.perform(post("/admin/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(adminReservationCreateRequest)));

        result.andExpect(status().isCreated())
                .andDo(document("admin/create-reservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("memberId").description("멤버의 id"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("timeId").description("시간의 id"),
                                fieldWithPath("themeId").description("테마의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약의 id"),
                                fieldWithPath("member.id").description("멤버의 id"),
                                fieldWithPath("member.name").description("멤버의 이름"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("time.id").description("시간의 id"),
                                fieldWithPath("time.startAt").description("시간"),
                                fieldWithPath("theme.id").description("테마의 id"),
                                fieldWithPath("theme.name").description("테마의 이름"),
                                fieldWithPath("theme.description").description("테마의 설명"),
                                fieldWithPath("theme.thumbnail").description("테마의 썸네일")
                        )
                ));
    }
}

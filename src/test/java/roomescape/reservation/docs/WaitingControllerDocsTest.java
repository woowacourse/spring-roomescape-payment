package roomescape.reservation.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.config.RestDocsConfig;
import roomescape.global.auth.dto.UserInfo;
import roomescape.global.auth.infrastructure.AuthorizationExtractor;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.global.auth.service.AuthService;
import roomescape.member.domain.MemberRole;
import roomescape.member.dto.response.MemberResponse;
import roomescape.reservation.controller.WaitingController;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.WaitingFacadeService;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

@Import({RestDocsConfig.class, AuthorizationExtractor.class})
@WebMvcTest(WaitingController.class)
@ExtendWith(RestDocumentationExtension.class)
public class WaitingControllerDocsTest {

    @Value("${application.ip}")
    private String ip;

    @Value("${application.port}")
    private int port;

    @MockitoBean
    private WaitingFacadeService waitingFacadeService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider provider
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider)
                        .uris()
                        .withScheme("http")
                        .withHost(ip)
                        .withPort(port))
                .alwaysDo(restDocs)
                .build();
    }

    @Test
    void findWaitings() throws Exception {
        List<ReservationResponse> response = List.of(
                new ReservationResponse(1L, new MemberResponse(1L, "cogi"), LocalDate.of(2025, 6, 7),
                        new ReservationTimeResponse(1L,
                                LocalTime.of(10, 0)), new ThemeResponse(1L, "a", "a", "a"),
                        "1번째 예약 대기"),
                new ReservationResponse(2L, new MemberResponse(1L, "cogi"), LocalDate.of(2025, 6, 8),
                        new ReservationTimeResponse(1L,
                                LocalTime.of(11, 0)), new ThemeResponse(1L, "a", "a", "a"),
                        "2번째 예약 대기")
        );
        when(waitingFacadeService.findWaitings())
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/waiting"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Reservation Id"),
                                        fieldWithPath("[].member.id").type(JsonFieldType.NUMBER)
                                                .description("Reservation Member Id"),
                                        fieldWithPath("[].member.name").type(JsonFieldType.STRING)
                                                .description("Reservation Member Name"),
                                        fieldWithPath("[].date").type(JsonFieldType.STRING).description("Reservation Date"),
                                        fieldWithPath("[].time.id").type(JsonFieldType.NUMBER)
                                                .description("Reservation Time Id"),
                                        fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                                                .description("Reservation Time"),
                                        fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER)
                                                .description("Reservation Theme Id"),
                                        fieldWithPath("[].theme.name").type(JsonFieldType.STRING)
                                                .description("Reservation Theme Name"),
                                        fieldWithPath("[].theme.description").type(JsonFieldType.STRING)
                                                .description("Reservation Theme Description"),
                                        fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                                .description("Reservation Theme Thumbnail"),
                                        fieldWithPath("[].reservedStatus").type(JsonFieldType.STRING)
                                                .description("Reservation ReservedStatus")
                                )
                        )
                );
    }

    @Test
    void deleteWaiting() throws Exception {
        when(jwtProvider.getRole(any()))
                .thenReturn(MemberRole.USER);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/waiting/{id}", 1L)
                        .cookie(new Cookie("token", "user_access_token")))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(restDocs.document());
    }

    @Test
    void createWaiting() throws Exception {
        ReservationRequest request = new ReservationRequest(LocalDate.of(2025, 6, 7), 1L, 1L);
        ReservationResponse response = new ReservationResponse(2L, new MemberResponse(1L, "cogi"),
                LocalDate.of(2025, 6, 8),
                new ReservationTimeResponse(1L,
                        LocalTime.of(11, 0)), new ThemeResponse(1L, "a", "a", "a"),
                ReservationStatus.WAITING.getName());
        when(jwtProvider.getRole(any()))
                .thenReturn(MemberRole.USER);
        when(authService.makeUserInfo(anyString()))
                .thenReturn(new UserInfo(1L, MemberRole.USER));
        when(waitingFacadeService.createWaiting(any(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/waiting")
                        .cookie(new Cookie("token", "user_access_token"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("Reservation Id"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER)
                                        .description("Reservation Member Id"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING)
                                        .description("Reservation Member Name"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("Reservation Date"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER)
                                        .description("Reservation Time Id"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                        .description("Reservation Time"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER)
                                        .description("Reservation Theme Id"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING)
                                        .description("Reservation Theme Name"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING)
                                        .description("Reservation Theme Description"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("Reservation Theme Thumbnail"),
                                fieldWithPath("reservedStatus").type(JsonFieldType.STRING)
                                        .description("Reservation ReservedStatus")
                        )
                ));
    }
}

package roomescape.reservationtime.docs;

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
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import roomescape.global.auth.infrastructure.AuthorizationExtractor;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.global.auth.service.AuthService;
import roomescape.member.domain.MemberRole;
import roomescape.reservationtime.controller.ReservationTimeController;
import roomescape.reservationtime.dto.request.ReservationTimeCreateRequest;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

@Import({RestDocsConfig.class, AuthorizationExtractor.class})
@WebMvcTest(ReservationTimeController.class)
@ExtendWith(RestDocumentationExtension.class)
public class ReservationTimeControllerDocsTest {

    @MockitoBean
    private ReservationTimeService reservationTimeService;

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
                        .withHost("123.123.123.123")
                        .withPort(8080))
                .alwaysDo(restDocs)
                .build();
    }

    @Test
    void getReservationTimes() throws Exception {
        List<ReservationTimeResponse> response = List.of(
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ReservationTimeResponse(2L, LocalTime.of(11, 0))
        );
        when(reservationTimeService.getReservationTimes())
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/times"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Time Id"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("Time")
                        )
                ));
    }

    @Test
    void getAvailableReservationTimes() throws Exception {
        List<AvailableReservationTimeResponse> response = List.of(
                new AvailableReservationTimeResponse(1L, LocalTime.of(10, 0), false),
                new AvailableReservationTimeResponse(2L, LocalTime.of(11, 0), true)
        );
        when(reservationTimeService.getAvailableReservationTimes(any(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/times/available")
                        .param("date", "2025-06-07")
                        .param("themeId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].timeId").type(JsonFieldType.NUMBER).description("Time Id"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("Time"),
                                fieldWithPath("[].alreadyBooked").type(JsonFieldType.BOOLEAN)
                                        .description("AlreadyBooked")
                        )
                ));
    }

    @Test
    void createReservationTime() throws Exception {
        ReservationTimeCreateRequest request = new ReservationTimeCreateRequest(LocalTime.of(10, 0));
        ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.ADMIN);
        when(reservationTimeService.create(any()))
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/times")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "admin_access_token")))
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("New Time Id"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("New Time")
                        )
                ));
    }

    @Test
    void deleteReservationTimes() throws Exception {
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.ADMIN);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/times/{timeId}", 1L)
                        .cookie(new Cookie("token", "admin_access_token")))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(restDocs.document());
    }
}

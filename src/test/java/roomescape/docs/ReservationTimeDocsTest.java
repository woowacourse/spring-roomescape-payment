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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.util.ApiDocumentUtils.getDocumentResponse;

import java.time.LocalTime;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import roomescape.config.TestWebMvcConfig;
import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;
import roomescape.reservationtime.controller.ReservationTimeController;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

@AutoConfigureRestDocs
@Import(TestWebMvcConfig.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class ReservationTimeDocsTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ReservationTimeService reservationTimeService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReservationTimeController(reservationTimeService))
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .addInterceptors(new TestAdminInterceptor())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void saveTime() throws Exception {
        var request = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        var response = new ReservationTimeResponse(1L, LocalTime.of(10, 0));

        when(reservationTimeService.saveTime(any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(post("/times")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("예약 시간 생성",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간")
                        )
                ));
    }

    @Test
    void findAll() throws Exception {
        var response = List.of(new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ReservationTimeResponse(2L, LocalTime.of(11, 0)));

        when(reservationTimeService.findAll())
                .thenReturn(response);

        ResultActions result = mockMvc.perform(get("/times")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("예약 시간 조회",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시작 시간")
                        )
                ));
    }

    @Test
    void delete() throws Exception {
        Long timeId = 1L;

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/times/{id}", timeId));

        result.andExpect(status().isNoContent())
                .andDo(document("예약 시간 삭제",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("예약 시간 아이디")
                        )
                ));
    }
}

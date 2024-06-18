package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.FindTimeAndAvailabilityResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.ReservationTimeService;

@AutoConfigureRestDocs
@WebMvcTest(UserReservationTimeController.class)
class UserReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationTimeService reservationTimeService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @DisplayName("유저 날짜 & 테마별 시간 목록 및 예약 가능 여부 조회")
    @Test
    void findAllWithAvailability() throws Exception {
        given(reservationTimeService.findAllWithBookAvailability(any(), any()))
            .willReturn(List.of(
                new FindTimeAndAvailabilityResponse(1L, LocalTime.parse("10:00"), true),
                new FindTimeAndAvailabilityResponse(2L, LocalTime.parse("11:00"), false),
                new FindTimeAndAvailabilityResponse(3L, LocalTime.parse("12:00"), false),
                new FindTimeAndAvailabilityResponse(4L, LocalTime.parse("13:00"), true)
            ));

        mockMvc.perform(get("/times/available")
                .param("date", "2060-01-01")
                .param("id", "1"))
            .andDo(print())
            .andDo(document("times/available",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("date").description("이용일"),
                    parameterWithName("id").description("테마 ID")
                ),
                responseFields(
                    fieldWithPath("[].id").description("시간 ID"),
                    fieldWithPath("[].startAt").description("이용 시간"),
                    fieldWithPath("[].alreadyBooked").description("이미 예약되었는지 여부 (true인 경우 이미 예약되어 예약 불가)")
                )
            ))
            .andExpect(status().isOk());
    }
}

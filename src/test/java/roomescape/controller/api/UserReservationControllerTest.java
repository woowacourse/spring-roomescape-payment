package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.TossPaymentService;
import roomescape.service.UserReservationService;

@AutoConfigureRestDocs
@WebMvcTest(UserReservationController.class)
class UserReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserReservationService userReservationService;

    @MockBean
    private TossPaymentService tossPaymentService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @BeforeEach
    void auth() {
        given(checkUserInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);
    }

    @DisplayName("유저 예약 추가")
    @Test
    void save() throws Exception {
        given(userReservationService.reserve(any(), any()))
            .willReturn(new CreateReservationResponse(
                1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "방탈출 테마"));

        String request = objectMapper.writeValueAsString(
            new CreateUserReservationRequest(
                LocalDate.parse("2060-01-01"), 1L, 1L, "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "MC4wODU4ODQwMzg4NDk0", 10000, "paymentType"));

        mockMvc.perform(post("/reservations")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservations/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())))
            .andExpect(status().isCreated());
    }

//    @DisplayName("유저 예약대기 결제")
//    @Test
//    void payStandby() {
//        given(tossPaymentService.pay())
//    }

    @DisplayName("유저 예약대기 추가")
    @Test
    void standby() {
    }

    @DisplayName("유저 예약대기 삭제")
    @Test
    void deleteStandby() {
    }

    @DisplayName("유저 본인의 예약 조회")
    @Test
    void findMyReservations() {
    }
}

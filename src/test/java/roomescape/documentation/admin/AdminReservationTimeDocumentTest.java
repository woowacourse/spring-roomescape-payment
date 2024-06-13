package roomescape.documentation.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ReservationTimeService;
import roomescape.application.dto.request.ReservationTimeRequest;
import roomescape.application.dto.response.ReservationTimeResponse;
import roomescape.documentation.AbstractDocumentTest;
import roomescape.domain.exception.DomainNotFoundException;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.exception.BadRequestException;
import roomescape.presentation.api.admin.AdminReservationTimeController;

@WebMvcTest(AdminReservationTimeController.class)
class AdminReservationTimeDocumentTest extends AbstractDocumentTest {

    @MockBean
    private ReservationTimeService reservationTimeService;

    @Test
    @DisplayName("예약 시간을 추가한다.")
    void addReservationTime() throws Exception {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 0));
        ReservationTimeResponse response = ReservationTimeResponse.from(new ReservationTime(1L, LocalTime.of(10, 0)));

        when(reservationTimeService.addReservationTime(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/admin/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("admin/times/add",
                        requestFields(
                                fieldWithPath("startAt").description("시작 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 시간 식별자"),
                                fieldWithPath("startAt").description("시작 시간")
                        ))
        );
    }

    @Test
    @DisplayName("예약 시간을 추가할 때, 이미 존재하는 시간이면 실패한다.")
    void addReservationTimeWithAlreadyExistTime() throws Exception {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 0));

        when(reservationTimeService.addReservationTime(any()))
                .thenThrow(new BadRequestException(
                        String.format("해당 시간의 예약 시간이 이미 존재합니다. (시작 시간: %s)", request.startAt())
                ));

        mockMvc.perform(
                        post("/admin/times")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        document("admin/times/add/already-exist",
                                responseFields(
                                        fieldWithPath("message").description("해당 시간의 예약 시간이 이미 존재합니다. (시작 시간: 10:00)")
                                )
                        )
                );
    }

    @Test
    @DisplayName("예약 시간을 삭제한다.")
    void deleteReservationTime() throws Exception {
        Long id = 1L;
        doNothing()
                .when(reservationTimeService).deleteReservationTimeById(id);

        mockMvc.perform(
                delete("/admin/times/{id}", id)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isNoContent()
        ).andDo(
                document("admin/times/delete",
                        pathParameters(
                                parameterWithName("id").description("예약 시간 식별자")
                        ))
        );
    }

    @Test
    @DisplayName("예약 시간을 삭제할 때, 존재하지 않는 시간이면 실패한다.")
    void deleteReservationTimeWithNotExistTime() throws Exception {
        Long id = 1L;
        doThrow(new DomainNotFoundException(String.format("해당 id의 예약 시간이 존재하지 않습니다. (id: %d)", id)))
                .when(reservationTimeService).deleteReservationTimeById(anyLong());

        mockMvc.perform(
                delete("/admin/times/{id}", id)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                document("admin/times/delete/not-exist",
                        responseFields(
                                fieldWithPath("message").description("해당 id의 예약 시간이 존재하지 않습니다. (id: 1)")
                        )
                ));
    }

    @Test
    @DisplayName("예약 시간을 삭제할 때, 사용 중인 시간이면 실패한다.")
    void deleteReservationTimeWithUsingTime() throws Exception {
        Long id = 1L;
        doThrow(new BadRequestException(String.format("해당 예약 시간을 사용하는 예약이 존재합니다. (예약 시간 id: %d)", id)))
                .when(reservationTimeService).deleteReservationTimeById(anyLong());

        mockMvc.perform(
                delete("/admin/times/{id}", id)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                document("admin/times/delete/using-time",
                        responseFields(
                                fieldWithPath("message").description("해당 예약 시간을 사용하는 예약이 존재합니다. (예약 시간 id: 1)"))
                )
        );
    }
}

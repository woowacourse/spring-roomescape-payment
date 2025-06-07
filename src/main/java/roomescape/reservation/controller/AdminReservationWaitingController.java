package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.AdminReservationWaitingService;
import roomescape.reservation.application.dto.response.AdminReservationWaitingServiceResponse;
import roomescape.reservation.controller.dto.response.AdminReservationWaitingResponse;

@Tag(name = "waiting", description = "대기 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations/waiting")
public class AdminReservationWaitingController {

    private final AdminReservationWaitingService adminReservationWaitingService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<AdminReservationWaitingResponse> getAll() {
        List<AdminReservationWaitingServiceResponse> responses = adminReservationWaitingService.getAll();
        return responses.stream()
                .map(AdminReservationWaitingResponse::from)
                .toList();
    }
}

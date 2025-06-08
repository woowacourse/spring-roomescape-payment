package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.AdminReservationCreateRequest;
import roomescape.dto.request.ReservationCondition;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.ReservationService;
import roomescape.service.WaitingService;

@Tag(name = "관리자용 API", description = "관리자용 API 입니다.")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public AdminController(final ReservationService reservationService, final WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @Operation(summary = "관리자용 예약 생성", description = "관리자가 예약을 생성합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody AdminReservationCreateRequest request) {
        ReservationResponse reservationResponse = reservationService.createReservation(
                request.memberId(), request.timeId(), request.themeId(), request.date()
        );
        return ResponseEntity
                .created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "관리자용 예약 조회", description = "관리자가 예약을 조회합니다.")
    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations(@ModelAttribute ReservationCondition cond) {
        return reservationService.findReservations(cond);
    }

    @Operation(summary = "관리자용 대기 조회", description = "관리자가 대기를 조회합니다.")
    @GetMapping("/waitings")
    public List<WaitingResponse> getWaitings() {
        return waitingService.findAll();
    }
}

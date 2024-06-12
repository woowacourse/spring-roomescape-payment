package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.ReservationSearchCondition;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

@RestController
@RequestMapping(value = "/admin")
@Tag(name = "관리자 API", description = "관리자 페이지 API 입니다.")
public class AdminController {

    private final ReservationService reservationService;

    public AdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    @Operation(summary = "예약 등록 API", description = "예약을 등록합니다.")
    public ResponseEntity<ReservationResponse> saveReservation(@RequestBody ReservationRequest reservationRequest) {
        ReservationResponse saved = reservationService.save(reservationRequest);

        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/reservations")
    @Operation(summary = "예약 목록 조회 API", description = "모든 예약 목록을 조회합니다.")
    public List<ReservationResponse> search(@ModelAttribute ReservationSearchCondition condition) {
        return reservationService.findByMemberAndThemeBetweenDates(condition);
    }

    @GetMapping("/reservations/waiting")
    @Operation(summary = "대기 중인 예약 목록 조회 API", description = "대기 중인 예약 목록을 조회합니다.")
    public List<ReservationResponse> findAllPendingReservations() {
        return reservationService.findByStatusPending();
    }
}

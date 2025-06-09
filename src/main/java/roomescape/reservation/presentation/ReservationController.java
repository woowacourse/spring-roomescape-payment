package roomescape.reservation.presentation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.dto.response.ReservationResponse;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        log.info("전체 예약 목록 조회 요청 수신");
        List<ReservationResponse> response = reservationService.getReservations();

        log.info("전체 예약 목록 조회 완료: 총 {}건", response.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id) {
        log.info("예약 삭제 요청 수신: reservationId={}", id);
        reservationService.deleteReservationById(id);

        log.info("예약 삭제 완료: reservationId={}", id);
        return ResponseEntity.noContent().build();
    }
}

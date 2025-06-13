package roomescape.reservation.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.domain.dto.ReservationInfo;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.domain.dto.ReservationWithPaymentDto;
import roomescape.reservation.service.ReservationService;
import roomescape.user.domain.User;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        log.debug("ReservationController initialized");
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> resDtos = service.findAll();
        return ResponseEntity.ok(resDtos);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> addWithPayment(@RequestBody ReservationWithPaymentDto requestDto,
                                                                 User user) {
        log.info("POST /reservations 요청 수신 - 사용자: {}, 요청 데이터: {}", user.getId(), requestDto);
        ReservationResponseDto resDto = service.addWithPayment(requestDto, user);
        log.info("POST /reservations 요청 완료 - 예약 ID: {}", resDto.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelAndApproveWaiting(@PathVariable("reservationId") Long id) {
        ReservationInfo reservationInfo = service.cancelReservationAndReturnInfo(id);
        service.approveWaiting(reservationInfo);
        return ResponseEntity.noContent().build();
    }
}

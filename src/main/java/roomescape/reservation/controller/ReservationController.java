package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.facade.ReservationPaymentFacade;
import roomescape.facade.dto.ReservationWithPaymentResponseDto;
import roomescape.reservation.domain.dto.ReservationInfo;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.domain.dto.ReservationWithPaymentDto;
import roomescape.reservation.service.ReservationService;
import roomescape.user.domain.User;

import java.util.List;

@Tag(name = "예약 API", description = "예약 관련 API입니다.")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;
    private final ReservationPaymentFacade facade;

    public ReservationController(ReservationService service, ReservationPaymentFacade facade) {
        this.service = service;
        this.facade = facade;
    }

    @Operation(summary = "모든 예약 조회", description = "모든 예약을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> resDtos = service.findAll();
        return ResponseEntity.ok(resDtos);
    }

    @Operation(summary = "결제 및 예약", description = "결제와 예약을 진행합니다.")
    @PostMapping
    public ResponseEntity<ReservationWithPaymentResponseDto> addWithPayment(@RequestBody ReservationWithPaymentDto requestDto,
                                                                 User user) {
        ReservationWithPaymentResponseDto responseDto = facade.addWithPayment(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "예약 취소 및 예약 대기 승인", description = "특정 예약을 취소 상태로 변경하고, 1순위의 예약 대기를 결제 대기 상태로 변경합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelAndApproveWaiting(@PathVariable("reservationId") Long id) {
        ReservationInfo reservationInfo = service.cancelReservationAndReturnInfo(id);
        service.approveWaiting(reservationInfo);
        return ResponseEntity.noContent().build();
    }
}

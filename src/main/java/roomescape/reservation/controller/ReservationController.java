package roomescape.reservation.controller;

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

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;
    private final ReservationPaymentFacade facade;

    public ReservationController(ReservationService service, ReservationPaymentFacade facade) {
        this.service = service;
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> resDtos = service.findAll();
        return ResponseEntity.ok(resDtos);
    }

    @PostMapping
    public ResponseEntity<ReservationWithPaymentResponseDto> addWithPayment(@RequestBody ReservationWithPaymentDto requestDto,
                                                                 User user) {
        ReservationWithPaymentResponseDto responseDto = facade.addWithPayment(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelAndApproveWaiting(@PathVariable("reservationId") Long id) {
        ReservationInfo reservationInfo = service.cancelReservationAndReturnInfo(id);
        service.approveWaiting(reservationInfo);
        return ResponseEntity.noContent().build();
    }
}

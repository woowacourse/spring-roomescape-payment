package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.dto.MemberReservationCreateRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.facade.ReservationFacadeService;

import java.util.List;

@Tag(name = "사용자 예약 컨트롤러")
@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationFacadeService reservationFacadeService;

    public MemberReservationController(ReservationFacadeService reservationFacadeService) {
        this.reservationFacadeService = reservationFacadeService;
    }

    @Operation(summary = "사용자 예약 생성")
    @PostMapping
    public MemberReservationResponse createReservation(@Valid @RequestBody MemberReservationCreateRequest request,
                                                       @Parameter(hidden = true) LoginMember member
    ) {
        return reservationFacadeService.createReservation(request, member);
    }

    @Operation(summary = "사용자 예약 결제")
    @PostMapping("/{id}/payments/confirm")
    public ResponseEntity<Void> confirmPendingReservation(@PathVariable Long id,
                                                          @RequestBody PaymentRequest paymentRequest
    ) {
        reservationFacadeService.confirmPendingReservation(id, paymentRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 예약 조회")
    @GetMapping("/my")
    public List<MyReservationResponse> readMyReservations(@Parameter(hidden = true) LoginMember loginMember) {
        return reservationFacadeService.readMyReservations(loginMember);
    }

    @Operation(summary = "사용자 예약 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberReservation(@PathVariable Long id,
                                                        @Parameter(hidden = true) LoginMember loginMember
    ) {
        reservationFacadeService.deleteReservation(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}

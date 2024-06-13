package roomescape.controller.reservation;

import java.net.URI;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.LoginMember;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.service.member.MemberService;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationListResponse;
import roomescape.service.reservation.dto.ReservationMineListResponse;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final MemberService memberService;

    public ReservationController(ReservationService reservationService, MemberService memberService) {
        this.reservationService = reservationService;
        this.memberService = memberService;
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/reservations")
    public ResponseEntity<ReservationListResponse> findAllReservation(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        ReservationListResponse response = reservationService.findAllReservation(memberId, themeId, dateFrom, dateTo);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @GetMapping("/reservations/mine")
    public ResponseEntity<ReservationMineListResponse> findMyReservation(@LoginMember Member member) {
        ReservationMineListResponse response = reservationService.findMyReservation(member);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(@RequestBody ReservationRequest request,
                                                               @LoginMember Member member) {
        ReservationSaveInput reservationSaveInput = request.toReservationSaveInput();
        PaymentConfirmInput paymentConfirmInput = request.toPaymentConfirmInput();

        ReservationResponse response = reservationService.saveReservationWithPayment(
                reservationSaveInput, paymentConfirmInput, member);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId())).body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveAdminReservation(@RequestBody AdminReservationRequest request) {
        ReservationSaveInput reservationSaveInput = request.toReservationSaveInput();
        Member member = memberService.findById(request.getMemberId());

        ReservationResponse response = reservationService.saveReservationWithoutPayment(reservationSaveInput, member);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId())).body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId,
                                                  @RequestParam Long memberId) {
        reservationService.deleteReservation(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}

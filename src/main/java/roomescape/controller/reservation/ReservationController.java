package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import roomescape.service.reservation.dto.ReservationPaymentRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

import java.net.URI;
import java.time.LocalDate;

@Tag(name = "Reservation")
@RestController
@Validated
public class ReservationController {
    private final ReservationService reservationService;
    private final MemberService memberService;

    public ReservationController(ReservationService reservationService, MemberService memberService) {
        this.reservationService = reservationService;
        this.memberService = memberService;
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/reservations")
    @Operation(summary = "[관리자] 예약 정보 검색", description = "회원ID, 테마ID, 시작일, 종료일로 예약 정보를 검색한다.")
    public ResponseEntity<ReservationListResponse> searchReservation(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        ReservationListResponse response = reservationService.searchReservation(memberId, themeId, dateFrom, dateTo);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @GetMapping("/reservations-mine")
    @Operation(summary = "[회원] 내 예약 정보 조회", description = "내 예약 정보를 조회한다.")
    public ResponseEntity<ReservationMineListResponse> findMyReservation(@Parameter(hidden = true) @LoginMember Member member) {
        ReservationMineListResponse response = reservationService.findMyReservation(member);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @PostMapping("/reservations")
    @Operation(summary = "[회원] 예약 추가", description = "결제 및 예약을 수행한다.")
    public ResponseEntity<ReservationResponse> saveReservation(@RequestBody @Valid ReservationRequest request,
                                                               @Parameter(hidden = true) @LoginMember Member member) {
        ReservationSaveInput reservationSaveInput = request.toReservationSaveInput();
        PaymentConfirmInput paymentConfirmInput = request.toPaymentConfirmInput();

        ReservationResponse response = reservationService.saveReservationWithPayment(
                reservationSaveInput, paymentConfirmInput, member);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId())).body(response);
    }

    @RoleAllowed
    @PostMapping("/reservations/{reservationId}/payment")
    @Operation(summary = "[회원] 예약 결제", description = "결제 대기 상태의 예약을 결제한다.")
    public ResponseEntity<ReservationResponse> payReservation(@PathVariable Long reservationId,
                                                              @RequestBody @Valid ReservationPaymentRequest request,
                                                              @Parameter(hidden = true) @LoginMember Member member) {
        PaymentConfirmInput paymentConfirmInput = request.toPaymentConfirmInput();

        ReservationResponse response = reservationService.payReservation(
                reservationId, paymentConfirmInput, member);
        return ResponseEntity.ok(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @PostMapping("/admin/reservations")
    @Operation(summary = "[관리자] 예약 추가", description = "결제 없이 예약을 수행한다.")
    public ResponseEntity<ReservationResponse> saveAdminReservation(@RequestBody @Valid AdminReservationRequest request) {
        Member member = memberService.findById(request.getMemberId());

        ReservationSaveInput reservationSaveInput = request.toReservationSaveInput();
        ReservationResponse response = reservationService.saveReservationWithoutPayment(reservationSaveInput, member);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId())).body(response);
    }

    @RoleAllowed
    @DeleteMapping("/reservations/{reservationId}/cancel")
    @Operation(summary = "[회원] 예약 취소", description = "예약을 취소하고 결제 금액을 환불한다.")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable @NotNull(message = "reservationId 값이 null일 수 없습니다.") Long reservationId,
            @Parameter(hidden = true) @LoginMember Member member) {
        reservationService.cancelReservation(reservationId, member);
        return ResponseEntity.noContent().build();
    }

    @RoleAllowed
    @DeleteMapping("/reservations/{reservationId}")
    @Operation(summary = "[회원] 예약 삭제", description = "취소 또는 결제 대기 상태의 예약 정보를 삭제한다.")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable @NotNull(message = "reservationId 값이 null일 수 없습니다.") Long reservationId,
            @Parameter(hidden = true) @LoginMember Member member) {
        reservationService.deleteReservation(reservationId, member);
        return ResponseEntity.noContent().build();
    }
}

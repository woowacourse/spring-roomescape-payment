package roomescape.reservation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.auth.dto.Accessor;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@Tag(name = "Reservation API", description = "예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "전체 예약 조회 API", description = "전체 예약을 조회합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationList() {
        return ResponseEntity.ok(reservationService.findAllReservation());
    }

    @Operation(summary = "단일 예약 조회 API", description = "예약 id로 예약을 조회합니다.")
    @GetMapping("/reservations/{id}")
    public ResponseEntity<MemberReservationResponse> getReservationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @Operation(summary = "조건부 예약 조회 API", description = "사용자별, 테마별, 기간별(시작, 종료) 예약을 조회합니다.")
    @GetMapping(path = "/reservations", params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "themeId") Long themeId,
            @RequestParam(name = "dateFrom") LocalDate dateFrom,
            @RequestParam(name = "dateTo") LocalDate dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo)
        );
    }

    @Operation(summary = "로그인한 회원의 예약 조회 API", description = "로그인한 회원 자신의 예약을 조회합니다.")
    @GetMapping("/reservations/my")
    public ResponseEntity<List<MemberReservationResponse>> findMemberReservationStatus(
            @Authenticated Accessor accessor) {
        return ResponseEntity.ok(reservationService.findAllByMemberId(accessor.id()));
    }

    @Operation(summary = "로그인한 회원의 예약 생성 API", description = "로그인한 회원이 예약을 추가합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveMemberReservation(
            @Authenticated Accessor accessor,
            @Valid @RequestBody MemberReservationWithPaymentAddRequest memberReservationWithPaymentAddRequest) {
        ReservationResponse saveResponse = reservationService.saveMemberReservation(accessor.id(),
                memberReservationWithPaymentAddRequest);
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @Operation(summary = "로그인한 회원의 예약 대기 생성 API", description = "로그인한 회원이 예약 대기를 추가합니다.")
    @PostMapping("/reservations/waiting")
    public ResponseEntity<ReservationResponse> saveMemberWaitingReseravtion(
            @Authenticated Accessor accessor,
            @Valid @RequestBody MemberReservationAddRequest memberReservationAddRequest) {
        ReservationResponse saveResponse = reservationService.saveMemberWaitingReservation(accessor.id(),
                memberReservationAddRequest);
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @Operation(summary = "로그인한 회원의 결제 API", description = "결제 대기 중인 예약에 대해 로그인한 회원이 결제를 진행합니다.")
    @PostMapping("/reservations/payment")
    public ResponseEntity<MemberReservationResponse> paymentForPending(
            @Authenticated Accessor accessor,
            @Valid @RequestBody ReservationPaymentRequest request
    ) {
        MemberReservationResponse response = reservationService.payForPendingReservation(accessor.id(), request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "예약 삭제 API", description = "예약을 취소합니다.")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable("id") Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.noContent().build();
    }
}

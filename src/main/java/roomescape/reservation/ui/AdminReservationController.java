package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.payment.application.dto.PrePaymentValidRequest;
import roomescape.reservation.application.ReservationCommandService;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.WaitingResponse;

@Tag(name = "관리자 예약", description = "관리자 예약 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Operation(summary = "모든 예약 조회 API", description = "등록된 모든 예약 정보를 조회합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllReserved() {
        return ResponseEntity.ok(reservationQueryService.findReservedReservations());
    }

    @Operation(summary = "모든 대기 예약 조회 API", description = "등록된 모든 대기 예약 정보를 조회합니다.")
    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> findAllWaiting() {
        return ResponseEntity.ok(reservationQueryService.findWaitingReservations());
    }

    @Operation(summary = "예약 필터링 API", description = "테마 ID, 회원 ID, 날짜 범위로 예약을 필터링합니다.")
    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> getFilteredReservations(
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "from") final LocalDate start,
            @RequestParam(required = false, name = "to") final LocalDate end
    ) {
        final List<ReservationResponse> reservationResponses = reservationQueryService.findReservationByThemeIdAndMemberIdInDuration(
                themeId, memberId, start, end);
        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "관리자 예약 추가 API", description = "관리자가 예약을 추가합니다. 요청 본문에 예약 정보를 포함해야 합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> add(
            @Valid @RequestBody final AdminReservationRequest request
    ) {
        final ReservationResponse response = reservationCommandService.addAdminReservation(request);
        return ResponseEntity.created(URI.create("/admin/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "관리자 예약 삭제 API", description = "관리자가 예약을 삭제합니다. 예약 ID를 경로 변수로 전달해야 합니다.")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long id) {
        reservationCommandService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 대기 예약 삭제 API", description = "관리자가 대기 예약을 삭제합니다. 대기 예약 ID를 경로 변수로 전달해야 합니다.")
    @PutMapping("/waitings/reject/{id}")
    public ResponseEntity<Void> rejectWaiting(@PathVariable("id") final Long id) {
        reservationCommandService.rejectWaitingById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자 대기 예약 수락 API", description = "관리자가 대기 예약을 수락합니다. 대기 예약 ID와 선결제 정보를 요청 본문에 포함해야 합니다.")
    @PutMapping("/waitings/accept/{id}")
    public ResponseEntity<Void> acceptReservation(
            @PathVariable("id") final Long id,
            @RequestBody final PrePaymentValidRequest request
    ) {
        reservationCommandService.acceptReservation(id, request);
        return ResponseEntity.ok().build();
    }
}

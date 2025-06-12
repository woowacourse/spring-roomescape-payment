package roomescape.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.config.AuthenticationPrincipal;
import roomescape.reservation.dto.MineReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "예약 관련 API", description = "관리자가 아닌 예약 생성, 조회 및 삭제를 위한 API입니다.")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid ReservationPaymentRequest request,
            @AuthenticationPrincipal final LoginMember member
    ) {
        final ReservationResponse response = reservationService.create(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "대기 예약 생성", description = "새로운 대기 예약을 생성합니다.")
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaiting(
            @RequestBody @Valid final ReservationRequest request,
            @AuthenticationPrincipal final LoginMember member
    ) {
        final ReservationResponse response = reservationService.createWaiting(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 예약 조회", description = "로그인한 사용자의 예약을 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<List<MineReservationResponse>> readMine(
            @AuthenticationPrincipal final LoginMember member
    ) {
        final List<MineReservationResponse> response = reservationService.readAllMine(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 예약 조회", description = "등록된 모든 예약의 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAll() {
        final List<ReservationResponse> response = reservationService.readAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "대기 예약 조회", description = "대기 중인 모든 예약의 정보를 조회합니다.")
    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> readAllWaiting() {
        final List<ReservationResponse> response = reservationService.readAllWaiting();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

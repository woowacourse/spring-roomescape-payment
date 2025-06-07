package roomescape.controller;

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
import roomescape.domain.auth.dto.LoginMember;
import roomescape.domain.reservation.ReservationService;
import roomescape.domain.reservation.dto.MineReservationResponse;
import roomescape.domain.reservation.dto.ReservationPaymentRequest;
import roomescape.domain.reservation.dto.ReservationRequest;
import roomescape.domain.reservation.dto.ReservationResponse;

@Tag(
        name = "예약 컨트롤러",
        description = "예약 API 목록, 로그인 필수(TOKNE 이름의 쿠키에 JWT 필수)"
)
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            description = "예약을 생성한다. 쿠키에 있는 토큰 기반으로 생성하고, 결제가 필수이다."
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid ReservationPaymentRequest request,
            @AuthenticationPrincipal final LoginMember member
    ) {
        final ReservationResponse response = reservationService.create(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            description = "예약 대기를 생성한다. PENDING 상태 예약 필수"
    )
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaiting(
            @RequestBody @Valid final ReservationRequest request,
            @AuthenticationPrincipal final LoginMember member
    ) {
        final ReservationResponse response = reservationService.createWaiting(request, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            description = "쿠키에 있는 토큰 기반으로 멤버의 예약을 모두 조회한다."
    )
    @GetMapping("/mine")
    public ResponseEntity<List<MineReservationResponse>> readMine(
            @AuthenticationPrincipal final LoginMember member
    ) {
        final List<MineReservationResponse> response = reservationService.readAllMine(member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            description = "예약을 모두 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAll() {
        final List<ReservationResponse> response = reservationService.readAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            description = "WAITING 상태의 예약을 모두 조회한다."
    )
    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> readAllWaiting() {
        final List<ReservationResponse> response = reservationService.readAllWaiting();
        return ResponseEntity.ok(response);
    }

    @Operation(
            description = "특정 id의 예약을 삭제한다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

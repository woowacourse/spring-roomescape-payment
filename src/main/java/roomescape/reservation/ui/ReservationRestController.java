package roomescape.reservation.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;
import static roomescape.auth.domain.AuthRole.MEMBER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.MemberAuthInfo;
import roomescape.auth.domain.RequiresRole;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.ui.dto.request.AvailableReservationTimeRequest;
import roomescape.reservation.ui.dto.request.CreateBookedReservationWithPaymentRequest;
import roomescape.reservation.ui.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.ui.dto.response.ReservationResponse;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "예약", description = "예약 관련 API")
public class ReservationRestController {

    private final ReservationService reservationService;

    @PostMapping
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    @Operation(summary = "회원 권한의 결제 후 예약 추가")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid final CreateBookedReservationWithPaymentRequest request,
            final MemberAuthInfo memberAuthInfo
    ) {
        final ReservationResponse response =
                reservationService.create(request, memberAuthInfo.id());

        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @DeleteMapping("/{id}")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    @Operation(summary = "회원 권한의 예약 삭제")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable final Long id,
            final MemberAuthInfo memberAuthInfo
    ) {
        reservationService.deleteIfOwner(id, memberAuthInfo.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    @Operation(summary = "회원 권한의 내 예약 목록 조회")
    public ResponseEntity<List<ReservationResponse.ForMember>> findAllMyReservations(
            final MemberAuthInfo memberAuthInfo
    ) {
        return ResponseEntity.ok()
                .body(reservationService.findReservationsByMemberId(memberAuthInfo.id()));
    }

    @GetMapping("/available-times")
    @Operation(summary = "특정 날짜, 테마에 대해 예약 가능한 시간 목록 조회")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAllAvailableReservationTimes(
            @ModelAttribute @Valid final AvailableReservationTimeRequest request
    ) {
        final List<AvailableReservationTimeResponse> availableReservationTimes =
                reservationService.findAvailableReservationTimes(request);

        return ResponseEntity.ok(availableReservationTimes);
    }
}

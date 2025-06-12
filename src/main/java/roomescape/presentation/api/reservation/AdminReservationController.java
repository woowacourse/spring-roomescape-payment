package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.DeleteReservationService;
import roomescape.application.reservation.command.ProcessReservationByAdminUseCase;
import roomescape.application.reservation.query.ReservationQueryService;
import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.application.reservation.query.dto.ReservationSearchCondition;
import roomescape.presentation.api.reservation.request.CreateAdminReservationRequest;
import roomescape.presentation.api.reservation.response.ReservationResponse;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "관리자 예약 API")
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private static final String RESERVATIONS_URL = "/reservations/%d";

    private final ProcessReservationByAdminUseCase processReservationByAdminUseCase;
    private final DeleteReservationService deleteReservationService;
    private final ReservationQueryService reservationQueryService;

    @Operation(
            summary = "관리자 예약 생성",
            description = "관리자가 예약을 생성합니다. 요청 본문에 필요한 예약 정보를 포함해야 합니다."
    )
    @PostMapping
    public ResponseEntity<Void> createReservation(
            @AuthPrincipal final AuthInfo authInfo,
            @Valid @RequestBody final CreateAdminReservationRequest createAdminReservationRequest) {
        final Long id = processReservationByAdminUseCase.execute(
                createAdminReservationRequest.toCreateCommand(),
                authInfo.memberId());
        return ResponseEntity.created(URI.create(RESERVATIONS_URL.formatted(id)))
                .build();
    }

    @Operation(
            summary = "관리자 예약 삭제",
            description = "관리자가 예약을 삭제합니다. 예약 ID를 경로 변수로 전달해야 합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long reservationId) {
        deleteReservationService.cancelById(reservationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "관리자 예약 조회",
            description = "관리자가 예약 목록을 조회합니다. 선택적으로 테마 ID, 회원 ID, 날짜 범위를 필터링할 수 있습니다."
    )
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll(
            @RequestParam(value = "themeId", required = false) final Long themeId,
            @RequestParam(value = "memberId", required = false) final Long memberId,
            @RequestParam(value = "from", required = false) final LocalDate from,
            @RequestParam(value = "to", required = false) final LocalDate to) {
        final List<ReservationResult> reservationResults = reservationQueryService.findReservationsBy(
                new ReservationSearchCondition(themeId, memberId, from, to)
        );
        final List<ReservationResponse> reservationResponses = reservationResults.stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservationResponses);
    }
}

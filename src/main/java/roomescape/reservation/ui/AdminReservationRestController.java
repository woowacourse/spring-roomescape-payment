package roomescape.reservation.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.reservation.application.AdminReservationService;
import roomescape.reservation.ui.dto.request.CreateBookedReservationRequest;
import roomescape.reservation.ui.dto.request.FilteredReservationsRequest;
import roomescape.reservation.ui.dto.response.ReservationResponse;
import roomescape.reservation.ui.dto.response.ReservationStatusResponse;

@Slf4j
@Tag(name = "관리자 예약", description = "관리자 예약 api")
@RestController
@RequestMapping("/admin/reservations")
@RequiresRole(authRoles = {ADMIN})
@RequiredArgsConstructor
public class AdminReservationRestController {

    private final AdminReservationService adminReservationService;

    @Operation(summary = "관리자 예약 생성", description = "관리자 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid final CreateBookedReservationRequest request
    ) {
        log.info("관리자 예약 생성 요청 수신");

        final ReservationResponse response = adminReservationService.create(request);

        log.info("관리자 예약 생성 완료 - 예약 ID: {}", response.id());

        return ResponseEntity.created(URI.create("/admin/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "관리자 예약 삭제", description = "관리자 예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsAdmin(
            @PathVariable final Long id
    ) {
        log.info("관리자 예약 삭제 요청 - 예약 ID: {}", id);

        adminReservationService.deleteAsAdmin(id);

        log.info("관리자 예약 삭제 완료 - 예약 ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 모든 예약 조회", description = "관리자 모든 예약을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAllReservations() {
        log.info("관리자 예약 전체 조회 요청 수신");

        final List<ReservationResponse> reservationResponses = adminReservationService.findAll();

        log.info("관리자 예약 전체 조회 완료 - 개수: {}", reservationResponses.size());

        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "관리자 필터링 예약 조회", description = "관리자 필터링 예약을 조회합니다.")
    @GetMapping("/filtered")
    public ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteredReservationsRequest request
    ) {
        log.info("관리자 예약 필터 조회 요청 수신 - 필터 조건: {}", request);

        final List<ReservationResponse> reservationResponses = adminReservationService.findAllByFilter(request);

        log.info("관리자 예약 필터 조회 완료 - 개수: {}", reservationResponses.size());

        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "관리자 예약 상태 확인", description = "관리자 예약 상태를 확인합니다.")
    @GetMapping("/statuses")
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<List<ReservationStatusResponse>> findAllReservationStatuses() {
        log.info("관리자 예약 상태 목록 조회 요청 수신");

        final List<ReservationStatusResponse> statuses = adminReservationService.findAllReservationStatuses();

        log.info("관리자 예약 상태 목록 조회 완료 - 개수: {}", statuses.size());

        return ResponseEntity.ok().body(statuses);
    }
}

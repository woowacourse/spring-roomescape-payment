package roomescape.reservation.ui;

import java.net.URI;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.aop.RequiredRoles;
import roomescape.common.uri.UriFactory;
import roomescape.reservation.application.ReservationFacade;
import roomescape.reservation.ui.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.ui.dto.CreateReservationWithUserIdWebRequest;
import roomescape.reservation.ui.dto.ReservationResponse;
import roomescape.reservation.ui.dto.ReservationSearchWebRequest;
import roomescape.reservation.ui.dto.ReservationWithPaymentInfoResponse;
import roomescape.user.domain.UserRole;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequiredRoles(UserRole.ADMIN)
@RequestMapping(AdminReservationController.BASE_PATH)
public class AdminReservationController {

    public static final String BASE_PATH = "/admin/reservations";

    private final ReservationFacade reservationFacade;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll() {
        log.info("[ADMIN-RESERVATION] 전체 예약 목록 조회 요청");
        final List<ReservationResponse> reservations = reservationFacade.getAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/times")
    public ResponseEntity<List<AvailableReservationTimeWebResponse>> getAvailable(
            @RequestParam final LocalDate date,
            @RequestParam final Long themeId) {
        log.info("[ADMIN-RESERVATION] 예약 가능 시간 조회 요청: date={}, themeId={}", date, themeId);
        final List<AvailableReservationTimeWebResponse> reservations = reservationFacade.getAvailable(date, themeId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> searchReservations(
            @ModelAttribute final ReservationSearchWebRequest request) {
        log.info("[ADMIN-RESERVATION] 예약 검색 요청: {}", request);
        return ResponseEntity.ok(
                reservationFacade.getByParams(request));
    }

    @PostMapping
    public ResponseEntity<ReservationWithPaymentInfoResponse> create(
            @RequestBody final CreateReservationWithUserIdWebRequest request
    ) {
        log.info("[ADMIN-RESERVATION] 예약 생성 요청: {}", request);
        ReservationWithPaymentInfoResponse reservationResponse = reservationFacade.create(request);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationResponse.reservationId()));
        return ResponseEntity.created(location)
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        log.info("[ADMIN-RESERVATION] 예약 삭제 요청: id={}", id);
        reservationFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}

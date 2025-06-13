package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
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
import roomescape.auth.aop.RequiredRoles;
import roomescape.common.uri.UriFactory;
import roomescape.reservation.application.ReservationFacade;
import roomescape.reservation.ui.dto.CreateReservationWithUserIdWebRequest;
import roomescape.reservation.ui.dto.ReservationResponse;
import roomescape.reservation.ui.dto.ReservationSearchWebRequest;
import roomescape.user.domain.UserRole;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequiredRoles(UserRole.ADMIN)
@RequestMapping(AdminReservationController.BASE_PATH)
public class AdminReservationController {

    public static final String BASE_PATH = "/admin/reservations";

    private final ReservationFacade reservationFacade;

    @GetMapping
    @Operation(summary = "전체 예약 조회")
    public ResponseEntity<List<ReservationResponse>> getAll() {
        final List<ReservationResponse> reservations = reservationFacade.getAll();
        return ResponseEntity.ok(reservations);
    }

//    @GetMapping("/times")
//    @Operation(summary = "이용 가능한 예약 시간 조회")
//    public ResponseEntity<List<AvailableReservationTimeWebResponse>> getAvailable(
//            @RequestParam final LocalDate date,
//            @RequestParam final Long themeId) {
//        final List<AvailableReservationTimeWebResponse> reservations = reservationFacade.getAvailable(date, themeId);
//        return ResponseEntity.ok(reservations);
//    }

    @GetMapping("/search")
    @Operation(summary = "예약 검색")
    public ResponseEntity<List<ReservationResponse>> searchReservations(
            @ModelAttribute final ReservationSearchWebRequest request) {
        return ResponseEntity.ok(reservationFacade.getByParams(request));
    }

    @PostMapping
    @Operation(summary = "관리자의 예약 생성")
    public ResponseEntity<ReservationResponse> create(
            @RequestBody final CreateReservationWithUserIdWebRequest request) {
        final ReservationResponse reservationResponse = reservationFacade.create(request);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationResponse.reservationId()));
        return ResponseEntity.created(location)
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "관리자의 예약 삭제")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        reservationFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}

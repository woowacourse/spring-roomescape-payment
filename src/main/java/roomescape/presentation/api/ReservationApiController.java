package roomescape.presentation.api;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.LoginInfo;
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ReservationService;
import roomescape.presentation.dto.request.AdminReservationRequest;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.ReservationResponse;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    @AuthRequired
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest request,
                                                                 LoginInfo loginInfo) {
        ReservationResponse response = reservationService.addAndGet(
                request.date(), request.timeId(), request.themeId(), loginInfo.id(), request.paymentKey(),
                request.orderId(), request.amount()
        );
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/admin/reservations")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<ReservationResponse> adminCreateReservation(
            @RequestBody @Valid AdminReservationRequest request) {
        ReservationResponse response = reservationService.addAndGetWithoutPayment(request.date(), request.timeId(),
                request.themeId(), request.userId());
        return ResponseEntity.created(URI.create("/reservations" + response.id())).body(response);
    }

    @GetMapping("/reservations")
    @AuthRequired
    public List<ReservationResponse> getReservations(
            @RequestParam(required = false) String themeId,
            @RequestParam(required = false, name = "memberId") String userId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo
    ) {
        return reservationService.findAllReservations(themeId, userId, dateFrom, dateTo);
    }

    @GetMapping("/reservations/me")
    @AuthRequired
    public List<ReservationResponse> getMyReservations(LoginInfo loginInfo) {
        return reservationService.getMyReservations(loginInfo.id());
    }

    @DeleteMapping("/reservations/{id}")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        reservationService.cancelReservationAndPromoteWait(id);
        return ResponseEntity.noContent().build();
    }
}

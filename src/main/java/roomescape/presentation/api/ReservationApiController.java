package roomescape.presentation.api;

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
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.LoginInfo;
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ReservationService;
import roomescape.presentation.dto.request.AdminReservationRequest;
import roomescape.presentation.dto.request.ReservationCondition;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ReservationWithPaymentResponse;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @PostMapping("/admin/reservations")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<ReservationResponse> adminCreateReservation(
            @RequestBody @Valid AdminReservationRequest request) {
        ReservationResponse response = reservationService.addAndGetWithoutPayment(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping("/reservations")
    @AuthRequired
    public List<ReservationResponse> getReservations(@ModelAttribute ReservationCondition condition) {
        return reservationService.findAllReservations(condition);
    }

    @GetMapping("/reservations/me")
    @AuthRequired
    public List<ReservationWithPaymentResponse> getMyReservations(LoginInfo loginInfo) {
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

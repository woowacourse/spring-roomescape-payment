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
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ReservationTimeService;
import roomescape.presentation.dto.request.ReservationTimeRequest;
import roomescape.presentation.dto.response.ReservationTimeResponse;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;

@RestController
@RequiredArgsConstructor
public class ReservationTimeApiController {

    private final ReservationTimeService reservationTimeService;

    @PostMapping("/times")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.addAndGet(request);
        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @GetMapping("/times")
    @AuthRequired
    public List<ReservationTimeResponse> getAllReservationTime() {
        return reservationTimeService.getAll();
    }

    @GetMapping("/times/possible")
    @AuthRequired
    public List<ReservationTimeResponseWithBooked> getAvailableReservationTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("themeId") String themeId
    ) {
        return reservationTimeService.getAllByDateAndThemeId(date, themeId);
    }

    @DeleteMapping("/times/{id}")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<Void> deleteReservationTime(@PathVariable String id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

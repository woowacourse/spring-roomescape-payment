package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
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
import roomescape.auth.session.Session;
import roomescape.auth.session.annotation.UserSession;
import roomescape.common.uri.UriFactory;
import roomescape.reservation.application.ReservationFacade;
import roomescape.reservation.application.dto.MyReservationsResponse;
import roomescape.reservation.ui.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.ui.dto.CreateReservationWebRequest;
import roomescape.reservation.ui.dto.ReservationResponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(ReservationController.BASE_PATH)
public class ReservationController {

    public static final String BASE_PATH = "/reservations";

    private final ReservationFacade reservationFacade;

    @GetMapping("/mine")
    @Operation(summary = "나의 예악 조회")
    public ResponseEntity<List<MyReservationsResponse>> getMine(@UserSession final Session session) {
        final List<MyReservationsResponse> reservations = reservationFacade.getAllByUserId(session.userId());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/times")
    @Operation(summary = "이용 가능한 예약 시간 조회")
    public ResponseEntity<List<AvailableReservationTimeWebResponse>> getAvailable(
            @RequestParam final LocalDate date,
            @RequestParam final Long themeId) {
        List<AvailableReservationTimeWebResponse> reservations = reservationFacade.getAvailable(date, themeId);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    @Operation(summary = "예약 생성")
    public ResponseEntity<ReservationResponse> create(
            @RequestBody final CreateReservationWebRequest request,
            @UserSession final Session session) {
        ReservationResponse reservationResponse = reservationFacade.createWithPayment(
                request.toRequestWithUserId(session.userId()), request.toPaymentRequest());
        URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationResponse.reservationId()));
        return ResponseEntity.created(location)
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 삭제")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        reservationFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}

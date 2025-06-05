package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.domain.Role;
import roomescape.dto.business.ReservationTimeCreationContent;
import roomescape.dto.business.ReservationTimeWithBookState;
import roomescape.dto.request.ReservationTimeCreationRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeWithBookingResponse;
import roomescape.service.command.ReservationTimeService;
import roomescape.service.query.ReservationTimeQueryService;

@Tag(name = "ReservationTimeController", description = "예약 시간 관련 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService timeService;
    private final ReservationTimeQueryService timeQueryService;

    public ReservationTimeController(ReservationTimeService timeService, ReservationTimeQueryService timeQueryService) {
        this.timeService = timeService;
        this.timeQueryService = timeQueryService;
    }

    @Operation(summary = "Find All Reservation Times", description = "모든 예약 시간 조회")
    @GetMapping
    public List<ReservationTimeResponse> findAllReservationTimes() {
        return timeQueryService.findAllReservationTimes();
    }

    @Operation(summary = "Find All Reservation Times With Booking", description = "예약 가능 여부와 함께 모든 예약 시간 조회")
    @GetMapping(params = {"themeId", "date"})
    public List<ReservationTimeWithBookingResponse> findReservationTimesWithBooking(
            @RequestParam("themeId") Long themeId,
            @RequestParam("date") LocalDate date
    ) {
        List<ReservationTimeWithBookState> reservations =
                timeQueryService.findReservationTimesWithBooking(themeId, date);
        return reservations.stream()
                .map(ReservationTimeWithBookingResponse::new)
                .toList();
    }

    @Operation(summary = "Add Reservation Time", description = "예약 시간 추가")
    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<ReservationTimeResponse> addReservationTime(
            @Valid @RequestBody ReservationTimeCreationRequest request
    ) {
        ReservationTimeCreationContent creationContent = new ReservationTimeCreationContent(request);
        ReservationTimeResponse reservationTimeResponse = timeService.addReservationTime(creationContent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/times/" + reservationTimeResponse.id()))
                .body(reservationTimeResponse);
    }

    @Operation(summary = "Delete Reservation Time By Id", description = "ID를 통해 예약 시간 삭제")
    @DeleteMapping("/{reservationTimeId}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteReservationTimeById(
            @PathVariable("reservationTimeId") Long id
    ) {
        timeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }
}

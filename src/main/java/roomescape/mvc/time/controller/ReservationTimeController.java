package roomescape.mvc.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.time.dto.ReservationTimeCreationContent;
import roomescape.mvc.time.dto.ReservationTimeWithBookState;
import roomescape.mvc.time.request.ReservationTimeCreationRequest;
import roomescape.mvc.time.response.AddTimeResponse;
import roomescape.mvc.time.response.FindAllTimeResponse;
import roomescape.mvc.time.response.FindAllTimeWithBookingResponse;
import roomescape.mvc.time.service.ReservationTimeQueryService;
import roomescape.mvc.time.service.ReservationTimeService;
import roomescape.annotation.Authority;
import roomescape.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.annotation.docs.DocsDeletableDataNotFoundExceptionResponse;
import roomescape.annotation.docs.DocsDuplicatedDateCreationResponse;
import roomescape.annotation.docs.DocsSuccessResponse;

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
    public List<FindAllTimeResponse> findAllTime() {
        return timeQueryService.findAllReservationTimes();
    }

    @Operation(summary = "Find All Reservation Times With Booking", description = "예약 가능 여부와 함께 모든 예약 시간 조회")
    @GetMapping(params = {"themeId", "date"})
    public List<FindAllTimeWithBookingResponse> findAllTimeWithBooking(
            @RequestParam("themeId") Long themeId,
            @RequestParam("date") LocalDate date
    ) {
        List<ReservationTimeWithBookState> reservations =
                timeQueryService.findReservationTimesWithBooking(themeId, date);
        return reservations.stream()
                .map(FindAllTimeWithBookingResponse::new)
                .toList();
    }

    @Operation(summary = "Add Reservation Time", description = "예약 시간 추가")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDuplicatedDateCreationResponse
    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<AddTimeResponse> addTime(
            @Valid @RequestBody ReservationTimeCreationRequest request
    ) {
        ReservationTimeCreationContent creationContent = new ReservationTimeCreationContent(request);
        AddTimeResponse response = timeService.addReservationTime(creationContent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/times/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Delete Reservation Time By Id", description = "ID를 통해 예약 시간 삭제")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDeletableDataNotFoundExceptionResponse
    @ApiResponse(responseCode = "400", description = "예약 시간에 대한 예약과 대기가 이미 존재하는 경우",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{reservationTimeId}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteTimeById(
            @PathVariable("reservationTimeId") Long id
    ) {
        timeService.deleteReservationTimeById(id);
        return ResponseEntity.noContent().build();
    }
}

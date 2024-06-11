package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
import roomescape.service.schedule.ReservationTimeService;
import roomescape.service.schedule.dto.AvailableReservationTimeResponse;
import roomescape.service.schedule.dto.ReservationTimeCreateRequest;
import roomescape.service.schedule.dto.ReservationTimeReadRequest;
import roomescape.service.schedule.dto.ReservationTimeResponse;

@Tag(name = "Reservation Time", description = "예약 시간 컨트롤러입니다.")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    @ApiSuccessResponse.Created("예약 시간 등록")
    @ApiErrorResponse.BadRequest
    public ResponseEntity<ReservationTimeResponse> createReservationTime(
            @RequestBody @Valid ReservationTimeCreateRequest reservationTimeCreateRequest) {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeCreateRequest);
        return ResponseEntity.created(URI.create("/times/" + reservationTimeResponse.id()))
                .body(reservationTimeResponse);
    }

    @GetMapping
    @ApiSuccessResponse.Ok("전체 예약 시간 조회")
    public List<ReservationTimeResponse> findAllReservationTimes() {
        return reservationTimeService.findAll();
    }

    @GetMapping("/available")
    @ApiSuccessResponse.Ok("예약 가능한 시간 조회")
    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(
            @ModelAttribute("ReservationTimeReadRequest") ReservationTimeReadRequest reservationTimeReadRequest) {
        return reservationTimeService.findAvailableTimes(reservationTimeReadRequest);
    }

    @DeleteMapping("/{id}")
    @ApiSuccessResponse.NoContent("id를 통해 예약 시간 삭제")
    public ResponseEntity<Void> deleteReservationTimeById(@PathVariable("id") long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

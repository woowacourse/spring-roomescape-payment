package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.reservationtime.domain.dto.AvailableReservationTimeResponseDto;
import roomescape.reservationtime.domain.dto.ReservationTimeRequestDto;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 시간 API", description = "예약 시간 관련 API입니다.")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService service;

    public ReservationTimeController(ReservationTimeService service) {
        this.service = service;
    }

    @Operation(summary = "모든 예약 시간 조회", description = "모든 예약 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponseDto>> findAll() {
        List<ReservationTimeResponseDto> resDtos = service.findAll();
        return ResponseEntity.ok(resDtos);
    }

    @Operation(summary = "예약 가능한 시간 조회", description = "예약 가능한 시간을 조회합니다.")
    @GetMapping("/availability")
    public ResponseEntity<List<AvailableReservationTimeResponseDto>> findReservationTimesWithAvailableStatus(
            @RequestParam("themeId") Long themeId, @RequestParam("date") LocalDate date, User user) {
        List<AvailableReservationTimeResponseDto> availableReservationTimeResponseDtos = service.findReservationTimesWithAvailableStatus(
                themeId,
                date,
                user);
        return ResponseEntity.ok(availableReservationTimeResponseDtos);
    }

    @Operation(summary = "예약 시간 추가", description = "예약 시간을 추가합니다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponseDto> add(@RequestBody ReservationTimeRequestDto requestDto) {
        ReservationTimeResponseDto resDto = service.add(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
    }

    @Operation(summary = "예약 시간 삭제", description = "예약 시간을 삭제합니다.")
    @DeleteMapping("/{reservationTimeId}")
    public ResponseEntity<Void> deleteById(@PathVariable("reservationTimeId") Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

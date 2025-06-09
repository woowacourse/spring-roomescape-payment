package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationTimeCreateRequest;
import roomescape.reservation.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationTimeCreateResponse;
import roomescape.reservation.dto.response.ReservationTimeReadResponse;
import roomescape.reservation.service.ReservationTimeService;

@Tag(name = "Reservation Time", description = "예약 가능한 시간 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 생성", description = "ADMIN 권한으로 예약 가능한 시간을 생성합니다.")
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ReservationTimeCreateResponse> createTime(
            @RequestBody @Valid ReservationTimeCreateRequest request
    ) {
        ReservationTimeCreateResponse response = reservationTimeService.createTime(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 예약 시간 조회", description = "모든 예약 가능한 시간을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeReadResponse>> getAllTimes() {
        List<ReservationTimeReadResponse> responses = reservationTimeService.getAllTimes();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "사용 가능한 예약 시간 조회", description = "지정된 날짜와 테마에 대해 사용 가능한 예약 시간을 조회합니다.")
    @GetMapping("/available-times")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableTimes(
            @RequestParam("date") LocalDate date,
            @RequestParam("themeId") long themeId
    ) {
        List<AvailableReservationTimeResponse> responses = reservationTimeService.getAvailableTimes(date, themeId);
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "예약 시간 삭제", description = "ADMIN 권한으로 예약 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteTime(
            @PathVariable("id") long id
    ) {
        reservationTimeService.deleteTime(id);
        return ResponseEntity.ok().build();
    }
}

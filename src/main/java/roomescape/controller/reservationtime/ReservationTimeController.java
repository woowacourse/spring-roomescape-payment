package roomescape.controller.reservationtime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.MemberRole;
import roomescape.service.reservationtime.ReservationTimeService;
import roomescape.service.reservationtime.dto.ReservationTimeAvailableListResponse;
import roomescape.service.reservationtime.dto.ReservationTimeListResponse;
import roomescape.service.reservationtime.dto.ReservationTimeRequest;
import roomescape.service.reservationtime.dto.ReservationTimeResponse;

import java.net.URI;
import java.time.LocalDate;

@Tag(name = "Reservation Time")
@RestController
@Validated
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/times")
    @Operation(summary = "[관리자] 전체 예약 가능 시간 조회", description = "전체 예약 가능 시간 정보를 조회한다.")
    public ResponseEntity<ReservationTimeListResponse> findAllReservationTime() {
        ReservationTimeListResponse response = reservationTimeService.findAllReservationTime();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/times/available")
    @Operation(summary = "날짜/테마의 예약 가능 시간 조회", description = "특정 날짜/테마의 예약 가능한 시간 정보를 조회한다.")
    public ResponseEntity<ReservationTimeAvailableListResponse> findAllAvailableReservationTime(
            @RequestParam LocalDate date, @RequestParam Long themeId) {
        ReservationTimeAvailableListResponse response =
                reservationTimeService.findAllAvailableReservationTime(date, themeId);
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @PostMapping("/times")
    @Operation(summary = "[관리자] 예약 가능 시간 추가", description = "예약 가능 시간을 추가한다.")
    public ResponseEntity<ReservationTimeResponse> saveReservationTime(
            @RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.saveReservationTime(request);
        return ResponseEntity.created(URI.create("/times/" + response.getId())).body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @DeleteMapping("/times/{timeId}")
    @Operation(summary = "[관리자] 예약 가능 시간 삭제", description = "예약 가능 시간을 삭제한다.")
    public ResponseEntity<Void> deleteReservationTime(
            @PathVariable @NotNull(message = "timeId 값이 null일 수 없습니다.") Long timeId) {
        reservationTimeService.deleteReservationTime(timeId);
        return ResponseEntity.noContent().build();
    }
}

package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.reservation.AdminReservationCreateRequest;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "관리자용 예약 추가 API",
            description = "관리자 권한이 없으면 사용하지 못합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 추가 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> addReservation(
            @RequestBody AdminReservationCreateRequest requestDto) {
        ReservationCreateRequest createDto = new ReservationCreateRequest(requestDto.date(), requestDto.themeId(),
                requestDto.timeId(),
                requestDto.memberId());
        ReservationResponse responseDto = reservationService.createReservation(createDto);
        return ResponseEntity.created(URI.create("reservations/" + responseDto.id())).body(responseDto);
    }

    @Operation(
            summary = "관리자용 예약 목록 검색 API",
            description = "관리자 권한이 없으면 사용하지 못합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 목록 검색 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> searchReservationsByPeriod(
            @RequestParam("themeId") long themeId,
            @RequestParam("memberId") long memberId,
            @RequestParam("dateFrom") LocalDate dateFrom,
            @RequestParam("dateTo") LocalDate dateTo) {
        List<ReservationResponse> reservationBetween = reservationService.findReservationBetween(themeId, memberId,
                dateFrom, dateTo);
        return ResponseEntity.ok(reservationBetween);
    }
}

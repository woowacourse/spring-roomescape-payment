package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "관리자 예약 API", description = "관리자용 예약 생성 및 검색 API입니다.")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "예약 추가 (관리자용)",
            description = "관리자가 특정 날짜, 시간, 테마, 회원 정보를 이용해 예약을 추가합니다."
    )
    @ApiResponse(responseCode = "201", description = "예약이 성공적으로 생성됨")
    @PostMapping
    public ResponseEntity<ReservationResponse> addReservation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "관리자 예약 생성 요청 데이터",
                    required = true
            )
            @RequestBody AdminReservationCreateRequest requestDto) {
        ReservationCreateRequest createDto = new ReservationCreateRequest(
                requestDto.date(), requestDto.themeId(), requestDto.timeId(), requestDto.memberId()
        );
        ReservationResponse responseDto = reservationService.createReservation(createDto);
        return ResponseEntity.created(URI.create("reservations/" + responseDto.id())).body(responseDto);
    }

    @Operation(
            summary = "기간별 예약 검색",
            description = "특정 테마 및 회원에 대해 지정된 날짜 범위 내의 예약을 검색합니다."
    )
    @ApiResponse(responseCode = "200", description = "예약 목록 반환 성공")
    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> searchReservationsByPeriod(
            @Parameter(description = "테마 ID") @RequestParam("themeId") long themeId,
            @Parameter(description = "회원 ID") @RequestParam("memberId") long memberId,
            @Parameter(description = "시작 날짜") @RequestParam("dateFrom") LocalDate dateFrom,
            @Parameter(description = "종료 날짜") @RequestParam("dateTo") LocalDate dateTo) {
        List<ReservationResponse> reservationBetween = reservationService.findReservationBetween(themeId, memberId,
                dateFrom, dateTo);
        return ResponseEntity.ok(reservationBetween);
    }
}

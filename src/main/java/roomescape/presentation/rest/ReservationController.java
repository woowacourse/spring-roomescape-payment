package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationService;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationSearchFilter;
import roomescape.domain.user.User;
import roomescape.presentation.auth.Authenticated;
import roomescape.presentation.request.CreateReservationAdminRequest;
import roomescape.presentation.request.CreateReservationRequest;
import roomescape.presentation.response.ReservationResponse;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약 생성", description = "관리자가 새로운 예약을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "409", description = "이미 예약된 시간")
    @PostMapping("/admin/reservations")
    @ResponseStatus(CREATED)
    public ReservationResponse createReservationWithAdminPrivileges(
            @RequestBody @Valid final CreateReservationAdminRequest request
    ) {
        Reservation reservation = reservationService.saveReservationWithAdminPrivileges(
                request.userId(), request.date(), request.timeId(), request.themeId());

        return ReservationResponse.fromReservation(reservation);
    }

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    @ApiResponse(responseCode = "409", description = "이미 예약된 시간")
    @PostMapping("/reservations")
    @ResponseStatus(CREATED)
    public ReservationResponse createReservationWithUserPrivileges(
            @Authenticated final User user, @RequestBody @Valid final CreateReservationRequest request
    ) {
        Reservation reservation = reservationService.saveReservationWithUserPrivileges(
                request.toPaymentInfo(), user.id(), request.date(), request.timeId(), request.themeId());

        return ReservationResponse.fromReservation(reservation);
    }

    @Operation(summary = "예약 목록 조회", description = "필터 조건에 맞는 예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    @GetMapping("/reservations")
    public List<ReservationResponse> readAllReservations(
            @Parameter(description = "테마 ID") @RequestParam(name = "themeId", required = false) final Long themeId,
            @Parameter(description = "사용자 ID") @RequestParam(name = "userId", required = false) final Long userId,
            @Parameter(description = "시작 날짜") @RequestParam(name = "dateFrom", required = false) final LocalDate dateFrom,
            @Parameter(description = "종료 날짜") @RequestParam(name = "dateTo", required = false) final LocalDate dateTo
    ) {
        ReservationSearchFilter searchFilter = new ReservationSearchFilter(themeId, userId, dateFrom, dateTo);
        List<Reservation> reservations = reservationService.findReservationsByFilter(searchFilter);

        return ReservationResponse.fromReservations(reservations);
    }

    @Operation(summary = "예약 취소", description = "기존 예약을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "예약 취소 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 예약")
    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteReservationById(
            @Parameter(description = "예약 ID") @PathVariable("id") final long id
    ) {
        reservationService.removeById(id);
    }
}

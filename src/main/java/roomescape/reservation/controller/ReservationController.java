package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "관리자 예약 조건별 조회 API")
    @GetMapping
    public List<ReservationResponse> findReservationsByCriteria(
            @ModelAttribute final ReservationSearchRequest request) {
        return reservationService.findReservationsByCriteria(request);
    }

    @Operation(summary = "사용자 예약 가능 시간대별 조회 API")
    @GetMapping("/times")
    public List<AvailableReservationTimeResponse> findAllAvailableTimes(
            @NotNull @RequestParam final LocalDate date,
            @NotNull @RequestParam final Long themeId
    ) {
        return reservationService.findAllReservationTime(date, themeId);
    }

    @Operation(summary = "예약 저장 API")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse saveReservation(
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember member
    ) {
        return reservationService.saveReservation(request, member);
    }

    @Operation(summary = "예약 삭제 API")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable final Long id) {
        reservationService.deleteReservation(id);
    }

    @Operation(summary = "예약 회원별 조회 API")
    @GetMapping("/mine")
    public List<MyReservationResponse> findMyReservations(final LoginMember loginMember) {
        return reservationService.findMyReservations(loginMember);
    }
}

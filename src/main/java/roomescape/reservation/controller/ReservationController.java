package roomescape.reservation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import roomescape.auth.application.LoginMember;
import roomescape.auth.config.AuthenticationPrincipal;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationResponse> findReservationsByCriteria(
            @ModelAttribute final ReservationSearchRequest request) {
        return reservationService.findReservationsByCriteria(request);
    }

    @GetMapping("/times")
    public List<AvailableReservationTimeResponse> findAllAvailableTimes(
            @NotNull @RequestParam final LocalDate date,
            @NotNull @RequestParam final Long themeId
    ) {
        return reservationService.findAllReservationTime(date, themeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse saveReservation(
            @Valid @RequestBody final ReservationRequest request,
            @AuthenticationPrincipal final LoginMember loginMember
    ) {
        log.info("[(유저) 예약 추가] date: {}, timeId: {}, themeId: {}, memberId: {}",
                request.date(),
                request.timeId(),
                request.themeId(),
                loginMember.getId()
        );
        return reservationService.saveReservation(request, loginMember);
    }

    @DeleteMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable final Long reservationId) {
        log.info("[(유저) 예약 삭제 요청] reservationId: {}", reservationId);
        reservationService.deleteReservation(reservationId);
    }

    @GetMapping("/mine")
    public List<MyReservationResponse> findMyReservations(@AuthenticationPrincipal final LoginMember loginMember) {
        return reservationService.findMyReservations(loginMember);
    }
}

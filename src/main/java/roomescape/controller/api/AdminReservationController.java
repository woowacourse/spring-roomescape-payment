package roomescape.controller.api;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.annotation.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.ReservationCreateCommonRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.query.ReservationQueryService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    public AdminReservationController(ReservationQueryService reservationQueryService,
                                      ReservationCommandService reservationCommandService) {
        this.reservationQueryService = reservationQueryService;
        this.reservationCommandService = reservationCommandService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> readReservedReservations() {
        return reservationQueryService.findReservedReservations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDto addReservationByAdmin(
            @RequestBody ReservationCreateCommonRequestDto requestDto,
            @CurrentMember LoginInfo loginInfo
    ) {
        return reservationCommandService.bookReservation(requestDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDto> searchReservationsByPeriod(
            @RequestParam("themeId") long themeId,
            @RequestParam("memberId") long memberId,
            @RequestParam("dateFrom") LocalDate dateFrom,
            @RequestParam("dateTo") LocalDate dateTo
    ) {
        return reservationQueryService.searchReservationsBy(themeId, memberId, dateFrom, dateTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(
            @PathVariable("id") final Long id
    ) {
        reservationCommandService.cancelReservationBy(id);
    }
}

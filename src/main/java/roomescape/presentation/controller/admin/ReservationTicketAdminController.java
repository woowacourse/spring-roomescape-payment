package roomescape.presentation.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTicketAdminService;
import roomescape.dto.request.ReservationAdminRegisterDto;

@Tag(name = "관리자 전용 예약 API")
@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationTicketAdminController {

    private final ReservationTicketAdminService reservationTicketAdminService;

    @Operation(summary = "새로운 예약 저장")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addReservation(@RequestBody @Valid ReservationAdminRegisterDto reservationAdminRegisterDto) {
        reservationTicketAdminService.saveReservation(reservationAdminRegisterDto);
    }
}

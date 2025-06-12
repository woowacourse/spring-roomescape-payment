package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.facade.ReservationTicketPaymentService;
import roomescape.application.service.ReservationTicketService;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearchDto;
import roomescape.dto.request.ReservationTicketPaymentRequestDto;
import roomescape.dto.response.ReservationTicketResponseDto;

@Tag(name = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationTicketController {

    private final ReservationTicketService reservationTicketService;
    private final ReservationTicketPaymentService reservationTicketPaymentService;

    @Operation(summary = "예약 전체 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTicketResponseDto> getReservations() {
        return reservationTicketService.getReservationTickets();
    }

    @Operation(summary = "예약 검색")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTicketResponseDto> getReservations(
            @ModelAttribute ReservationSearchDto reservationSearchDto) {
        return reservationTicketService.searchReservationTickets(reservationSearchDto);
    }

    @Operation(summary = "새로운 예약 저장")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationTicketResponseDto addReservation(
            @RequestBody @Valid ReservationTicketPaymentRequestDto reservationTicketPaymentRequestDto,
            @Parameter(hidden = true) LoginMember loginMember) {

        reservationTicketPaymentService.processReservationRegistration(reservationTicketPaymentRequestDto, loginMember);

        return reservationTicketPaymentService.saveReservationWithPayment(reservationTicketPaymentRequestDto,
                loginMember);
    }

    @Operation(summary = "예약 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable("id") Long id) {
        reservationTicketService.cancelReservationTicket(id);
    }
}

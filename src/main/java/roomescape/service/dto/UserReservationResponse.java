package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.WaitingMember;
import roomescape.domain.reservation.dto.WaitingRank;

public record UserReservationResponse(
        long id,
        String theme,
        LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime time,
        String status,
        Long rank,
        String paymentKey,
        Integer amount
) {

        public static Stream<UserReservationResponse> reservationsToResponseStream(List<BookedMember> bookedMembers, List<PaymentResponse> paymentResponses) {
                return bookedMembers.stream()
                        .map(bookedMember -> createByReservation(bookedMember, paymentResponses));
        }

        private static UserReservationResponse createByReservation(BookedMember bookedMember, List<PaymentResponse> paymentResponses) {
                Reservation reservation = bookedMember.getReservation();
                Optional<PaymentResponse> payment = paymentResponses.stream()
                        .filter(paymentResponse -> paymentResponse.reservationId().equals(reservation.getId()))
                        .findFirst();

                return new UserReservationResponse(
                        reservation.getId(),
                        reservation.getTheme().getName(),
                        reservation.getDate(),
                        reservation.getTime().getStartAt(),
                        payment.isPresent() ?  ReservationStatus.BOOKED.getValue() : ReservationStatus.WAIT_PAYMENT.getValue(),
                        0L,
                        payment.isPresent() ? payment.get().paymentKey() : "",
                        payment.isPresent() ? payment.get().amount() : 0
                );
        }

        public static Stream<UserReservationResponse> waitingsToResponseStream(List<WaitingRank> waitingRanks) {
                return waitingRanks.stream()
                        .map(UserReservationResponse::createByWaiting);
        }

        private static UserReservationResponse createByWaiting(WaitingRank waitingRank) {
                WaitingMember waitingMember = waitingRank.waitingMember();
                Reservation reservation = waitingMember.getReservation();
                return new UserReservationResponse(
                        waitingMember.getId(),
                        reservation.getTheme().getName(),
                        reservation.getDate(),
                        reservation.getTime().getStartAt(),
                        ReservationStatus.WAIT.getValue(),
                        waitingRank.rank(),
                        "",
                        0
                );
        }

        public LocalDateTime dateTime() {
                return LocalDateTime.of(date, time);
        }
}

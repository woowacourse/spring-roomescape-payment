package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

        public static Stream<UserReservationResponse> reservationsToResponseStream(List<BookedReservationResponse> bookedReservationResponses, List<PaymentResponse> paymentResponses) {
                return bookedReservationResponses.stream()
                        .map(bookedMember -> createByReservation(bookedMember, paymentResponses));
        }

        private static UserReservationResponse createByReservation(BookedReservationResponse bookedReservationResponse, List<PaymentResponse> paymentResponses) {
                Optional<PaymentResponse> payment = paymentResponses.stream()
                        .filter(paymentResponse -> paymentResponse.reservationId().equals(bookedReservationResponse.reservationId()))
                        .findFirst();

                return new UserReservationResponse(
                        bookedReservationResponse.id(),
                        bookedReservationResponse.theme(),
                        bookedReservationResponse.date(),
                        bookedReservationResponse.startAt(),
                        payment.isPresent() ?  ReservationStatus.BOOKED.getValue() : ReservationStatus.WAIT_PAYMENT.getValue(),
                        0L,
                        payment.isPresent() ? payment.get().paymentKey() : "",
                        payment.isPresent() ? payment.get().amount() : 0
                );
        }

        public static Stream<UserReservationResponse> waitingsToResponseStream(List<WaitingRankResponse> waitingRanks) {
                return waitingRanks.stream()
                        .map(UserReservationResponse::createByWaiting);
        }

        private static UserReservationResponse createByWaiting(WaitingRankResponse waitingRank) {
                return new UserReservationResponse(
                        waitingRank.id(),
                        waitingRank.theme(),
                        waitingRank.date(),
                        waitingRank.startAt(),
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

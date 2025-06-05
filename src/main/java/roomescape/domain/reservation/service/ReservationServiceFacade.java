package roomescape.domain.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.auth.dto.LoginMember;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.service.MemberService;
import roomescape.domain.payment.entity.Payment;
import roomescape.domain.payment.service.PaymentService;
import roomescape.domain.reservation.dto.CreateReservationRequest;
import roomescape.domain.reservation.dto.CreateReservationResponse;
import roomescape.domain.reservation.dto.ReservationMineResponse;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.time.dto.AvailableReservationTimeRequest;
import roomescape.domain.time.dto.AvailableReservationTimeResponse;
import roomescape.domain.waiting.dto.CreateWaitingRequest;
import roomescape.domain.waiting.dto.CreateWaitingResponse;
import roomescape.domain.waiting.entity.Waiting;
import roomescape.domain.waiting.service.ReservationWaitingService;

@RequiredArgsConstructor
@Service
public class ReservationServiceFacade {

    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;
    private final MemberService memberService;
    private final PaymentService paymentService;

    @Transactional
    public CreateReservationResponse saveReservation(
            final CreateReservationRequest request,
            final LoginMember loginMember) {

        final Member member = memberService.findMemberByEmail(loginMember.email());
        final LocalDate date = request.date();
        final Long timeId = request.timeId();
        final Long themeId = request.themeId();

        final Reservation savedReservation = reservationService.save(member, date, timeId, themeId);

        paymentService.processPayment(request.paymentType(), request.paymentRequest(), savedReservation);

        return CreateReservationResponse.from(savedReservation);
    }

    @Transactional
    public CreateWaitingResponse saveWaiting(final CreateWaitingRequest request, final LoginMember loginMember) {
        final Member member = memberService.findMemberByEmail(loginMember.email());
        final LocalDate date = request.date();
        final Long time = request.time();
        final Long theme = request.theme();

        final Waiting savedWaiting = reservationWaitingService.createWaitingReservation(member, date, time, theme);

        return CreateWaitingResponse.from(savedWaiting);
    }

    @Transactional
    public void deleteWaiting(final Long id) {
        reservationWaitingService.deleteWaitingById(id);
    }

    @Transactional(readOnly = true)
    public List<ReservationMineResponse> findMyReservations(final LoginMember loginMember) {
        final Member member = memberService.findMemberByEmail(loginMember.email());
        final List<Payment> payments = paymentService.findAll();
        final List<Waiting> waitings = reservationWaitingService.findWaitingByMember(member);

        return Stream.concat(
                payments.stream().map(payment -> {
                    final Reservation reservation = payment.getReservation();
                    return ReservationMineResponse.from(reservation, payment);
                }),
                waitings.stream().map(waiting -> {
                    final long rank = reservationWaitingService.getRankInWaiting(waiting);
                    return ReservationMineResponse.from(waiting, rank);
                })
        ).toList();
    }

    @Transactional(readOnly = true)
    public List<CreateReservationResponse> findAll() {
        final List<Reservation> reservations = reservationService.findAll();

        return reservations.stream()
                .map(CreateReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailableReservationTimeResponse> findAvailableTimes(final AvailableReservationTimeRequest request) {

        final LocalDate date = request.date();
        final Long themeId = request.themeId();

        return reservationService.findAvailableReservationTimes(date, themeId)
                .stream()
                .map(availableReservationTime -> new AvailableReservationTimeResponse(
                        availableReservationTime.id(),
                        availableReservationTime.startAt(),
                        availableReservationTime.alreadyBooked()
                ))
                .toList();
    }
}

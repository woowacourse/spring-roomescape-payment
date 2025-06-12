package roomescape.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.DuplicatedException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationSearchDto;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.response.MemberReservationResponseDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.Waiting;
import roomescape.persistence.repository.MemberRepository;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;
import roomescape.persistence.repository.ThemeRepository;
import roomescape.persistence.repository.TossPaymentRepository;
import roomescape.persistence.repository.WaitingRepository;
import roomescape.persistence.vo.Period;

@Service
@RequiredArgsConstructor
public class ReservationTicketService {

    private final ReservationTicketRepository reservationTicketRepository;
    private final WaitingRepository waitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentRepository tossPaymentRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public ReservationTicket saveReservationTicket(ReservationTicketRegisterDto reservationTicketRegisterDto,
                                                   LoginMember loginMember) {
        ReservationTicket reservationTicket = createReservationTicket(reservationTicketRegisterDto, loginMember);
        return reservationTicketRepository.save(reservationTicket);
    }

    @Transactional(readOnly = true)
    public void validateReservationTicket
            (ReservationTicketRegisterDto reservationTicketRegisterDto,
             LoginMember loginMember) {
        ReservationTicket reservationTicket = createReservationTicket(reservationTicketRegisterDto,
                loginMember);
        assertReservationIsNotDuplicated(reservationTicket);
    }

    public List<ReservationTicketResponseDto> getReservationTickets() {
        return reservationTicketRepository.findAll().stream()
                .map(ReservationTicketResponseDto::new)
                .toList();
    }

    public List<MemberReservationResponseDto> getReservationTicketsOfMember(LoginMember loginMember) {
        List<ReservationTicket> reservationTickets = reservationTicketRepository.findForMember(
                loginMember.id());

        return reservationTickets.stream()
                .map(reservationTicket -> new MemberReservationResponseDto(reservationTicket,
                        tossPaymentRepository.findForReservationTicket(reservationTicket)))
                .toList();
    }

    public List<ReservationTicketResponseDto> searchReservationTickets(
            ReservationSearchDto reservationSearchDto) {
        Long themeId = reservationSearchDto.themeId();
        Long memberId = reservationSearchDto.memberId();
        LocalDate startDate = reservationSearchDto.startDate();
        LocalDate endDate = reservationSearchDto.endDate();

        return reservationTicketRepository.findForThemeAndMemberInPeriod(
                        themeId,
                        memberId,
                        new Period(startDate, endDate)
                ).stream()
                .map(ReservationTicketResponseDto::new)
                .toList();
    }

    public void cancelReservationTicket(Long id) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(id);
        reservationTicketRepository.deleteById(id);

        promoteNextWaitingToReservationTicket(reservationTicket);
    }

    private void promoteNextWaitingToReservationTicket(ReservationTicket reservationTicket) {
        Optional<Waiting> optionalNextWaiting = waitingRepository.findNextWaiting(
                reservationTicket.getDate(),
                reservationTicket.getReservationTime(),
                reservationTicket.getTheme()
        );

        if (optionalNextWaiting.isEmpty()) {
            return;
        }

        Waiting nextWaiting = optionalNextWaiting.get();

        promoteToReservationTicket(nextWaiting);
        waitingRepository.delete(nextWaiting);
    }

    private void promoteToReservationTicket(Waiting nextWaiting) {
        ReservationTicket convertedReservationTicket = convertToReservationTicket(nextWaiting);
        reservationTicketRepository.save(convertedReservationTicket);
    }

    private ReservationTicket convertToReservationTicket(Waiting nextWaiting) {
        return new ReservationTicket(
                new Reservation(
                        nextWaiting.getReservationDate(),
                        nextWaiting.getReservationTime(),
                        nextWaiting.getTheme(),
                        nextWaiting.getReservation().getMember(),
                        nextWaiting.getRegisteredAt().toLocalDate()
                ));
    }

    private ReservationTicket createReservationTicket(
            ReservationTicketRegisterDto reservationTicketRegisterDto,
            LoginMember loginMember) {
        ReservationTime time = reservationTimeRepository.findById(
                reservationTicketRegisterDto.timeId());
        Theme theme = themeRepository.findById(reservationTicketRegisterDto.themeId());
        Member member = memberRepository.findById(loginMember.id());

        return reservationTicketRegisterDto.convertToReservation(time, theme, member);
    }

    private void assertReservationIsNotDuplicated(ReservationTicket reservationTicket) {
        if (reservationTicketRepository.isDuplicatedForDateAndReservationTime(
                reservationTicket.getDate(),
                reservationTicket.getReservationTime())) {
            throw new DuplicatedException("이미 예약이 존재합니다.");
        }
    }
}

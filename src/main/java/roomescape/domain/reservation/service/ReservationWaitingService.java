package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.model.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.reservation.dto.ApproveReservationWaitingRequest;
import roomescape.domain.reservation.dto.ReservationDto;
import roomescape.domain.reservation.dto.ReservationWaitingDto;
import roomescape.domain.reservation.dto.ReservationWaitingWithOrderDto;
import roomescape.domain.reservation.dto.SaveReservationRequest;
import roomescape.domain.reservation.dto.SaveReservationWaitingRequest;
import roomescape.domain.reservation.exception.InvalidReservationWaitInputException;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationStatus;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationWaiting;
import roomescape.domain.reservation.model.ReservationWaitingWithOrder;
import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.repository.CustomReservationWaitingRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ReservationWaitingRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationWaitingService {

    private static final int FIRST_RESERVATION_WAITING_ORDER_VALUE = 1;

    private final ReservationWaitingRepository reservationWaitingRepository;
    private final CustomReservationWaitingRepository customReservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationService reservationService;

    public ReservationWaitingService(
            final CustomReservationWaitingRepository customReservationWaitingRepository,
            final ReservationWaitingRepository reservationWaitingRepository,
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ThemeRepository themeRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationService reservationService
    ) {
        this.customReservationWaitingRepository = customReservationWaitingRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationService = reservationService;
    }

    public List<ReservationWaitingDto> getAllReservationWaiting() {
        return reservationWaitingRepository.findAll()
                .stream()
                .map(ReservationWaitingDto::from)
                .toList();
    }

    public List<ReservationWaitingWithOrderDto> getMyReservationWaiting(final Long memberId) {
        return customReservationWaitingRepository.findAllReservationWaitingWithOrdersByMemberId(memberId)
                .stream()
                .map(reservationWaitingWithOrder -> ReservationWaitingWithOrderDto.from(reservationWaitingWithOrder, checkPaymentAvailable(reservationWaitingWithOrder)))
                .toList();
    }

    private boolean checkPaymentAvailable(final ReservationWaitingWithOrder reservationWaitingWithOrder) {
        if (reservationWaitingWithOrder.getOrder() != FIRST_RESERVATION_WAITING_ORDER_VALUE) {
            return false;
        }

        final ReservationWaiting reservationWaiting = reservationWaitingWithOrder.getReservationWaiting();
        final boolean existReservation = reservationRepository.existsByDateAndTime_IdAndTheme_IdAndStatus(
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getId(),
                reservationWaiting.getTheme().getId(),
                ReservationStatus.RESERVATION
        );

        return !existReservation;
    }

    public Long saveReservationWaiting(final SaveReservationWaitingRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(request.time())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 예약 시간이 존재하지 않습니다."));
        final Theme theme = themeRepository.findById(request.theme())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 테마가 존재하지 않습니다."));
        final Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 회원이 존재하지 않습니다."));

        final ReservationDate reservationDate = new ReservationDate(request.date());

        checkReservationExist(theme, reservationDate, reservationTime);
        checkReservationWaitingAlreadyExist(member, reservationDate, reservationTime, theme);

        final ReservationWaiting reservationWaiting = new ReservationWaiting(
                reservationTime,
                theme,
                member,
                reservationDate,
                LocalDateTime.now()
        );
        return reservationWaitingRepository.save(reservationWaiting).getId();
    }

    private void checkReservationExist(
            final Theme theme,
            final ReservationDate date,
            final ReservationTime reservationTime
    ) {
        final boolean existReservation = reservationRepository.existsByDateAndTime_IdAndTheme_IdAndStatus(
                date,
                reservationTime.getId(),
                theme.getId(),
                ReservationStatus.RESERVATION
        );

        if (!existReservation) {
            throw new InvalidReservationWaitInputException("존재하지 않는 예약에 대한 대기 신청을 할 수 없습니다.");
        }
    }

    private void checkReservationWaitingAlreadyExist(
            final Member member,
            final ReservationDate reservationDate,
            final ReservationTime reservationTime,
            final Theme theme
    ) {
        if (reservationWaitingRepository.existsByMemberAndDateAndTimeAndTheme(member, reservationDate, reservationTime, theme)) {
            throw new InvalidReservationWaitInputException("이미 해당 예약 대기가 존재합니다.");
        }
    }

    public void deleteReservationWaiting(final Long reservationWaitingId) {
        reservationWaitingRepository.deleteById(reservationWaitingId);
    }

    @Transactional
    public ReservationDto approveReservationWaiting(final ApproveReservationWaitingRequest request) {
        final ReservationWaitingWithOrder reservationWaitingWithOrder = customReservationWaitingRepository.findReservationWaitingWithOrder(request.reservationWaitingId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 대기 정보입니다."));

        validateReservationWaiting(reservationWaitingWithOrder);
        final SaveReservationRequest saveReservationRequest = convertSaveReservationRequest(request, reservationWaitingWithOrder);
        final ReservationDto reservationDto = reservationService.saveReservation(saveReservationRequest);
        deleteReservationWaiting(reservationWaitingWithOrder.getReservationWaiting().getId());

        return reservationDto;
    }

    private void validateReservationWaiting(final ReservationWaitingWithOrder reservationWaitingWithOrder) {
        if (!checkPaymentAvailable(reservationWaitingWithOrder)) {
            throw new IllegalStateException("결제할 수 없는 예약 대기 입니다.");
        }
    }

    private SaveReservationRequest convertSaveReservationRequest(final ApproveReservationWaitingRequest request, final ReservationWaitingWithOrder reservationWaitingWithOrder) {
        final ReservationWaiting reservationWaiting = reservationWaitingWithOrder.getReservationWaiting();
        return new SaveReservationRequest(
                reservationWaiting.getDate().getValue(),
                reservationWaiting.getMember().getId(),
                reservationWaiting.getTime().getId(),
                reservationWaiting.getTheme().getId(),
                request.orderId(),
                request.amount(),
                request.paymentKey()
        );
    }
}

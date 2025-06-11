package roomescape.domain.waiting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.service.MemberQueryService;
import roomescape.domain.payment.domain.Payment;
import roomescape.domain.payment.service.PaymentQueryService;
import roomescape.domain.reservation.service.ReservationQueryService;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.service.ThemeQueryService;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.time.service.ReservationTimeQueryService;
import roomescape.domain.waiting.domain.Waiting;
import roomescape.domain.waiting.dto.WaitingCreationContent;
import roomescape.domain.waiting.repository.WaitingRepository;
import roomescape.domain.waiting.response.AddWaitingResponse;
import roomescape.exception.BadRequestException;

@Service
@Transactional
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final MemberQueryService memberQueryService;
    private final ThemeQueryService themeQueryService;
    private final ReservationTimeQueryService timeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;
    private final PaymentQueryService paymentQueryService;

    public WaitingService(
            WaitingRepository waitingRepository,
            MemberQueryService memberQueryService,
            ThemeQueryService themeQueryService,
            ReservationTimeQueryService timeQueryService,
            ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService, PaymentQueryService paymentQueryService
    ) {
        this.waitingRepository = waitingRepository;
        this.memberQueryService = memberQueryService;
        this.themeQueryService = themeQueryService;
        this.timeQueryService = timeQueryService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
        this.paymentQueryService = paymentQueryService;
    }

    @Transactional
    public AddWaitingResponse addWaiting(
            WaitingCreationContent content,
            Long paymentId
    ) {
        Payment payment = paymentQueryService.getPaymentById(paymentId);
        Theme theme = themeQueryService.getThemeById(content.themeId());
        ReservationTime time = timeQueryService.getTimeById(content.timeId());
        Member member = memberQueryService.getMemberById(content.memberId());

        Waiting waiting = Waiting.createWithoutId(content.date(), theme, time, member, payment);

        validateEmptyReservation(waiting);
        validatePastWaitingCreation(waiting);
        validateDuplicatedWaiting(waiting);
        Waiting savedWaiting = waitingRepository.save(waiting);
        return new AddWaitingResponse(savedWaiting);
    }

    public void deleteWaitingById(long id) {
        Waiting waiting = waitingQueryService.getWaitingById(id);
        waitingRepository.delete(waiting);
    }

    private void validatePastWaitingCreation(Waiting waiting) {
        boolean isPast = waiting.isPastWaiting();
        if (isPast) {
            throw new BadRequestException("과거 날짜와 시간으로 예약 대기를 생성할 수 없습니다.");
        }
    }

    private void validateDuplicatedWaiting(Waiting waiting) {
        boolean isDuplicated = waitingQueryService.existsAlreadyWaiting(
                waiting.getTheme(), waiting.getDate(), waiting.getTime(), waiting.getMember());
        if (isDuplicated) {
            throw new BadRequestException("중복된 예약 대기는 허용하지 않습니다.");
        }
    }

    private void validateEmptyReservation(Waiting waiting) {
        boolean isExisted = reservationQueryService.existsAlreadyReservation(
                waiting.getTheme(), waiting.getDate(), waiting.getTime());
        if (!isExisted) {
            throw new BadRequestException("예약이 존재하지 않는 예약 대기는 허용하지 않습니다.");
        }
    }
}

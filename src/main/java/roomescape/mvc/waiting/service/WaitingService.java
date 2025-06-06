package roomescape.mvc.waiting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadRequestException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.service.MemberQueryService;
import roomescape.mvc.payment.domain.Payment;
import roomescape.mvc.payment.dto.PaymentCreationContent;
import roomescape.mvc.payment.service.PaymentService;
import roomescape.mvc.reservation.service.ReservationQueryService;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.time.service.ReservationTimeQueryService;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.dto.WaitingCreationContent;
import roomescape.mvc.waiting.repository.WaitingRepository;
import roomescape.mvc.waiting.response.AddWaitingResponse;

@Service
@Transactional
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final MemberQueryService memberQueryService;
    private final ThemeQueryService themeQueryService;
    private final ReservationTimeQueryService timeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;
    private final PaymentService paymentService;

    public WaitingService(
            WaitingRepository waitingRepository,
            MemberQueryService memberQueryService,
            ThemeQueryService themeQueryService,
            ReservationTimeQueryService timeQueryService,
            ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService,
            PaymentService paymentService
    ) {
        this.waitingRepository = waitingRepository;
        this.memberQueryService = memberQueryService;
        this.themeQueryService = themeQueryService;
        this.timeQueryService = timeQueryService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
        this.paymentService = paymentService;
    }

    @Transactional
    public AddWaitingResponse addWaiting(
            WaitingCreationContent content,
            PaymentCreationContent paymentCreationContent
    ) {
        Theme theme = themeQueryService.getThemeById(content.themeId());
        ReservationTime time = timeQueryService.getTimeById(content.timeId());
        Member member = memberQueryService.getMemberById(content.memberId());

        Payment paymentHistory = paymentService.savePayment(paymentCreationContent);
        Waiting waiting = Waiting.createWithoutId(content.date(), theme, time, member, paymentHistory);

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

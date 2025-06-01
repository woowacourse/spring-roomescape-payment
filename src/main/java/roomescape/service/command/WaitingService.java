package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.PaymentHistory;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.WaitingCreationContent;
import roomescape.dto.response.WaitingResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.WaitingRepository;
import roomescape.service.query.MemberQueryService;
import roomescape.service.query.ReservationQueryService;
import roomescape.service.query.ReservationTimeQueryService;
import roomescape.service.query.ThemeQueryService;
import roomescape.service.query.WaitingQueryService;

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
    public WaitingResponse addWaiting(
            WaitingCreationContent content,
            PaymentHistoryCreationContent paymentHistoryCreationContent
    ) {
        Theme theme = themeQueryService.getThemeById(content.themeId());
        ReservationTime time = timeQueryService.getTimeById(content.timeId());
        Member member = memberQueryService.getMemberById(content.memberId());

        PaymentHistory paymentHistory = paymentService.pay(paymentHistoryCreationContent);
        Waiting waiting = Waiting.createWithoutId(content.date(), theme, time, member, paymentHistory);

        validateEmptyReservation(waiting);
        validatePastWaitingCreation(waiting);
        validateDuplicatedWaiting(waiting);
        Waiting savedWaiting = waitingRepository.save(waiting);
        return new WaitingResponse(savedWaiting);
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

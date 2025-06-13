package roomescape.reservation.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.admin.domain.dto.SearchReservationRequestDto;
import roomescape.payment.global.domain.PgPayment;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;
import roomescape.payment.global.repository.PaymentRepository;
import roomescape.payment.global.service.PgPaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.dto.ReservationInfo;
import roomescape.reservation.domain.dto.ReservationRequestDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.domain.dto.ReservationWithPaymentDto;
import roomescape.reservation.exception.DuplicateReservationException;
import roomescape.reservation.exception.InvalidReservationTimeException;
import roomescape.reservation.exception.NotFoundReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.InvalidThemeException;
import roomescape.theme.repository.ThemeRepository;
import roomescape.user.domain.User;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository repository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;
    private final PgPaymentService pgPaymentService;

    public ReservationService(ReservationRepository repository,
                              ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
                              WaitingRepository waitingRepository, PaymentRepository paymentRepository, PgPaymentService pgPaymentService) {
        this.repository = repository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
        this.pgPaymentService = pgPaymentService;
    }

    public List<ReservationResponseDto> findAll() {
        List<Reservation> reservations = repository.findAll();
        return reservations.stream()
                .map(this::convertReservationResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponseDto add(ReservationRequestDto requestDto, User user) {
        validateDuplicateDateTime(requestDto);
        Reservation reservation = convertReservation(requestDto, user);
        Reservation savedReservation = repository.save(reservation);
        return convertReservationResponseDto(savedReservation);
    }

    @Transactional
    public ReservationResponseDto addWithPayment(ReservationWithPaymentDto requestDto, User user) {
        log.info("addWithPayment 요청 - 사용자 ID: {}, 요청 데이터: {}", user.getId(), requestDto);

        ReservationRequestDto reservationRequestDto = convertReservationRequestDto(requestDto);
        validateDuplicateDateTime(reservationRequestDto);
        log.info("중복 예약 검사 통과 - 날짜: {}, 시간 ID: {}, 테마 ID: {}",
                reservationRequestDto.date(), reservationRequestDto.timeId(), reservationRequestDto.themeId());

        PgPaymentRequestDto pgPaymentRequestDto = convertPaymentRequestDto(requestDto);
        log.info("결제 승인 시도 - PaymentKey: {}, OrderId: {}, 금액: {}",
                pgPaymentRequestDto.paymentKey(), pgPaymentRequestDto.orderId(), pgPaymentRequestDto.amount());

        PgPaymentDataDto pgPaymentDataDto = pgPaymentService.approve(pgPaymentRequestDto);
        log.info("결제 승인 성공 - PG사 응답: {}", pgPaymentDataDto);

        PgPayment savedPgPayment = paymentRepository.save(pgPaymentDataDto.toEntity());
        log.info("결제 정보 저장 완료 - Payment ID: {}", savedPgPayment.getId());

        Reservation reservation = convertReservation(reservationRequestDto, user, savedPgPayment);
        Reservation savedReservation = repository.save(reservation);
        log.info("예약 저장 완료 - 예약 ID: {}", savedReservation.getId());

        return convertReservationResponseDto(savedReservation);
    }

    private ReservationRequestDto convertReservationRequestDto(ReservationWithPaymentDto requestDto) {
        return ReservationRequestDto.ofReservationWithPaymentDto(requestDto);
    }

    private PgPaymentRequestDto convertPaymentRequestDto(ReservationWithPaymentDto requestDto) {
        return PgPaymentRequestDto.ofReservationWithPaymentDto(requestDto);
    }

    @Transactional
    public ReservationInfo cancelReservationAndReturnInfo(Long id) {
        Reservation oldReservation = findByIdOrThrow(id);
        repository.deleteById(id);
        return new ReservationInfo(oldReservation.getDate(), oldReservation.getReservationTime(), oldReservation.getTheme());
    }

    @Transactional
    public void approveWaiting(ReservationInfo reservationInfo) {
        Optional<Waiting> waitingOptional = waitingRepository.findOneByReservationInfoDesc(reservationInfo.date(),
                reservationInfo.time().getId(), reservationInfo.theme().getId());
        if (waitingOptional.isEmpty()) {
            return ;
        }

        Waiting waiting = waitingOptional.get();
        waitingRepository.deleteById(waiting.getId());

        Reservation newReservation = Reservation.ofWaiting(waiting);
        repository.save(newReservation);
    }

    private Reservation findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundReservationException("해당 예약 id가 존재하지 않습니다."));
    }

    private void validateDuplicateDateTime(ReservationRequestDto dto) {
        ReservationTime reservationTime = reservationTimeRepository.findById(dto.timeId())
                .orElseThrow(InvalidReservationTimeException::new);
        boolean exists = repository.existsByDateAndReservationTime(
                dto.date(),
                reservationTime
        );
        if (exists) {
            throw new DuplicateReservationException();
        }
    }

    public List<ReservationResponseDto> findReservationsByUserAndThemeAndFromAndTo(
            SearchReservationRequestDto searchReservationRequestDto) {
        List<Reservation> reservations = repository.findReservationsByUserAndThemeAndFromAndTo(
                searchReservationRequestDto.userId(),
                searchReservationRequestDto.themeId(),
                searchReservationRequestDto.from(),
                searchReservationRequestDto.to()
                );

        return reservations.stream()
                .map(this::convertReservationResponseDto)
                .toList();
    }

    private Reservation convertReservation(ReservationRequestDto dto, User user) {
        return convertReservation(dto, user, null);
    }

    private Reservation convertReservation(ReservationRequestDto dto, User user, PgPayment payment) {
        ReservationTime reservationTime = reservationTimeRepository.findById(dto.timeId())
                .orElseThrow(InvalidReservationTimeException::new);
        Theme theme = themeRepository.findById(dto.themeId())
                .orElseThrow(InvalidThemeException::new);

        return dto.toEntity(reservationTime, theme, user, payment);
    }

    private ReservationResponseDto convertReservationResponseDto(Reservation reservation) {
        return ReservationResponseDto.of(reservation);
    }
}

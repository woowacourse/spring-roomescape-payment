package roomescape.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.AdminReservationRequest;
import roomescape.controller.request.PaymentRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.exception.PaymentException;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ReservationService {

    private final static long RESERVATION_PRICE = 1999999;

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final RestClient restClient;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository,
                              RestClient restClient) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.restClient = restClient;
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> filterReservation(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo) {
        Theme theme = themeRepository.findById(themeId)
                .orElse(null);
        Member member = memberRepository.findById(memberId)
                .orElse(null);
        return reservationRepository.findByConditions(theme, member, dateFrom, dateTo);
    }

    @Transactional
    public Reservation addReservation(ReservationRequest request, Member member) {
        confirmPayments(request.paymentKey(), request.orderId(), request.amount());

        ReservationTime reservationTime = findReservationTime(request.date(), request.timeId(),
                request.themeId());
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(request.themeId())));

        Reservation reservation = new Reservation(request.date(), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    private void confirmPayments(String paymentKey, String orderId, Long amount) {
        validatePayments(amount);
        restClient.post()
                .uri("/confirm")
                .contentType(APPLICATION_JSON)
                .body(new PaymentRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new PaymentException("결제 정보가 일치하지 않습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentException("결제 시스템이 원활하게 동작하지 않습니다.");
                }).toBodilessEntity();
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }

    @Transactional
    public Reservation addReservation(AdminReservationRequest request) {
        ReservationTime reservationTime = findReservationTime(request.date(), request.timeId(), request.themeId());

        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(request.themeId())));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 사용자가 존재하지 않습니다.".formatted(request.memberId())));

        Reservation reservation = new Reservation(request.date(), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    private ReservationTime findReservationTime(LocalDate date, long timeId, long themeId) {
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(timeId)));
        validateDuplicatedReservation(date, themeId, timeId);
        return reservationTime;
    }

    private void validateDuplicatedReservation(LocalDate date, Long themeId, Long timeId) {
        ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(timeId)));
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(themeId)));

        boolean exists = reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
        if (exists) {
            throw new DuplicatedException("이미 해당 시간에 예약이 존재합니다.");
        }
    }

    @Transactional
    public void deleteReservation(long id) {
        validateExistReservation(id);
        reservationRepository.deleteById(id);
    }

    private void validateExistReservation(long id) {
        boolean exists = reservationRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id));
        }
    }

    @Transactional(readOnly = true)
    public List<Reservation> findMemberReservations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(memberId)));
        return reservationRepository.findAllByMember(member);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id)));
    }
}

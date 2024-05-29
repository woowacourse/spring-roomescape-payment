package roomescape.reservation.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.global.restclient.PaymentWithRestClient;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.specification.ReservationSpecification;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final ReservationRepository reservationRepository;
    private final RestClient restClient;

    public ReservationService(MemberRepository memberRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              ReservationSlotRepository reservationSlotRepository,
                              ReservationRepository reservationRepository,
                              RestClient restClient) {
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationSlotRepository = reservationSlotRepository;
        this.reservationRepository = reservationRepository;
        this.restClient = restClient;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(ReservationQueryRequest request) {
        Specification<Reservation> spec = Specification
                .where(ReservationSpecification.greaterThanOrEqualToStartDate(request.getStartDate()))
                .and(ReservationSpecification.lessThanOrEqualToEndDate(request.getEndDate()))
                .and(ReservationSpecification.equalMemberId(request.getMemberId()))
                .and(ReservationSpecification.equalThemeId(request.getThemeId()));
        return reservationRepository.findAll(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWithStatus> findReservations(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        return reservationRepository.findAllByMember(member)
                .stream()
                .map(ReservationWithStatus::from)
                .toList();
    }

    public ReservationResponse reserve(ReservationPaymentRequest reservationPaymentRequest, Long memberId) {
        ReservationRequest reservationRequest = new ReservationRequest(reservationPaymentRequest.date(), reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());
        ReservationResponse reservationResponse = createReservation(reservationRequest, memberId);
        PaymentRequest paymentRequest = new PaymentRequest(reservationPaymentRequest);
        PaymentResponse paymentResponse = confirm(paymentRequest);

        if (!Objects.equals(paymentResponse.totalAmount(), reservationResponse.amount())) {
            throw new BadRequestException("결제 금액이 잘못되었습니다.");
        }

        return reservationResponse;
    }

    public ReservationResponse createReservation(ReservationRequest reservationRequest, Long memberId) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 예약 시간이 없습니다."));
        Theme theme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 테마가 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        ReservationSlot reservationSlot = reservationSlotRepository.findByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> reservationSlotRepository.save(new ReservationSlot(date, reservationTime, theme)));
        ReservationStatus reservationStatus = ReservationStatus.BOOKED;

        validateReservation(reservationSlot, member);

        if (reservationRepository.existsByReservationSlot(reservationSlot)) {
            reservationStatus = ReservationStatus.WAITING;
        }

        Reservation reservation = reservationRepository.save(
                new Reservation(member, reservationSlot, reservationStatus));
        return ReservationResponse.from(reservation.getId(), reservationSlot, member);
    }

    private PaymentResponse confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }

    private void validateReservation(ReservationSlot reservationSlot, Member member) {
        if (reservationSlot.isPast()) {
            throw new BadRequestException("올바르지 않는 데이터 요청입니다.");
        }
        if (reservationRepository.existsByReservationSlotAndMember(reservationSlot, member)) {
            throw new ForbiddenException("중복된 예약입니다.");
        }
    }

    public void deleteReservation(AuthInfo authInfo, long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 사용자 예약이 없습니다."));
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        if (!member.isAdmin() && !reservation.isBookedBy(member)) {
            throw new ForbiddenException("예약자가 아닙니다.");
        }
        reservationRepository.deleteById(reservationId);
    }

    public void delete(long reservationId) {
        reservationRepository.deleteByReservationSlot_Id(reservationId);
        reservationSlotRepository.deleteById(reservationId);
    }
}

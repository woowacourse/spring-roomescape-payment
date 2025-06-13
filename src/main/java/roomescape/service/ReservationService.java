package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.internal.ReservationDetail;
import roomescape.dto.request.AddReservationRequest;
import roomescape.dto.request.AdminCreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.entity.Member;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidMemberException;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.exception.custom.InvalidReservationTimeException;
import roomescape.exception.custom.InvalidThemeException;
import roomescape.global.ReservationStatus;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
@Slf4j
public class ReservationService {

        private final MemberRepository memberRepository;
        private final ReservationRepository reservationRepository;
        private final ReservationTimeRepository reservationTimeRepository;
        private final ThemeRepository themeRepository;
        private final PaymentRepository paymentRepository;

        public ReservationService(MemberRepository memberRepository, ReservationRepository reservationRepository,
                        ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
                        PaymentRepository paymentRepository) {
                this.memberRepository = memberRepository;
                this.reservationRepository = reservationRepository;
                this.reservationTimeRepository = reservationTimeRepository;
                this.themeRepository = themeRepository;
                this.paymentRepository = paymentRepository;
        }

        public ReservationResponse addReservationByMember(
                        AddReservationRequest request,
                        LoginMemberRequest loginMemberRequest) {
                log.info("회원 예약 생성 시작 - memberId: {}, themeId: {}, date: {}, timeId: {}",
                                loginMemberRequest.id(), request.themeId(), request.date(), request.timeId());

                Reservation reservation = createReservation(loginMemberRequest.id(), request.themeId(), request.date(),
                                request.timeId(), ReservationStatus.RESERVED);

                log.info("회원 예약 생성 완료 - reservationId: {}", reservation.getId());
                return ReservationResponse.from(reservation);
        }

        public ReservationResponse addReservationByAdmin(AdminCreateReservationRequest request) {
                log.info("관리자 예약 생성 시작 - memberId: {}, themeId: {}, date: {}, timeId: {}",
                                request.memberId(), request.themeId(), request.date(), request.timeId());

                Reservation reservation = createReservation(request.memberId(), request.themeId(), request.date(),
                                request.timeId(), ReservationStatus.RESERVED);

                log.info("관리자 예약 생성 완료 - reservationId: {}", reservation.getId());
                return ReservationResponse.from(reservation);
        }

        public ReservationWaitResponse addWaitReservation(
                        CreateWaitReservationRequest request,
                        LoginMemberRequest loginMemberRequest) {
                log.info("대기 예약 생성 시작 - memberId: {}, themeId: {}, date: {}, timeId: {}",
                                loginMemberRequest.id(), request.themeId(), request.date(), request.timeId());

                Reservation reservation = createReservation(loginMemberRequest.id(), request.themeId(), request.date(),
                                request.timeId(),
                                ReservationStatus.WAIT);

                log.info("대기 예약 생성 완료 - reservationId: {}", reservation.getId());
                return ReservationWaitResponse.from(reservation);
        }

        public void approveWaitReservationByAdmin(long waitReservationId) {
                log.info("대기 예약 승인 시작 - waitReservationId: {}", waitReservationId);

                Reservation waitReservation = reservationRepository.findById(waitReservationId)
                                .orElseThrow(() -> {
                                        log.error("대기 예약을 찾을 수 없음 - waitReservationId: {}", waitReservationId);
                                        return new InvalidReservationException("존재하지 않는 예약 대기입니다.");
                                });

                if (waitReservation.getStatus() == ReservationStatus.RESERVED) {
                        log.error("이미 예약 처리된 대기 예약 - waitReservationId: {}", waitReservationId);
                        throw new InvalidReservationException("이미 예약 처리 되었습니다.");
                }

                Optional<Reservation> cancelTargetOptional = reservationRepository
                                .findByDateAndReservationTimeAndThemeAndStatus(
                                                waitReservation.getDate(),
                                                waitReservation.getReservationTime(),
                                                waitReservation.getTheme(),
                                                ReservationStatus.RESERVED);
                cancelTargetOptional.ifPresent(Reservation::cancel);

                waitReservation.changeStatusWaitToReserve();
                log.info("대기 예약 승인 완료 - waitReservationId: {}", waitReservationId);
        }

        public void rejectWaitReservationByAdmin(long waitReservationId) {
                log.info("대기 예약 거절 시작 - waitReservationId: {}", waitReservationId);

                Reservation waitReservation = reservationRepository.findById(waitReservationId)
                                .orElseThrow(() -> {
                                        log.error("대기 예약을 찾을 수 없음 - waitReservationId: {}", waitReservationId);
                                        return new InvalidReservationException("존재하지 않는 예약 대기입니다.");
                                });
                waitReservation.cancel();

                log.info("대기 예약 거절 완료 - waitReservationId: {}", waitReservationId);
        }

        public Reservation createReservation(
                        long memberId,
                        long themeId,
                        LocalDate date,
                        long timeId,
                        ReservationStatus status) {
                log.info("예약 생성 시작 - memberId: {}, themeId: {}, date: {}, timeId: {}, status: {}",
                                memberId, themeId, date, timeId, status);

                Member member = memberRepository.findFetchById(memberId)
                                .orElseThrow(() -> {
                                        log.error("회원을 찾을 수 없음 - memberId: {}", memberId);
                                        return new InvalidMemberException("존재하지 않는 멤버 ID입니다.");
                                });
                ReservationTime reservationTime = reservationTimeRepository.findById(timeId)
                                .orElseThrow(() -> {
                                        log.error("예약 시간을 찾을 수 없음 - timeId: {}", timeId);
                                        return new InvalidReservationTimeException("존재하지 않는 예약 시간입니다.");
                                });
                Theme theme = themeRepository.findById(themeId)
                                .orElseThrow(() -> {
                                        log.error("테마를 찾을 수 없음 - themeId: {}", themeId);
                                        return new InvalidThemeException("존재하지 않는 테마입니다.");
                                });

                Reservation reservation = member.reserve(date, reservationTime, theme, status);
                log.info("예약 생성 완료 - reservationId: {}, status: {}", reservation.getId(), status);
                return reservation;
        }

        public List<ReservationResponse> findAll() {
                log.info("예약 목록 조회 시작");
                List<Reservation> reservations = reservationRepository.findAll();
                List<ReservationResponse> responses = reservations.stream()
                                .map(ReservationResponse::from)
                                .toList();
                log.info("예약 목록 조회 완료 - 총 {}개", responses.size());
                return responses;
        }

        public void deleteReservation(Long reservationId) {
                log.info("예약 삭제 시작 - reservationId: {}", reservationId);
                Reservation reservation = reservationRepository.findById(reservationId)
                                .orElseThrow(() -> {
                                        log.error("예약을 찾을 수 없음 - reservationId: {}", reservationId);
                                        return new InvalidReservationException("존재하지 않는 예약 입니다.");
                                });
                reservation.cancel();
                confirmNextWaitingReservation(reservation);
                log.info("예약 삭제 완료 - reservationId: {}", reservationId);
        }

        private void confirmNextWaitingReservation(Reservation reservation) {
                List<Member> targetMembers = memberRepository.findNextReserveMember(reservation.getDate(),
                                reservation.getReservationTime().getId(),
                                reservation.getTheme().getId(),
                                ReservationStatus.WAIT,
                                PageRequest.of(0, 1));
                if (targetMembers.isEmpty()) {
                        return;
                }
                Member member = targetMembers.getFirst();
                member.waitToReserve(reservation.getDate(), reservation.getReservationTime(), reservation.getTheme());
        }

        public List<MyReservationResponse> findAllReservationOfMember(Long memberId) {
                log.info("회원 예약 목록 조회 시작 - memberId: {}", memberId);

                Member member = memberRepository.findFetchById(memberId)
                                .orElseThrow(() -> {
                                        log.error("회원을 찾을 수 없음 - memberId: {}", memberId);
                                        return new InvalidMemberException("존재하지 않는 멤버 ID입니다.");
                                });
                List<Reservation> reservations = member.getReservations();
                List<Payment> payments = paymentRepository.findAllByReservations(reservations);

                Map<Reservation, Payment> reservationsWithPayment = payments.stream()
                                .collect(Collectors.toMap(Payment::getReservation, payment -> payment));

                List<ReservationDetail> details = member.calculateReservationRanks(reservations).stream()
                                .map(rank -> new ReservationDetail(
                                                rank.reservation(),
                                                reservationsWithPayment.get(rank.reservation()),
                                                rank.rank()))
                                .toList();

                List<MyReservationResponse> responses = details.stream()
                                .map(MyReservationResponse::from)
                                .toList();

                log.info("회원 예약 목록 조회 완료 - memberId: {}, 총 {}개", memberId, responses.size());
                return responses;
        }

        public List<Reservation> findAllByFilter(
                        Long memberId,
                        Long themeId,
                        LocalDate dateFrom,
                        LocalDate dateTo) {
                log.info("필터링된 예약 목록 조회 시작 - memberId: {}, themeId: {}, dateFrom: {}, dateTo: {}",
                                memberId, themeId, dateFrom, dateTo);

                List<Reservation> reservations = reservationRepository.findAllByFilter(memberId, themeId, dateFrom,
                                dateTo);

                log.info("필터링된 예약 목록 조회 완료 - 총 {}개", reservations.size());
                return reservations;
        }

        public List<ReservationWaitResponse> findAllByStatus(ReservationStatus status) {
                log.info("상태별 예약 목록 조회 시작 - status: {}", status);

                List<Reservation> waitReservations = reservationRepository.findAllFetchByStatus(status);
                List<ReservationWaitResponse> responses = waitReservations.stream()
                                .map(ReservationWaitResponse::from)
                                .toList();

                log.info("상태별 예약 목록 조회 완료 - status: {}, 총 {}개", status, responses.size());
                return responses;
        }
}

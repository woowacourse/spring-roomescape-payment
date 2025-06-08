package roomescape.application.reservation.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.member.query.dto.MemberResult;
import roomescape.application.payment.PaymentQueryService;
import roomescape.application.payment.PaymentResult;
import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.application.reservation.query.dto.ReservationSearchCondition;
import roomescape.application.reservation.query.dto.ReservationTimeResult;
import roomescape.application.reservation.query.dto.ReservationWithStatusAndPaymentResult;
import roomescape.application.reservation.query.dto.ThemeResult;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.TossPayment;
import roomescape.domain.payment.repository.TossPaymentRepository;
import roomescape.domain.reservation.PaymentType;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationQueryServiceTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationPaymentRepository reservationPaymentRepository;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private Clock clock;

    @Autowired
    private ReservationQueryService reservationQueryService;

    @Test
    void 전체_예약을_조회할_수_있다() {
        // given
        reservationRepository.deleteAll();

        final Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        final ReservationTime time1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        final ReservationTime time2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(14, 0)));
        final Reservation reservation1 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time1, theme));
        final Reservation reservation2 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time2, theme));

        // when
        final List<ReservationResult> reservationResults = reservationQueryService.findAll();

        // then
        assertThat(reservationResults)
                .isEqualTo(List.of(
                                new ReservationResult(
                                        reservation1.getId(),
                                        new MemberResult(member.getId(), "벨로"),
                                        LocalDate.now(clock),
                                        ReservationTimeResult.from(time1),
                                        new ThemeResult(theme.getId(), "테마", "설명", "이미지")
                                ),
                                new ReservationResult(
                                        reservation2.getId(),
                                        new MemberResult(member.getId(), "벨로"),
                                        LocalDate.now(clock),
                                        ReservationTimeResult.from(time2),
                                        new ThemeResult(theme.getId(), "테마", "설명", "이미지")
                                )
                        )
                );
    }

    @Test
    void 검색_조건에_맞는_예약을_조회할_수_있다() {
        // given
        final Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        final ReservationTime time1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        final ReservationTime time2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(14, 0)));
        final Reservation reservation1 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time1, theme)
        );
        final Reservation reservation2 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock).plusDays(1), time2, theme)
        );
        final ReservationSearchCondition reservationSearchCondition = new ReservationSearchCondition(
                theme.getId(),
                member.getId(),
                LocalDate.now(clock),
                LocalDate.now(clock)
        );

        // when
        final List<ReservationResult> reservationResults = reservationQueryService.findReservationsBy(
                reservationSearchCondition
        );

        // then
        assertThat(reservationResults)
                .isEqualTo(List.of(
                                new ReservationResult(
                                        reservation1.getId(),
                                        new MemberResult(member.getId(), "벨로"),
                                        LocalDate.now(clock),
                                        ReservationTimeResult.from(time1),
                                        new ThemeResult(theme.getId(), "테마", "설명", "이미지")
                                )
                        )
                );
    }

    @Test
    void 사용자의_예약을_모두_조회할_수_있다() {
        // given
        final Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        final ReservationTime time1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        final ReservationTime time2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(14, 0)));
        final ReservationTime time3 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(15, 0)));
        final ReservationTime time4 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(16, 0)));
        final Reservation reservation1 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time1, theme));
        final Reservation reservation2 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time2, theme));
        final Reservation reservation3 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time3, theme));
        final Reservation reservation4 = reservationRepository.save(
                new Reservation(member, LocalDate.now(clock), time4, theme));

        final TossPayment tossPayment1 = TossPayment.init("paymentKey1", "orderId1", 10000L);
        tossPayment1.approve();
        tossPaymentRepository.save(tossPayment1);

        final TossPayment tossPayment2 = TossPayment.init("paymentKey2", "orderId2", 10000L);
        // 기본값은 PENDING이다
        tossPaymentRepository.save(tossPayment2);

        final TossPayment tossPayment3 = TossPayment.init("paymentKey2", "orderId2", 10000L);
        tossPayment3.fail();
        tossPaymentRepository.save(tossPayment3);

        final ReservationPayment reservationPayment1 = reservationPaymentRepository.save(
                new ReservationPayment(
                        reservation1.getId(),
                        PaymentType.TOSS,
                        tossPayment1.getId()));
        final ReservationPayment reservationPayment2 = reservationPaymentRepository.save(
                new ReservationPayment(
                        reservation2.getId(),
                        PaymentType.TOSS,
                        tossPayment2.getId()));
        final ReservationPayment reservationPayment3 = reservationPaymentRepository.save(
                new ReservationPayment(
                        reservation3.getId(),
                        PaymentType.TOSS,
                        tossPayment3.getId()));

        final Long pkOfSomeAdmin = 10L;
        final ReservationPayment reservationPayment4 = reservationPaymentRepository.save(
                new ReservationPayment(
                        reservation4.getId(),
                        PaymentType.ADMIN,
                        pkOfSomeAdmin));

        // when
        final List<ReservationWithStatusAndPaymentResult> results = reservationQueryService.getReservationsWithStatusAndPayment(
                member.getId()
        );

        final List<Long> reservationIds = List.of(reservation1.getId(), reservation2.getId(), reservation3.getId(), reservation4.getId());
        final Map<Long, PaymentResult> paymentResultByReservationId =
                paymentQueryService.getAllPaymentResultsByReservationIds(reservationIds);

        // then
        assertThat(results)
                .isEqualTo(List.of(
                        new ReservationWithStatusAndPaymentResult(
                                reservation1.getId(),
                                "테마",
                                LocalDate.now(clock),
                                LocalTime.of(13, 0),
                                ReservationStatus.RESERVE,
                                paymentResultByReservationId.get(reservation1.getId()).paymentType(),
                                paymentResultByReservationId.get(reservation1.getId()).paymentKey(),
                                paymentResultByReservationId.get(reservation1.getId()).amount()
                        ),
                        new ReservationWithStatusAndPaymentResult(
                                reservation2.getId(),
                                "테마",
                                LocalDate.now(clock),
                                LocalTime.of(14, 0),
                                ReservationStatus.RESERVE,
                                paymentResultByReservationId.get(reservation2.getId()).paymentType(),
                                paymentResultByReservationId.get(reservation2.getId()).paymentKey(),
                                paymentResultByReservationId.get(reservation2.getId()).amount()
                        ),
                        new ReservationWithStatusAndPaymentResult(
                                reservation3.getId(),
                                "테마",
                                LocalDate.now(clock),
                                LocalTime.of(15, 0),
                                ReservationStatus.RESERVE,
                                paymentResultByReservationId.get(reservation3.getId()).paymentType(),
                                paymentResultByReservationId.get(reservation3.getId()).paymentKey(),
                                paymentResultByReservationId.get(reservation3.getId()).amount()
                        ),
                        new ReservationWithStatusAndPaymentResult(
                                reservation4.getId(),
                                "테마",
                                LocalDate.now(clock),
                                LocalTime.of(16, 0),
                                ReservationStatus.RESERVE,
                                paymentResultByReservationId.get(reservation4.getId()).paymentType(),
                                paymentResultByReservationId.get(reservation4.getId()).paymentKey(),
                                paymentResultByReservationId.get(reservation4.getId()).amount()
                        )
                ));
    }
}

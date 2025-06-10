package roomescape.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.JpaMemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.JpaPaymentRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.repository.time.JpaReservationTimeRepository;
import roomescape.reservation.repository.waiting.JpaWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
public class JpaWaitingRepositoryTest {

    @Autowired
    private JpaWaitingRepository jpaWaitingRepository;

    @Autowired
    private JpaMemberRepository jpaMemberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private JpaReservationTimeRepository jpaReservationTimeRepository;

    @Autowired
    private JpaPaymentRepository paymentRepository;

    @Test
    void 대기_저장() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment = new Payment(10000, "orderId", "paymentKey");
        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment);

        jpaMemberRepository.save(member);
        jpaReservationTimeRepository.save(reservationTime);
        themeRepository.save(theme);
        paymentRepository.save(payment);

        // when
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);

        // then
        Assertions.assertThat(jpaWaitingRepository.findAll()).containsExactly(savedWaiting);
    }

    @Test
    void 날짜와_테마와_시간이_같은_대기_존재_여부_확인() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment = new Payment(10000, "orderId", "paymentKey");
        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment);

        jpaMemberRepository.save(member);
        jpaReservationTimeRepository.save(reservationTime);
        themeRepository.save(theme);
        paymentRepository.save(payment);
        jpaWaitingRepository.save(waiting);

        // when
        final boolean exists = jpaWaitingRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);

        // then
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void 멤버를_기준으로_대기_찾기() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final Member member2 = new Member("엘리", "yebink@gmail.com", "1234", Role.ADMIN);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment1 = new Payment(10000, "orderId", "paymentKey");
        final Payment payment2 = new Payment(10000, "orderId", "paymentKey");

        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment1);
        final Waiting waiting2 = new Waiting(member2, reservationTime, theme, date, payment2);

        jpaMemberRepository.save(member);
        jpaMemberRepository.save(member2);
        jpaReservationTimeRepository.save(reservationTime);
        themeRepository.save(theme);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);
        jpaWaitingRepository.save(waiting2);

        // when
        final List<Waiting> foundWaiting = jpaWaitingRepository.findByMember(member);

        // then
        Assertions.assertThat(foundWaiting).containsOnly(savedWaiting);
    }

    @Test
    void 아이디_기준으로_대기_찾기() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment = new Payment(10000, "orderId", "paymentKey");

        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment);

        jpaMemberRepository.save(member);
        jpaReservationTimeRepository.save(reservationTime);
        themeRepository.save(theme);
        paymentRepository.save(payment);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);

        // when
        final Waiting foundWaiting = jpaWaitingRepository.findById(savedWaiting.getId()).orElseThrow();

        // then
        Assertions.assertThat(foundWaiting).isEqualTo(savedWaiting);
    }

    @Test
    void 테마와_날짜와_시간이_같은_대기를_찾는데_ID를_기준으로_첫번째_찾기() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final Member member2 = new Member("엘리", "yebink@gmail.com", "1234", Role.ADMIN);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment1 = new Payment(10000, "orderId", "paymentKey");
        final Payment payment2 = new Payment(10000, "orderId", "paymentKey");
        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment1);
        final Waiting waiting2 = new Waiting(member2, reservationTime, theme, date, payment2);

        jpaMemberRepository.save(member);
        jpaMemberRepository.save(member2);
        jpaReservationTimeRepository.save(reservationTime);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        themeRepository.save(theme);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);
        jpaWaitingRepository.save(waiting2);

        // when
        final Waiting foundWaiting = jpaWaitingRepository.findFirstByThemeAndDateAndTimeOrderByIdAsc(theme, date,
                        reservationTime)
                .orElseThrow();

        // then
        Assertions.assertThat(foundWaiting).isEqualTo(savedWaiting);
    }

    @Test
    void 아이디를_기준으로_대기_삭제() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment = new Payment(10000, "orderId", "paymentKey");

        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment);

        jpaMemberRepository.save(member);
        jpaReservationTimeRepository.save(reservationTime);
        themeRepository.save(theme);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);

        // when
        jpaWaitingRepository.deleteById(savedWaiting.getId());

        // then
        Assertions.assertThat(jpaWaitingRepository.findById(savedWaiting.getId())).isEmpty();
    }

    @Test
    void 테마와_날짜와_시간과_아이디보다_작은_것의_개수_세기() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final Member member2 = new Member("엘리", "yebink@gmail.com", "1234", Role.ADMIN);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);

        final Payment payment1 = new Payment(10000, "orderId", "paymentKey");
        final Payment payment2 = new Payment(10000, "orderId", "paymentKey");

        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment1);
        final Waiting waiting2 = new Waiting(member2, reservationTime, theme, date, payment2);

        jpaMemberRepository.save(member);
        jpaMemberRepository.save(member2);
        jpaReservationTimeRepository.save(reservationTime);
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        themeRepository.save(theme);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);
        final Waiting savedWaiting2 = jpaWaitingRepository.save(waiting2);

        // when
        final long count = jpaWaitingRepository.countBefore(theme, date, reservationTime, savedWaiting2.getId());

        // then
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    void 모든_대기_정보_찾기() {
        // given
        final Member member = new Member("우가", "wooga@email.com", "1234", Role.USER);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(20, 20));
        final Theme theme = new Theme("공포", "설명", "썸네일");
        final LocalDate date = LocalDate.of(2026, 10, 10);
        final Payment payment = new Payment(10000, "orderId", "paymentKey");

        final Waiting waiting = new Waiting(member, reservationTime, theme, date, payment);

        jpaMemberRepository.save(member);
        jpaReservationTimeRepository.save(reservationTime);
        paymentRepository.save(payment);
        themeRepository.save(theme);
        final Waiting savedWaiting = jpaWaitingRepository.save(waiting);

        // when
        final List<Waiting> allWaitings = jpaWaitingRepository.findAll();

        // then
        Assertions.assertThat(allWaitings).containsExactly(savedWaiting);
    }
}

package roomescape.startup;

import static roomescape.reservation.domain.Status.SUCCESS;
import static roomescape.reservation.domain.Status.WAIT;
import static roomescape.startup.DataFixture.ADMIN;
import static roomescape.startup.DataFixture.MEMBER_BRE;
import static roomescape.startup.DataFixture.MEMBER_BROWN;
import static roomescape.startup.DataFixture.MEMBER_GOOGOO;
import static roomescape.startup.DataFixture.MEMBER_JOJO;
import static roomescape.startup.DataFixture.MEMBER_LISA;
import static roomescape.startup.DataFixture.MEMBER_NEO;
import static roomescape.startup.DataFixture.MEMBER_POBI;
import static roomescape.startup.DataFixture.MEMBER_SOLAR;
import static roomescape.startup.DataFixture.MEMBER_TOMI;
import static roomescape.startup.DataFixture.THEME_ART_GALLERY;
import static roomescape.startup.DataFixture.THEME_AZTEC_TEMPLE;
import static roomescape.startup.DataFixture.THEME_BASEMENT;
import static roomescape.startup.DataFixture.THEME_DENTIST;
import static roomescape.startup.DataFixture.THEME_HORROR;
import static roomescape.startup.DataFixture.THEME_HORROR_THEME_PARK;
import static roomescape.startup.DataFixture.THEME_MONKEY;
import static roomescape.startup.DataFixture.THEME_NAGAYA_SANDA;
import static roomescape.startup.DataFixture.THEME_PRISON_BREAK;
import static roomescape.startup.DataFixture.THEME_SECRET_AGENT;
import static roomescape.startup.DataFixture.THEME_SF;
import static roomescape.startup.DataFixture.THEME_SPACE_STATION;
import static roomescape.startup.DataFixture.THEME_TITANIC;
import static roomescape.startup.DataFixture.THEME_VIRUS;
import static roomescape.startup.DataFixture.THEME_ZOMBIE;
import static roomescape.startup.DataFixture.TIME_10_00;
import static roomescape.startup.DataFixture.TIME_10_30;
import static roomescape.startup.DataFixture.TIME_11_00;
import static roomescape.startup.DataFixture.TIME_11_30;
import static roomescape.startup.DataFixture.TIME_12_00;
import static roomescape.startup.DataFixture.TIME_12_30;
import static roomescape.startup.DataFixture.TIME_13_00;
import static roomescape.startup.DataFixture.TIME_13_30;
import static roomescape.startup.DataFixture.TIME_14_00;
import static roomescape.startup.DataFixture.TIME_14_30;
import static roomescape.startup.DataFixture.TODAY;
import static roomescape.startup.DataFixture.TOMORROW;

import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationPaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Profile("!test")
@Component
public class DataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public DataLoader(
            MemberRepository memberRepository,
            ThemeRepository themeRepository,
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository, PaymentRepository paymentRepository,
            ReservationPaymentRepository reservationPaymentRepository
    ) {
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // member
        Member jojo = memberRepository.save(MEMBER_JOJO);
        Member admin = memberRepository.save(ADMIN);
        Member solar = memberRepository.save(MEMBER_SOLAR);
        Member brown = memberRepository.save(MEMBER_BROWN);
        Member neo = memberRepository.save(MEMBER_NEO);
        Member bre = memberRepository.save(MEMBER_BRE);
        Member pobi = memberRepository.save(MEMBER_POBI);
        Member googoo = memberRepository.save(MEMBER_GOOGOO);
        Member tomi = memberRepository.save(MEMBER_TOMI);
        Member risa = memberRepository.save(MEMBER_LISA);

        // theme
        Theme horror = themeRepository.save(THEME_HORROR);
        Theme sf = themeRepository.save(THEME_SF);
        Theme monkey = themeRepository.save(THEME_MONKEY);
        Theme zombie = themeRepository.save(THEME_ZOMBIE);
        Theme nagayaSanda = themeRepository.save(THEME_NAGAYA_SANDA);
        Theme horrorThemePark = themeRepository.save(THEME_HORROR_THEME_PARK);
        Theme basement = themeRepository.save(THEME_BASEMENT);
        Theme titanic = themeRepository.save(THEME_TITANIC);
        Theme artGallery = themeRepository.save(THEME_ART_GALLERY);
        Theme virus = themeRepository.save(THEME_VIRUS);
        Theme prisonBreak = themeRepository.save(THEME_PRISON_BREAK);
        Theme aztecTemple = themeRepository.save(THEME_AZTEC_TEMPLE);
        Theme spaceStation = themeRepository.save(THEME_SPACE_STATION);
        Theme dentist = themeRepository.save(THEME_DENTIST);
        Theme secretAgent = themeRepository.save(THEME_SECRET_AGENT);

        // reservationTime
        ReservationTime time10_00 = reservationTimeRepository.save(TIME_10_00);
        ReservationTime time10_30 = reservationTimeRepository.save(TIME_10_30);
        ReservationTime time11_00 = reservationTimeRepository.save(TIME_11_00);
        ReservationTime time11_30 = reservationTimeRepository.save(TIME_11_30);
        ReservationTime time12_00 = reservationTimeRepository.save(TIME_12_00);
        ReservationTime time12_30 = reservationTimeRepository.save(TIME_12_30);
        ReservationTime time13_00 = reservationTimeRepository.save(TIME_13_00);
        ReservationTime time13_30 = reservationTimeRepository.save(TIME_13_30);
        ReservationTime time14_00 = reservationTimeRepository.save(TIME_14_00);
        ReservationTime time14_30 = reservationTimeRepository.save(TIME_14_30);

        // payment
        Payment payment1 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment2 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment3 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment4 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment5 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment6 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment7 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment8 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment9 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment10 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment11 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment12 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment13 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment14 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment15 = paymentRepository.save(new Payment("paymentKey", 1000L));
        Payment payment16 = paymentRepository.save(new Payment("paymentKey", 1000L));

        // reservation
        Reservation reservation1 = reservationRepository.save(new Reservation(jojo, TODAY, horror, time10_00, SUCCESS));
        Reservation reservation2 = reservationRepository.save(new Reservation(solar, TODAY, horror, time10_00, WAIT));
        Reservation reservation3 = reservationRepository.save(new Reservation(brown, TODAY, sf, time12_00, SUCCESS));
        Reservation reservation4 = reservationRepository.save(new Reservation(jojo, TODAY, sf, time12_00, WAIT));
        Reservation reservation5 = reservationRepository.save(
                new Reservation(googoo, TOMORROW, zombie, time14_30, SUCCESS));
        Reservation reservation6 = reservationRepository.save(new Reservation(jojo, TOMORROW, zombie, time14_30, WAIT));
        Reservation reservation7 = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(2), monkey, time11_00, SUCCESS));
        Reservation reservation8 = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(5), nagayaSanda, time10_00, SUCCESS));
        Reservation reservation9 = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(7), virus, time12_30, SUCCESS));
        Reservation reservation10 = reservationRepository.save(
                new Reservation(neo, TODAY.plusDays(3), virus, time11_30, SUCCESS));
        Reservation reservation11 = reservationRepository.save(
                new Reservation(bre, TODAY.plusDays(2), horrorThemePark, time14_30, SUCCESS));
        Reservation reservation12 = reservationRepository.save(
                new Reservation(pobi, TODAY.plusDays(2), horror, time14_30, SUCCESS));
        Reservation reservation13 = reservationRepository.save(
                new Reservation(tomi, TODAY.plusDays(3), titanic, time10_30, SUCCESS));
        Reservation reservation14 = reservationRepository.save(
                new Reservation(risa, TODAY.plusDays(4), artGallery, time14_00, SUCCESS));
        Reservation reservation15 = reservationRepository.save(
                new Reservation(solar, TODAY.plusDays(2), horror, time14_30, WAIT));
        Reservation reservation16 = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(2), horror, time14_30, WAIT));

        // ReservationPayment
        List<ReservationPayment> reservationPayments = List.of(
                new ReservationPayment(reservation1, payment1),
                new ReservationPayment(reservation3, payment3),
                new ReservationPayment(reservation5, payment5),
                new ReservationPayment(reservation7, payment7),
                new ReservationPayment(reservation8, payment8),
                new ReservationPayment(reservation9, payment9),
                new ReservationPayment(reservation10, payment10),
                new ReservationPayment(reservation11, payment11),
                new ReservationPayment(reservation12, payment12),
                new ReservationPayment(reservation13, payment13),
                new ReservationPayment(reservation14, payment14)
        );
        reservationPaymentRepository.saveAll(reservationPayments);
    }
}

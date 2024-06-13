package roomescape.startup;

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
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.repository.WaitingRepository;

@Profile("!test")
@Component
public class DataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public DataLoader(
            MemberRepository memberRepository,
            ThemeRepository themeRepository,
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository,
            WaitingRepository waitingRepository,
            PaymentRepository paymentRepository
    ) {
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
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

        // reservation
        Reservation reservation_horror_jojo = reservationRepository.save(
                new Reservation(jojo, TODAY, horror, time10_00));
        Reservation reservation_sf_brown = reservationRepository.save(new Reservation(brown, TODAY, sf, time12_00));
        Reservation reservation_zombie_googoo = reservationRepository.save(
                new Reservation(googoo, TOMORROW, zombie, time14_30));
        Reservation reservation_monkey_jojo = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(2), monkey, time11_00));
        Reservation reservation_nagayaSanda_jojo = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(5), nagayaSanda, time10_00));
        Reservation reservation_virus_jojo = reservationRepository.save(
                new Reservation(jojo, TODAY.plusDays(7), virus, time12_30));
        Reservation reservation_virus_neo = reservationRepository.save(
                new Reservation(neo, TODAY.plusDays(3), virus, time11_30));
        Reservation reservation_horrorThemePark_bre = reservationRepository.save(
                new Reservation(bre, TODAY.plusDays(2), horrorThemePark, time14_30));
        Reservation reservation_horror_pobi = reservationRepository.save(
                new Reservation(pobi, TODAY.plusDays(2), horror, time14_30));
        Reservation reservation_titanic_tomi = reservationRepository.save(
                new Reservation(tomi, TODAY.plusDays(3), titanic, time10_30));
        Reservation reservation_artGallery_risa = reservationRepository.save(
                new Reservation(risa, TODAY.plusDays(4), artGallery, time14_00));

        // waiting
        Waiting waiting_horror_solar = waitingRepository.save(new Waiting(solar, TODAY, horror, time10_00));
        Waiting waiting_sf_jojo = waitingRepository.save(new Waiting(jojo, TODAY, sf, time12_00));
        Waiting waiting_zombie_pobi = waitingRepository.save(new Waiting(pobi, TOMORROW, zombie, time14_30));
        Waiting waiting_zombie_jojo = waitingRepository.save(new Waiting(jojo, TOMORROW, zombie, time14_30));
        Waiting waiting_horror_jojo = waitingRepository.save(new Waiting(jojo, TODAY.plusDays(2), horror, time14_30));

        // payment
        List<Payment> payments = List.of(
                new Payment("5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "MC4wODU4ODQwMzg4NDk0", 10000L,
                        reservation_horror_jojo),
                new Payment("4EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "kC4wODU4ODQwMzg4NDk0", 11000L,
                        reservation_sf_brown),
                new Payment("3EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "1C4wODU4ODQwMzg4NDk0", 10500L,
                        reservation_zombie_googoo),
                new Payment("2EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "8C4wODU4ODQwMzg4NDk0", 10000L,
                        reservation_monkey_jojo),
                new Payment("1EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "dC4wODU4ODQwMzg4NDk0", 10000L,
                        reservation_nagayaSanda_jojo),
                new Payment("6EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "zC4wODU4ODQwMzg4NDk0", 12000L,
                        reservation_virus_jojo),
                new Payment("7EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "wC4wODU4ODQwMzg4NDk0", 12000L,
                        reservation_virus_neo),
                new Payment("8EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "oC4wODU4ODQwMzg4NDk0", 11000L,
                        reservation_horrorThemePark_bre),
                new Payment("9EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "aC4wODU4ODQwMzg4NDk0", 10000L,
                        reservation_horror_pobi),
                new Payment("0EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "2w4wODU4ODQwMzg4NDk0", 10500L,
                        reservation_titanic_tomi),
                new Payment("22nNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "Ad4wODU4ODQwMzg4NDk0", 10500L,
                        reservation_artGallery_risa)
        );
        paymentRepository.saveAll(payments);
    }
}

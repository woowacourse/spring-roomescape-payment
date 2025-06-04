package roomescape;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.repository.jpa.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
@Profile("local")
public class TestDataInitializer {

    private static final String THUMBNAIL_URL = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg";

    private final ReservationJpaRepository reservationRepository;
    private final ReservationItemJpaRepository itemRepository;
    private final MemberJpaRepository memberRepository;
    private final ReservationTimeJpaRepository timeRepository;
    private final ReservationThemeJpaRepository themeJpaRepository;
    private final PaymentJpaRepository paymentRepository;

    @PostConstruct
    public void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Member user1 = new Member("user1@email.com", "1234", "user1", MemberRole.USER);
        Member user2 = new Member("user2@email.com", "1234", "user2", MemberRole.USER);
        Member admin = new Member("admin@email.com", "1234", "admin", MemberRole.ADMIN);
        memberRepository.saveAll(List.of(user1, user2, admin));

        ReservationTheme theme1 = new ReservationTheme("테마1", "설명", THUMBNAIL_URL);
        ReservationTheme theme2 = new ReservationTheme("테마2", "설명", THUMBNAIL_URL);
        List<ReservationTheme> themes = List.of(theme1, theme2);
        themeJpaRepository.saveAll(themes);

        ReservationTime time1 = new ReservationTime(LocalTime.of(13, 0));
        ReservationTime time2 = new ReservationTime(LocalTime.of(15, 0));
        List<ReservationTime> times = List.of(time1, time2);
        timeRepository.saveAll(times);

        LocalDate now = LocalDate.now();
        LocalDate past1 = now.minusDays(2);
        LocalDate past2 = now.minusDays(1);
        LocalDate future1 = now.plusDays(1);
        LocalDate future2 = now.plusDays(2);
        List<LocalDate> dateValues = List.of(past1, past2, now, future1, future2);
        List<ReservationItem> items = new ArrayList<>();
        for (ReservationTheme theme : themes) {
            for (ReservationTime time : times) {
                for (LocalDate dateValue : dateValues) {
                    // 날짜 검증을 피하기 위해 리플렉션 사용
                    ReservationItem item = createItemByReflection(theme, time, dateValue, items);
                    items.add(item);
                }
            }
        }
        itemRepository.saveAll(items);

        Reservation accepted1 = new Reservation(user1, items.get(0), ReservationStatus.ACCEPTED);
        Reservation accepted2 = new Reservation(user2, items.get(1), ReservationStatus.ACCEPTED);
        Reservation accepted3 = new Reservation(user1, items.get(2), ReservationStatus.ACCEPTED);
        Reservation accepted4 = new Reservation(user2, items.get(3), ReservationStatus.ACCEPTED);
        Reservation accepted5 = new Reservation(user1, items.get(4), ReservationStatus.ACCEPTED);
        Reservation accepted6 = new Reservation(user2, items.get(5), ReservationStatus.ACCEPTED);
        Reservation accepted7 = new Reservation(user1, items.get(6), ReservationStatus.ACCEPTED);
        Reservation accepted8 = new Reservation(user2, items.get(7), ReservationStatus.ACCEPTED);
        Reservation accepted9 = new Reservation(user1, items.get(8), ReservationStatus.ACCEPTED);
        Reservation accepted10 = new Reservation(user2, items.get(9), ReservationStatus.ACCEPTED);
        Reservation accepted11 = new Reservation(user1, items.get(10), ReservationStatus.ACCEPTED);
        Reservation accepted12 = new Reservation(user2, items.get(11), ReservationStatus.ACCEPTED);
        Reservation accepted13 = new Reservation(user1, items.get(12), ReservationStatus.ACCEPTED);
        Reservation accepted14 = new Reservation(user2, items.get(13), ReservationStatus.ACCEPTED);
        Reservation accepted15 = new Reservation(user1, items.get(14), ReservationStatus.ACCEPTED);
        Reservation accepted16 = new Reservation(user2, items.get(15), ReservationStatus.ACCEPTED);
        Reservation accepted17 = new Reservation(user1, items.get(16), ReservationStatus.ACCEPTED);
        Reservation accepted18 = new Reservation(user2, items.get(17), ReservationStatus.ACCEPTED);
        Reservation accepted19 = new Reservation(user1, items.get(18), ReservationStatus.ACCEPTED);
        Reservation accepted20 = new Reservation(user2, items.get(19), ReservationStatus.ACCEPTED);

        Reservation pending1 = new Reservation(user1, items.get(0), ReservationStatus.PENDING);
        Reservation pending2 = new Reservation(user2, items.get(2), ReservationStatus.PENDING);
        Reservation pending3 = new Reservation(user1, items.get(4), ReservationStatus.PENDING);
        Reservation pending4 = new Reservation(user2, items.get(6), ReservationStatus.PENDING);
        Reservation pending5 = new Reservation(user1, items.get(8), ReservationStatus.PENDING);
        Reservation pending6 = new Reservation(user2, items.get(10), ReservationStatus.PENDING);
        Reservation pending7 = new Reservation(user1, items.get(12), ReservationStatus.PENDING);
        Reservation pending8 = new Reservation(user2, items.get(14), ReservationStatus.PENDING);
        Reservation pending9 = new Reservation(user1, items.get(16), ReservationStatus.PENDING);
        Reservation pending10 = new Reservation(user2, items.get(18), ReservationStatus.PENDING);

        reservationRepository.saveAll(List.of(
                accepted1, accepted2, accepted3, accepted4, accepted5,
                accepted6, accepted7, accepted8, accepted9, accepted10,
                accepted11, accepted12, accepted13, accepted14, accepted15,
                accepted16, accepted17, accepted18, accepted19, accepted20,
                pending1, pending2, pending3, pending4, pending5,
                pending6, pending7, pending8, pending9, pending10
        ));

        Payment payment1 = new Payment("paymentKey1", 10000, accepted1);
        Payment payment2 = new Payment("paymentKey2", 15000, accepted2);
        Payment payment3 = new Payment("paymentKey3", 20000, accepted3);
        Payment payment4 = new Payment("paymentKey4", 25000, accepted4);
        Payment payment5 = new Payment("paymentKey5", 10000, accepted5);
        Payment payment6 = new Payment("paymentKey6", 15000, accepted6);
        Payment payment7 = new Payment("paymentKey7", 20000, accepted7);
        Payment payment8 = new Payment("paymentKey8", 25000, accepted8);
        Payment payment9 = new Payment("paymentKey9", 10000, accepted9);
        Payment payment10 = new Payment("paymentKey10", 15000, accepted10);
        Payment payment11 = new Payment("paymentKey11", 20000, accepted11);
        Payment payment12 = new Payment("paymentKey12", 25000, accepted12);
        Payment payment13 = new Payment("paymentKey13", 10000, accepted13);
        Payment payment14 = new Payment("paymentKey14", 15000, accepted14);
        Payment payment15 = new Payment("paymentKey15", 20000, accepted15);
        Payment payment16 = new Payment("paymentKey16", 25000, accepted16);
        Payment payment17 = new Payment("paymentKey17", 10000, accepted17);
        Payment payment18 = new Payment("paymentKey18", 15000, accepted18);
        Payment payment19 = new Payment("paymentKey19", 20000, accepted19);
        Payment payment20 = new Payment("paymentKey20", 25000, accepted20);
        paymentRepository.saveAll(List.of(
                payment1, payment2, payment3, payment4, payment5,
                payment6, payment7, payment8, payment9, payment10,
                payment11, payment12, payment13, payment14, payment15,
                payment16, payment17, payment18, payment19, payment20
        ));
    }

    private static ReservationItem createItemByReflection(final ReservationTheme theme, final ReservationTime time, final LocalDate dateValue, final List<ReservationItem> items) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        Constructor<ReservationItem> constructor = ReservationItem.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ReservationItem item = constructor.newInstance();

        Field dateField = ReservationItem.class.getDeclaredField("date");
        Field timeField = ReservationItem.class.getDeclaredField("time");
        Field themeField = ReservationItem.class.getDeclaredField("theme");

        dateField.setAccessible(true);
        timeField.setAccessible(true);
        themeField.setAccessible(true);

        dateField.set(item, dateValue);
        timeField.set(item, time);
        themeField.set(item, theme);

        return item;
    }
}

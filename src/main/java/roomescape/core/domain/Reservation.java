package roomescape.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
public class Reservation {
    protected static final String DATE_FORMAT_EXCEPTION_MESSAGE = "날짜 형식이 잘못되었습니다.";
    protected static final String PAST_DATE_EXCEPTION_MESSAGE = "지난 날짜에는 예약할 수 없습니다.";
    protected static final String PAST_TIME_EXCEPTION_MESSAGE = "지난 시간에는 예약할 수 없습니다.";
    private static final String TIME_ZONE = "Asia/Seoul";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "예약 상태는 비어있을 수 없습니다.")
    @Column(nullable = false)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(final Member member, final String date, final ReservationTime time, final Theme theme,
                       final ReservationStatus status) {
        this(null, member, date, time, theme, status);
    }

    public Reservation(final Long id, final Member member, final String date, final ReservationTime time,
                       final Theme theme, final ReservationStatus status) {
        this.id = id;
        this.member = member;
        this.date = parseDate(date);
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    private LocalDate parseDate(final String date) {
        try {
            return LocalDate.parse(date);
        } catch (final DateTimeParseException e) {
            throw new IllegalArgumentException(DATE_FORMAT_EXCEPTION_MESSAGE);
        }
    }

    public void validateDateAndTime() {
        if (isDatePast()) {
            throw new IllegalArgumentException(PAST_DATE_EXCEPTION_MESSAGE);
        }
        if (isDateToday() && time.isPast()) {
            throw new IllegalArgumentException(PAST_TIME_EXCEPTION_MESSAGE);
        }
    }

    private boolean isDatePast() {
        final ZoneId kst = ZoneId.of(TIME_ZONE);
        return date.isBefore(LocalDate.now(kst));
    }

    private boolean isDateToday() {
        final ZoneId kst = ZoneId.of(TIME_ZONE);
        return date.isEqual(LocalDate.now(kst));
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDateString() {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    public ReservationTime getReservationTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}

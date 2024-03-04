package roomescape.waiting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import roomescape.theme.Theme;

@Entity
public class Waiting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Theme theme;
    private Long memberId;
    private String date;
    private String time;

    public Waiting() {
    }

    public Waiting(Theme theme, Long memberId, String date, String time) {
        this.theme = theme;
        this.memberId = memberId;
        this.date = date;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isOwner(Long memberId) {
        return this.memberId.equals(memberId);
    }
}

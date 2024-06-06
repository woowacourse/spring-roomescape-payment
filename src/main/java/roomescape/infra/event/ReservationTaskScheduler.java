package roomescape.infra.event;

import java.time.Instant;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ReservationTaskScheduler {
    private final TaskScheduler taskScheduler;

    public ReservationTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("reservation-scheduler-");
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    public void schedule(Runnable task, Instant startTime) {
        taskScheduler.schedule(task, startTime);
    }
}

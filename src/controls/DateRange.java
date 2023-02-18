package controls;

import java.time.LocalDateTime;

public class DateRange {

    public LocalDateTime start, stop;
    String taskKey;

    public DateRange(LocalDateTime start, LocalDateTime stop, String taskKey) {
        if (stop.isBefore(start)) {
            throw new IllegalArgumentException("The stop date is before the start date.");
        }
        this.start = start;
        this.stop = stop;
        this.taskKey = taskKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DateRange dateRange = (DateRange) o;

        if (!start.equals(dateRange.start)) {
            return false;
        }
        if (!stop.equals(dateRange.stop)) {
            return false;
        }
        return taskKey.equals(dateRange.taskKey);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + stop.hashCode();
        result = 31 * result + taskKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DateRange{ " + "start=" + start + ", stop=" + stop + " }";
    }

}

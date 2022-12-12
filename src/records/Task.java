package records;

public class Task extends AbstractTask {
    public Task(String recordTitle, String recordDescription, String recordId, String recordStatus) {
        super(recordTitle, recordDescription, recordId, recordStatus);
    }

    @Override
    public String toString() {
        return "Task{" + super.toString();
    }
}

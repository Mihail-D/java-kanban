package records;

public class SubTask extends AbstractTask {
    int subTaskId;

    public SubTask(String recordTitle, String recordDescription, String recordId, String recordStatus, int subTaskId) {
        super(recordTitle, recordDescription, recordId, recordStatus);
        this.subTaskId = subTaskId;
    }
}

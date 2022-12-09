package records;

public class SubTask extends AbstractTask {
    private String subTaskId;

    public SubTask(String recordTitle, String recordDescription, String recordId, String recordStatus, String subTaskId) {
        super(recordTitle, recordDescription, recordId, recordStatus);
        this.subTaskId = subTaskId;
    }

    public String getSubTaskId() {
        return subTaskId;
    }
    @Override
    public String toString() {
        return "SubTask{subTaskId=" + subTaskId + " " + super.toString();
    }
}


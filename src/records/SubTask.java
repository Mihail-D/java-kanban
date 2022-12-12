package records;

public class SubTask extends AbstractTask {
    private String subTaskId;

    public SubTask(
            String recordTitle, String recordDescription, String parentId, String recordStatus,
            String subTaskId
    ) {
        super(recordTitle, recordDescription, parentId, recordStatus);
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


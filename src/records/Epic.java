package records;

import java.util.HashMap;

public class Epic extends AbstractTask {

    public HashMap<String, String> relatedSubTask;

    public Epic(
            String recordTitle, String recordDescription, String recordId, String recordStatus,
            HashMap<String, String> relatedSubTask
    ) {
        super(recordTitle, recordDescription, recordId, recordStatus);
        this.relatedSubTask = relatedSubTask;
    }
    @Override
    public String toString() {
        return "Epic{ relatedSubTask" + relatedSubTask + " " + super.toString();
    }
}

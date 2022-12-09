package records;

public class Epic extends AbstractTask{

    public Epic(String recordTitle, String recordDescription, String recordId, String recordStatus) {
        super(recordTitle, recordDescription, recordId, recordStatus);
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString();
    }
}

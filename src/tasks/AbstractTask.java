package tasks;

import java.util.Objects;

public abstract class AbstractTask {
    String recordTitle;
    private String recordDescription;
    private String recordId;
    private String recordStatus;

    public AbstractTask(String recordTitle, String recordDescription, String recordId, String recordStatus) {
        this.recordTitle = recordTitle;
        this.recordDescription = recordDescription;
        this.recordId = recordId;
        this.recordStatus = recordStatus;
    }

    public String getRecordTitle() {
        return recordTitle;
    }
    public String getRecordDescription() {
        return recordDescription;
    }
    public String getRecordId() {
        return recordId;
    }
    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordTitle(String recordTitle) {
        this.recordTitle = recordTitle;
    }
    public void setRecordDescription(String recordDescription) {
        this.recordDescription = recordDescription;
    }
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractTask that = (AbstractTask) o;

        if (!Objects.equals(getRecordId(), that.getRecordId())) {
            return false;
        }
        if (!getRecordTitle().equals(that.getRecordTitle())) {
            return false;
        }
        if (!getRecordDescription().equals(that.getRecordDescription())) {
            return false;
        }
        return getRecordStatus().equals(that.getRecordStatus());
    }
    @Override
    public int hashCode() {
        int result = getRecordTitle().hashCode();
        result = 31 * result + getRecordDescription().hashCode();
        result = 31 * result + getRecordId().hashCode();
        result = 31 * result + getRecordStatus().hashCode();
        return result;
    }
    @Override
    public String toString() {
        return "Title='" + recordTitle + '\'' +
                ", recordDescription='" + recordDescription + '\'' +
                ", recordId=" + recordId +
                ", recordStatus='" + recordStatus + '\'' +
                '}';
    }
}

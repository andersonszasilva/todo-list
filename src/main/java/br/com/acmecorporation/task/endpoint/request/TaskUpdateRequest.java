package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.task.domain.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class TaskUpdateRequest {

    private String summary;
    private String description;
    private LocalDateTime updateDate = LocalDateTime.now();

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Task merge(Task task) {
        task.setSummary(this.summary);
        task.setDescription(this.description);
        task.setUpdateDate(LocalDateTime.now());
        return task;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

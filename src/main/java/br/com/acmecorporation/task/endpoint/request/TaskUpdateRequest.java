package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.task.domain.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class TaskUpdateRequest {

    private String summary;
    private String description;

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public Task buildTask(Task task) {
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

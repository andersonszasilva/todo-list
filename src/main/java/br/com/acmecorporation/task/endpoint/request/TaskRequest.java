package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.user.domain.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class TaskRequest {

    private String summary;
    private String description;

    public Task create(User user) {
       return new Task(user, LocalDateTime.now(), this.summary, this.description, TaskStatus.PENDING, null);
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}

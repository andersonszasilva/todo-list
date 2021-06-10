package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.user.domain.User;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.task.domain.Task;

import java.time.LocalDateTime;

public class TaskRequet {

    private String summary;
    private String description;
    private LocalDateTime createDate = LocalDateTime.now();

    public Task create(User user) {
       return new Task(user, createDate, summary, description, TaskStatus.PENDING, null);
    }


    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

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

}

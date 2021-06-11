package br.com.acmecorporation.task.endpoint.response;

import br.com.acmecorporation.task.domain.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskResponse {

    private final Long id;
    private final String summary;
    private final String description;
    private final String status;
    private final LocalDateTime createDate;

    protected TaskResponse(Task task) {
        this.id = task.getId();
        this.summary = task.getSummary();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        this.createDate = task.getCreateDate();
    }

    public Long getId() { return id; }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public static TaskResponse createResponse(Task task) {
        return new TaskResponse(task);
    }

    public static List<TaskResponse> createListResponse(List<Task> tasks) {
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }


}

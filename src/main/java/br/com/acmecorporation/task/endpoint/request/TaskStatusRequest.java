package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.task.domain.TaskStatus;

public class TaskStatusRequest {

    private StatusRequest status;

    public StatusRequest getStatus() {
        return status;
    }

    public TaskStatus convertTaskStatus() {
        return TaskStatus.valueOf(this.status.name());
    }
}

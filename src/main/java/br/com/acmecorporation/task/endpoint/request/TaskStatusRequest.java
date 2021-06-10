package br.com.acmecorporation.task.endpoint.request;

import br.com.acmecorporation.task.domain.TaskStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TaskStatusRequest {

    PENDING("PENDING"),
    COMPLETED("COMPLETED");

    private String status;

    TaskStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static TaskStatusRequest forValue(String status) {
        return Arrays.stream(TaskStatusRequest.values())
                .filter(value -> value.status.equals(status))
                .findFirst().orElse(null);
    }

    public TaskStatus convertTaskStatus() {
        return TaskStatus.valueOf(this.name());
    }


}

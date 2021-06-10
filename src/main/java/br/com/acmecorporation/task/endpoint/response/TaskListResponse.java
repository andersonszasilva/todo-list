package br.com.acmecorporation.task.endpoint.response;

import br.com.acmecorporation.task.domain.Task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListResponse {

    private List<TaskResponse> tasks;

    public TaskListResponse(List<Task> tasks) {
        this.tasks = tasks.stream()
               .map(TaskResponse::new)
               .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}

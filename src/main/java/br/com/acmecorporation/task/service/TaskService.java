package br.com.acmecorporation.task.service;

import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.task.exception.TaskNotFoundException;
import br.com.acmecorporation.task.repository.TaskRepository;
import br.com.acmecorporation.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAllBy(User user, TaskStatus status) {
        return taskRepository.findAllByUserAndStatusInOrderByStatusDesc(user, getStatus(status));
    }

    public List<Task> findAllBy(TaskStatus status) {
        return taskRepository.findAllByStatusInOrderByStatusDesc(getStatus(status));
    }

    private List<TaskStatus> getStatus(TaskStatus status) {
        return status == null ? Arrays.asList(TaskStatus.values()) : Arrays.asList(status);
    }

    public Optional<Task> findBy(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }

    public Task changeTaskStatus(Long id, TaskStatus status, User user) throws TaskNotFoundException {
        Optional<Task> taskToComplete = findBy(id, user);

        if(taskToComplete.isEmpty()) throw new TaskNotFoundException("Tarefa não encontrada");

        Task task = taskToComplete.get();
        task.setStatus(status);
        return save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }


}

package br.com.acmecorporation.task.repository;

import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUserAndStatusInOrderByStatusDesc(User user, List<TaskStatus> status);
    List<Task> findAllByStatusInOrderByStatusDesc(List<TaskStatus> status);
    Optional<Task> findByIdAndUser(Long id, User user);
}

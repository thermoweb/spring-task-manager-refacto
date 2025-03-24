package org.thermoweb.tasks.modular.domain.task;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;
import org.thermoweb.tasks.model.TaskStatus;
import org.thermoweb.tasks.modular.domain.sharedkernel.Anemic;

import lombok.Getter;

@AggregateRoot
@Anemic
@Getter
public class Task {
    @Identity
    private final TaskId id;
    private String taskName;
    private TaskStatus taskStatus;
    private String description;
    private Assignee assignee;

    public Task(TaskId id, String taskName, TaskStatus taskStatus, String description, Assignee assignee) {
        this.id = id;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.description = description;
        this.assignee = assignee;
    }

    @ValueObject
    public record TaskId(String value) {}
}

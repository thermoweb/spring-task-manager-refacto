package org.thermoweb.tasks.model;

import java.util.Arrays;
import java.util.List;

public enum TaskStatus {
    COMPLETED,
    CANCELLED,
    IN_PROGRESS(COMPLETED, CANCELLED),
    TODO(IN_PROGRESS, CANCELLED),
    OPEN(TODO, CANCELLED);

    private final List<TaskStatus> availableNextStatus;

    TaskStatus(TaskStatus... availableNextStatus) {
        this.availableNextStatus = Arrays.stream(availableNextStatus).toList();
    }

    public boolean isAuthorizedNewStatus(TaskStatus newStatus) {
        if (availableNextStatus.isEmpty()) {
            return false;
        }
        return availableNextStatus.contains(newStatus);
    }
}

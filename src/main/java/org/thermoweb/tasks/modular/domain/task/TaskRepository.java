package org.thermoweb.tasks.modular.domain.task;

import java.util.Optional;

import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface TaskRepository {
    Optional<Task> findById(String id);
}

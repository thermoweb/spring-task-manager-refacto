package org.thermoweb.tasks.modular.domain.task;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;
import org.jmolecules.ddd.annotation.ValueObject;
import org.thermoweb.tasks.modular.domain.sharedkernel.Anemic;

import lombok.Getter;

@Entity
@Anemic
@Getter
public class Assignee {
    @Identity
    private final UserId id;
    private String name;

    public Assignee(UserId id, String name) {
        this.id = id;
        this.name = name;
    }

    @ValueObject
    public record UserId(Long value) {
    }

}

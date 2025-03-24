package org.thermoweb.tasks.modular.domain.sharedkernel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an {@link org.jmolecules.ddd.annotation.Entity} that is anemic. That's to say this entity only holds data,
 * and it is missing business behaviors, invariants etc.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Anemic {
}

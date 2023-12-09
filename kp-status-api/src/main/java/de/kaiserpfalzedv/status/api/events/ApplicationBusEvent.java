package de.kaiserpfalzedv.status.api.events;

import de.kaiserpfalzedv.status.api.model.HasId;

/**
 * The central BaseEvent interface that has to be implemented by all events on the {@link ApplicationEventBus}.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-09
 */
public interface ApplicationBusEvent<A> extends HasId {
    /** @return The source of the event. */
    A getSource();
}
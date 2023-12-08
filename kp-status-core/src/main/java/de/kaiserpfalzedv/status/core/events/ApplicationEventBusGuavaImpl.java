/*
 * Copyright (c) 2023 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.kaiserpfalzedv.status.core.events;



import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.kaiserpfalzedv.status.api.events.ApplicationEventBus;
import de.kaiserpfalzedv.status.api.events.BaseEvent;
import de.kaiserpfalzedv.status.api.events.applicationeventbusevents.ApplicationEventBusCloses;
import de.kaiserpfalzedv.status.api.events.applicationeventbusevents.ListenerRegisteredEvent;
import de.kaiserpfalzedv.status.api.events.applicationeventbusevents.ListenerUnregisteredEvent;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-08
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class ApplicationEventBusGuavaImpl implements ApplicationEventBus, Closeable {
    /** The event bus used. */
    private final EventBus bus;

    private final List<Object> registered = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.info("Created application event bus. bus={}", bus);
        register(this);
    }

    @Override
    public ApplicationEventBus register(Object listener) {
        if (registered.contains(listener)) {
            log.warn("Listener to application event bus already registered. bus={}, listener={}", bus, listener);
        } else {
            bus.register(listener);
            registered.add(registered);
        }

        post(ListenerRegisteredEvent.builder()
                .listener(listener)
                .build());
        return this;
    }

    @Override
    public ApplicationEventBus unregister(Object listener) {
            post(ListenerUnregisteredEvent.builder()
                    .listener(listener)
                    .build());

        if (registered.contains(listener)) {
            bus.unregister(listener);
            registered.remove(listener);
        } else {
            log.warn("Listener is not registered for application event bus. Can't unregister. bus={}, listener={}", bus, listener);
        }

        return this;
    }

    @Override
    public <E extends BaseEvent<?>> ApplicationEventBus post(E event) {
        log.debug("Posting event to application event bus: bus={}, event={}", bus, event);
        bus.post(event);
        log.trace("Event has been posted to application event bus: bus={}, event={}", bus, event);

        return this;
    }

    @Override
    public void close() {
        log.debug("Unregistering all listeners from event bus. bus={}, listeners={}", bus, registered);
        post(ApplicationEventBusCloses.builder()
            .source(this)
            .build());
        
        registered.stream()
            .forEach(bus::unregister);

        log.info("Closed application event bus. bus={}", bus);
    }


    @Subscribe
    public void registerListener(@NotNull final ListenerRegisteredEvent event) {
        log.trace("Registered listener to application event bus. bus={}, listener={}", bus, event.getListener());
    }

    @Subscribe
    public void unregisterListener(@NotNull final ListenerUnregisteredEvent event) {
        log.trace("Unregistering listener from application event bus. bus={}, listener={}", bus, event.getListener());
    }
}

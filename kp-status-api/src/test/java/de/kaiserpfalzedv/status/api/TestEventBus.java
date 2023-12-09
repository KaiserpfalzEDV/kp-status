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
package de.kaiserpfalzedv.status.api;



import com.google.common.eventbus.EventBus;

import de.kaiserpfalzedv.status.api.events.ApplicationBusEvent;
import de.kaiserpfalzedv.status.api.events.ApplicationEventBus;
import de.kaiserpfalzedv.status.api.events.applicationeventbusevents.ListenerRegisteredEvent;
import de.kaiserpfalzedv.status.api.events.applicationeventbusevents.ListenerUnregisteredEvent;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-03
 */
public class TestEventBus implements ApplicationEventBus {
    private static final EventBus bus = new EventBus();

    @Override
    public ApplicationEventBus register(Object listener) {
        bus.register(listener);
        bus.post(ListenerRegisteredEvent.builder().source(this).listener(listener).build());
        return this;
    }

    @Override
    public ApplicationEventBus unregister(Object listener) {
        bus.post(ListenerUnregisteredEvent.builder().source(this).listener(listener).build());
        bus.unregister(listener);
        return this;
    }

    @Override
    public <E extends ApplicationBusEvent<?>> ApplicationEventBus post(E event) {
        bus.post(event);
        return this;
    }
}

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
package de.kaiserpfalzedv.status.api.events;

/**
 * The application event bus.
 *
 * Helps objects to communicated loosely coupled.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-08
 */
public interface ApplicationEventBus {
    /**
     * Registers a listener to the event bus.
     * 
     * @param listener the listener to register to the bus.
     * @return The application event bus itself.
     */
    ApplicationEventBus register(Object listener);

    /**
     * Unregisters a listener from the event bus.
     * 
     * @param listener the listener to unregister from the bus.
     * @return The application event bus itself.
     */
    ApplicationEventBus unregister(Object listener);
    
    /**
     * Posts an event to the event bus.
     * 
     * @param event The event to be sent.
     * @return The application event bus itself.
     */
    <E extends BaseEvent<?>> ApplicationEventBus post(E event);
}

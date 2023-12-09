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
package de.kaiserpfalzedv.status.api.model.state;

import java.util.Set;

import de.kaiserpfalzedv.status.api.events.ApplicationBusEvent;
import de.kaiserpfalzedv.status.api.model.HasDuration;
import de.kaiserpfalzedv.status.api.model.degradation.Degradation;
import jakarta.validation.constraints.NotNull;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
public interface State extends HasDuration, HasState, ApplicationBusEvent<State> {
    /** @return The degragation or multiple degradations which are the base reason(s) to this state. */
    public Set<Degradation> getDegradation();
    
    /** @return The new state after a new failing degradation. */
    public State fail(@NotNull final Degradation degradation);

    /** @return The new state after the given degradation is recovered. */
    public State recover(@NotNull final Degradation degradation);
}

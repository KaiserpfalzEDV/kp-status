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



import java.util.UUID;

import de.kaiserpfalzedv.status.api.model.HasId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;


/**
 * An event base for all events.
 * 
 * @param <A> The source type of this base event.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-08
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseEvent<A> implements HasId {
    /** The ID of this event. */
    @Default
    @ToString.Include
    @EqualsAndHashCode.Include
    private final UUID id = UUID.randomUUID();

    /** The source of this event. */
    @NotNull
    @NonNull
    private final A source;
}

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
package de.kaiserpfalzedv.status.model.state;



import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import ch.qos.logback.core.util.Duration;
import de.kaiserpfalzedv.status.model.service.Service;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
public abstract class StateBase implements State {
    @ToString.Include
    @Default
    @NotNull
    private final OffsetDateTime start = OffsetDateTime.now(ZoneOffset.UTC);
    @ToString.Include
    @Nullable
    private final OffsetDateTime end;

    @ToString.Include
    @NotNull
    private final Service service;


    @Override
    public Optional<OffsetDateTime> getEnd() {
        return Optional.ofNullable(end);
    }

    @Override
    public Duration getDuration() {
        final OffsetDateTime current = end != null ? end : OffsetDateTime.now(ZoneOffset.UTC);

        long millis = current.toInstant().toEpochMilli() - start.toInstant().toEpochMilli();
        return Duration.buildByMilliseconds(millis);
    }

    @Override
    public boolean isSubServiceDown() {
        return service.getSubServices().stream().anyMatch(s -> s.isDown());
    }

    @Override
    public boolean isDependencyDown() {
        return service.getDependencies().stream().anyMatch(s -> s.isDown());
    }
}

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



import java.util.HashSet;
import java.util.Set;

import de.kaiserpfalzedv.status.model.service.Degradation;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = true)
@Slf4j
public class Down extends StateBase {
    @NotNull
    @Getter(AccessLevel.NONE)
    private final Degradation degradation;

    @Override
    public Set<Degradation> getDegradation() {
        return Set.of(degradation);
    }

    @Override
    public boolean isServiceDown() {
        return true;
    }    

    @Override
    public State fail(@NotNull final Degradation degradation) {
        if (degradation.equals(degradation)) {
            log.warn("Degradatation is already known. state={}, degradation={}", this, degradation);
            return this;
        }

        // TODO 2023-11-19 klenkes74 Add notification of change state via bus.

        return MultiDown.builder()
                .service(getService())
                .degradations(new HashSet<>(Set.of(this.degradation, degradation)))
                .build();
    }

    @Override
    public State recover(@NotNull final Degradation degradation) {
        if (! getDegradation().contains(degradation)) {
            log.warn("Degradation is not known. state={}, degradation={}", this, degradation);
            return this;
        }

        // TODO 2023-11-19 klenkes74 Add notification of change state via bus.

        log.debug("Service state is recovering.");
        return Up.builder()
                .service(getService())
                .build();
    }
}

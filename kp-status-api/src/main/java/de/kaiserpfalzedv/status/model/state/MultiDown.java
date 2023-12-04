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

import de.kaiserpfalzedv.status.degradation.Degradation;
import jakarta.validation.constraints.NotEmpty;
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
public class MultiDown extends Down {
    @NotNull @NotEmpty
    @Getter(AccessLevel.NONE)
    private final HashSet<Degradation> degradations;

    @Override
    public Set<Degradation> getDegradation() {
        return new HashSet<>(degradations);
    }

    @Override
    public boolean isServiceDown() {
        return true;
    }    

    @Override
    public State fail(@NotNull final Degradation degradation) {
        if (getDegradation().contains(degradation)) {
            log.warn("Degradation is already known. state={}, degradation={}", this, degradation);
            return this;
        }

        HashSet<Degradation> all = new HashSet<>(getDegradation());
        all.add(degradation);

        return MultiDown.builder()
            .service(getService())
            .degradations(all)
            .build();
    }

    @Override
    public State recover(@NotNull final Degradation degradation) {
        if (! getDegradation().contains(degradation)) {
            log.warn("Degradation is not known. state={}, degradation={}", this, degradation);
            return this;
        }

        HashSet<Degradation> all = new HashSet<>(degradations);
        all.remove(degradation);
            
        if (all.size() > 1) {
            return toBuilder()
                .degradations(all)
                .build();
        } else {
            return Down.builder()
                .service(getService())
                .degradation(all.toArray(new Degradation[0])[0])
                .build();
        }
    }
}

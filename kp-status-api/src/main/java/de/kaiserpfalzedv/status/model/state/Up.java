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


import java.util.Set;

import de.kaiserpfalzedv.status.degradation.Degradation;
import de.kaiserpfalzedv.status.model.service.Service;
import jakarta.validation.constraints.NotNull;
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
public class Up extends StateBase {
    @Override
    public boolean isServiceDown() {
        return false;
    }


    @Override
    public Set<Degradation> getDegradation() {
        return Set.of();
    }    


    @Override
    public State fail(@NotNull final Degradation degradation) {
        log.debug("Service state is transitioning to failing. Degradation is: {}", degradation);

        return Down.builder()
                .service(getService())
                .degradation(degradation)
                .build();
    }

    @Override
    public State recover(@NotNull final Degradation degradation) {
        log.debug("Service is already up. No change.");
        return this;
    }

    public State initService(final Service service) {
        return toBuilder().service(service).build();
    }
}

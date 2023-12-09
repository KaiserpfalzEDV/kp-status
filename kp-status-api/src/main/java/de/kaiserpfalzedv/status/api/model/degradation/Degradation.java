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
package de.kaiserpfalzedv.status.api.model.degradation;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.kaiserpfalzedv.status.api.events.ApplicationEventBus;
import de.kaiserpfalzedv.status.api.model.HasMetadata;
import de.kaiserpfalzedv.status.api.model.Metadata;
import de.kaiserpfalzedv.status.api.model.service.HasService;
import de.kaiserpfalzedv.status.api.model.service.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
@Jacksonized
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Degradation implements HasMetadata, HasService, AutoCloseable {
    @EqualsAndHashCode.Include
    @ToString.Include
    @Default
    private final Metadata metadata = Metadata.builder().build();

    @NotNull
    @NonNull
    private final Service service;

    @NotEmpty
    @NonNull
    private final String description;

    @Default
    private final List<DegradationHistory> history = new ArrayList<>();


    /**
     * Adds a new note to the degradation.
     * 
     * @param note the new note that will be added to the history.
     * @return The degradation itself.
     */
    public Degradation addNote(@NotEmpty final String note) {
        DegradationHistory entry = DegradationHistory.builder()
                .description(note)
                .build();
        List<DegradationHistory> entries = new ArrayList<>(history);
        entries.add(entry);

        return toBuilder().history(entries).build();
    }


    /** The communication bus */
    @Autowired
    @JsonIgnore
    @Setter
    @Getter(AccessLevel.NONE)
    private ApplicationEventBus bus;

    @PostConstruct
    public Degradation init() {
        bus.register(this);
        return this;
    }

    @PreDestroy
    @Override
    public void close() {
        bus.unregister(this);
    }
}

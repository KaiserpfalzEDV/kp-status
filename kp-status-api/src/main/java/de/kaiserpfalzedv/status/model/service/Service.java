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
package de.kaiserpfalzedv.status.model.service;

import java.util.HashSet;
import java.util.UUID;

import de.kaiserpfalzedv.status.model.Metadata;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * 
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
public class Service {
    /** The metadata of this service. */
    @NotNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Metadata metadata;

    /** The services composing this service. */
    @Default
    private final HashSet<Service> subServices = new HashSet<>();

    /** The other services this service depends on. */
    @Default
    private final HashSet<Service> dependencies = new HashSet<>();

    /** The current service state. */
    @ToString.Include
    private final ServiceState state;


    /** The ID of the service. */
    public UUID getId() {
        return metadata.getId();
    }

    /** The namespace of this service. */
    public String getNamespace() {
        return metadata.getNamespace();
    }

    /** The name of this service. */
    public String getName() {
        return metadata.getName();
    }

    /** If this service is deleted. */
    public boolean isDeleted() {
        return metadata.isDeleted();
    }

    public boolean isDown() {
        return state.isDown();
    }

    public Service fail(final Degradation degradation) {
        return toBuilder().fail(degradation).build();
    }

    public Service recover() {
        return toBuilder().recover().build();
    }

    /** 
     * The builder that will be augmented by lombok.
     */
    public static class ServiceBuilder {
        private Metadata metadata = Metadata.builder().build();
        private ServiceState state = ServiceStateUp.builder().build();

        public ServiceBuilder metadata(@NotNull final Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public ServiceBuilder id(@NotNull final UUID id) {
            metadata = metadata.toBuilder().id(id).modify().build();
            return this;
        }

        public ServiceBuilder namespace(@NotNull final String name) {
            metadata = metadata.toBuilder().namespace(name).modify().build();
            return this;
        }

        public ServiceBuilder name(@NotNull final String name) {
            metadata = metadata.toBuilder().name(name).modify().build();
            return this;
        }

        public ServiceBuilder fail(@NotNull Degradation degradation) {
            state = state.fail(degradation);
            return this;
        }

        public ServiceBuilder recover() {
            state = state.recover();
            return this;
        }

        public ServiceBuilder delete() {
            metadata = metadata.toBuilder().delete().build();
            return this;
        }

        public ServiceBuilder undelete() {
            metadata = metadata.toBuilder().undelete().build();
            return this;
        }
    }
}

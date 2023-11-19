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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.kaiserpfalzedv.status.model.HasMetadata;
import de.kaiserpfalzedv.status.model.Metadata;
import de.kaiserpfalzedv.status.model.state.HasState;
import de.kaiserpfalzedv.status.model.state.State;
import de.kaiserpfalzedv.status.model.state.Up;
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
public class Service implements HasMetadata, HasState {
    /** The metadata of this service. */
    @NotNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Metadata metadata;

    /** The services composing this service. */
    @Default
    private final Set<Service> subServices = new HashSet<>();

    /** The other services this service depends on. */
    @Default
    private final Set<Service> dependencies = new HashSet<>();

    /** The current service state. */
    @ToString.Include
    private final State state;


    public void fail(@NotNull final Degradation degradation) {
        state.fail(degradation);
    }

    public void recover(@NotNull final Degradation degradation) {
        state.recover(degradation);
    }

    @Override
    public boolean isServiceDown() {
        return state.isDown();
    }

    @Override
    public boolean isSubServiceDown() {
        return state.isSubServiceDown();
    }

    @Override
    public boolean isDependencyDown() {
        return state.isDependencyDown();
    }

    public Service addSubService(final Service subService) {
        Set<Service> subservices = new HashSet<>(getSubServices());
        subservices.add(subService);

        return toBuilder().subServices(subservices).build();
    }

    public Service addDependencies(final Service dependency) {
        Set<Service> dependencies = new HashSet<>(getDependencies());
        dependencies.add(dependency);

        return toBuilder().dependencies(dependencies).build();
    }

    /** 
     * The builder that will be augmented by lombok.
     */
    public static class ServiceBuilder {
        private Metadata metadata = Metadata.builder().build();
        private State state = Up.builder().build();
        private HashSet<Service> subServices = new HashSet<>();
        private HashSet<Service> dependencies = new HashSet<>();

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

        public ServiceBuilder recover(@NotNull Degradation degradation) {
            state = state.recover(degradation);
            return this;
        }

        public ServiceBuilder subServices(@NotNull final Collection<? extends Service> services) {
            this.subServices.clear();
            this.subServices.addAll(services);
            this.dependencies.removeAll(services);
            return this;
        }

        public ServiceBuilder addSubService(@NotNull final Service service) {
            this.subServices.add(service);
            this.dependencies.remove(service);
            return this;
        }

        public ServiceBuilder dependencies(@NotNull final Collection<? extends Service> services) {
            this.dependencies.clear();
            services.removeAll(subServices);
            this.dependencies.addAll(services);
            return this;
        }

        public ServiceBuilder addDependency(@NotNull final Service service) {
            if (!this.subServices.contains(service))
                this.dependencies.add(service);
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

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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.eventbus.EventBus;

import de.kaiserpfalzedv.status.degradation.Degradation;
import de.kaiserpfalzedv.status.model.HasDuration;
import de.kaiserpfalzedv.status.model.HasMetadata;
import de.kaiserpfalzedv.status.model.Metadata;
import de.kaiserpfalzedv.status.model.state.HasState;
import de.kaiserpfalzedv.status.model.state.State;
import de.kaiserpfalzedv.status.model.state.Up;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * 
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
@Configurable
@Jacksonized
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Service implements HasMetadata, HasState, HasDuration, AutoCloseable {
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
    @Setter
    private State state;


    /** The communication bus */
    @Autowired
    @JsonIgnore
    @Setter
    @Getter(AccessLevel.NONE)
    private EventBus bus;

    
    @PostConstruct
    public Service init() {
        bus.register(this);
        return this;
    }

    @PreDestroy
    @Override
    public void close() {
        bus.unregister(this);
    }


    public Service fail(@NotNull final Degradation degradation) {
        state = state.fail(degradation);
        bus.post(state);
        return this;
    }

    public Service recover(@NotNull final Degradation degradation) {
        state = state.recover(degradation);
        bus.post(state);
        return this;
    }

    public Service initState() {
        state = Up.builder()
                .service(this)
                .build();
        bus.post(state);
        return this;
    }



    @Override
    public boolean isServiceDown() {
        return ! (state instanceof Up);
    }

    @Override
    public boolean isSubServiceDown() {
        return subServices.stream()
            .anyMatch(s -> { return s.isDown(); });
    }

    @Override
    public boolean isDependencyDown() {
        return dependencies.stream()
            .anyMatch(s -> { return s.isDown(); });
    }

    public Service addSubService(final Service subService) {
        subServices.add(subService);

        return this;
    }

    public Service addDependencies(final Service dependency) {
        dependencies.add(dependency);

        return this;
    }


    /**
     * The last state change.
     * 
     * @return The time of the last state change.
     */
    @Override
    public OffsetDateTime getStart() {
        return state.getStart();
    }

    /**
     * The duration since last state change.
     * 
     * @return The duration since the last state change.
     */
    @Override
    public Duration getDuration() {
        return state.getDuration();
    }


    /** 
     * The builder that will be augmented by lombok.
     */
    public static class ServiceBuilder {
        private Metadata metadata = Metadata.builder()
                .build();
        private State state;
        private HashSet<Service> subServices = new HashSet<>();
        private HashSet<Service> dependencies = new HashSet<>();
        private EventBus bus;

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

        public ServiceBuilder bus(EventBus bus) {
            this.bus = bus;
            return this;
        }
    }
}

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
package de.kaiserpfalzedv.status.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private final Metadata metadata;
    

    public static class ServiceBuilder {
        private Metadata metadata = Metadata.builder().build();

        public ServiceBuilder id(@NotNull final UUID id) {
            metadata = metadata.toBuilder().id(id).build();
            return this;
        }

        public ServiceBuilder created(@NotNull final OffsetDateTime date) {
            metadata = metadata.toBuilder().created(date).build();
            return this;
        }

        public ServiceBuilder modified(@NotNull final OffsetDateTime date) {
            metadata = metadata.toBuilder().modified(date).build();
            return this;
        }

        public ServiceBuilder deleted(@NotNull final OffsetDateTime date) {
            metadata = metadata.toBuilder().deleted(date).build();
            return this;
        }

        public ServiceBuilder undelete() {
            metadata = metadata.toBuilder().undelete().build();
            return this;
        }
    }
}

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
package de.kaiserpfalzedv.status.api.model;

import java.util.UUID;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
public interface HasMetadata {
    public Metadata getMetadata();

    /** The ID of the service. */
    default UUID getId() {
        return getMetadata().getId();
    }

    /** The namespace of this service. */
    default String getNamespace() {
        return getMetadata().getNamespace();
    }

    /** The name of this service. */
    default String getName() {
        return getMetadata().getName();
    }

    /** If this service is deleted. */
    default boolean isDeleted() {
        return getMetadata().isDeleted();
    }

}

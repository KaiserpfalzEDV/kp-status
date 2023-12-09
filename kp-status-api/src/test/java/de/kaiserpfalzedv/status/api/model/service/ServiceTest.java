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
package de.kaiserpfalzedv.status.api.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.kaiserpfalzedv.status.api.TestEventBus;
import de.kaiserpfalzedv.status.api.events.ApplicationEventBus;
import de.kaiserpfalzedv.status.api.model.Metadata;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
@Slf4j
public class ServiceTest {
    private final ApplicationEventBus bus = new TestEventBus();
    
    private final OffsetDateTime DEFAULT_START = OffsetDateTime.now();
    private final Service DEFAULT = Service.builder()
            .bus(bus)
            .build()
        .initState();


    @Test
    public void shouldCreateAValidService() {
        final Service result = Service.builder().build();

        assert result != null;

        assertEquals(result.getName(), result.getId().toString());
    }

    @Test
    public void shouldChangeIdWhenMetadataIsChanged() {
        final Metadata metadata = Metadata.builder().build();

        final Service result = DEFAULT.toBuilder().metadata(metadata).build();

        assert result != null;

        assertEquals(metadata.getId(), result.getId());
    }

    @Test
    public void shouldChangeIdWhenIdIsChanged() {
        final UUID id = UUID.randomUUID();

        final Service result = DEFAULT.toBuilder().id(id).build();

        assert result != null;

        assertEquals(id, result.getId());
        assertEquals(id.toString(), result.getName());
        assertEquals(DEFAULT.getNamespace(), result.getNamespace());
    }

    @Test
    public void shouldChangeNameWhenNameIsChanged() {
        final String name = "Name";

        final Service result = DEFAULT.toBuilder().name(name).build();

        assert result != null;

        assertEquals(DEFAULT.getId(), result.getId());
        assertEquals(name, result.getName());
    }

    @Test
    public void shouldChangeNamespaceWhenNamespaceIsChanged() {
        final String name = "Name";

        final Service result = DEFAULT.toBuilder().namespace(name).build();

        assert result != null;

        assertEquals(DEFAULT.getId(), result.getId());
        assertEquals(name, result.getNamespace());
    }

    @Test
    public void shouldDeleteServiceWhenDeletedDateIsSet() {
        final Service result = DEFAULT.toBuilder().delete().build();

        assert result != null;

        assertTrue(result.isDeleted());
    }

    @Test
    public void shouldHaveNoDeletedServiceWhenServiceIsUndeletedSet() {
        final Service orig = DEFAULT.toBuilder().delete().build();

        assert orig.isDeleted();

        final Service result = orig.toBuilder().undelete().build();

        assert result != null;

        assertFalse(result.isDeleted());
    }

    @Test
    public void shouldReturnTheCorrectDuration() {
        final OffsetDateTime post = OffsetDateTime.now();

        final Duration result = DEFAULT.getDuration();
        log.trace("Duration of current state: {}", result);

        assertEquals(-1, Duration.between(post, DEFAULT_START).compareTo(result));
    }
}

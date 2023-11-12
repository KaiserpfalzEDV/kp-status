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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
public class MetadatenTest {

    @Test
    public void shouldCreateADefaultMetadataWithCurrentDateWhenNoParametersAreGiven() {
        final OffsetDateTime pre = OffsetDateTime.now(ZoneOffset.UTC);
        final Metadata result = Metadata.builder().build();
        final OffsetDateTime post = OffsetDateTime.now(ZoneOffset.UTC);

        assert result != null;

        assertTrue(result.getCreated().isAfter(pre));
        assertTrue(result.getCreated().isBefore(post));
        assertTrue(result.getModified().isAfter(pre));
        assertTrue(result.getModified().isBefore(post));
        assertEquals(result.getId().toString(), result.getName());
        assertEquals(Metadata.DEFAULT_NAMESPACE, result.getNamespace());
        assertNull(result.getDeleted());
    }

    @Test
    public void shouldCreateAValidMetadataWhenACreationDateIsGiven() {
        final OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
        final Metadata result = Metadata.builder().created(created).build();

        assert result != null;

        assertEquals(created, result.getCreated());
        assertEquals(created, result.getModified());
        assertNull(result.getDeleted());
    }

    @Test
    public void shouldCreateAValidMetadataWhenACreationAndModificationDateIsGiven() {
        final OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC).minusHours(1L);
        final OffsetDateTime modified = OffsetDateTime.now(ZoneOffset.UTC);
        final Metadata result = Metadata.builder()
                .created(created)
                .modified(modified)
                .build();

        assert result != null;

        assertEquals(created, result.getCreated());
        assertEquals(modified, result.getModified());
        assertNull(result.getDeleted());
    }

    @Test
    public void shouldUndeleteMetadataWhenUndeleteIsGiven() {
        final Metadata orig = Metadata.builder().delete().build();

        final OffsetDateTime pre = OffsetDateTime.now(ZoneOffset.UTC);
        final Metadata result = orig.toBuilder().undelete().build();
        final OffsetDateTime post = OffsetDateTime.now(ZoneOffset.UTC);

        assertFalse(result.isDeleted());
        assertTrue(result.getModified().isAfter(pre));
        assertTrue(result.getModified().isBefore(post));
    }

    @Test
    public void shouldModifyObjectWhenDataIsChanged() {
        final Metadata orig = Metadata.builder().build();

        final OffsetDateTime pre = OffsetDateTime.now(ZoneOffset.UTC);
        final Metadata result = orig.toBuilder().modify().build();
        final OffsetDateTime post = OffsetDateTime.now(ZoneOffset.UTC);

        assertTrue(result.getModified().isAfter(pre));
        assertTrue(result.getModified().isBefore(post));
    }

    @Test
    public void shouldCreateAValidMetadataWhenAnUuidIsGiven() {
        final UUID id = UUID.randomUUID();
        final Metadata result = Metadata.builder()
                .id(id)
                .build();

        assert result != null;

        assertEquals(id, result.getId());
    }

    @Test
    public void shouldFailWhenModificationDateIsBeforeCreationDate() {
        final OffsetDateTime creation = OffsetDateTime.now(ZoneOffset.UTC);
        final OffsetDateTime modification = creation.minusSeconds(5L);

        Assertions.assertThrows(IllegalStateException.class, () -> {
                Metadata.builder()
                        .created(creation)
                        .modified(modification)
                        .build();
        });

        Assertions.assertThrows(IllegalStateException.class, () -> {
                Metadata.builder()
                        .modified(modification)
                        .created(creation)
                        .build();
        });        
    }

    @Test
    public void shouldFailWhenDeletionDateIsBeforeCreationDate() {
        final OffsetDateTime creation = OffsetDateTime.now(ZoneOffset.UTC);
        final OffsetDateTime deletion = creation.minusSeconds(5L);

        Assertions.assertThrows(IllegalStateException.class, () -> {
                Metadata.builder()
                        .created(creation)
                        .deleted(deletion)
                        .build();
        });
        
        Assertions.assertThrows(IllegalStateException.class, () -> {
                Metadata.builder()
                        .deleted(deletion)
                        .created(creation)
                        .build();
        });
    }

    @Test
    public void shouldCreateAValidMetadataWhenANameIsGiven() {
        final Metadata result = Metadata.builder()
                .name("Test")
                .build();

        assert result != null;

        assertEquals("Test", result.getName());
    }

    @Test
    public void shouldCreateAValidMetadataWhenANamespaceIsGiven() {
        final Metadata result = Metadata.builder()
                .namespace("Test")
                .build();

        assert result != null;

        assertEquals("Test", result.getNamespace());
    }

    @Test
    public void shouldChangeDefaultNameWhenIdIsChanged() {
        final UUID id = UUID.randomUUID();
        final Metadata orig = Metadata.builder().build();
        final Metadata result = orig.toBuilder().id(id).build();

        assert result != null;

        assertEquals(id.toString(), result.getName());
    }

    @Test
    public void shouldNotChangeNameWhenIdIsChangedButANameIsSet() {
        final String name = "Test";
        final Metadata orig = Metadata.builder().name(name).build();
        final Metadata result = orig.toBuilder().id(UUID.randomUUID()).build();

        assert result != null;

        assertEquals(name, result.getName());
    }

    @Test
    public void shouldHaveTheCorretNamespaceWhenANamespaceIsDefined() {
        final Metadata result = Metadata.builder().namespace("Test").build();

        assert result != null;

        assertEquals("Test", result.getNamespace());
    }

    @Test
    public void shouldGetDefaultNamespaceWhenNamespaceIsRemoved() {
        final Metadata orig = Metadata.builder().namespace("Test").build();
        final Metadata result = orig.toBuilder().defaultNamespace().build();

        assert result != null;

        assertEquals(Metadata.DEFAULT_NAMESPACE, result.getNamespace());
    }
}

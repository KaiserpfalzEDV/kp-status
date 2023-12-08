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


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Builder.Default;
import lombok.extern.jackson.Jacksonized;


/**
 * Metadata for any object.
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
public class Metadata implements HasId {
    /** Default namespace if no namespace is set. */
    public static final String DEFAULT_NAMESPACE = "default";


    /** The ID of the object. */
    @Default
    @NotNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id = UUID.randomUUID();

    /** The namespace of this object. */
    @NotNull
    @ToString.Include
    private final String namespace;

    /** The name of the object. Defaults to a string representation of the ID. */
    @NotNull
    @ToString.Include
    private final String name;

    /** The creation timestamp of the object. */
    @NotNull
    @ToString.Include
    private final OffsetDateTime created;

    /** The last modification of the object. */
    @NotNull
    private final OffsetDateTime modified;

    /** The deletion timestamp of the object. */
    @Null
    private final OffsetDateTime deleted;

    /**
     * @return true if the object is deleted.
     */
    public boolean isDeleted() {
        return deleted != null;
    }


    /** 
     * The builder that will be augmentend by lombok.
     *
     * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
     * @version 1.0.0
     * @since 2023-11-12
     */
    public static class MetadataBuilder {
        private UUID id = UUID.randomUUID();
        private String name = id.toString();
        private String namespace = DEFAULT_NAMESPACE;

        private OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
        private OffsetDateTime modified = created;
        private OffsetDateTime deleted;

        /**
         * Sets the id of the object.
         * @param id the ID of the object.
         * @return the builder.
         */
        public MetadataBuilder id(@NotNull final UUID id) {
            if (name.equals(this.id.toString())) {
                this.name = id.toString();
            }
            this.id = id;
            return this;
        }

        /**
         * Sets the namespace of the object.
         * @param namespace The namespace of the object.
         * @return the builder.
         */
        public MetadataBuilder namespace(@NotNull final String namespace) {
            this.namespace = namespace;
            return this;
        }

        /**
         * Removes the namespace from the object and sets the default namespace ({@link Metadata#DEFAULT_NAMESPACE},
         * {@value Metadata#DEFAULT_NAMESPACE}). 
         * @return the builder.
         */
        public MetadataBuilder defaultNamespace() {
            this.namespace = DEFAULT_NAMESPACE;
            return this;
        }

        /**
         * Sets the name of the object.
         * @param name the name of the object.
         * @return the builder.
         */
        public MetadataBuilder name(@NotNull final String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the creation date.
         * @param date The new creation date.
         * @return the builder
         * @throws IllegalStateException when the date is after modification or deletion date.
         */
        public MetadataBuilder created(@NotNull final OffsetDateTime date) {
            checkCreationIsBeforeModification(date, modified, "Creation date '%s' can't be after modification date '%s'!");
            checkCreationIsBeforeModification(date, deleted, "Creation date '%s' can't be after deletion date '%s'!");

            if (modified.isEqual(created)) {
                modified = date;
            }
            created = date;

            return this;
        }

        private void checkCreationIsBeforeModification(
            final OffsetDateTime created, 
            final OffsetDateTime modified,
            final String messageFormat
        ) {
            if (modified != null && created != null && created.isAfter(modified)) {
                throw new IllegalStateException(
                    String.format(
                        messageFormat,
                        created.toString(), modified.toString()
                    )
                );
            }
        }

        /**
         * Marks the object as modified (sets the timestamp).
         * @return the builder
         */
        public MetadataBuilder modify() {
            return modified(OffsetDateTime.now(ZoneOffset.UTC));
        }

        /**
         * Sets the modification date.
         * @param date The modification date
         * @return the builder
         * @throws IllegalStateException When the modification date is before the creation date or after the deletion date.
         */
        public MetadataBuilder modified(@NotNull final OffsetDateTime date) {
            checkCreationIsBeforeModification(created, date, "Modification date '%s' has to be after creation date '%s'!");
            checkCreationIsBeforeModification(date, deleted, "Modification date '%s' can't be after deletion date '%s'!");
            modified = date;

            return this;
        }

        public MetadataBuilder delete() {
            return deleted(OffsetDateTime.now(ZoneOffset.UTC));
        }

        /**
         * Sets the deletion date.
         * @param date The deletion date.
         * @return the builder
         * @throws IllegalStateException When the deletion date is before the modification or creation date.
         */
        public MetadataBuilder deleted(@NotNull final OffsetDateTime date) {
            checkCreationIsBeforeModification(created, date, "The object can't be deleted (%s) before it is created (%s).");
            checkCreationIsBeforeModification(modified, date, "The object can't be deleted (%s) before it is modified (%s).");
            deleted = date;
            return this;
        }

        /**
         * Undeletes the object and sets the modification timestamp to now.
         * @return the builder.
         */
        public MetadataBuilder undelete() {
            deleted = null;
            modified = OffsetDateTime.now(ZoneOffset.UTC);
            return this;
        }
    }
}

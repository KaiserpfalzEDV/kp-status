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
package de.kaiserpfalzedv.status.api.model.state;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.kaiserpfalzedv.status.api.model.Metadata;
import de.kaiserpfalzedv.status.api.model.service.Service;
import de.kaiserpfalzedv.status.api.model.degradation.Degradation;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
public class ServiceStateTransitionTest {
    private static final Service SERVICE = Service.builder()
            .metadata(Metadata.builder().build())
            .build();
    private static final Degradation DEGRADATION = Degradation.builder()
            .metadata(Metadata.builder().build())
            .description("Leeres Beispiel")
            .build();
    private static final Degradation ANOTHER_DEGRADATION = Degradation.builder()
            .metadata(Metadata.builder().build())
            .description("Noch ein Beispiel")
            .build();
    private static final State UP = Up.builder()
            .service(SERVICE)
            .build();
    private static final State DOWN = Down.builder()
            .service(SERVICE)
            .degradation(DEGRADATION)
            .build();
    private static final State MULTI_DOWN = MultiDown.builder()
            .service(SERVICE)
            .degradations(new HashSet<>(Set.of(DEGRADATION, ANOTHER_DEGRADATION)))
            .build();


    @Test
    public void shouldBeUpIfServiceIsNotFailing() {
        assert UP.isDown() == false;

        final State result = UP.recover(DEGRADATION);

        assert result != null;

        assertFalse(result.isDown());
    }

    @Test
    public void shouldBeDownIfServiceIsAlreadyFailingWithSameDegradation() {
        final State result = DOWN.fail(DEGRADATION);

        assert result != null;

        assertTrue(result.isDown());
    }

    @Test
    public void shouldBeMultiDownIfServiceIsAlreadyFailingWithAnotherDegradation() {
        final State result = DOWN.fail(ANOTHER_DEGRADATION);

        assert result != null;

        assertTrue(result.isDown());
    }

    @Test
    public void shouldBeDownIfSecondLastDegradationIsRemoved() {
        final State result = MULTI_DOWN.recover(ANOTHER_DEGRADATION);

        assert result != null;

        assertArrayEquals(DOWN.getDegradation().toArray(), result.getDegradation().toArray());
    }

    @Test
    public void shouldBeUpWhenLastDegradationIsRecovered() {
        final State result = DOWN.recover(DEGRADATION);

        assert result != null;

        assertFalse(result.isDown());
    }
}

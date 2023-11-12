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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-11-12
 */
public class ServiceStateTransitionTest {
    private static final Service DEFAULT = Service.builder().build();


    @Test
    public void shouldDoNothingWhenStateIsAlreadyUp() {
        final Service result = DEFAULT.toBuilder().recover().build();

        assert result != null;

        assertFalse(result.isDown());
    }

    @Test
    public void shouldDoNothingWhenStateIsAlreadyDown() {
        final Degradation degradation = Degradation.builder().build();
        final Service orig = DEFAULT.fail(degradation);

        assert orig.isDown() == true;

        final Service result = orig.toBuilder().fail(degradation).build();

        assert result != null;

        assertTrue(result.isDown());
    }

    @Test
    public void shouldBeDownWhenStateHasBeenUpAndIsFailing() {
        final Degradation degradation = Degradation.builder().build();
        final Service result = DEFAULT.fail(degradation);

        assert result != null;

        assertTrue(result.isDown());
    }
    @Test
    public void shoudBeUpWhenItHasBeenDownAndStateIsRecovered() {
        final Degradation degradation = Degradation.builder().build();
        final Service orig = DEFAULT.fail(degradation);

        assert orig.isDown() == true;

        final Service result = orig.toBuilder().recover().build();

        assert result != null;

        assertFalse(result.isDown());
    }
}

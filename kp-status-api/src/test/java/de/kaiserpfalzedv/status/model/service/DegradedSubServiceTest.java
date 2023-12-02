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
public class DegradedSubServiceTest {
    private static final Service SUBSERVICE =Service.builder()
            .build()
        .initState()
        .fail(Degradation.builder()
                .description("failure").build())
        ;
    private static final Service SERVICE = Service.builder()
            .build()
        .initState()
        .addSubService(SUBSERVICE);


    @Test
    public void shouldBeDownWhenASubServiceIsDown() {
        assert SERVICE.getState().isServiceDown() == false;
        
        assertTrue(SERVICE.isDown());
        assertFalse(SERVICE.isDependencyDown());
        assertFalse(SERVICE.isServiceDown());
        assertTrue(SERVICE.isSubServiceDown());
    }
}

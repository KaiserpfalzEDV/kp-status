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



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.Subscribe;

import de.kaiserpfalzedv.status.api.TestEventBus;
import de.kaiserpfalzedv.status.api.events.ApplicationEventBus;
import de.kaiserpfalzedv.status.api.model.degradation.Degradation;
import de.kaiserpfalzedv.status.api.model.state.Down;
import de.kaiserpfalzedv.status.api.model.state.State;
import de.kaiserpfalzedv.status.api.model.state.Up;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-03
 */
@Slf4j
public class ServiceCommunicationTest {
    private final ApplicationEventBus bus = new TestEventBus();

    private final Service DEFAULT = Service.builder()
            .bus(bus)
            .build()
        .initState();

    @Test
    public void shouldSendAndEventWhenServiceFails() {
        BusListener listener = new BusListener(bus);
        DEFAULT.init();

        final String degregationReason = UUID.randomUUID().toString();

        DEFAULT.fail(
            Degradation.builder()
                .description(degregationReason)
                .service(DEFAULT)
                .build()
        );

        listener.close();
        DEFAULT.close();

        assertTrue(listener.getEvents().stream()
                .anyMatch(ds -> {
                        return ds.getDegradation().stream()
                            .anyMatch(d -> { return d.getDescription().equals(degregationReason); 
                        });
                })
        );
    }

    @Test
    public void shouldSendAndEventWhenServiceRecovers() {
        BusListener listener = new BusListener(bus);
        
        final String degregationReason = UUID.randomUUID().toString();

        DEFAULT.init()
        .fail(
            Degradation.builder()
                .description(degregationReason)
                .service(DEFAULT)
                .build()
        );

        Degradation degradation = DEFAULT.getState().getDegradation().stream().findFirst().orElseThrow(IllegalStateException::new);

        DEFAULT.recover(degradation);

        listener.close();
        DEFAULT.close();

        assertTrue(listener.getEvents().stream()
                .anyMatch(ds -> {
                        return ds.getDegradation().stream()
                            .anyMatch(d -> { return d.getDescription().equals(degregationReason); 
                        });
                })
        );
    }

    class BusListener implements AutoCloseable {
        private final ApplicationEventBus bus;

        @Getter
        private final List<State> events = new ArrayList<>();


        public BusListener(final ApplicationEventBus bus) {
            this.bus = bus;
            this.bus.register(this);
        }

        @Override
        public void close() {
            bus.unregister(this);
        }

        @Subscribe
        public void readFail(final Down event) {
            log.trace("Received: {}", event);

            events.add(event);
        }

        @Subscribe
        public void readRecover(final Up event) {
            log.trace("Received: {}", event);

            events.add(event);
        }
    }
}

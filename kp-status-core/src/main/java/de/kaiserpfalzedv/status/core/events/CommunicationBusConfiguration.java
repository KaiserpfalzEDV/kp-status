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
package de.kaiserpfalzedv.status.core.events;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;


/**
 * Provides the event bus to use for communication.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2023-12-03
 */
@Configuration
@Slf4j
public class CommunicationBusConfiguration {
    /** The event bus to use. */
    private EventBus applicationEventBus;

    /**
     * Initializes the event bus.
     */
    @PostConstruct
    public void init() {
        applicationEventBus = new EventBus("application");

        log.debug("Created application event bus. bus={}", applicationEventBus);
    }

    /**
     * @return the application event bus of the system.
     */
    @Bean
    public EventBus applicationEventBus() {
        return applicationEventBus;
    }
}

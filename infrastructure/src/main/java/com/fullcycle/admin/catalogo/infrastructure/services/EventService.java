package com.fullcycle.admin.catalogo.infrastructure.services;

import com.fullcycle.admin.catalogo.domain.events.DomainEvent;

public interface EventService {

    void send(Object event);
}

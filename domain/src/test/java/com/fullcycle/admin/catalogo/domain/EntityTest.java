package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.events.DomainEvent;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EntityTest extends UnitTest{

    @Test
    public void givenNullAsEvents_whenInstantiate_shouldBeOk() {

        final List<DomainEvent> events = null;
        final var entity = new DummyEntity(new DummyID(), events);

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    public void givenDomainEvents_whenPassInConstructor_shouldCreateADefensiveClone() {

        final List<DomainEvent> events = new ArrayList<>();
        events.add(new DomainEvent() {
            @Override
            public Instant occurredOn() {
                return null;
            }
        });

        final var entity = new DummyEntity(new DummyID(), events);

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(1, entity.getDomainEvents().size());
        Assertions.assertThrows(RuntimeException.class, () -> {
            final var actualEvents = entity.getDomainEvents();
            actualEvents.add((DomainEvent) () -> null);
        });
    }

    @Test
    public void givenEmptyDomainEvents_whenCallsRegisterEvent_shouldAddEventToList() {

        final var expectedEvents = 1;

        final var entity = new DummyEntity(new DummyID(), new ArrayList<>());

        entity.registerEvent(new DummyEvent());

        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, entity.getDomainEvents().size());
    }

    @Test
    public void givenAFewDomainEvents_whenCallsPublishEvents_shouldCallPublisherAnClearTheList() {

        final var expectedEvents = 0;
        final var entity = new DummyEntity(new DummyID(), new ArrayList<>());
        entity.registerEvent(new DummyEvent());
        entity.registerEvent(new DummyEvent());

        Assertions.assertEquals(2, entity.getDomainEvents().size());

        entity.publishDomainEvents(event -> {
        });


        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, entity.getDomainEvents().size());
    }

    public static class DummyEvent implements DomainEvent {

        @Override
        public Instant occurredOn() {
            return InstantUtils.now();
        }
    }

    public static class DummyID extends Identifier {

        private final String id;

        public DummyID() {
            this.id = IdUtils.uuid();
        }

        @Override
        public String getValue() {
            return this.id;
        }
    }

    public static class DummyEntity extends Entity<DummyID> {

        public DummyEntity(DummyID dummyID, List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        protected DummyEntity() {
            this(new DummyID(), null);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }

}

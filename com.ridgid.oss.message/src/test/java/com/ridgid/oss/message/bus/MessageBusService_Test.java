package com.ridgid.oss.message.bus;

//import com.ridgid.oss.message.bus.test.MessageBusMock;

import com.ridgid.oss.message.bus.impl.inmemory.InMemoryMessageBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("LocalVariableOfConcreteClass")
class MessageBusService_Test
{
    private static final String COM_RIDGID_OSS_MESSAGE_BUS_SERVICE_CLASS = "com.ridgid.oss.message.bus.service.class";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void it_looks_up_an_intance_when_no_configuration_is_given() {
        assertNotNull(MessageBusService.instance(),
                      "Unable to locate MessageBus service");
    }

    @Test
    void it_gives_the_same_singleton_instance_on_each_call_to_instance() {
        MessageBusService firstInstance = MessageBusService.instance();
        for ( int i = 0; i < 100; i++ )
              assertSame(firstInstance,
                         MessageBusService.instance(),
                         "Second instance different from first - should be a singleton");
    }

    @SuppressWarnings("MessageMissingOnJUnitAssertion")
    @Test
    void it_gives_the_inmemory_implementation_by_default() {
        System.clearProperty(COM_RIDGID_OSS_MESSAGE_BUS_SERVICE_CLASS);
        assertNull(System.clearProperty(COM_RIDGID_OSS_MESSAGE_BUS_SERVICE_CLASS));
        assertSame(InMemoryMessageBus.class,
                   MessageBusService.instance().defaultProvider().getClass(),
                   "Did not get the In-Memory implementation by default");
    }

//    @Test
//    void it_gives_the_specific_implementation_if_configured() {
//        System.setProperty(COM_RIDGID_OSS_MESSAGE_BUS_SERVICE_CLASS, MessageBusMock.class.getName());
//        assertSame(MessageBusMock.class,
//                   MessageBusService.instance().defaultProvider().getClass(),
//                   "Did not get the configured implementation");
//        System.clearProperty(COM_RIDGID_OSS_MESSAGE_BUS_SERVICE_CLASS);
//    }
}

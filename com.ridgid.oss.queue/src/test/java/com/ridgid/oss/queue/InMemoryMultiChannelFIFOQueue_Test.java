package com.ridgid.oss.queue;

import com.ridgid.oss.queue.impl.inmemory.InMemoryMultiChannelFIFOQueue;
import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue;
import com.ridgid.oss.queue.spi.MultiChannelFIFOQueue.MultiChannelFIFOQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ridgid.oss.common.function.Consumers.uncheck;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({
                      "DuplicateStringLiteralInspection",
                      "FieldCanBeLocal",
                      "StaticCollection"
                      , "StaticVariableOfConcreteClass"
                      , "CallToSuspiciousStringMethod"
                      , "LiteralAsArgToStringEquals"
                  })
class InMemoryMultiChannelFIFOQueue_Test
{
    private MultiChannelFIFOQueue<DummyBase> fifoSingle;
    private MultiChannelFIFOQueue<DummyBase> fifoFromCollection;
    private MultiChannelFIFOQueue<DummyBase> fifoFromArray;
    private MultiChannelFIFOQueue<DummyBase> fifoFromStream;

    @SuppressWarnings("unchecked")
    private static final Class<? extends DummyBase>[]
        testArrayOfChildTypes = new Class[]
        {
            DummyChild_1.class,
            DummyChild_3.class,
            DummyChild_2_2.class,
            DummyChild_2_1.class,
            DummyChild_2.class,
            DummyChild_3_2.class,
            DummyChild_2_1_1.class,
            DummyChild_3_1.class
        };

    private static final List<Class<? extends DummyBase>>
        testListOfChildTypes = asList
        (
            DummyChild_2.class,
            DummyChild_3_2.class,
            DummyChild_2_2.class,
            DummyChild_3.class,
            DummyChild_2_1.class,
            DummyChild_3_1.class,
            DummyChild_1.class,
            DummyChild_2_1_1.class
        );

    private static final List<Class<? extends DummyBase>>
        testListForStreamOfChildTypes = asList
        (
            DummyChild_1.class,
            DummyChild_2.class,
            DummyChild_3.class,
            DummyChild_2_1.class,
            DummyChild_3_1.class,
            DummyChild_3_2.class,
            DummyChild_2_2.class,
            DummyChild_2_1_1.class
        );

    private static final DummyBase[]
        testMessages =
        {
            new DummyChild_1(),
            new DummyChild_2(),
            new DummyChild_3(),
            new DummyChild_2_1(),
            new DummyChild_2_2(),
            new DummyChild_3_1(),
            new DummyChild_3_2(),
            new DummyChild_2_1_1(),
            new DummyChild_1(),
            new DummyChild_2(),
            new DummyChild_2_1(),
            new DummyChild_2_1_1(),
            new DummyChild_2_2(),
            new DummyChild_3(),
            new DummyChild_3_1(),
            new DummyChild_3_2()
        };

    @BeforeEach
    void setup() {
        fifoSingle         = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class
            );
        fifoFromArray      = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class,
                testArrayOfChildTypes
            );
        fifoFromCollection = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class,
                testListOfChildTypes
            );
        fifoFromStream     = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class,
                testListForStreamOfChildTypes.stream()
            );
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class);
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_a_single_message_type_that_is_the_sanme_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class,
                                                  singletonList(DummyBase.class));
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_a_single_message_type_that_inherits_from_the_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class,
                                                  singletonList(DummyChild_1.class));
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_two_message_types_that_inherits_from_the_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class,
                asList
                    (
                        DummyChild_1.class,
                        DummyChild_2.class
                    )
            );
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_three_message_types_that_inherits_from_the_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>
            (
                DummyBase.class,
                asList
                    (
                        DummyChild_1.class,
                        DummyChild_2.class,
                        DummyChild_3_1.class
                    )
            );
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_a_single_message_type_that_inherits_from_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class,
                                                  DummyChild_1.class);
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_two_message_types_that_inherit_from_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class,
                                                  DummyChild_1.class,
                                                  DummyChild_2.class);
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_performs_getBaseMessageType_with_only_a_base_type_and_three_message_types_that_inherit_from_base_and_it_returns_the_correct_type() {
        MultiChannelFIFOQueue<DummyBase> fifo
            = new InMemoryMultiChannelFIFOQueue<>(DummyBase.class,
                                                  DummyChild_1.class,
                                                  DummyChild_2.class,
                                                  DummyChild_2_1.class);
        assertSame(DummyBase.class,
                   fifo.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void it_gets_the_correct_message_base_type_no_matter_which_constructor_is_used() {
        assertSame(DummyBase.class,
                   fifoSingle.getBaseMessageType(),
                   "Invalid Base Class Reported");
        assertSame(DummyBase.class,
                   fifoFromArray.getBaseMessageType(),
                   "Invalid Base Class Reported");
        assertSame(DummyBase.class,
                   fifoFromCollection.getBaseMessageType(),
                   "Invalid Base Class Reported");
        assertSame(DummyBase.class,
                   fifoFromStream.getBaseMessageType(),
                   "Invalid Base Class Reported");
    }

    @Test
    void streamChannelMessageTypes_shows_the_correct_lists_of_types_for_all_constructors() {
        assertIterableEquals
            (
                (Iterable<Class<DummyBase>>) of(DummyBase.class)::iterator,
                (Iterable<Class<? extends DummyBase>>) fifoSingle.streamChannelMessageTypes()::iterator,
                "Invalid list of channel message types"
            );
        assertIterableEquals
            (
                (Iterable<Class<? extends DummyBase>>)
                    Arrays.stream(testArrayOfChildTypes)
                          .sorted(comparing(Class::getName))::iterator,
                (Iterable<Class<? extends DummyBase>>)
                    fifoFromArray.streamChannelMessageTypes()
                                 .sorted(comparing(Class::getName))::iterator,
                "Invalid list of channel message types"
            );
        assertIterableEquals
            (
                (Iterable<Class<? extends DummyBase>>)
                    testListOfChildTypes.stream()
                                        .sorted(comparing(Class::getName))::iterator,
                (Iterable<Class<? extends DummyBase>>)
                    fifoFromCollection.streamChannelMessageTypes()
                                      .sorted(comparing(Class::getName))::iterator,
                "Invalid list of channel message types"
            );
        assertIterableEquals
            (
                (Iterable<Class<? extends DummyBase>>)
                    testListForStreamOfChildTypes.stream().sorted(comparing(Class::getName))::iterator,
                (Iterable<Class<? extends DummyBase>>)
                    fifoFromStream.streamChannelMessageTypes()
                                  .sorted(comparing(Class::getName))::iterator,
                "Invalid list of channel message types"
            );
    }

    @Test
    void it_sends_and_polls_correctly_when_done_consecutively() throws MultiChannelFIFOQueueException {
        for ( DummyBase msg : testMessages ) {
            fifoFromStream.send(msg);
            Optional<? extends DummyBase> actualReceived = fifoFromStream.poll(DummyBase.class);
            assertTrue(actualReceived.isPresent(), "No Message Received");
            assertSame(msg, actualReceived.get(), "Incorrect Message Received");
        }
    }

    @Test
    void it_sends_and_polls_correctly_when_done_all_are_sent_first_then_all_are_consumed_by_specific_message_type()
        throws MultiChannelFIFOQueueException
    {
        Arrays.stream(testMessages)
              .forEach(uncheck(MultiChannelFIFOQueueException.class,
                               fifoFromStream::send));
        for ( DummyBase msg : testMessages )
            assertSame(msg,
                       fifoFromStream.poll(msg.getClass()).orElse(null),
                       "Different message received than what was expected based on order sent");
        assertNull(fifoFromStream.poll(DummyBase.class).orElse(null),
                   "Should have already consumed all messages");
    }

    @Test
    void it_sends_and_polls_correctly_when_done_all_are_sent_first_then_batches_are_consumed_by_super_type()
        throws MultiChannelFIFOQueueException
    {
        Arrays.stream(testMessages)
              .forEach(uncheck(MultiChannelFIFOQueueException.class,
                               fifoFromStream::send));

        for ( DummyBase msg : (Iterable<DummyBase>) Arrays.stream(testMessages)
                                                          .filter(DummyChild_2.class::isInstance)
                                                          .sorted(comparing(msg -> msg.getClass()
                                                                                      .getSimpleName()
                                                                                      .equals("DummyChild_2"))
                                                                      .reversed())
                                                          .peek(out::println)::iterator )
            assertSame(msg,
                       fifoFromStream.poll(DummyChild_2.class).orElse(null),
                       "Different message received than what was expected based on order sent");

        assertNull(fifoFromStream.poll(DummyChild_2.class)
                                 .orElse(null),
                   "Should have already consumed all messages");
    }

    @Test
    void it_throws_exception_when_attempt_is_made_to_send_a_message_of_a_type_that_is_not_configured() {
        assertThrows
            (
                MultiChannelFIFOQueueException.class,
                () -> fifoFromStream.send(new DummyBase())
            );
        assertThrows
            (
                MultiChannelFIFOQueueException.class,
                () -> fifoFromStream.send(new DummyChild_Unused())
            );
        assertDoesNotThrow
            (
                () -> fifoFromStream.send(new DummyChild_2_1_1_IndirectlyUsed())
            );
    }

    @Test
    void it_throws_exception_when_attempt_is_made_to_receive_a_message_of_a_type_that_is_not_configured() {
        assertThrows
            (
                MultiChannelFIFOQueueException.class,
                () -> fifoFromStream.poll(DummyChild_Unused.class)
            );
        assertDoesNotThrow
            (
                () -> {fifoFromStream.poll(DummyBase.class, 0);}
            );
        assertDoesNotThrow
            (
                () -> {fifoFromStream.poll(DummyChild_2.class, 0);}
            );
        assertDoesNotThrow
            (
                () -> {fifoFromStream.poll(DummyChild_2_1_1_IndirectlyUsed.class, 0);}
            );
    }

    private static class DummyBase implements Serializable
    {
        private static final long serialVersionUID = -1681231126879490181L;
    }

    private static class DummyChild_1 extends DummyBase
    {
        private static final long serialVersionUID = -2358908971850883645L;
    }

    private static class DummyChild_2 extends DummyBase
    {
        private static final long serialVersionUID = -8182089764303577785L;
    }

    private static class DummyChild_3 extends DummyBase
    {
        private static final long serialVersionUID = -5366186100238003398L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_2_1 extends DummyChild_2
    {
        private static final long serialVersionUID = -3390762108127488383L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_3_1 extends DummyChild_3
    {
        private static final long serialVersionUID = -4130434268563404624L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_3_2 extends DummyChild_3
    {
        private static final long serialVersionUID = 3322056138944990037L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_2_2 extends DummyChild_2
    {
        private static final long serialVersionUID = -508659282728176349L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_2_1_1 extends DummyChild_2_1
    {
        private static final long serialVersionUID = 3253032106616195378L;
    }

    private static class DummyChild_Unused extends DummyBase
    {
        private static final long serialVersionUID = 3253032106616195378L;
    }

    @SuppressWarnings("ClassTooDeepInInheritanceTree")
    private static class DummyChild_2_1_1_IndirectlyUsed extends DummyChild_2_1
    {
        private static final long serialVersionUID = 3253032106616195378L;
    }
}

package org.agilewiki.jactor.events;

/**
 * A base class for actors with one-way messages (events).
 *
 * @param <E>
 */
abstract public class JActor<E> {

    /**
     * The actor's inbox.
     */
    private EventDispatcher<E> inbox;

    /**
     * Handles callbacks from the inbox.
     */
    private ActiveEventProcessor<E> eventProcessor = new ActiveEventProcessor<E>() {
        @Override
        public void haveEvents() {
            inbox.dispatchEvents();
        }

        @Override
        public void processEvent(E event) {
            JActor.this.processEvent(event);
        }
    };

    /**
     * Create a JAEventActor
     *
     * @param inbox The actor's inbox.
     */
    public JActor(EventDispatcher<E> inbox) {
        this.inbox = inbox;
        inbox.setEventProcessor(eventProcessor);
    }

    /**
     * The processMessage method is called when there is an incoming event to process.
     *
     * @param event The event to be processed.
     */
    abstract protected void processEvent(E event);
}

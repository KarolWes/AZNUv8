package uni.aznu.state;

public class StateMachine {
    private ProcessingState state;
    private final StateMachineBuilder stateMachineBuilder;

    public StateMachine(ProcessingState initialState, StateMachineBuilder stateMachineBuilder) {
        this.state=initialState;
        this.stateMachineBuilder=stateMachineBuilder;
    }

    public synchronized ProcessingState sendEvent(ProcessingEvent processingEvent) {
        ProcessingState previousState=state;
        state = stateMachineBuilder.sendEvent(state, processingEvent);
        return previousState;
    }

    public ProcessingState getState() {
        return state;
    }
}

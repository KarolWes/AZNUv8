package uni.aznu.state;

import java.util.HashMap;

public class StateMachineBuilder {
    private final HashMap<ProcessingState, HashMap<ProcessingEvent, ProcessingState>> transitions = new HashMap<>();
    private ProcessingState initialState;

    public StateMachineBuilder initialState(ProcessingState initialState) {
        this.initialState=initialState;
        return this;
    }

    public StateMachineBuilder add(ProcessingState source, ProcessingEvent event, ProcessingState target) {
        HashMap<ProcessingEvent, ProcessingState> transitionsForSource= transitions.get(source);
        if (transitionsForSource==null) {
            transitionsForSource=new HashMap<>();
            transitions.put(source, transitionsForSource);
        }
        transitionsForSource.put(event, target);
        return this;
    }

    public StateMachine build() {
        StateMachine stateMachine = new StateMachine(initialState, this);
        return stateMachine;
    }

    public ProcessingState sendEvent(ProcessingState state, ProcessingEvent processingEvent) {
        HashMap<ProcessingEvent, ProcessingState> transitionsForSource= transitions.get(state);
        if (transitionsForSource==null)
            throw new RuntimeException("Source state "+state.name()+" is not defined");
        ProcessingState targetState = transitionsForSource.get(processingEvent);
        if (targetState==null)
            throw new RuntimeException("Event "+processingEvent.name()+" is not defined for source state "+state.name());
        return targetState;
    }
}

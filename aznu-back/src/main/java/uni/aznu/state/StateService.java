package uni.aznu.state;

import java.util.HashMap;

public class StateService {
    private final HashMap<String, StateMachine> processingStates=new HashMap<>();

    public StateService(StateMachineBuilder stateMachineBuilder) {
        this.stateMachineBuilder = stateMachineBuilder;
    }


    private StateMachineBuilder stateMachineBuilder = null;

    public ProcessingState sendEvent(String bookingId, ProcessingEvent event) {
        StateMachine stateMachine;
        synchronized(this){
            stateMachine = processingStates.get(bookingId);
            if (stateMachine==null) {
                stateMachine=stateMachineBuilder.build();
                processingStates.put(bookingId, stateMachine);
            }
        }
        return stateMachine.sendEvent(event);

    }

    public void removeState(String bookingId) {
        processingStates.remove(bookingId);
    }

    public ProcessingState getState(String bookingId) {
        StateMachine stateMachine;
        synchronized(this){
            stateMachine = processingStates.get(bookingId);
            if (stateMachine==null) {
                stateMachine=stateMachineBuilder.build();
            }
        }
        return stateMachine.getState();
    }
}

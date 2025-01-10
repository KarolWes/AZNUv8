package uni.aznu;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import uni.aznu.state.*;

@Configuration
public class ApplicationConfig {
    @Bean(name="basicStateMachineBuilder")
    public StateMachineBuilder basicStateMachineBuilder() {
        StateMachineBuilder smb = new StateMachineBuilder();
        smb.initialState(ProcessingState.NONE)
                .add(ProcessingState.NONE,ProcessingEvent.START,ProcessingState.STARTED)
                .add(ProcessingState.STARTED,ProcessingEvent.FINISH,ProcessingState.FINISHED)
                .add(ProcessingState.NONE,ProcessingEvent.CANCEL,ProcessingState.CANCELLED)
                .add(ProcessingState.STARTED,ProcessingEvent.CANCEL,ProcessingState.CANCELLED)
                .add(ProcessingState.FINISHED,ProcessingEvent.CANCEL,ProcessingState.CANCELLED)
                .add(ProcessingState.CANCELLED,ProcessingEvent.START,ProcessingState.CANCELLED)
                .add(ProcessingState.CANCELLED,ProcessingEvent.FINISH,ProcessingState.CANCELLED)
                .add(ProcessingState.FINISHED,ProcessingEvent.COMPLETE,ProcessingState.COMPLETED)
                .add(ProcessingState.CANCELLED,ProcessingEvent.COMPLETE,ProcessingState.COMPLETED)
        ;
        return smb;
    }

    @Bean
    @Scope("prototype")
    public StateService stateService(@Qualifier("basicStateMachineBuilder") StateMachineBuilder stateMachineBuilder) {
        return new StateService (stateMachineBuilder);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}

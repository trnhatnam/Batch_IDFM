package fr.natsystem.batch.IDFTransports.processor;


import fr.natsystem.batch.IDFTransports.models.Stop;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;


import java.util.HashSet;

@Component("importStopProcessor")
public class ImportStopProcessor implements ProcessorStrategy {

    @Override
    public ItemProcessor getProcessor() {
        return new ItemProcessor<Stop,Stop>() {
            private final HashSet<String> stopsSeen = new HashSet<>();

            @Override
            public Stop process(Stop item) throws Exception {
                Stop retour = null;
                if (!stopsSeen.contains(item.stopId())){
                    stopsSeen.add(item.stopId());
                    retour = item;
                }
                return retour;
            }
        };
    }
}

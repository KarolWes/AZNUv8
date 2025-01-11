package uni.aznu;
import uni.aznu.model.ResultModel;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage {
    private static final ConcurrentHashMap<String, ResultModel> storage = new ConcurrentHashMap<>();

    public static void addResult(ResultModel result) {
        if(result != null && result.getId() != null) {
            if(!storage.containsKey(result.getId())) {
                storage.put(result.getId(), result);
            }
        }
    }

    public static ResultModel getResult(String id) {
        if(id != null && storage.containsKey(id)) {
            return storage.get(id);
        }
        else return null;

    }
}
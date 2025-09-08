package org.example.util;

import org.example.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskUtils {


    public static Map<Integer, Task> enumerateTasks(List<Task> tasks) {
        Map<Integer, Task> taskMap = new HashMap<>();
        int counter = 1;
        for (Task task : tasks) {
            taskMap.put(counter++, task);
        }
        return taskMap;
    }
}

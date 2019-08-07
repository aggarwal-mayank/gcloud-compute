package com.ma.programming.compute;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class DatastoreController {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind("Task");

    @GetMapping("/datastore/properties")
    public List<String> getDatastoreProperties() {
        List<String> properties = new ArrayList<>();
        properties.add("CREDENTIAL_ENV_NAME: "+DatastoreOptions.CREDENTIAL_ENV_NAME);
        properties.add("AppEngineAppId: "+DatastoreOptions.getAppEngineAppId());
        properties.add("DefaultProjectId: "+DatastoreOptions.getDefaultProjectId());
        properties.add("LibraryName: "+DatastoreOptions.getLibraryName());
        properties.add("GoogApiClientLibName: "+DatastoreOptions.getGoogApiClientLibName());
        return properties;
    }

    @GetMapping("/task/add")
    public Key addTask() {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity task = Entity.newBuilder(key)
                .set("description", StringValue.newBuilder("My Task").setExcludeFromIndexes(true).build())
                .set("created", Timestamp.now())
                .set("done", false)
                .build();
        datastore.put(task);
        return key;
    }


    @GetMapping("/task/list")
    public List<String> listTask() {
        Query<Entity> query =
                Query.newEntityQueryBuilder().setKind("Task").setOrderBy(StructuredQuery.OrderBy.asc("created")).build();
        return formatTasks(datastore.run(query));
    }

    static List<String> formatTasks(Iterator<Entity> tasks) {
        List<String> strings = new ArrayList<>();
        while (tasks.hasNext()) {
            Entity task = tasks.next();
            if (task.getBoolean("done")) {
                strings.add(
                        String.format("%d : %s (done)", task.getKey().getId(), task.getString("description")));
            } else {
                strings.add(String.format("%d : %s (created %s)", task.getKey().getId(),
                        task.getString("description"), task.getTimestamp("created")));
            }
        }
        return strings;
    }
}


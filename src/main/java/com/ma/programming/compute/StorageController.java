package com.ma.programming.compute;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StorageController {

    private Storage storage = StorageOptions.getDefaultInstance().getService();

    @GetMapping("/storage/properties")
    public List<String> getCloudStorageProperties() {
        List<String> properties = new ArrayList<>();
        properties.add("CREDENTIAL_ENV_NAME: "+StorageOptions.CREDENTIAL_ENV_NAME);
        properties.add("AppEngineAppId: "+StorageOptions.getAppEngineAppId());
        properties.add("DefaultProjectId: "+StorageOptions.getDefaultProjectId());
        properties.add("LibraryName: "+StorageOptions.getLibraryName());
        properties.add("GoogApiClientLibName: "+StorageOptions.getGoogApiClientLibName());
        return properties;
    }

    @GetMapping("/storage/bucket/{bucketName}")
    public String createBucket(@PathVariable("bucketName") String name) {
        Bucket bucket = storage.create(BucketInfo.of(name));
        return bucket.getName();
    }


}

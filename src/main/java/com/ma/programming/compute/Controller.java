package com.ma.programming.compute;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private static final String APPLICATION_NAME = "";


    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/instances")
    public String instances(@RequestHeader("project-id") String PROJECT_ID, @RequestHeader("zone") String ZONE_NAME) {
        try {
            System.out.println("GOOGLE_APPLICATION_CREDENTIALS: "+System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Authenticate using Google Application Default Credentials.
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            System.out.println("SA User: "+credential.getServiceAccountUser());
            System.out.println("SA Id: "+credential.getServiceAccountId());
            System.out.println("Client Authentication: "+credential.getClientAuthentication());
            System.out.println("Access Method: "+credential.getMethod());

            if (credential.createScopedRequired()) {
                System.out.println("Adding Scopes");
                List<String> scopes = new ArrayList<>();
                // Set Google Cloud Storage scope to Full Control.
                scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL);
                // Set Google Compute Engine scope to Read-write.
                scopes.add(ComputeScopes.COMPUTE);
                credential = credential.createScoped(scopes);
            }

            // Create Compute Engine object for listing instances.
            Compute compute =
                    new Compute.Builder(httpTransport, JSON_FACTORY, credential)
                            .setApplicationName(APPLICATION_NAME)
                            .build();

            // List out instances
            return printInstances(compute, PROJECT_ID, ZONE_NAME);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "Empty";

    }

    private String printInstances(Compute compute, String PROJECT_ID, String ZONE_NAME) throws IOException {
        System.out.println("================== Listing Compute Engine Instances ==================");
        StringBuffer buffer = new StringBuffer();
        Compute.Instances.List instances = compute.instances().list(PROJECT_ID, ZONE_NAME);
        InstanceList list = instances.execute();
        if (list.getItems() == null) {
            return "No instances found. Sign in to the Google Developers Console and create "
                    + "an instance at: https://console.developers.google.com/";
        } else {
            for (Instance instance : list.getItems()) {
                buffer.append(instance.toPrettyString());
            }
        }
        return buffer.toString();
    }

    @GetMapping("/create-bucket")
    public String createBucket() {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // The name for the new bucket
        String bucketName = "my-new-bucket-26111986";  // "my-new-bucket";

        // Creates the new bucket
        Bucket bucket = storage.create(BucketInfo.of(bucketName));

        return "Bucket :"+bucket.getName()+" created";
    }

    @GetMapping("/instances-default")
    public String instancesDefault(@RequestHeader("project-id") String PROJECT_ID, @RequestHeader("zone") String ZONE_NAME) {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Explicitly request service account credentials from the compute engine instance.
            GoogleCredentials credential = ComputeEngineCredentials.create();
            System.out.println("SA Auth Type: "+credential.getAuthenticationType());

            if (credential.createScopedRequired()) {
                System.out.println("Adding Scopes");
                List<String> scopes = new ArrayList<>();
                // Set Google Cloud Storage scope to Full Control.
                scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL);
                // Set Google Compute Engine scope to Read-write.
                scopes.add(ComputeScopes.COMPUTE);
                credential = credential.createScoped(scopes);
            }

            // Create Compute Engine object for listing instances.
            HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(credential);
            Compute compute =
                    new Compute.Builder(httpTransport, JSON_FACTORY, adapter)
                            .setApplicationName(APPLICATION_NAME)
                            .build();

            // List out instances
            return printInstances(compute, PROJECT_ID, ZONE_NAME);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "Empty";

    }




}

package project.controllers;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import project.model.Patient;
import project.model.PatientPayload;
import project.services.PatientService;
import project.util.JsonConverterHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PatientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);
    @Autowired
    private PatientService patientService;

    @Autowired
    private JsonConverterHelper jsonConverterHelper;

    @GetMapping("/patients")
    public Page<Patient> index(Pageable pageable) {
        return patientService.fetchAllPatients(pageable);
    }

    @GetMapping("/create_bucket")
    public String create_bucket(@RequestParam("bucket_name") String bucketName, @RequestParam("folder_name") String folderName, @RequestParam("file_name") String fileName) throws IOException {
        File gcpCredentials = ResourceUtils.getFile("classpath:gcp_credentials.txt");
        File requestedFile = ResourceUtils.getFile("classpath:"+fileName);

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(gcpCredentials));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("sema4-test").build().getService();

        Bucket bucket = storage.create(BucketInfo.of(bucketName));
        bucket.create(folderName+"/"+fileName, new FileInputStream(requestedFile)); // '/' will
        byte[] data = new byte[(int) gcpCredentials.length()];
        bucket.create(folderName+"/bytedata", data);
//
        return "created!";
    }

    @GetMapping("/delete_folder")
    public String delete_folder(@RequestParam("bucket_name") String bucketName, @RequestParam("folder_name") String folderName) throws IOException {
        File file = ResourceUtils.getFile("classpath:gcp_credentials.txt");
//        File requestedFile = ResourceUtils.getFile("classpath:"+fileName);

        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(file));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("sema4-test").build().getService();
        storage.delete(bucketName, folderName);
        return "deleted folder!";
    }

    @GetMapping("/delete_multi_folder")
    public String delete_multi_folder(@RequestParam("bucket_name") String bucketName, @RequestParam("folder_prefix") String folderPrefix) throws IOException {
        File file = ResourceUtils.getFile("classpath:gcp_credentials.txt");
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(file));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("sema4-test").build().getService();

        Iterable<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPrefix)).iterateAll();
        for(Blob blob : blobs){
            System.out.println(blob);
            blob.delete(Blob.BlobSourceOption.generationMatch());
        }
        return "deleted multi folder";
    }



    @GetMapping("/patient/{id}")
    public Patient show(@PathVariable String id) {
        int patientId = Integer.parseInt(id);
        return patientService.getPatientById(patientId).get();
    }

    @PostMapping("/patient")
    public Patient create(@RequestBody Map<String, Object> body) {
        Gson gson = new Gson();
        System.out.println(body.get("payload"));
        PatientPayload patientPayload = jsonConverterHelper.parseJsonData(body.get("payload").toString(), PatientPayload.class);
        Optional<Patient> newPatient = patientService.createPatient(patientPayload);
        return newPatient.get();
    }

    @PutMapping("/patient/{id}")
    public Patient update(@RequestBody Map<String, String> body, @PathVariable String id) {
        int patientId = Integer.parseInt(id);
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String phoneNumber = body.get("phoneNumber");
        String bloodGroup = body.get("bloodGroup");
        String gender = body.get("gender");
        return patientService.updatePatient(patientId, firstName, lastName, gender, bloodGroup);
    }

    @DeleteMapping("/patient/{id}")
    public boolean delete(@PathVariable String id) {
        int patientId = Integer.parseInt(id);
        return patientService.deletePatient(patientId);
    }

    @GetMapping("/patient/search")
    public List<Patient> search(@RequestParam(name = "query") String query) {
        return patientService.searchPatient(query);
    }
}

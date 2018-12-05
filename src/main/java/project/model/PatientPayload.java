package project.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientPayload {

    @SerializedName("patientDetails")
    private Patient patient;

    @SerializedName("contacts")
    private List<Contact> contacts;

    @SerializedName("addresses")
    private List<Address> addresses;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "PatientPayload{" +
                "patient=" + patient +
                ", contacts=" + contacts +
                ", addresses=" + addresses +
                '}';
    }
}

package com.emergingtech.orchestration.producer;

import com.emergingtech.proto.Patient.PatientUpdate;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class PatientProducer {

    @Channel("patient-producer")
    Emitter<PatientUpdate> patientUpdateEmitter;

    public void updatePatient(PatientUpdate patientUpdate) {
        patientUpdateEmitter.send(patientUpdate);
    }

}

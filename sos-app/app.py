from flask import Flask, render_template, request, session, jsonify
from kafka import KafkaProducer, KafkaConsumer
from base64 import b64encode
from random import randint
from threading import Thread
from json import dumps, loads
import patient_pb2

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret_key'
producer = KafkaProducer(
    bootstrap_servers='kafka.cm3202.uk',
    max_request_size=10485880,
    value_serializer=lambda x: dumps(x).encode('utf-8')
)

patients = {}

class Patient:
    def __init__(self):
        self.id = randint(1, 1000000)
        self.longitude = 0
        self.latitude = 0
        self.notifications = []
        self.audio = None

        patients[self.id] = self

    def __dict__(self):
        return {
            'patient_id': self.id,
            'longitude': self.longitude,
            'latitude': self.latitude,
            'audio': None if not self.audio else b64encode(self.audio).decode('utf-8')
        }

@app.route('/')
def index():
    patient = Patient()
    session['patient_id'] = patient.id

    print(patient.id)

    return render_template('index.html')

@app.route('/notifications', methods=['GET'])
def get_notifications():
    if "patient_id" not in session:
        print("here")
        return ("", 500)

    notifications = patients[session["patient_id"]].notifications
    notifications.reverse()
    return jsonify(notifications)

@app.route('/audio', methods=['POST'])
def audio():
    if session["patient_id"] not in patients:
        return ("", 500)

    patient = patients[session["patient_id"]]
    patient.audio = request.data

    future = producer.send('audio', value=patient.__dict__())
    future.get()

    return ("", 204)

def consume():
    consumer = KafkaConsumer(
        'patient-update',
        bootstrap_servers='kafka.cm3202.uk',
        fetch_max_bytes=10485880
    )

    for msg in consumer:
        proto_msg = patient_pb2.PatientUpdate()
        proto_msg.ParseFromString(msg.value)

        print(proto_msg)

        patient_id = proto_msg.patientId
        if patient_id in patients:
            patient = patients[patient_id]
            patient.notifications.append(proto_msg.incidentStatus)

if __name__ == '__main__':
    Thread(target=consume, daemon=True).start()
    app.run(host="0.0.0.0", port=5000)
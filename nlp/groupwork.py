import numpy as np
import joblib
import os
#import incident_pb2
from kafka import KafkaConsumer, KafkaProducer
from random import randint
from base64 import b64decode
from json import loads, dumps

#incidentRequest = incident_pb2.IncidentRequest()
#incidentRequest.patientId = 1234
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.feature_extraction.text import TfidfVectorizer

from transcription import TranscriptionService

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_FILE = os.path.join(BASE_DIR,"gbdt_model.pkl")
VECTORIZER_FILE = os.path.join(BASE_DIR,"vectorizer.pkl")
UPLOADS_DIR = os.path.join(BASE_DIR, "uploads")
os.makedirs(UPLOADS_DIR, exist_ok=True)

ffmpeg_path = r'C:\ffmpeg-8.1-essentials_build\bin' 
os.environ["PATH"] += os.pathsep + ffmpeg_path

def get_training_data():
    data = [
        # HIGH
        ("severe chest pain going to my arm and sweating", 2),
        ("i cant speak properly and my face feels numb", 2),
        ("i passed out and feel very weak", 2),
        ("difficulty breathing and chest tightness", 2),
        ("high fever and confusion", 2),

        # MODERATE
        ("i feel short of breath and tired", 1),
        ("persistent cough and mild fever", 1),
        ("my heart is racing sometimes", 1),
        ("i feel dizzy and weak", 1),
        ("stomach pain and nausea", 1),

        # LOW
        ("runny nose and slight headache", 0),
        ("just a mild headache", 0),
        ("feeling tired and a bit unwell", 0),
        ("small cough and sore throat", 0),
        ("light dizziness but otherwise fine", 0),
    ]

    texts = [x[0] for x in data]
    labels = [x[1] for x in data]
    return texts, labels


CONDITIONS = {
    "heart attack":   ["chest pain", "arm", "sweating"],
    "stroke":         ["slurred", "speech", "numb", "weak"],
    "sepsis":         ["fever", "confusion"],
    "asthma":         ["shortness of breath", "breathing"],
    "flu":            ["fever", "cough", "tired"],
    "common cold":    ["runny nose", "sore throat"],
    "migraine":       ["headache"],
    "food poisoning": ["vomiting", "nausea", "stomach"],
}


def predict_conditions(text):
    text = text.lower()
    matches = []

    for condition, keywords in CONDITIONS.items():
        score = sum(1 for k in keywords if k in text)
        if score > 0:
            matches.append((condition, score))

    matches.sort(key=lambda x: x[1], reverse=True)
    return [m[0] for m in matches[:3]]


def train_model():
    texts, labels = get_training_data()

    vectorizer = TfidfVectorizer()
    X = vectorizer.fit_transform(texts)
    y = np.array(labels)

    model = GradientBoostingClassifier()
    model.fit(X, y)

    joblib.dump(model, MODEL_FILE)
    joblib.dump(vectorizer, VECTORIZER_FILE)

    print("Model trained and saved.")


def load_model():
    if not os.path.exists(MODEL_FILE):
        return None, None
    return joblib.load(MODEL_FILE), joblib.load(VECTORIZER_FILE)


def predict(text, model, vectorizer):
    X = vectorizer.transform([text])
    probs = model.predict_proba(X)[0]
    pred = np.argmax(probs)
    risk = ["LOW", "MODERATE", "HIGH"][pred]
    return risk, probs

def analyse_symptoms(text, model, vectorizer):
    risk, probs = predict(text, model, vectorizer)
    conditions = predict_conditions(text)

    print("\n--- Results ---")
    print("Symptoms:", text)
    print("Risk Level:", risk)
    print("Confidence:", probs)

    return text, probs


if __name__ == "__main__":

    # Ensure classifier model is ready
    model, vectorizer = load_model()
    if model is None:
        print("No saved model found. Training now...")
        train_model()
        model, vectorizer = load_model()

    whisper_size = "base"
    transcriber = TranscriptionService(model_size=whisper_size)

    counter = randint(1,1000000)

    print("Setting up a Kafka CONSUMER")
    consumer = KafkaConsumer(
        'audio',
        bootstrap_servers='kafka.cm3202.uk',
        fetch_max_bytes=10485880,
        value_deserializer=lambda x: loads(x.decode('utf-8'))
    )

    producer = KafkaProducer(
        bootstrap_servers='kafka.cm3202.uk',
        max_request_size=10485880,
        value_serializer=lambda x: dumps(x).encode('utf-8')
    )

    # print("Setting up a Kafka PRODUCER")
    # producer = KafkaProducer(
    #     bootstrap_servers='kafka.cm3202.uk',
    #     group_id='nlp-processor-group',
    #     auto_offset_reset='latest',

    # )


    if not os.path.exists(UPLOADS_DIR):
        os.makedirs(UPLOADS_DIR)

    try:
        for msg in consumer:
            with open(os.path.join(UPLOADS_DIR, f"recording_{counter}.webm"), "wb") as f:
                audio_data = msg.value["audio"].encode('utf-8')
                f.write(b64decode(audio_data))
                print(f"Written audio to uploads/recording_{counter}.webm")    
            
            patient_id = msg.value["patient_id"]
            longitude = msg.value["longitude"]
            latitude = msg.value["latitude"]
            print(f"Patient ID: {patient_id}, long: {longitude}, lat: {latitude}")
            
            output_path = os.path.join(UPLOADS_DIR, f"recording_{counter}.webm")
            text = transcriber.transcribe(output_path)
            symptoms, probs = analyse_symptoms(text, model, vectorizer)

            incident_request = {
                'patient_id': patient_id,
                'transcript': text,
            }
            print(f"Incident request: {incident_request}")

            future = producer.send('incident-request', value=incident_request)
            future.get()


            counter += 1

    except KeyboardInterrupt:
        print("\nStopping consumer...")
    finally:
        consumer.close()
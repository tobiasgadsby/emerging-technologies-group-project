import numpy as np
import os
from kafka import KafkaConsumer, KafkaProducer
from random import randint
from base64 import b64decode
from json import loads, dumps
from transcription import TranscriptionService
from models import model, vectoriser

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
UPLOADS_DIR = os.path.join(BASE_DIR, "uploads")
os.makedirs(UPLOADS_DIR, exist_ok=True)

ffmpeg_path = r'C:\ffmpeg\bin'
os.environ["PATH"] += os.pathsep + ffmpeg_path

def analyse_symptoms(text):
    X = vectoriser.transform([text])
    probs = model.predict_proba(X)[0]
    pred = np.argmax(probs)
    risk = ["LOW", "MODERATE", "HIGH"][pred]

    print("\n--- Results ---")
    print("Symptoms:", text)
    print("Risk Level:", risk)
    print("Confidence:", probs)

    return probs

if __name__ == "__main__":
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

    try:
        for msg in consumer:
            file_path = os.path.join(UPLOADS_DIR, f"recording_{counter}.webm")
            counter += 1

            with open(file_path, "wb") as f:
                audio_data = msg.value["audio"].encode('utf-8')
                f.write(b64decode(audio_data))
                print(f"Written audio to {file_path}")
            
            patient_id = msg.value["patient_id"]
            longitude = msg.value["longitude"]
            latitude = msg.value["latitude"]
            print(f"Patient ID: {patient_id}, long: {longitude}, lat: {latitude}")
            
            text = transcriber.transcribe(file_path)
            print(f"Text:  {text}")
            probs = analyse_symptoms(text)

            incident_request = {
                'patient_id': patient_id,
                'transcript': text,
            }

            future = producer.send('incident-request', value=incident_request)
            future.get()

    except KeyboardInterrupt:
        print("\nStopping consumer...")

    finally:
        consumer.close()
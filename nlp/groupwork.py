import numpy as np
import joblib
import os

from sklearn.ensemble import GradientBoostingClassifier
from sklearn.feature_extraction.text import TfidfVectorizer

from transcription import TranscriptionService


MODEL_FILE = "gbdt_model.pkl"
VECTORIZER_FILE = "vectorizer.pkl"


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

    if conditions:
        print("Possible Conditions:")
        for c in conditions:
            print(" -", c)
    else:
        print("No clear condition identified.")


if __name__ == "__main__":

    # Ensure classifier model is ready
    model, vectorizer = load_model()
    if model is None:
        print("No saved model found. Training now...")
        train_model()
        model, vectorizer = load_model()

    whisper_size = "base"
    transcriber = TranscriptionService(model_size=whisper_size)

    while True:
        audio_path = input("\nPath to audio file (or 'quit' to exit): ").strip()
        if audio_path.lower() == "quit":
            break

        print("Transcribing...")
        transcribed = transcriber.transcribe(audio_path)

        if transcribed.startswith("Error:"):
            print(transcribed)
            continue

        analyse_symptoms(transcribed, model, vectorizer)
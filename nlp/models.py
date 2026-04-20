from numpy import array as np_array
from os import path
from joblib import load as joblib_load, dump as joblib_dump
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.feature_extraction.text import TfidfVectorizer
from data.train import texts, labels

BASE_DIR = path.dirname(path.abspath(__file__))
MODEL_FILE = path.join(BASE_DIR,"gbdt_model.pkl")
VECTORISER_FILE = path.join(BASE_DIR,"vectoriser.pkl")

if not path.exists(MODEL_FILE) or not path.exists(VECTORISER_FILE):
    vectorizer = TfidfVectorizer()
    X = vectorizer.fit_transform(texts)
    y = np_array(labels)

    model = GradientBoostingClassifier()
    model.fit(X, y)

    joblib_dump(model, MODEL_FILE)
    joblib_dump(vectorizer, VECTORISER_FILE)

    print("Model trained and saved.")

model = joblib_load(MODEL_FILE)
vectoriser = joblib_load(VECTORISER_FILE)
# SOS App Prototype, a University Assignment
Completed as a part of CM3202 Emerging Technology module at Cardiff University

This app allows a user to toggle between into and out of an SOS state, by pressing either the SOS button, or the cancel button.
When the SOS state is toggled, the app will send the location of the user to the backend, and begin recording audio, sending audio to the backend every 5 seconds.
Whilst in the SOS state, notifications will appear as they are delivered from the orch service.
When the user presses cancel, notifications will no longer be delivered to the user, and audio recording will stop.

This app is a part of a wider system that is created as a group project within the scope of CM3202 here: https://github.com/tobiasgadsby/emerging-technologies-group-project

### Dependencies:
 - python 3.14
 - pip 25.1.1
 - python modules can be found in requirements.txt

### Initialisation:
 - Build 
 - Create a Python VM `python -m venv venv`
 - Activate Python VM `cd venv/Scripts && activate.bat && cd ../..` (Optional - dependent on step above)
 - Install dependencies `pip install -r requirements.txt`

### Usage:
 - To run the app, `python app.py`
 - Go to http://localhost:5000/

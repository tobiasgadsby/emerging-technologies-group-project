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

texts, labels = zip(*data)
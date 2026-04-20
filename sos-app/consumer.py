from kafka import KafkaConsumer
from random import randint
from base64 import b64decode
from json import loads

counter = randint(1, 1000000)

consumer = KafkaConsumer(
    'audio',
    bootstrap_servers='kafka.cm3202.uk',
    fetch_max_bytes=10485880,
    value_deserializer=lambda x: loads(x.decode('utf-8'))
)

for msg in consumer:
    with open(f"uploads/recording_{counter}.webm", "wb") as f:
        audio_data = msg.value["audio"].encode('utf-8')
        f.write(b64decode(audio_data))
        counter += 1
        print(f"Written uploads/recording_{counter}.webm")
from json import dumps
from kafka import KafkaProducer

patient_id = 119184

producer = KafkaProducer(
    bootstrap_servers='kafka.cm3202.uk',
    max_request_size=10485880,
    value_serializer=lambda x: dumps(x).encode('utf-8')
)

while True:
    notif_message = input("Enter a notification: ")

    data = {
        "patient_id": patient_id,
        "notification": notif_message,
    }

    producer.send("notifs", value=data)
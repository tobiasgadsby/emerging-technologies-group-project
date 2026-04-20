FROM python:3.14-slim

RUN apt-get update && apt-get install -y \
    build-essential \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

ENV PATH="$PATH:/usr/local/bin/ffmpeg:/usr/local/bin/"

COPY . /sos-nlp
WORKDIR /sos-nlp

RUN mkdir -p /uploads
RUN pip install -r requirements.txt

ENTRYPOINT ["python"]

CMD ["service.py"]
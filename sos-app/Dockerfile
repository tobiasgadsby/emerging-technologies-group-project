FROM python:3.14-alpine

COPY . /sos-app

WORKDIR /sos-app

RUN mkdir -p /uploads
RUN pip install -r requirements.txt

EXPOSE 5000

ENTRYPOINT ["python"]

CMD ["app.py"]
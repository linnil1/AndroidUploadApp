FROM nvidia/cuda:10.0-devel
RUN apt update -y && \
    apt install -y python3-pip
RUN mkdir /app
COPY requirements.txt /app/requirements.txt
COPY yolo_requirements.txt /app/yolo_requirements.txt
     
RUN pip3 install -r /app/requirements.txt && \
    pip3 install -r /app/yolo_requirements.txt

WORKDIR /app
CMD [python3, app.py]


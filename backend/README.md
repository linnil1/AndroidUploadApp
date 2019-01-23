# FLASK BACKEND
This is a realtime flask server that using pytorch model to detect objects

## install
`pip3 install -r requirements`


## Deploy demo
```
sed -ie 's/process/process_easy/g' app.py
mkdir images
```

## Deploy with YOLO
```
git clone https://github.com/eriklindernoren/PyTorch-YOLOv3
cd PyTorch-YOLOv3
pip3 install -r requirements.txt
cp ../app.py .
cp ../process.py .
mkdir images
```

## Run
Change key and somethings in `app.py`

run `python3 app.py`

## Thanks
YOLO model powered by https://github.com/eriklindernoren/PyTorch-YOLOv3

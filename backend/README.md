# FLASK BACKEND
This is a realtime flask server that using pytorch model to detect objects

and a frontend web capture image by WebCam running on Browser.

## install
`pip3 install -r requirements`

# Use https
`openssl req -new -x509 -keyout key.pem -out cert.pem -days 365 -nodes`

## Deploy with YOLO
```
sed -ie 's/process_easy/process/g' app.py
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
* YOLO model powered by https://github.com/eriklindernoren/PyTorch-YOLOv3
* https://gist.github.com/rozifus/c529caf170699f117c53
* https://davidwalsh.name/convert-canvas-image
* Bootstrap

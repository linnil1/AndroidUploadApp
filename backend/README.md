# FLASK BACKEND
This is a realtime flask server that using pytorch model to detect objects

and a frontend web capture image by WebRTC Camera running on Browser.

## install
`pip3 install -r requirements`

# Use https
`openssl req -new -x509 -keyout privkey.pem -out cert.pem -days 365 -nodes`

## Deploy with YOLO
```
sed -i '1iimport sys\nsys.path.insert(0, "/app/PyTorch-YOLOv3")' app.py
sed -ie 's/process_easy/process/g' app.py
git clone https://github.com/eriklindernoren/PyTorch-YOLOv3
pip3 install -r PyTorch-YOLOv3/requirements.txt
```

## Run
Change key and somethings in `app.py`

run `python3 app.py`

## Run on Docker with YOLO
`docker build . linnil1/yolo_deploy`

`docker run -it --rm -p 5000:5000 -v $PWD:/app linnil1/yolo_deploy`


## Frontend

There are two modes for web: `click` and `video`.

You can change it in `static/setupCamera.js`,
also you can change `fps` or flip in it.


## Result

video mode
![](https://raw.githubusercontent.com/linnil1/AndroidUploadApp/master/demoImage/web.png)

click mode
![](https://raw.githubusercontent.com/linnil1/AndroidUploadApp/master/demoImage/web1.png)


## Thanks
* backend
    * YOLO model powered by https://github.com/eriklindernoren/PyTorch-YOLOv3

* HTML CSS JS
    * https://gist.github.com/rozifus/c529caf170699f117c53
    * WEBRTC demo https://davidwalsh.name/convert-canvas-image
    * Bootstrap
    * Font awesome
    * Collapse icon https://medium.com/@disjfa/animate-a-open-and-closing-element-using-bootstraps-collapse-79540f641a8e
    * WEBRTC demo good https://webrtc.github.io/samples/src/content/getusermedia/gum/
    * Play SVG http://bl.ocks.org/guilhermesimoes/fbe967d45ceeb350b765

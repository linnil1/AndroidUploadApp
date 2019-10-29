from flask import Flask, request, jsonify, Response, render_template
import cv2


# Init
app = Flask(__name__)
fourcc = cv2.VideoWriter_fourcc(*"XVID")
cap = cv2.VideoCapture(0)
while not cap.isOpened():
    print("Reset")
    time.sleep(0.1)
    cap = cv2.VideoCapture(0)
print("Init OK")


def steamVideo():
    while True:
        _, frame = cap.read()
        frame = cv2.flip(frame, 1)
        _, encode_img = cv2.imencode(".jpg", frame)
        yield (b"--frame\r\n"
               b"Content-Type: image/jpeg\r\n\r\n"
               + encode_img.tobytes() + b"\r\n")


@app.route("/video")
def get_video_file():
    return Response(steamVideo(),
                    mimetype="multipart/x-mixed-replace; boundary=frame")


@app.route("/")
def index():
    return render_template("index.html")


if __name__ == "__main__":
    try:
        app.run(use_reloader=False, debug=True, host="0.0.0.0")
    finally:
        print("Close")
        cap.release()

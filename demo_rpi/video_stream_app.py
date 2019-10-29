from flask import Flask, request, jsonify, Response, render_template
import threading
import cv2
import time


# parameters
video_name = "video.avi"
fps = 10
video_size = (640, 480)


# Init
app = Flask(__name__)
fourcc = cv2.VideoWriter_fourcc(*"XVID")
video = cv2.VideoWriter(video_name, fourcc, fps, video_size)
cap = cv2.VideoCapture(0)
while not cap.isOpened():
    print("Reset")
    time.sleep(0.1)
    cap = cv2.VideoCapture(0)
print("Init OK")


# Thread
def streaming():
    while True:
        global frame
        _, frame = cap.read()
        time.sleep(0.01)
        video.write(cv2.flip(frame, 1))
frame = None
thread = threading.Thread(target=streaming, daemon=True)


def steamVideo():
    while True:
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
        thread.start()
        app.run(use_reloader=False, debug=True, host="0.0.0.0")
    finally:
        print("Close")
        thread.join(0)
        cap.release()
        video.release()

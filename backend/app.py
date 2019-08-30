from flask import Flask, send_from_directory, request, jsonify, render_template
from flask_uploads import UploadSet, configure_uploads, IMAGES
import time
import os
from process_easy import run
from flask_socketio import SocketIO
import json


app = Flask(__name__)
image_path = "images/"
os.makedirs(image_path, exist_ok=True)
app.config["SECRET_KEY"] = "secret"  # need change!!
app.config["UPLOADED_PHOTOS_DEST"] = image_path
photos = UploadSet("photos", IMAGES)
configure_uploads(app, (photos))
sock = SocketIO(app)


# use static path to server images
@app.route("/images/<path:path>")
def getImage(path):
    rep = send_from_directory(image_path, path)
    os.remove(os.path.join(image_path, path))
    return rep


@app.route("/", methods=["GET"])
def main():
    return render_template("camera.html")


@app.route("/upload", methods=["POST"])
def upload():
    """
    Upload by POST

    Parameters
    ----------
    photo: binary
        Binary image data.

    Returns
    -------
    json
        result is result of text.
        resimg is result id of image, you can get the image data from
        https://your_url:443/images/id
    """

    # read image and save
    filename = photos.save(request.files["photo"],
                           name="{}.".format(time.time()))

    # process
    text, resimg = get_process(filename)

    # return
    return jsonify({"result": text,
                    "resimg": resimg})


@sock.on("upload")
def sockUpload(req):
    """
    Upload images by web socket io
    Same parameters and returns as above
    """
    # read image and save
    filename = "{}.jpg".format(time.time())
    new_file = open("./images/" + filename, "wb")
    new_file.write(req["photo"])
    new_file.close()

    # process
    text, resimg = get_process(filename)

    # return
    return json.dumps({"result": text,
                       "resimg": resimg})


# run and result
def get_process(filename):
    print("Process", filename)
    text, resimg = run("./images/", filename)
    os.remove("./images/" + filename)
    return text, resimg


if __name__ == "__main__":
    """
    You can uncomment one of below for testing
    """
    # sock.run(app, debug=True, host="0.0.0.0")
    sock.run(app, debug=True, host="0.0.0.0",
             certfile="cert.pem", keyfile="privkey.pem")
    # app.run(debug=True, host="0.0.0.0")
    # app.run(debug=True, host="0.0.0.0",
    #         ssl_context=("cert.pem", "privkey.pem"))

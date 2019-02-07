from flask import Flask, send_from_directory, request, jsonify, render_template
from flask_uploads import UploadSet, configure_uploads, IMAGES
import time
import os
from process_easy import run


app = Flask(__name__)
image_path = 'images/'
os.makedirs(image_path, exist_ok=True)
app.config['SECRET_KEY'] = 'secret'  # need change!!
app.config['UPLOADED_PHOTOS_DEST'] = image_path
photos = UploadSet('photos', IMAGES)
configure_uploads(app, (photos))

# use static path to server images
@app.route('/images/<path:path>')
def getImage(path):
    rep = send_from_directory(image_path, path)
    os.remove(os.path.join(image_path, path))
    return rep


@app.route("/", methods=['GET', 'POST'])
def main():
    """
    Detect objects in image.
    Only POST will work.

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
    if request.method == 'GET':
        return render_template('camera.html')

    # read image
    filename = photos.save(request.files['photo'],
                           name='{}.'.format(time.time()))

    # run and result
    print("Process", filename)
    text, resimg = run('./images/', filename)
    os.remove('./images/' + filename)

    return jsonify({'result': text,
                    'resimg': resimg})


if __name__ == '__main__':
    # app.run(debug=True, host='0.0.0.0')
    app.run(debug=True, host='0.0.0.0', ssl_context=('cert.pem', 'key.pem'))

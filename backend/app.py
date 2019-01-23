from flask import Flask, send_from_directory, request, jsonify
from flask_uploads import UploadSet, configure_uploads, IMAGES
from flask_sqlalchemy import SQLAlchemy
import time
import os
from process import run, runBatch
# from process_easy import run


app = Flask(__name__)
image_path = 'images/'
app.config['SECRET_KEY'] = 'secret'  # need change!!
batch_size = 4
time_wait = 4
app.config['UPLOADED_PHOTOS_DEST'] = image_path
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////app/test.db'
db = SQLAlchemy(app)
photos = UploadSet('photos', IMAGES)
configure_uploads(app, (photos))

# debug
db.create_all()


class Image(db.Model):
    """
    define class that store image data temportary which make
    mode can run with batch

    If you first use it, init by
    python3 -c 'import app;app.db.create_all()'
    """
    id = db.Column(db.Integer, primary_key=True)
    origin_img = db.Column(db.String(80), unique=True, nullable=False)
    text = db.Column(db.String(80))
    result_img = db.Column(db.String(80), unique=True)
    running = db.Column(db.Boolean, nullable=False, default=False)

    def __repr__(self):
        return '<Image %r>' % self.origin_img


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
    if request.method == 'POST':
        # read image
        filename = photos.save(request.files['photo'],
                               name='{}.'.format(time.time()))

        # run and result
        print("Process", filename)
        text, resimg = run('./images/', filename)
        os.remove('./images/' + filename)

        return jsonify({'result': text,
                        'resimg': resimg})

    return jsonify({'result': 'Error! Not Implement'})


@app.route("/batch", methods=['POST'])
def mainBatch():
    """
    Detect objects in image batchly.
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
    filename = photos.save(request.files['photo'],
                           name='{}.'.format(time.time()))

    # insert to database
    print("GET", filename)
    img = Image(origin_img=filename)
    db.session.add(img)
    db.session.commit()

    # wait for someone clean it
    if len(Image.query.filter_by(running=False).all()) < batch_size:
        t = time.time()
        while not img.result_img and time.time() - t < time_wait:
            # print("WAIT", img.origin_img)
            db.session.refresh(img)  # this is very important to refresh
            time.sleep(0.1)

        print(img.result_img, Image.query.get(img.id).result_img)

    # clean it if enough images
    # or not more image coming
    if not img.result_img:
        # takeout
        q = Image.query.filter_by(running=False).all()[:batch_size]
        print("CLEAN UP", q)
        for qi in q:
            qi.running = True
            db.session.commit()
        # process
        texts, resimgs = runBatch([os.path.join('.', image_path)] * len(q),
                                  [qi.origin_img for qi in q])
        # store result
        for i in range(len(q)):
            q[i].text = texts[i]
            q[i].result_img = resimgs[i]
            db.session.commit()

    # remove from database
    text = img.text
    resimg = img.result_img
    db.session.delete(img)
    db.session.commit()
    os.remove(os.path.join(image_path, filename))

    return jsonify({'result': text,
                    'resimg': resimg})


if __name__ == '__main__':
    # app.run(debug=True, host='0.0.0.0')
    app.run(debug=False, host='0.0.0.0')

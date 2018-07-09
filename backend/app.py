from flask import Flask, render_template, send_from_directory, request, flash, jsonify
from flask_uploads import UploadSet, configure_uploads, IMAGES
from flask_sqlalchemy import SQLAlchemy
import time
import os
from process import run

app = Flask(__name__)
app.config['UPLOADED_PHOTOS_DEST'] = 'images'
app.config['SECRET_KEY'] = 'secret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.abspath('.') + '/test.db'
db = SQLAlchemy(app)
photos = UploadSet('photos', IMAGES)
configure_uploads(app, (photos))

@app.route('/images/<path:path>')
def getImage(path):
    return send_from_directory('images', path)

class Result(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text   = db.Column(db.String(100), nullable=False)
    # i'm not sure the limit of path length
    oriimg = db.Column(db.String(100), unique=True, nullable=False)
    resimg = db.Column(db.String(100), unique=True, nullable=False)

    def __repr__(self):
        return '<Result ID%r>' % id

@app.route("/", methods=['GET', 'POST'])
def main():
    if request.method == 'POST':
        needjson = False
        try:
            needjson = request.form['json'] != 'false'
            filename = photos.save(request.files['photo'],
                           name='{}.'.format(time.time()))
        except:
            return 'The upload was not allowed'

        print(filename)
        text, resimg = run('./images/', filename)
        db.session.add(Result(text=text, oriimg=filename, resimg=resimg))
        db.session.commit()
        if needjson:
            return jsonify({'text': text,
                            'resimg': '/images/' + resimg,
                            'oriimg': '/images/' + filename})

    results = Result.query.all()
    results.reverse()
    return render_template('result.html', results=results)

def init():
    db.create_all()

# if you first time use it
# python3 -c 'import app;app.init()'
if __name__ == '__main__':
    app.run(debug=True)

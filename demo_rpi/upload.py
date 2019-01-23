import requests
import shutil
import os


baseurl = 'http://192.168.1.2:443'
url = baseurl
# url = baseurl + '/batch'  # use batch
download_url = baseurl + '/images/'
save_path = 'images/'


def upload(name):
    files = {'photo': open(os.path.join(save_path, name), 'rb')}
    rep = requests.post(url, files=files)
    if rep.status_code != 200:
        print(rep.text)  # debug
        return {'result': 'error'}
    return rep.json()


def download(name, output_name=''):
    if not output_name:
        output_name = name
    response = requests.get(download_url + name, stream=True)
    with open(os.path.join(save_path, output_name), 'wb') as out_file:
        shutil.copyfileobj(response.raw, out_file)
    del response


def send(in_file, out_file):
    rep = upload(in_file)
    # just ignore if error and output original image
    if 'error' in rep['result']:
        return out_file
    download(rep['resimg'], out_file)
    return out_file


# debug
if __name__ == '__main__':
    send('image.jpg', 'test.jpg')

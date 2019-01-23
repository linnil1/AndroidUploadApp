import sys
import os
import cv2
import numpy as np
import time
from upload import send, save_path
import concurrent.futures
from datetime import datetime

# Sleep time is the time interval between camera captured two images.
sleep_time = 0.04
# Too many number of executor in pool will cause server overload.
pool_num = 32


# open camera (forced)
cap = cv2.VideoCapture(0)
while not cap.isOpened():
    print("Reset")
    time.sleep(0.1)
    cap = cv2.VideoCapture(0)


# main
tasks = []  # play as queue role
try:
    with concurrent.futures.ProcessPoolExecutor(pool_num) as executor:
        while True:
            # read and save image
            ret, frame = cap.read()
            frame = cv2.flip(frame, 1)
            randomname = datetime.now().strftime("%Y%m%d%H%S%f")
            randomname = 'image_' + randomname + '.jpg'
            cv2.imwrite(os.path.join(save_path, randomname), frame)

            # send to server
            # overwrite original image
            tasks.append(executor.submit(send, randomname, randomname))

            # wait for server processing
            for i in range(10):
                if tasks and tasks[0].done():
                    # server response OK
                    print('get', tasks[0].result())
                    name = tasks[0].result()
                    del tasks[0]

                    # show result
                    frame = cv2.imread(os.path.join(save_path, name))
                    cv2.imshow('name', frame)
                    # remove result image after show
                    os.remove(os.path.join(save_path, name))

                    if cv2.waitKey(1) & 0xFF == ord('q'):
                        cap.release()
                        sys.exit()

                # wait for sleep_time
                time.sleep(sleep_time / 10)
finally:
    cap.release()

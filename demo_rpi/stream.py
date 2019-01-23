import sys
import os
import cv2
import numpy as np
import time
from upload import send, save_path
import concurrent.futures
from datetime import datetime

# Sleep time is the time interval between camera captured two images.
sleep_time = 0.1
# Too many number of executor in pool will cause server overload.
pool_num = 16
# Number of frame for delay
delay_num = 0


# open camera (forced)
cap = cv2.VideoCapture(0)
while not cap.isOpened():
    print("Reset")
    time.sleep(0.1)
    cap = cv2.VideoCapture(0)


# main
tasks = []  # play as queue role
num = 0
accnum = 0
now = time.time()
try:
    with concurrent.futures.ProcessPoolExecutor(pool_num) as executor:
        while True:
            ts = time.time()
            # read and save image
            ret, frame = cap.read()
            frame = cv2.flip(frame, 1)
            randomname = datetime.now().strftime("%Y%m%d%H%S%f")
            randomname = 'image_' + randomname + '.jpg'
            cv2.imwrite(os.path.join(save_path, randomname), frame)

            # send to server
            # overwrite original image
            accnum += 1
            tasks.append(executor.submit(send, randomname, randomname))

            # wait for server processing
            while time.time() - ts < sleep_time:
                if tasks and accnum > delay_num and tasks[0].done():
                    # server response OK
                    print('get', tasks[0].result())
                    name = tasks[0].result()
                    del tasks[0]
                    accnum -= 1

                    # show result
                    frame = cv2.imread(os.path.join(save_path, name))
                    frame = cv2.resize(frame, (0, 0), fx=2, fy=2)
                    cv2.imshow('name', frame)
                    num += 1
                    # remove result image after show
                    os.remove(os.path.join(save_path, name))

                    if cv2.waitKey(1) & 0xFF == ord('q'):
                        cap.release()
                        sys.exit()

            if time.time() - now > 10:
                print("FPS", num / (time.time() - now))
                now = time.time()
                num = 0
finally:
    cap.release()

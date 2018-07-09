import numpy as np
import cv2

def run(path, name):
    img = cv2.imread(path + name)
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    newname = "res_" + name
    cv2.imwrite(path + newname, hsv)

    return "result HSV:", newname

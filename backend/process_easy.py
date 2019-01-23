import numpy as np
import matplotlib.pyplot as plt
import os

def run(path, name):
    img = plt.imread(os.path.join(path, name))
    img = np.array(img)
    img[:, :, 0] = img[:, :, 2]
    img[:, :, 1] = img[:, :, 1]
    img[:, :, 2] = img[:, :, 0]

    newname = "res_" + name
    plt.imsave(os.path.join(path, newname), img)

    return "text result", newname

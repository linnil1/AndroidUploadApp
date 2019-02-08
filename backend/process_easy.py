import numpy as np
import matplotlib.pyplot as plt
import os

def run(path, name):
    img = plt.imread(os.path.join(path, name))
    img = np.array(img)
    img = np.fliplr(img)
    newname = "res_" + name
    plt.imsave(os.path.join(path, newname), img)

    return "text result", newname

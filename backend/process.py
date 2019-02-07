# modified from https://github.com/eriklindernoren/PyTorch-YOLOv3/blob/master/detect.py
from __future__ import division

from models import *
from utils.utils import *
from utils.datasets import *

import os
import sys
import time
import datetime
import argparse

import torch
from torch.utils.data import DataLoader
from torchvision import datasets
from torch.autograd import Variable

import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.ticker import NullLocator

parser = argparse.ArgumentParser()
parser.add_argument('--image_folder', type=str, default='data/samples', help='path to dataset')
parser.add_argument('--config_path', type=str, default='config/yolov3.cfg', help='path to model config file')
parser.add_argument('--weights_path', type=str, default='weights/yolov3.weights', help='path to weights file')
parser.add_argument('--class_path', type=str, default='data/coco.names', help='path to class label file')
parser.add_argument('--conf_thres', type=float, default=0.8, help='object confidence threshold')
parser.add_argument('--nms_thres', type=float, default=0.4, help='iou thresshold for non-maximum suppression')
parser.add_argument('--batch_size', type=int, default=1, help='size of the batches')
parser.add_argument('--n_cpu', type=int, default=8, help='number of cpu threads to use during batch generation')
parser.add_argument('--img_size', type=int, default=416, help='size of each image dimension')
parser.add_argument('--use_cuda', type=bool, default=True, help='whether to use cuda if available')
opt = parser.parse_args('')  # you can change configuarion above
print(opt)

cuda = torch.cuda.is_available() and opt.use_cuda

os.makedirs('output', exist_ok=True)

# Set up model
model = Darknet(opt.config_path, img_size=opt.img_size)
model.load_weights(opt.weights_path)

if cuda:
    model.cuda()

model.eval()  # Set in evaluation mode

classes = load_classes(opt.class_path)  # Extracts class labels from file

Tensor = torch.cuda.FloatTensor if cuda else torch.FloatTensor

cmap = plt.get_cmap('tab20b')
colors = [cmap(i) for i in np.linspace(0, 1, 20)]


def padImage(img_path):
    img = np.array(Image.open(img_path))
    h, w, _ = img.shape
    dim_diff = np.abs(h - w)
    # Upper (left) and lower (right) padding
    pad1, pad2 = dim_diff // 2, dim_diff - dim_diff // 2
    # Determine padding
    pad = ((pad1, pad2), (0, 0), (0, 0)) if h <= w else ((0, 0), (pad1, pad2), (0, 0))
    # Add padding
    input_img = np.pad(img, pad, 'constant', constant_values=127.5) / 255.
    # Resize and normalize
    input_img = resize(input_img, (opt.img_size, opt.img_size, 3), mode='reflect')
    # Channels-first
    input_img = np.transpose(input_img, (2, 0, 1))
    # As pytorch tensor
    input_img = torch.from_numpy(input_img).float()
    return input_img


def run(path, name):
    # Get detections
    torch.cuda.empty_cache()
    with torch.no_grad():
        input_imgs = padImage(os.path.join(path, name))[None].type(Tensor)
        detections = model(input_imgs)
        detections = non_max_suppression(detections, 80, opt.conf_thres, opt.nms_thres)
        detections = detections[0].cpu()
        input_imgs = None

    # Create plot
    img = np.array(Image.open(os.path.join(path, name)))
    fig, ax = plt.subplots(1)
    ax.imshow(img)

    # The amount of padding that was added
    pad_x = max(img.shape[0] - img.shape[1], 0) * (opt.img_size / max(img.shape))
    pad_y = max(img.shape[1] - img.shape[0], 0) * (opt.img_size / max(img.shape))
    # Image height and width after padding is removed
    unpad_h = opt.img_size - pad_y
    unpad_w = opt.img_size - pad_x

    # Draw bounding boxes and labels of detections
    if detections is not None:
        unique_labels = detections[:, -1].unique()
        n_cls_preds = len(unique_labels)
        bbox_colors = random.sample(colors, n_cls_preds)
        for x1, y1, x2, y2, conf, cls_conf, cls_pred in detections:

            # print('\t+ Label: %s, Conf: %.5f' %
            #       (classes[int(cls_pred)], cls_conf.item()))

            # Rescale coordinates to original dimensions
            box_h = ((y2 - y1) / unpad_h) * img.shape[0]
            box_w = ((x2 - x1) / unpad_w) * img.shape[1]
            y1 = ((y1 - pad_y // 2) / unpad_h) * img.shape[0]
            x1 = ((x1 - pad_x // 2) / unpad_w) * img.shape[1]

            if x1 >= img.shape[1] or y1 >= img.shape[0]:
                continue
            if box_h < 0 or box_w < 0:
                continue
            if x1 + box_w >= img.shape[1]:
                box_w = img.shape[1] - x1
            if y1 + box_h >= img.shape[0]:
                box_h = img.shape[0] - y1
            if x1 < 0:
                box_w += x1
                x1 = 0
            if y1 < 0:
                box_h += y1
                y1 = 0

            color = bbox_colors[int(np.where(unique_labels == int(cls_pred))[0])]
            # Create a Rectangle patch
            bbox = patches.Rectangle((x1, y1), box_w, box_h,
                                     linewidth=2,
                                     edgecolor=color,
                                     facecolor='none')
            # Add the bbox to the plot
            ax.add_patch(bbox)
            # Add label
            ax.text(x1, y1, s=classes[int(cls_pred)],
                    color='white',
                    verticalalignment='top',
                    bbox={'color': color, 'pad': 0})

    # Save generated image with detections
    ax.axis('off')
    ax.xaxis.set_major_locator(NullLocator())
    ax.yaxis.set_major_locator(NullLocator())
    newname = 'res_' + name
    fig.savefig(os.path.join(path, newname),
                bbox_inches='tight', pad_inches=0.0)
    plt.close()
    return str(len(detections)) + " results", newname


if __name__ == '__main__':
    run("images/", "1548228638.6999013.jpg")

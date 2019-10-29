import cv2


video_name = "video.avi"
fps = 20
video_size = (640, 480)


fourcc = cv2.VideoWriter_fourcc(*"XVID")
video = cv2.VideoWriter(video_name, fourcc, fps, video_size)
cap = cv2.VideoCapture(0)
while not cap.isOpened():
    print("Reset")
    time.sleep(0.1)
    cap = cv2.VideoCapture(0)


if __name__ == "__main__":
    try:
        while True:
            _, frame = cap.read()
            frame = cv2.flip(frame, 1)
            video.write(frame)
            cv2.imshow('frame', frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

    finally:
        print("Close")
        cap.release()
        video.release()
        cv2.destroyAllWindows()

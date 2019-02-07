// Grab elements, create settings, etc.
var video = document.getElementById('video');
var canvas = document.getElementById('canvas');
var context = canvas.getContext('2d');
var text = document.getElementById('text')
var texterror = document.getElementById('texterror')


// clean everything
resultEnd()

// start camera
if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: true }).then(function(stream) {
        video.srcObject = stream;
        video.play();
    });
}


// click the video
video.addEventListener("click", function() {
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0, video.videoWidth, video.videoHeight);
    video.pause();
    video.style.display = 'none';
    canvas.style.display = 'block';
    imageProcess(resultShow);
});

// click to start camera again
canvas.addEventListener("click", resultEnd);

// input: src of image, and pure text
function resultShow(img, txt) {
    var image = new Image();
    image.onload = function() {
        context.drawImage(image, 0, 0, canvas.width, canvas.height);
    };
    image.src = img;
    text.hidden = null;
    text.textContent = txt;
}

function errorShow(txt) {
    texterror.hidden = null;
    texterror.textContent = txt;;
}

function resultEnd() {
    canvas.style.display = 'none';
    video.style.display = 'block';
    text.hidden = true;
    texterror.hidden = true;
    video.play();
}

"use strict";

// Grab elements, create settings, etc.
var video = document.getElementById("video");
var canvas = document.getElementById("canvas");
var tmpcanvas = document.getElementById("tmpcanvas");
var context = canvas.getContext("2d");
var tmpcontext = tmpcanvas.getContext("2d");
var text = document.getElementById("text");
var texterror = document.getElementById("texterror");
var resultButton = document.getElementById("result_button");
var historyDiv = document.getElementById("history");
var camera_choose = document.getElementById("cameraid");

// when status = true, start record
var videoStatus = false;
// this three variable is for video
var num = 0;
var queueNum = [];
var queueResult = {};

// mode
// click -> caputre by clicking
// video -> all image are captured
var mode = "click";
var patience = 3; // 3 seconds
var flipHorizon = false;
// set fps
var fps = 1 / 25;
var no_hidden_video = false;

// Find camera
navigator.mediaDevices.enumerateDevices().then(devices => {
    devices = devices.filter(device => device.kind === "videoinput")
    for (var i=0; i<devices.length; ++i) {
        const option = document.createElement("option");
        option.value = devices[i].deviceId;
        option.text = devices[i].label || "camera " + i;
        camera_choose.appendChild(option);
    }
}).catch(() => alert("Cannot Find Camera ID") );


// clean everything
resultEnd();
showCards();
videoStatus = false;

// Draw start button
var p = new Path2D("M11,10 L18,13.74 18,22.28 11,26 M18,13.74 L26,18 26,18 18,22.28");
canvas.width = 350;
canvas.height = 350;
context.fillStyle = "#000";
context.fillRect(0, 0, canvas.width, canvas.height);
context.fillStyle = "#FFF";
context.scale(10, 10);
context.stroke(p);
context.fill(p);


// listen settings
document.getElementById("flip").addEventListener("input", (evt) => {
    flipHorizon = evt.target.checked;
});

camera_choose.addEventListener("input", () => {
    video.srcObject.getTracks().forEach(track => track.stop());
    enableCamera(() => {});
});


// Click to start
canvas.addEventListener("click", function handler() {
    enableCamera(init);
    this.removeEventListener('click', handler);
});


function enableCamera(fun) {
    if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        var constraints = {
              'video': {deviceId: camera_choose.value ? {exact: camera_choose.value} : undefined},
        };
        navigator.mediaDevices.getUserMedia(constraints).then(stream => {
            video.srcObject = stream;
            videoStatus = true;
            fun();
            // video.play();
        })
        .catch(error => alert(error.name + ": " + error.message));
    }
    else
        alert("No Camera detected");
};


// init
function init() {
    console.log("Init");
    // setting
    mode = document.getElementById("mode").value;
    fps = 1 / parseFloat(document.getElementById("fps").value);
    no_hidden_video = document.getElementById("no_hidden").checked;

    // Do not need tmp for clicking mode
    if (mode == "click") {
        tmpcanvas = canvas;
        tmpcontext = context;
    }

    if (no_hidden_video) {
        video.hidden = null;
        canvas.hidden = true;
    }

    // capture image to canvas
    setInterval(() => {
        if (!(mode == "click" && !videoStatus)) {
            tmpcanvas.width = video.videoWidth;
            tmpcanvas.height = video.videoHeight;
            if (flipHorizon) {
                tmpcontext.translate(video.videoWidth, 0);
                tmpcontext.scale(-1, 1);
            };
            tmpcontext.drawImage(video, 0, 0, video.videoWidth, video.videoHeight);
        }
        // When video mode, put all data into queue
        if (mode == "video") {
            // put into queue
            if (queueNum.length > patience / fps) return;
            queueNum.push(num);
            var nowNum = num;
            num += 1;
            imageProcess(
                (img, txt) => {
                    queueResult['a' + nowNum] = [img,txt];
                }, (txt) => {
                    queueResult['a' + nowNum] = [txt];
                }
            );
        }
    }, 1000 * fps);


    // Clicking mode: wait for human to get image and check result
    if (mode == "click") {
        // click to toggle video
        if (no_hidden_video) {
            function toggle() {
                videoStatus = !videoStatus;
                if (!videoStatus) {
                    video.hidden = true;
                    canvas.hidden = null;
                    imageProcess(resultShow, errorShow);
                }
                else {
                    video.hidden = null;
                    canvas.hidden = true;
                    resultEnd();
                }
            };
            video.addEventListener("click", toggle);
            canvas.addEventListener("click", toggle);
        }
        else
            canvas.addEventListener("click", () => {
                videoStatus = !videoStatus;
                if (!videoStatus)
                    imageProcess(resultShow, errorShow);
                else
                    resultEnd();
            });

        // save to local storage if ok
        document.getElementById("button_ok").addEventListener("click", () => {
            var img = canvas.toDataURL("image/jpeg");
            var txt = text.textContent;
            var arr = [];
            if (localStorage.getItem("arr"))
                arr = JSON.parse(localStorage.getItem("arr"));
            arr.push([img, txt]);
            localStorage.setItem("arr", JSON.stringify(arr));
            console.log(localStorage);
            resultEnd();
            showCards();
        });
        // default is No
        document.getElementById("button_no").addEventListener("click", resultEnd);
        // clear all history
        document.getElementById("history_clear").addEventListener("click", () => {
            historyDiv.innerHTML = "";
            localStorage.setItem("arr", JSON.stringify([]));
        });
    }

    // Video mode: read result in the queue
    else {
        setInterval(() => {
            if (queueResult['a' + queueNum[0]]) {
                var res = queueResult['a' + queueNum[0]];
                var nowNum = queueNum.shift();
                console.log(res);
                resultEnd();
                if (res.length > 1)
                    resultShow(res[0], res[1]);
                else
                    errorShow(res[0]);
                delete queueResult['a' + nowNum];
            }
        }, 100 * fps);
    }
}


// input: src of image, and pure text
function resultShow(img, txt) {
    var image = new Image();
    image.onload = () => {
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        context.drawImage(image, 0, 0, canvas.width, canvas.height);
    };
    image.src = img;
    text.hidden = null;
    text.textContent = txt;
    if (mode == "click")
        resultButton.style.display = "";
}


function errorShow(txt) {
    texterror.hidden = null;
    texterror.textContent = txt;
}


function resultEnd() {
    text.hidden = true;
    texterror.hidden = true;
    resultButton.setAttribute("style", "display:none !important");
    videoStatus = true;
    // video.play();
}


function showCards() {
    /* 
    <div class="card" style="width: 18rem;">
        <img class="card-img-top" src="" alt="Card image cap">
        <div class="card-body">
            <h5 class="card-title">Card title</h5>
      </div>
    </div>
    */ 
    // brute force
    historyDiv.innerHTML = "";
    var s = '<div class="card" style="width: 18rem;"> <img class="card-img-top" src="{0}" alt="Card image cap"> <div class="card-body"> <h5 class="card-title">{1}</h5> </div> </div>';
    var datas = JSON.parse(localStorage.getItem("arr"));
    if (!datas) return;
    for (var i = 0; i < datas.length; ++i) {
        var inhtml = s.replace("{0}", datas[i][0])
                      .replace("{1}", datas[i][1]);
        historyDiv.insertAdjacentHTML("beforeend", inhtml);
    }
}

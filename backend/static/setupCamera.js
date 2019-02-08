// Grab elements, create settings, etc.
var video = document.getElementById('video');
var canvas = document.getElementById('canvas');
var tmpcanvas = document.getElementById('tmpcanvas');
var context = canvas.getContext('2d');
var tmpcontext = tmpcanvas.getContext('2d');
var text = document.getElementById('text');
var texterror = document.getElementById('texterror');
var resultButton = document.getElementById('result_button');
var historyDiv = document.getElementById('history')

// when status = true, start record
var videoStatus = true
// this three variable is for video
var num = 0;
var queueNum = []
var queueResult = {}

// mode
// click -> caputre by clicking
// video -> all image are captured
var mode = "click";
// set fps
const fps = 1 / 25;


// Do not need tmp for clicking mode
if (mode == "click") {
    tmpcanvas = canvas;
    tmpcontext = context;
}


// clean everything
resultEnd();
showCards();


// start camera
if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices.getUserMedia({ video: true }).then(function(stream) {
        video.srcObject = stream;
        video.play();
    });
}


// capture image to canvas
var instanceInterval = setInterval(() => {
    if (!(mode == "click" && !videoStatus)) {
        tmpcanvas.width = video.videoWidth;
        tmpcanvas.height = video.videoHeight;
        tmpcontext.drawImage(video, 0, 0, video.videoWidth, video.videoHeight);
    }
    // When video mode, put all data into queue
    if (mode == "video") {
        // put into queue
        queueNum.push(num);
        var nowNum = num;
        imageProcess(
            (img, txt) => {
                queueResult[nowNum] = [img,txt]
            }, (txt) => {
                queueResult[nowNum] = [txt]
            }
        )
        num += 1;
    }
}, 1000 * fps);


// Clicking mode: click to start or stop
if (mode == "click")
    canvas.addEventListener("click", function() {
        videoStatus = !videoStatus
        if (!videoStatus)
            imageProcess(resultShow, errorShow);
        else
            resultEnd()

    });
// Video mode: read result in the queue
else
    setInterval(() => {
        if (queueResult[queueNum[0]]) {
            var res = queueResult[queueNum[0]];
            var nowNum = queueNum.shift();
            console.log(res);
            if (res.length > 1)
                resultShow(res[0], res[1]);
            delete queueResult[nowNum]
        }
    }, 100 * fps)


// set interative mode for click mode
if (mode == "click") {
    // save to local storage
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


// input: src of image, and pure text
function resultShow(img, txt) {
    var image = new Image();
    image.onload = function() {
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
    resultButton.setAttribute('style', 'display:none !important');
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
    for (var i = 0; i < datas.length; ++i) {
        var inhtml = s.replace("{0}", datas[i][0])
                      .replace("{1}", datas[i][1]);
        historyDiv.insertAdjacentHTML('beforeend', inhtml);
    }
}

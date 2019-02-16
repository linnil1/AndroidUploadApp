const url = window.location.href;
const timeout = 2000;

var getImageUrl = (resimg) => url + "images/" + resimg;

function imageProcess(func, errorfunc) {
    tmpcanvas.toBlob((blob) => {
        // upload
        if (blob == null) {
            console.log("Fail");
            errorfunc("Not Init");
            return
        }
        httpGetAsync(blob, (rep) => {
            console.log(rep);
            func(getImageUrl(rep.resimg), rep.result);
        }, errorfunc);
    }, "image/jpeg");
}


function httpGetAsync(img, callback, errorcallback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.timeout = timeout;
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4) {
            if (xmlHttp.status == 200)
                callback(JSON.parse(xmlHttp.responseText));
            // timeout error
            else if (xmlHttp.status == 0)
                errorcallback("TIMEOUT ERROR");
            // server error
            else
                errorcallback(xmlHttp.statusText);
        }
    }
    xmlHttp.open("POST", url, true); // true for asynchronous
    var form = new FormData();
    form.append("photo", img, 'tmp.jpg');
    xmlHttp.send(form);
}

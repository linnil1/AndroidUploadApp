const url = "https://192.168.1.3:5000";
const timeout = 1000;

var getImageUrl = (resimg) => url + "/images/" + resimg

function imageProcess(resultShow) {
    canvas.toBlob((blob) => {
        // upload
        httpGetAsync(blob, (rep) => {
            console.log(rep);
            // image.src = "https://blog.mozilla.org/firefox/files/2017/12/firefox-logo-300x310.png"
            resultShow(getImageUrl(rep.resimg), rep.result);
        })
    }, "image/jpeg");
}


function httpGetAsync(img, callback)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.timeout = timeout;
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4)
            if (xmlHttp.status == 200)
                callback(JSON.parse(xmlHttp.responseText));
            // timeout error
            else if (xmlHttp.status == 0)
                errorShow("TIMEOUT ERROR");
            // server error
            else
                errorShow(xmlHttp.statusText);
    }
    xmlHttp.open("POST", url, true); // true for asynchronous
    var form = new FormData();
    form.append("photo", img, 'tmp.jpg');
    xmlHttp.send(form);
}

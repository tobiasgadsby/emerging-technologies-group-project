const sosButton = document.getElementById("sosButton");
const cancelButton = document.getElementById("cancelButton");
const iphoneScreen = document.getElementById("iphoneScreen");
const homeView = document.getElementById("homeView");
const activeView = document.getElementById("activeView");

let isActive = false;

let lat = 51.481285;
let long = -3.180642;

sosButton.addEventListener("click", async () => {
    isActive = true;
    updateUI();

    getMediaRecorder();

    try {
        const position = await getLocation();
        lat = position.coords.latitude;
        long = position.coords.longitude;

    } catch (error) {
        console.error("Failed to get coordinates", error);
    }

    await getNotifications();
});

cancelButton.addEventListener("click", () => {
    isActive = false;
    updateUI();
});

function updateUI() {
    if (isActive) {
        iphoneScreen.classList.add("active");
        homeView.classList.add("hidden");
        activeView.classList.remove("hidden");
    } else {
        iphoneScreen.classList.remove("active");
        activeView.classList.add("hidden");
        homeView.classList.remove("hidden");
    }
}

function getLocation() {
    return new Promise((resolve, reject) => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(resolve, reject);
        } else {
            reject(new Error("Geolocation is not supported by this browser."));
        }
    });
}

function getMediaRecorder() {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {

        console.log("getUserMedia supported.");

        navigator.mediaDevices
            .getUserMedia({
                audio: true
            })

            .then((stream) => {
                const mediaRecorder = new MediaRecorder(stream);
                const chunks = [];

                mediaRecorder.addEventListener("dataavailable", (e) => {
                    if (e.data && e.data.size > 0) {
                        chunks.push(e.data);

                        fetch("/audio", {
                            method: "POST",
                            headers: {
                                'Content-Type': mediaRecorder.mimeType,
                            },
                            body: new Blob(chunks, { type: mediaRecorder.mimeType })
                        });
                    }
                });

                mediaRecorder.start();

                const intervalVal = setInterval(() => {
                    if (isActive) {
                        mediaRecorder.requestData();
                    } else {
                        mediaRecorder.stop();
                        clearInterval(intervalVal);
                    }
                }, 5000);
            })

            .catch((err) => {
                console.error(`The following getUserMedia error occurred: ${err}`);
            });

    } else {
        console.log("getUserMedia not supported on your browser!");
    }
}

async function getNotifications() {
    const notificationsInterval = setInterval(async () => {
        if (isActive) {
            const response = await fetch("/notifications");
            const data = await response.json();

            console.log(data)

            if (data !== null) {

                let inner = "";

                for (const notification of data) {
                    inner += `<div class="notification-item">${notification}</div>`
                }

                document.getElementById("notificationsPanel").innerHTML = inner;

            }

        } else {
            clearInterval(notificationsInterval);
        }
    }, 1000);
}
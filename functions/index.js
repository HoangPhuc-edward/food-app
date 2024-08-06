const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendNotification = functions.https.onCall((data, context) => {
  const uid = data.params1;
  const type = data.params2;

  const titleList = {
    "ready": "Your order is ready",
    "shipping": "Your order is shipping",
    "shipping_failed": "Cannot find shipper",
    "finished": "Your order is finished",
    "canceled": "Your order is canceled",
  };

  const bodyList = {
    "ready": "Your order has been confirmed by the store!",
    "shipping": "Shipper found, your order is being delivered!",
    "shipping_failed": "Cannot find shipper to deliver your order!",
    "finished": "Your order has been delivered!",
    "canceled": "Your order has been canceled!",
  };
  return admin.database().ref(`/User/${uid}/token`)
      .once("value")
      .then((snapshot) => {
        const token = snapshot.val();

        if (token) {
          const message = {
            notification: {
              title: titleList[type],
              body: bodyList[type],
            },
            token: token,
          };

          return admin.messaging().send(message)
              .then((response) => {
                console.log("Successfully sent test message:", response);
                return "Message sent";
              })
              .catch((error) => {
                console.log("Error sending test message:", error);
                return "Error sending message";
              });
        } else {
          console.log("No token available for user:", uid);
          return "No token available";
        }
      });
});

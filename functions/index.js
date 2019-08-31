"use strict";
const functions = require("firebase-functions");
const admin = require("firebase-admin");
var hash = require("object-hash");
admin.initializeApp();

// const txns = [
//   "tx1",
//   "tx2",
//   "tx3",
//   "tx4",
//   "tx5",
//   "tx6",
//   "tx7",
//   "tx8",
//   "tx9",
//   "tx10",
//   "tx11"
// ];

// exports.snapshot = functions.database
//   .ref("TxnList")
//   .onCreate((snap, context) => {
//     const messageSnap = snap.val();
//     console.log(messageSnap);
//   });

// exports.addMerkleRoot = functions.https.onRequest(async (req, res) => {
//   // Grab the text parameter.
//   const currentRoot = findMerkleRoot(txs);
//   // Push the new message into the Realtime Database using the Firebase Admin SDK.

//   const snapshot = await admin
//     .database()
//     .ref("/merkleRoot")
//     .push({ original: original });
//   // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
//   res.redirect(303, snapshot.ref.toString());
// });

// return admin
//     .database()
//     .ref("TxnList")
//     .once("value")
//     .then(snapshot => {
//       console.log("hello");
//       console.log(snapshot.val());
//       return snapshot.val();
//     })
//     .catch(err => {
//       console.log(err);
//     });

exports.merkleRoot = functions.https.onRequest(async (request, response) => {
  const txns = [
    "tx1",
    "tx2",
    "tx3",
    "tx4",
    "tx5",
    "tx6",
    "tx7",
    "tx8",
    "tx9",
    "tx10",
    "tx11"
  ];

  var txnlist = [];

  txnlist = admin
    .database()
    .ref("TxnList")
    .once("value")
    .val();

  const merkle = await findMerkleRoot(txnlist);
  response.send(merkle);
  return merkle;
});

function findMerkleRoot(txs) {
  let hashedtxs = [],
    branches = [],
    branchCounter = 0,
    merkleRoot;

  if (txs.length === 0) {
    console.log(`There are no transactions to be summarized`);
  } else {
    console.log(`Transactions to be summarized: ${txs}`);
    makeEvenElements(txs);
    console.log(`Leaf nodes: ${txs}`);

    for (const tx of txs) {
      hashedtxs.push(hash(hash(tx)));
    }
    console.log("Leaf nodes hashed using double-SHA256 algorithm:");
    console.log(hashedtxs);

    const rep = Math.ceil(Math.log2(txs.length));

    for (let i = 0; i <= rep; i += 1) {
      if (hashedtxs.length === 1) {
        merkleRoot = hashedtxs[0];
        console.log(`The Merkle Root is ${merkleRoot}`);
      } else if (branches.length === 1) {
        merkleRoot = branches[0];
        console.log(`The Merkle Root is ${merkleRoot}`);
      } else if (hashedtxs.length > 1 && branches.length === 0) {
        hashedtxs.forEach((item, index) => {
          if (index % 2 === 0) {
            branches.push(
              hash(hash(hashedtxs[index].concat(hashedtxs[index + 1])))
            );
          }
        });
        console.log(`Branch: ${(branchCounter += 1)}`);
        console.log(branches);
        if (branches.length > 1 && branches.length % 2 !== 0) {
          makeEvenElements(branches);
          console.log(`Now, Branch ${branchCounter} is: `);
          console.log(branches);
        }
      } else if (branches.length > 0) {
        let branchesCopy = Array.from(branches);
        branches = [];
        branchesCopy.forEach((item, index) => {
          if (index % 2 === 0) {
            branches.push(
              hash(hash(branchesCopy[index].concat(branchesCopy[index + 1])))
            );
          }
        });
        console.log(`Branch: ${(branchCounter += 1)}`);
        console.log(branches);
        if (branches.length > 1 && branches.length % 2 !== 0) {
          makeEvenElements(branches);
          console.log(`Now, Branch ${branchCounter} is: `);
          console.log(branches);
        }
      }
    }
  }
  return merkleRoot;
}

function makeEvenElements(arr) {
  if (arr.length > 1 && arr.length % 2 !== 0) {
    console.log(`Cannot build Merkle Tree for odd number of data elements.`);
    console.log(
      `Duplicating the last transaction to achieve an even number of data elements.`
    );
    arr.push(arr[arr.length - 1]);
  }
}

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//   console.log("hello");
//   response.send("Hello from Firebase!");
// });

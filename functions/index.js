const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const database = admin.database();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.getCredentials = functions.https.onCall(async request => {
	try {
		if (!('phoneNumber' in request)) return {
			error: "no number provided"
		}
		const phoneNumber = request['phoneNumber'];
		console.log("phoneNumber " + phoneNumber);
		if (!('organization' in request)) return {
			error: "no organization provided"
		}
		const organization = request['organization'];
		console.log("organization " + organization);
		
		let credential = {email: "error", password: "error"};
		const dRef = database.ref("root").child("ManagerList")/*.equalTo(organization, "groupName")*/;
		console.log('dRef ' + dRef);
		const anything = await dRef.once("value");
		anything.forEach(child => {
			console.log(child.val());
			console.log(child.val()['groupName']);
			const childX = child.val();
			if ('phoneNumbers' in childX) {
				for (let i = 0; i < childX['phoneNumbers'].length; i++) {
					console.log(childX['phoneNumbers'][i]);
					if (childX['phoneNumbers'][i] === phoneNumber) {
						credential = childX;
						break;
					}
				}
			}
		});
		
		console.log(anything);
		return {
			email: credential['email'],
			password: credential['password']
		}
	} catch (error) {
		console.log(error);
		return {
			error: error
		}
	}
});

exports.getCredentialsById = functions.https.onCall(async request => {
	try {
		if (!('phoneNumber' in request)) return {
			error: "no number provided"
		}
		
		const phoneNumber = request['phoneNumber'];
		console.log("phoneNumber " + phoneNumber);
		
		if (!('groupId' in request)) return {
			error: "no groupId provided"
		}
		
		const organizationID = request['groupId'];
		console.log("organization " + organizationID);
		
		if (!('licensePlate' in request)) return {
			error: "no licensePlate provided"
		}
		
		const licensePlate = request['licensePlate'];
		console.log("licensePlate " + licensePlate);
		
		let credential = {email: "error", password: "error"};
		const dRef = database.ref("root").child("ManagerList")/*.equalTo(organization, "groupName")*/;
		console.log('dRef ' + dRef);
		const anything = await dRef.once("value");
		anything.forEach(child => {
			console.log(child.val());
			console.log(child.val()['groupId']);
			const childX = child.val();
			if ('phoneNumbers' in childX && childX['groupId'] === organizationID && childX['licensePlate'] === licensePlate) {
				for (let i = 0; i < childX['phoneNumbers'].length; i++) {
					console.log(childX['phoneNumbers'][i]);
					if (childX['phoneNumbers'][i] === phoneNumber) {
						credential = childX;
						break;
					}
				}
			}
		});
		if (credential['email'] === 'error' || credential['password'] === 'error') return {
			error: "not found"
		}
		console.log(anything);
		return {
			email: credential['email'],
			password: credential['password']
		}
	} catch (error) {
		console.log(error);
		return {
			error: error
		}
	}
});

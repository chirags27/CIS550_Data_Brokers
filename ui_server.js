var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var ObjectId = require('mongodb').ObjectID;
var urlDb = 'mongodb://localhost:27017/test';

//mongoimport --db test --collection user_data --drop --file user_dataset.json
/* id = "1"
name = "Chirag"
password ="databrokers"
gender = "M"
email = "chiragshah271093@gmail.com"  */
var body = '';
var searchPage;
var htmlFile;

var http = require('http'),
   fs = require('fs'),
   url = require('url'),
   exp = require('express'),
   qs = require('querystring');

fs.readFile('./dataBrokers.html', function(err, data) {
    if (err){
        throw err;
    }
    htmlFile = data;
});

fs.readFile('./search.html', function(err, data) {
    if (err){
        throw err;
    }
    searchPage = data;
});
var insertUserDetails = function(db, name,email, age, password,callback) 
{
   
	db.collection('user_data').insert( {

		"name" :name , "_id" :email, "age" :age ,"password" :password  

	   }, function(err, result) {
		//assert.equal(err, null);
		//console.log("Inserted a document into the restaurants collection.");
		callback();
	  });
};

var findUserDetails = function(db, email, password, response, callback) {
	var cursor = db.collection('user_data').find( { "_id" : email, "password" : password } );
	var iter = 0;
	cursor.each(function(err, doc) {
		
      //assert.equal(err, null);
		if(iter == 0) 
		{
			
			
			if (doc != null) 
			{
				console.log("User with credentials: " + email + " and " + password + " has successfully registered");
				iter++;
				//console.dir(doc);
				response.writeHeader(200, {"Content-Type": "text/html"});
				response.write(searchPage);
				response.end();
				console.log("****************** SUCCESSFULLY LOGGED IN ************************");
				callback();
			} 
			else 
			{
				iter++;
				console.log("user with credentials "  + email + " and " + password + " does not exists");
				response.writeHeader(200, {"Content-Type": "text/html"});
				response.write(htmlFile);
				response.end();
				console.log("****************** COULD NOT SUCCESSFULLY LOG IN ************************");
				callback();
			}
		}
   });
};


	console.log("************** SERVER STARTED ************ ");
    http.createServer(function(request, response) {
		
		
    	var myPath = url.parse(request.url).pathname;
    	   //console.log(myPath);

        if(request.method == 'POST')
        {
			console.log("inside post for: " + myPath);
			body = "";
            request.on('data', function (data) {
                body +=data;
				body.trim();
            });
            request.on('end',function() {
				// console.log(body);
                var credentials =  qs.parse(body);
				// console.log(credentials);
				var typeOfCall = credentials.submit;
				if(typeOfCall == 'SignUp')
				{
					console.log("****************** SIGNING UP ************************");
					var userEnteredName = credentials.signUpNam;
					var userEnteredEmail = credentials.signUpEmail;
					var userEnteredAge = credentials.signUpAge;
					var userEnteredPassword = credentials.signUpPassword;
					console.log("Name entered : " + userEnteredName);
					console.log("Email entered : " + userEnteredEmail);
					console.log("Age entered : " + userEnteredAge);
					console.log("Password entered : " + userEnteredPassword);
					
					// Add into DB if user does not exists
					
					MongoClient.connect(urlDb, function(err, db) {
					  //assert.equal(null, err);
					  insertUserDetails(db, userEnteredName, userEnteredEmail, userEnteredAge , userEnteredPassword, function() {
						  db.close();
					  });
					});

					response.writeHeader(200, {"Content-Type": "text/html"});
					response.write(htmlFile);
					
					response.end();
					console.log("****************** SUCCESSFULLY SIGNED UP ************************");
				}
				else if(typeOfCall == 'Login') {
					
					console.log("Body: " + body);
					console.log("****************** LOGGING IN ************************");
					var userEnteredEmail = credentials.loginEmail;
					var userEnteredPassword = credentials.loginPass;
					
					console.log("************** AUTHENTICATING ****************");
					// check into db whether the user exists or not
					MongoClient.connect(urlDb, function(err, db) {
					  //assert.equal(null, err);
					findUserDetails(db, userEnteredEmail, userEnteredPassword, response, function() {
							db.close();
					  });
					});
					
				}
				else 
				{
					console.log("Body: " + body);
					console.log(typeOfCall);
					console.log("pppppppppppppppp");
					response.end();
				}
                
            });
        }
        else 
		{	
			/* console.log(request.method);
			console.log(myPath); */
			if(myPath == '/favicon.ico')
			{
				// response.writeHeader(200, {"Content-Type": "text/html"});
		  //   	response.write(htmlFile);
				// response.end();
				// console.log("Done1");
			}
			else if(myPath == '/' || myPath == 'iden')
			{
				response.writeHeader(200, {"Content-Type": "text/html"});
				response.write(htmlFile);
				response.end();
				// console.log("Done1");
			}
			else
			{

				var file_data = fs.readFileSync('.'+myPath)
				// console.log(myPath);
				response.writeHeader(200, {"Content-Type": "text/css"});
				response.write(file_data);
				response.end();
				 
			}
		}
         
    }).listen(8080);
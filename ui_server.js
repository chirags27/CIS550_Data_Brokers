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
var resultFile;

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
            /* console.log("inside post for: " + myPath); */
            body = "";
            request.on('data', function (data) {
                body +=data;
                body.trim();
            });
            request.on('end',function() {
                // console.log(body);
                var credentials =  qs.parse(body);
                /* console.log(body);
                console.log(credentials); */
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
                    /* console.log("Body: " + body); */
                    if(credentials.search != null && credentials.search.split("[\s+]") != null)
                    {
                        var access = credentials.mode;
                        var pwd = credentials.permissions;
                        var query = credentials.search.split(" ");
                        var runSingleQuery = "/usr/bin/java -jar /home/ubuntu/DataBrokers/JARS_EC2/query.jar " + query[0];
                        var runDoubleQuery = "/usr/bin/java -jar /home/ubuntu/DataBrokers/JARS_EC2/query.jar " + query[0] + " " + query[1];
                        if(access == "Root" && pwd == "admin") 
						{
                            console.log("Root access granted");
                            runSingleQuery = runSingleQuery + " root";
                            runDoubleQuery = runDoubleQuery + " root";
                        }
                        else 
						{
                            console.log("Base access granted!!");
                            runSingleQuery = runSingleQuery + " base";
                            runDoubleQuery = runDoubleQuery + " base";
                        }
                        
                        if(query.length == 1)
                        {
                            console.log(query[0]);
                            var exec = require('child_process').exec, child;
							
                            child = exec(runSingleQuery, function (error, stdout, stderr)
							{
								stdout = stdout.trim();
								/* console.log("..............." + stdout); */
								if(stdout == "Nothing stored") 
								{
									var singleFileOutput = "<!DOCTYPE html><html style=\"color: white;\"><h1><center>No Results found... </center></h1><head><meta charset=\"utf-8\" /><title>Final Result6</title>"+
										"<link rel=\"stylesheet\" href=\"assets/css/styles.css\" /></head></html>";
									response.writeHeader(200, {"Content-Type": "text/html"});
                                    response.write(singleFileOutput);
									response.end();
									console.log("No results found..." );
								}
								else 
								{
									var op = stdout.split(",");
									var temp_count = 1;
									
									//console.log('stderr: ' + stderr);
									if(error !== null) 
									{
										console.log('exec error: ' + error);
									}
									else 
									{
									   var singleFileOutput = "<!DOCTYPE html><html><h1><center>Following are the nodes that contain: " + query[0] + "</center></h1><head><meta charset=\"utf-8\" /><title>Final Result6</title>"+
											"<link rel=\"stylesheet\" href=\"assets/css/styles.css\" /></head>" + 
											"<body style=\"color: white;\">"
										while(temp_count != op.length)
										{
											/* console.log(op[temp_count]); */
											singleFileOutput = singleFileOutput + "<br><br> >> " + op[temp_count];
											temp_count = temp_count + 1;
										}
										singleFileOutput = singleFileOutput + "</body></html>";
										response.writeHeader(200, {"Content-Type": "text/html"});
										response.write(singleFileOutput);
										response.end();
										console.log('Output of Query evaluator: ' + op.toString());
										console.log("************ Displaying single final Result ***************");
										
									}
								}
							});
                        }
                        else if(query.length >= 2)
                        {
                            /* console.log(query[0]);
                            console.log(query[1]);
                            console.log("The query is: " + runDoubleQuery); */
                            var exec = require('child_process').exec, child;
                            child = exec(runDoubleQuery, function (error, stdout, stderr) {
                                console.log('Output of Query evaluator: ' + stdout);
                                //console.log('stderr: ' + stderr);
                                if(error !== null)
                                {
                                    console.log('exec error: ' + error);
                                }
                                else 
								{
									stdout = stdout.trim();
									/* console.log("..............." + stdout); */
									if(stdout == "No relation between the entered stuff") 
									{
										var singleFileOutput = "<!DOCTYPE html><html><title>No Result</title><body background=\"assets/img/bg7.jpg\" style=\"background-repeat: no-repeat; color: white;  background-size: cover;\"><h1><center>No Results found... </center></h1></html>";
										response.writeHeader(200, {"Content-Type": "text/html"});
										response.write(singleFileOutput);
										response.end();
										console.log("No results found..." );
									}
									else
									{
										fs.readFile('./result.html', function(err, data) {
                                        if (err){
                                            throw err;
                                        }
                                        resultFile = data;
                                        response.writeHeader(200, {"Content-Type": "text/html"});
                                        response.write(resultFile);
                                        console.log("************ Displaying final Result ***************");
                                        response.end();
                                    });
									}
                                }
                            });
                        }
                    }
                    //console.log(typeOfCall);
                    console.log("PROCESSING...");
                    console.log("PROCESSING...");
                    console.log("PROCESSING...");
                    console.log("PROCESSING...");
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
          //    response.write(htmlFile);
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

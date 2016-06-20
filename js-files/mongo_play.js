var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var ObjectId = require('mongodb').ObjectID;
var url = 'mongodb://localhost:27017/test';


//mongoimport --db test --collection user_data --drop --file user_dataset.json
id = "1"
name = "Chirag"
password ="databrokers"
gender = "M"
email = "chiragshah271093@gmail.com" 


var insertUserDetails = function(db, id, name, password,  gender, email, callback) {
   
db.collection('user_data').insert( {

   		"id" :id , "name" :name , "password" :password ,"gender" : gender , "_id" :email

   }, function(err, result) {
    //assert.equal(err, null);
    //console.log("Inserted a document into the restaurants collection.");
    callback();
  });
};




var findUserDetails = function(db, email ,callback) {
   var cursor =db.collection('user_data').find( { "_id" : email } );
   cursor.each(function(err, doc) {
      //assert.equal(err, null);
      if (doc != null) {
         //console.dir(doc);
         console.log("Email ID:  " + doc._id);
         console.log("Name: " + doc.name);
      } else {
         callback();
      }
   });
};

MongoClient.connect(url, function(err, db) {
  //assert.equal(null, err);
  insertUserDetails(db, id, name, password, gender , email,function() {
      db.close();
  });
});

MongoClient.connect(url, function(err, db) {
  //assert.equal(null, err);
  findUserDetails(db, email,function() {
      db.close();
  });
});



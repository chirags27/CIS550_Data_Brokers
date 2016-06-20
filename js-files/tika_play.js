node_id = 5000;

var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var ObjectId = require('mongodb').ObjectID;
var url = 'mongodb://localhost:27017/test';
var tika = require('tika');
var snowball = require('node-snowball');

var HashMap = require('hashmap');

var map  = new HashMap();
// tika.text('/home/chirags/Desktop/Resume_Chirag_M_Shah.pdf', function(err, text) {
// 	console.log(text);
// });

filename_ = '/home/chirags/Desktop/Resume_Chirag_M_Shah.pdf';

var options_pdf = {
 	// Hint the content-type. This is optional but would help Tika choose a parser in some cases. 
	contentType: 'application/pdf'
};

var options_pdf = {
 	// Hint the content-type. This is optional but would help Tika choose a parser in some cases. 
	contentType: 'application/pdf'
};





tika.extract(filename_, options_pdf ,function(err, data, meta) {

	db.collection('ext_table').insert( {

   		"_id" :node_id , "key" : 'author' , "value" : meta.Author.toString()  ,"parent_id" : node_id -2 , "" :email

   }, function(err, result) {

  });
};

	//map.set('filename', filename_);
	//map.set('author', meta.Author.toString() );
	//map.set('text', data.toString());

	//console.log(map);
	//console.log(data);
	console.log(snowball.stemword(arr));

});

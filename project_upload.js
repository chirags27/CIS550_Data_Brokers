var AWS = require('aws-sdk'); 
var fs = require('fs');

 var s3 = new AWS.S3(); 



// AWS.config.update(
//   {
//     accessKeyId: "AKIAIRZG7PNLRNL3K4MA",
//     secretAccessKey: "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",
//   }
// );


//  s3.createBucket({Bucket: 'databucketcis550'}, function() {

//   var params = {Bucket: 'databucketcis550', Key: 'myKey', Body: 'Hello!'};

//   s3.putObject(params, function(err, data) {

//       if (err)       

//           console.log(err)     

//       else       console.log("Successfully uploaded data to myBucket/myKey");   

//    });

// });



// Read in the file, convert it to base64, store to S3
var filename = 'hello.txt';

fs.readFile(filename.toString(), function (err, data) {
  if (err) { throw err; }



  var s3 = new AWS.S3();
  s3.putObject({
    Bucket: 'databucketcis550',
    Key: filename.toString(),
    Body: data
  }, function (err) {
    if (err) { throw err; }
  });

});

s3.getObject(
  { Bucket: "databucketcis550", Key: filename.toString()},
  function (err, data) {
    if (err != null) {
      console.log(err);
    } else {
      console.log(data.Body.toString('ascii'));
      // do something with data.body
    }
  }
);
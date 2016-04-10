var AWS = require('aws-sdk'); 

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



s3.getObject(
  { Bucket: "databucketcis550", Key: "myKey.txt" },
  function (err, data) {
    if (err != null) {
      console.log(err);
    } else {
      console.log(data.Body.toString('ascii'));
      // do something with data.body
    }
  }
);
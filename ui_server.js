var http = require('http'),
   fs = require('fs'),
   url = require('url'),
   exp = require('express'),
   qs = require('querystring');

// app.use(express.bodyParser());

// app.post('/', function(request, response){
//     console.log(request.body);
//     console.log(request.body);
// });

var body = ' ';

fs.readFile('./dataBrokers.html', function(err, data) {
    if (err){
        throw err;
    }
    htmlFile = data;
});


    http.createServer(function(request, response) {
    	var myPath = url.parse(request.url).pathname;
    	   //console.log(myPath);

        if(request.method == 'POST')
        {
            request.on('data', function (data) {
                body +=data;
            });
            request.on('end',function(){
                var POST =  qs.parse(body);
                
                console.log(POST);
            });
        }
        else
        {

    	if(myPath == '/favicon.ico')
    	{
    		// response.writeHeader(200, {"Content-Type": "text/html"});
      //   	response.write(htmlFile);
      //   	response.end();
        	// console.log("Done1");
    	}
    	else if(myPath == '/')
    	{
    		response.writeHeader(200, {"Content-Type": "text/html"});
        	response.write(htmlFile);
        	response.end();
        	// console.log("Done1");
    	}
    	else
    	{

    		
			var file_data = fs.readFileSync('.'+myPath);
			console.log(myPath);
			response.writeHeader(200, {"Content-Type": "text/css"});
        	response.write(file_data);
        	response.end();
        	 
    	}
    }
         
    }).listen(8080);
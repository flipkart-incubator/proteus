var http = require('http');
var fs = require('fs');
var Path = require('path');

http.createServer(function (request, response) {

  var path = '.' + request.url;

  if (path == './') {
    path = './index.html';
  }

  var ext = Path.extname(path);
  var contentType = 'text/html';

  switch (ext) {
    case '.js':
      contentType = 'text/javascript';
      break;
    case '.css':
      contentType = 'text/css';
      break;
    case '.json':
      contentType = 'application/json';
      break;
    case '.png':
      contentType = 'image/png';
      break;
    case '.jpg':
      contentType = 'image/jpg';
      break;
    case '.wav':
      contentType = 'audio/wav';
      break;
  }

  fs.readFile(path, function(error, content) {
    if (error) {
      if(error.code == 'ENOENT') {
        response.writeHead(404, { 'Content-Type': 'application/json' });
        response.end(JSON.stringify({ error : 'File Not Found'}), 'utf-8');
        console.error(404 + ' : ' + path);
      } else {
        response.writeHead(500);
        response.end(JSON.stringify({ error: 'Sorry, check with the site admin for error: ' + error.code }), 'utf-8');
        console.error(500 + ' : ' + path);
      }
    } else {
      response.writeHead(200, { 'Content-Type': contentType });
      response.end(content, 'utf-8');
      console.log(200 + ' : ' + path);
    }
  });
}).listen(8080);
console.log('Server running at http://127.0.0.1:8080/');

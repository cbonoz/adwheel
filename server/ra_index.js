// index.js
// https://scotch.io/tutorials/a-realtime-animal-chat-app-using-node-webkit-socket-io-and-mean
// Import all our dependencies
var express  = require('express');
var mongoose = require('mongoose');
var cors = require('cors');
var app      = express();
var bodyParser = require('body-parser');
var RateLimit = require('express-rate-limit');
 
// Start rateLimiter
var WINDOW_MS = 10*(60*1000);
var limiter = new RateLimit({
  windowMs: WINDOW_MS, // 10 minutes 
  max: 1000, // limit each IP to 1000 requests per windowMs 
  delayMs: 0 // disable delaying - full speed until the max limit is reached 
});
 
//  apply to all requests 
app.use(limiter);
app.enable('trust proxy'); // only if you're behind a reverse proxy (Heroku, Bluemix, AWS if you use an ELB, custom Nginx setup, etc) 
// END rateLimiter
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
// Enable all cors requests.
app.use(cors());

var io       = require('socket.io')(server);

var DB_NAME = "random-animals";
var PLAY_EVENT = "playevent";
var JOIN_EVENT = "joinevent";
var LEADER_EVENT = "leaderevent";

mongoose.connect("mongodb://127.0.0.1:27017/" + DB_NAME);

// create a schema for animal sounds.
var AnimalSchema = mongoose.Schema({
  userId: {type: String},
  username: String,
  animal: String,
  created: { type: Date, default: Date.now },
  count: { type: Number, default: 1} 
});

// create a model from the animal sounds schema
var Animal = mongoose.model('Animal', AnimalSchema);

// allow CORS
app.all('*', function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-type,Accept,X-Access-Token,X-Key');
  if (req.method == 'OPTIONS') {
    res.status(200).end();
  } else {
    next();
  }
});

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

var server = require('http').Server(app);

// Test endpoint.
app.get('/ra/hello', function(req, res) {
  res.json({"hello": "world"});
});

// Retrieve a list of users that have listened to the particular animal. 
app.post('/ra/animal', function(req, res) {
  //Find
  Animal.find({'animal': req.body.animal.toLowerCase()})
    .sort({count: -1})
    .exec(function(err, msgs) {
      res.json(msgs);
  });
});

// Get list of animal sound plays associated with the given userId.
app.post('/ra/userId', function(req, res) {
  //Find
  Animal.find({'userId': req.body.userId.toLowerCase()})
    .sort({count: -1})
    .exec(function(err, msgs) {
        console.log('callback of userId: ' + msgs);
        res.json(msgs);
  });
});

// Change username associated with android or user id.
app.post('/ra/username', function(req, res) {
  //Find
  var newUsername = req.body.username;
  var targetObj = {userId: req.body.userId}
  Animal.update(targetObj, {username: newUsername}, {multi: true}, 
    function(err, num) {
      res.json({status: "updated: " + JSON.stringify(num)})
    }
  );
})

// Increment the count for a particular user and animal by 1.
app.post('/ra/increment', function(req, res) {
  //Find
  var targetObj = {
    'userId': req.body.userId.toLowerCase(),
    'animal': req.body.animal.toLowerCase()
  };
  Animal.findOneAndUpdate(targetObj, 
    { $set: { "userId" : targetObj.userId, "animal" : targetObj.animal, "username": req.body.username}, $inc : { "count" : 1 } },
    { sort: { "count" : 1 }, upsert: true, setDefaultsOnInsert: true, returnNewDocument : true })
    .exec(function(err, db_res) { 
      if (err) { 
        // throw err; 
        res.json({status: err})
      } else { 
        console.log(db_res); 
        res.json({status: db_res});
      } 
  });
});

/*||||||||||||||||SOCKET|||||||||||||||||||||||*/
//Listen for connection
// io.on('connection', function(socket) {
//     var defaultAnimal = "cat"
//   //Emit the animals array
// //   socket.emit('setup', {
// //     animals: animals
// //   });

//   Animal.find({
//         'animal': data.animal 
//     }).exec(function(err, msgs) {
//         socket.emit("playevent", {
//             animals: msgs
//         });
//   });

//   //Listens for new user
//   socket.on(LEADER_EVENT, function(data) {
//     data.animal = defaultAnimal;
//     //New user joins the default animal
//     socket.join(defaultAnimal);
//     //Tell all those in the animal that a new user joined
//     io.in(defaultAnimal).emit('user joined', data);
//     Animal.find({
//         'animal': data.animal 
//     }).exec(function(err, msgs) {
//         io.in()
//     })
//   });

//   //Listens for switch animal
//   socket.on('switch animal', function(data) {
//     //Handles joining and leaving animals
//     //console.log(data);
//     socket.leave(data.oldAnimal);
//     socket.join(data.newAnimal);
//     io.in(data.oldAnimal).emit('user left', data);
//     io.in(data.newAnimal).emit('user joined', data);

//   });

//   //Listens for a new chat message
//   socket.on(PLAY_EVENT, function(data) {
//     var existingCount = 0;
//     var targetObj = {"userId": data.userId, "animal": data.animal};
//     Animal.findOneAndUpdate(targetObj, 
//     { $set: { "userId" : data.userId, "animal" : data.animal, "username": data.username}, $inc : { "count" : 5 } },
//     { sort: { "count" : 1 }, upsert:true, returnNewDocument : true });
//     // //Save it to database
//     // newMsg.save(function(err, msg){
//     //     //Send message to those connected in the animal
//     //     io.in(msg.animal).emit(PLAY_EVENT, msg);
//     // });
//     //Create message
    
  
//   });
// });
/*||||||||||||||||||||END SOCKETS||||||||||||||||||*/

/* Start Server */
// index.js
var PORT = 9001;
server.listen(PORT);
console.log('Server started on port: ' + PORT);

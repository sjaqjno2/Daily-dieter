var express = require('express'),
    http    = require('http'),
    path    = require('path');
    jwt     = require('jsonwebtoken');

var app = express();
var bodyParser = require('body-parser');

var datetime = require('node-datetime');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false}));

var server = http.createServer(app);
var socketIo = require('socket.io')
var io = socketIo(server);

server.listen(3000, '172.27.0.115');

console.log('Server running at http://172.27.0.115:3000/');

var mysql      = require('mysql');
var conn = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '1234',
  database : 'Dailydieter',
  charset : 'utf8',
  multipleStatements: true
});

conn.connect();

//chatting
var system = io.of('/daily');
var usernames={};
var numUsers = 0;
var secret = '12doo34gyu56yeon';
system.on('connection', function(socket){
  console.log('a user connected');

  socket.on('enterGroupRoom', function(groupName) {
    socket.join(groupName, function () {
      // let rooms = object.keys(sockets.rooms);
      socket.rooms = groupName;
      // console.log(rooms);
      console.log(socket.rooms);// [ <socket.id>, 'room 237' ]
    });
  });

  socket.on('getUserInfo', function(token){
    var decoded = jwt.verify(token, secret);
    var userid = decoded._id;
    socket.username = userid;
    usernames[userid]=userid;
    ++numUsers;
    addedUser = true;
    system.to(socket.rooms).emit('login', {username: socket.username, numUsers: numUsers});
    console.log('user is added to chat room');

    socket.broadcast.to(socket.rooms).emit('user joined', {username: socket.username, numUsers:numUsers});
  });

  socket.on('disconnect', function(){
    system.to(socket.rooms).emit('user left', {username: socket.username, numUsers:numUsers});
    --numUsers;
    socket.leave(socket.rooms);
    console.log('user disconnected');
  });

  socket.on('new message', function(mMessage){
    socket.broadcast.to(socket.rooms).emit('new message', {username: socket.username, message: mMessage.message, date: mMessage.date, time: mMessage.time, groupID: mMessage.groupID});
  });

  socket.on('typing', function(){
    socket.broadcast.to(socket.rooms).emit('typing', {username: socket.username});
  });
  socket.on('stop typing', function(){
    socket.broadcast.to(socket.rooms).emit('stop typing', {username: socket.username});
  });

});

app.post('/saveMessage', function(req, res){
      var secret = '12doo34gyu56yeon';
      var token = req.headers.authorization;
      var decoded = jwt.verify(token, secret);
      var id = decoded._id;
      var message = req.query.message;
      var date = req.query.date;
      var time = req.query.time;
      var groupID = req.query.groupID;

      var sql = 'INSERT INTO message (sender, message, date, time, groupID) VALUES(?, ?, ?, ?, ?)';
    conn.query(sql, [id, message, date, time, groupID],  function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        res.send("save_success");
        }
      });
});

app.post('/getMessage', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var groupID = req.query.groupID;
  var sql = 'SELECT * FROM message WHERE groupID = ? ORDER BY id DESC LIMIT 20';
  conn.query(sql, [groupID],  function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(result);
      }
    });
});


app.get('/getUserInfo', function(req, res) {
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var id = decoded._id;
  var sql = 'SELECT * FROM user WHERE id = ?'

  conn.query(sql, [id], function(err, rows, fields) {
    if(err) {
      console.log(err)
    }else {
      var result = JSON.stringify(rows);;
      res.send(result);
    }
  })
});


app.get('/getDietCalendar', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;
  var clickeddate = req.query.dateclicked;

  var sql = 'SELECT * FROM diet WHERE userid = ? AND date = ?'
  conn.query(sql, [userid, clickeddate], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.get('/getDietFromId', function(req, res){
  var secret = '12doo34gyu56yeon';
  var id = req.query.id;

  var sql = 'SELECT * FROM diet WHERE id = ?'
  conn.query(sql, [id], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/logintest', function(req, res) {

  var id = req.query.id;
  var password = req.query.password;
  var sql = 'SELECT * FROM user WHERE id = ?'
  var secret = '12doo34gyu56yeon';
  conn.query(sql, id, function(err, rows, fields) {
    user = rows[0];
    if(err) {
      console.log(err)
    }else {
      if(id===user.id) {
        if(password === user.password ) {
          jwt.sign( {
            _id: user.id,
            username: user.name,
            height: user.height,
            weight: user.weight,
            phone: user.phone,
            email: user.email,
            gender: user.gender
          },
          secret,
          {
            algorithm: 'HS256',
            expiresIn: '7h',
            issuer: 'Dailydieter',
            subject: 'userInfo'
          }, function(err, token) {

            if(err){
              console.log('Error occurred while generating token');
              console.log(err);
              return false;
            }
            else{
              if(token != false){
                //res.send(token);
                console.log('token');
                res.header();
                res.json({
                  // "results":
                  // {"status": "true"},
                  "token" : token
                  // "data" : results
                });
                res.end();
              }
              else{
                res.json({
                  "token" : 'Could not create token'
                });
                res.end();
              }

            }
          });
        }else {
          console.log('Not Correct password');
          res.json({
            "token" : 'Not correct password'
          });
          res.end();
        }
      }else{
        console.log('there is no user');
        res.json({
          "token" : 'There is no user'
        });
        res.end();
      }



    };
  });
});

app.get('/nutrient', function(req, res){
  var namefood = req.query.namefood;
  console.log(namefood);
  var sql = 'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ? ';
    conn.query(sql,namefood, function(err,rows,fields){
    if(err){
      console.log(err);
    }else{
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.get('/nutrientArray', function(req, res){
  var namefood1 = req.query.namefood1;
  var namefood2 = req.query.namefood2;
  var namefood3 = req.query.namefood3;
  var namefood4 = req.query.namefood4;
  var namefood5 = req.query.namefood5;
  console.log(namefood1);
  var sql = 'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ?;' +
  'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ?;' +
  'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ?;' +
  'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ?;' +
  'SELECT carbohydrate, protein, fat, salt, calorie, weightfood FROM nutrient WHERE namefood= ?;';
    conn.query(sql,[namefood1,namefood2,namefood3,namefood4,namefood5], function(err,rows,fields){
    if(err){
      console.log(err);
    }else{
      var result = JSON.stringify(rows);
      res.send(rows);
      console.log(rows);
    }
  });
});

app.get('/registerID', function(req, res) {
  var id = req.query.id;
  var password = req.query.password;
  var name = req.query.name;
  var height = req.query.height;
  var weight = req.query.weight;
  var phone = req.query.phone;
  var email = req.query.email;
  var gender = req.query.gender;

  var sql = 'INSERT INTO user (id, password, name, height, weight, phone, email, gender) VALUES(?, ?, ?, ?, ?, ?, ?, ?)';
  conn.query(sql, [id, password, name, height, weight, phone, email, gender], function(err, rows, fields) {
    if(err) {
      console.log(err);
    }else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});


app.post('/community', function(req, res) {
  var secret = '12doo34gyu56yeon';
  var title = req.query.title;
  var content = req.query.content;
  var count = 0;
  var token = req.headers.authorization;

  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var dt = datetime.create();
  var formatted = dt.format('m/d/Y H:M:S');

  var sql = 'INSERT INTO community (userid, title, content, date, count) VALUES(?, ?, ?, ?, ?)';

    conn.query(sql, [userid, title, content, formatted, count], function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        var result = JSON.stringify(rows);
        res.send(result);
        console.log(rows)
      }
    });

});


app.post('/getPost', function(req, res){
conn.query('SELECT * FROM community', function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});


app.post('/updatePost', function(req, res) {
  var new_title = req.query.new_title;
  var new_content = req.query.new_content;
  var original_title = req.query.original_title;
  var original_content = req.query.original_content;
  var sql = 'UPDATE community SET title = ?, content = ? WHERE title = ? AND content = ?';

  conn.query(sql, [new_title, new_content, original_title, original_content], function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        var result = JSON.stringify(rows);
        res.send(result);
        console.log(rows)
      }
    });
});

app.post('/deletePost', function(req, res){
  var title = req.query.title;
  var content = req.query.content;
  var sql = 'DELETE FROM community WHERE title = ? AND content = ?'

  conn.query(sql, [title, content], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/confirmWriter', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var title = req.query.title;
  var content = req.query.content;
  var sql = 'SELECT * FROM community WHERE title = ? AND content = ?'

  conn.query(sql, [title, content], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var post = rows[0];
      console.log("1");
      console.log(userid);
      console.log("2");
      console.log(post.userid);
      if(userid === post.userid){
        console.log("same_user");
        res.send('same_user');
      }else {
        console.log("not_same_user");
        res.send('not_same_user');
      }
    }
  });
});

app.get('/registerDiet', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var namefood1 = req.query.namefood1;
  var calorie1 = req.query.calorie1;
  var foodweight1 = req.query.foodweight1;
  var namefood2 = req.query.namefood2;
  var calorie2 = req.query.calorie2;
  var foodweight2 = req.query.foodweight2;
  var namefood3 = req.query.namefood3;
  var calorie3 = req.query.calorie3;
  var foodweight3 = req.query.foodweight3;
  var namefood4 = req.query.namefood4;
  var calorie4 = req.query.calorie4;
  var foodweight4 = req.query.foodweight4;
  var namefood5 = req.query.namefood5;
  var calorie5 = req.query.calorie5;
  var foodweight5 = req.query.foodweight5;
  var sum_carb = req.query.sum_carb;
  var sum_prot = req.query.sum_prot;
  var sum_fat = req.query.sum_fat;
  var sum_salt = req.query.sum_salt;
  var mealtime = req.query.mealtime;
  var dt = datetime.create();
  var formatted = dt.format('Y/m/d');

  var check_sql = 'SELECT * FROM diet WHERE userid =? AND date = ? AND mealtime =?'
  var sql = 'INSERT INTO diet (userid, namefood1, calorie1,foodweight1, namefood2, calorie2, foodweight2, namefood3, calorie3, foodweight3, namefood4, calorie4, foodweight4, namefood5, calorie5, foodweight5, sum_carbohydrate, sum_protein, sum_fat, sum_salt, mealtime, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)';

  conn.query(check_sql, [userid, date, mealtime], function (err, rows, fields) {
      if(err) {
        console.log(err);
        res.send('error happened');
        //res.send(''); 해서 error가 일어난 걸 보내서 안드로이드에서 진행 안되게 만들 것
      } else {
        var result = rows[0];
        if(result != null) {
          res.send("duplication insertion");
        } else {
          conn.query(sql, [userid, namefood1, calorie1, foodweight1, namefood2, calorie2, foodweight2, namefood3, calorie3, foodweight3, namefood4, calorie4, foodweight4, namefood5, calorie5, foodweight5, sum_carb, sum_prot, sum_fat, sum_salt, mealtime, formatted], function(err, rows, fields) {
            if(err) {
              console.log(err);
              res.send('error happened');
            } else {
              res.send("insert_success");
              console.log(rows)
            }
          });
        }
      }
  })
});
app.post('/countUp', function(req, res){
  var title = req.query.title;
  var content = req.query.content;
  var sql = 'UPDATE community SET count = count + 1 WHERE title = ? AND content = ?'
  conn.query(sql, [title, content], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.get('/getDiet', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var sql = 'SELECT * FROM diet WHERE userid = ?'
  conn.query(sql, [userid], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/deleteDiet', function(req, res){
  var id = req.query.id;
  var userid = req.query.userid;
  var mealtime = req.query.mealtime;
  var date = req.query.date;
  var sql = 'DELETE FROM diet WHERE id =? userid =? AND mealtime = ? AND date = ?'

  conn.query(sql, [id, userid, mealtime, date], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/getDietFromSelection', function(req, res){
  var userid = req.query.userid;
  var mealtime = req.query.mealtime;
  var date = req.query.date;
  var sql = 'SELECT * FROM diet WHERE userid = ? AND mealtime = ? AND date = ?'
  conn.query(sql, [userid, mealtime, date], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});



app.post('/getTipPost', function(req, res){
conn.query('SELECT * FROM tip', function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});

app.post('/getCelebrityPost', function(req, res){
conn.query('SELECT * FROM celebrity', function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});

app.post('/getGroup', function(req, res){
conn.query('SELECT * FROM group_info', function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});

app.get('/makeGroup', function(req, res){
  var secret = '12doo34gyu56yeon';
  var groupname = req.query.groupname;
  var groupgoal = req.query.groupgoal;
  var count = 1;

  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;
  var sql = 'INSERT INTO group_info (groupname, goal, usercount, producer) VALUES(?, ?, ?, ?)';

conn.query(sql, [groupname, groupgoal, count, userid], function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});

app.get('/changeGoal', function(req, res){
  var secret = '12doo34gyu56yeon';
  var groupname = req.query.groupname;
  var groupgoal = req.query.groupgoal;

  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;
  var sql = 'UPDATE group_info SET goal = ? WHERE groupname = ? AND producer = ? '
conn.query(sql, [groupgoal, groupname, userid], function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });

});


app.get('/checkParticipation', function(req, res){
  var secret = '12doo34gyu56yeon';
  var groupname = req.query.groupname;

  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;
  console.log('check come');
  var check_sql = 'SELECT * FROM group_user WHERE groupname = ? AND userid =?';
  conn.query(check_sql, [groupname, userid], function( err,rows,fields) {
    if(err) {
      console.log(err);
      res.send('error_happened');
    } else {
      var result = rows[0];
      console.log('result     ' + result);
      if(result != null) {
        console.log('send string');
        res.send('Already_participateGroup')
      } else {
        console.log('send string2');
        res.send('No_participation_user');
      }
    }
  });
});

app.get('/checkProducer', function(req, res){
  var secret = '12doo34gyu56yeon';
  var groupname = req.query.groupname;

  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var check_sql = 'SELECT * FROM group_info WHERE groupname = ?';
  conn.query(check_sql, [groupname], function( err,rows,fields) {
    if(err) {
      console.log(err);
      res.send('error_happened');
    } else {
      var result = rows[0];
      console.log(rows[0]);
      console.log(result.producer);
      if(userid === result.producer) {
        res.send('sameuser');
        console.log('sameuser');
      } else {
        res.send('not_sameuser');
        console.log('not_sameuser');
      }

    }
  });
});

app.get('/participateGroup', function(req, res){
  var secret = '12doo34gyu56yeon';
  var groupname = req.query.groupname;

  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var sql = 'INSERT INTO group_user (groupname, userid) VALUES(?, ?)';
  conn.query(sql, [groupname, userid], function(err, rows,fields){
    if(err){
      console.log(err);
      res.send('error_happened');
    } else {
      res.send('participate_success');
    }
  });
});

app.get('/userCountUp', function(req, res){
  console.log('들어옴');
  var groupname = req.query.groupname;
  var sql = 'UPDATE group_info SET usercount = usercount + 1 WHERE groupname = ?'
  conn.query(sql, [groupname], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});



app.post('/getTipFromSelection', function(req, res){
    var title = req.query.title;
    var sql = 'SELECT * FROM tip WHERE title = ?;'
    conn.query(sql, [title], function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        var result = JSON.stringify(rows);
        res.send(result);
        console.log(rows);
        }
    });
});

app.post('/getCelebrityFromSelection', function(req, res){
    var title = req.query.title;
    var sql = 'SELECT * FROM celebrity WHERE title = ?;'
    conn.query(sql, [title], function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        var result = JSON.stringify(rows);
        res.send(result);
        console.log(rows);
        }
    });
});







app.post('/tipCountUp', function(req, res){
  var title = req.query.title;
  var sql = 'UPDATE tip SET count = count + 1 WHERE title = ?;'
  conn.query(sql, [title], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/celebrityCountUp', function(req, res){
  var title = req.query.title;
  var sql = 'UPDATE celebrity SET count = count + 1 WHERE title = ?'
  conn.query(sql, [title], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});
app.post('/searchPost', function(req, res){
  console.log("select 1");
  var search = req.query.search;
console.log(search);
  var changeform = "%" + search+ "%";
  console.log(changeform);
  var sql = 'SELECT * FROM community WHERE title LIKE ?'
  conn.query(sql, [changeform], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/searchTip', function(req, res){
  var search = req.query.search;
  var changeform = "%" + search+ "%";
  var sql = 'SELECT * FROM tip WHERE title LIKE ?'
  conn.query(sql, [changeform], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

app.post('/searchCelebrity', function(req, res){
  var search = req.query.search;
  var changeform = "%" + search+ "%";
  var sql = 'SELECT * FROM celebrity WHERE title LIKE ?'
  conn.query(sql, [changeform], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});


app.post('/getTipFromSelection', function(req, res){
var sql = 'SELECT title, content FROM tip WHERE title = ?, content = ?';
var title = req.query.title;
var content = req.query.content;
conn.query(sql, [title, content], function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });
});

app.get('/getWishPost', function(req, res) {
    var secret = '12doo34gyu56yeon';
    var token = req.headers.authorization;
    var decoded = jwt.verify(token, secret);
    var id = decoded._id;
    var dashboard = req.query.dashboard;
    var sql = 'SELECT * FROM community WHERE (userid, title) IN (SELECT writerid, title FROM wish WHERE userid=? AND dashboard =?)';

    conn.query(sql, [id,dashboard], function (err, rows,fields) {
      if(err) {
        console.log(err);
      } else {
        var results = JSON.stringify(rows);
        res.send(results);
        console.log(results);
      }
    });
});

app.get('/getWishTip', function(req, res) {
    var secret = '12doo34gyu56yeon';
    var token = req.headers.authorization;
    var decoded = jwt.verify(token, secret);
    var id = decoded._id;
    var dashboard = req.query.dashboard;
    var sql = 'SELECT * FROM tip WHERE (userid, title) IN (SELECT writerid, title FROM wish WHERE userid=? AND dashboard =?)';

    conn.query(sql, [id,dashboard], function(err, rows, fields) {
        if(err) {
          console.log(err)
        } else {
          var result = JSON.stringify(rows);;
          res.send(result);
          console.log(rows);
        }
      })
});

app.get('/getWishCelebrity', function(req, res) {
    var secret = '12doo34gyu56yeon';
    var token = req.headers.authorization;
    var decoded = jwt.verify(token, secret);
    var id = decoded._id;
    var dashboard = req.query.dashboard;
    var sql = 'SELECT * FROM celebrity WHERE (userid, title) IN (SELECT writerid, title FROM wish WHERE userid=? AND dashboard =?)';

    conn.query(sql, [id,dashboard], function(err, rows, fields) {
        if(err) {
          console.log(err)
        } else {
          var result = JSON.stringify(rows);;
          res.send(result);
          console.log(rows);
        }
      })
});

app.post('/saveWishlist', function(req, res){
      var secret = '12doo34gyu56yeon';
      var token = req.headers.authorization;
      var decoded = jwt.verify(token, secret);
      var id = decoded._id;
      var title = req.query.title;
      var writerid = req.query.writerid;
      var dashboard = req.query.dashboard;


      var sql = 'INSERT INTO wish (userid, writerid, title, dashboard) VALUES(?, ?, ?, ?)';
    conn.query(sql, [id, writerid, title, dashboard],  function(err, rows, fields) {
      if(err) {
        console.log(err);
      } else {
        var result = JSON.stringify(rows);
        res.send(result);
        console.log(rows);
        }
      });
});

app.post('/deleteWish', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;
  var title = req.query.title;
  var writerid = req.query.writerid;
  var sql = 'DELETE FROM wish WHERE userid = ? AND title = ? AND writerid = ?';

conn.query(sql, [userid, title, writerid],  function(err, rows, fields) {
  if(err) {
    console.log(err);
  } else {
    var result = JSON.stringify(rows);
    res.send(result);
    console.log(rows);
    }
  });
});

app.post('/updateDiet', function(req,res) {
  console.log('update diet');
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var id = req.query.id;
  var namefood1 = req.query.namefood1;
  var calorie1 = req.query.calorie1;
  var namefood2 = req.query.namefood2;
  var calorie2 = req.query.calorie2;
  var namefood3 = req.query.namefood3;
  var calorie3 = req.query.calorie3;
  var namefood4 = req.query.namefood4;
  var calorie4 = req.query.calorie4;
  var namefood5 = req.query.namefood5;
  var calorie5 = req.query.calorie5;
  var sum_carb = req.query.sum_carb;
  var sum_prot = req.query.sum_prot;
  var sum_fat = req.query.sum_fat;
  var sum_salt = req.query.sum_salt;

  var sql = 'UPDATE diet SET namefood1 = ?, calorie1 =?, namefood2 = ?, calorie2 =?, namefood3 = ?, calorie3 =?, namefood4 = ?, calorie4 =?, namefood5 = ?, calorie5 =?, sum_carbohydrate = ?, sum_protein = ?, sum_fat =?, sum_salt = ? WHERE id = ?';
  conn.query(sql, [namefood1, calorie1, namefood2, calorie2, namefood3, calorie3, namefood4, calorie4, namefood5, calorie5, sum_carb, sum_prot, sum_fat, sum_salt, id], function (err, rows, fields) {
    if(err) {
      console.log(err)
    } else {
res.send("update_success");
    }

  });
});

app.get('/sticker', function(req, res){
  var secret = '12doo34gyu56yeon';
  var token = req.headers.authorization;
  var decoded = jwt.verify(token, secret);
  var userid = decoded._id;

  var sql = 'SELECT date, mealtime FROM diet WHERE userid = ?'
  conn.query(sql, [userid], function(err, rows, fields) {
    if(err) {
      console.log(err);
    } else {
      var result = JSON.stringify(rows);
      res.send(result);
      console.log(rows);
    }
  });
});

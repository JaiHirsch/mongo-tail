start mongod --replSet s0 --dbpath c:\data\tailable\repl_mongo1 --port 37017 --smallfiles --oplogSize 100
start mongod --replSet s0 --dbpath c:\data\tailable\repl_mongo2 --port 37018 --smallfiles --oplogSize 100
start mongod --replSet s0 --dbpath c:\data\tailable\repl_mongo3 --port 37019 --smallfiles --oplogSize 100

start mongod --dbpath c:\data\tailable\no_repl_mongo --port 27018 --smallfiles --oplogSize 100

PING 1.1.1.1 -n 1 -w 5000 >NUL

REM  connect to one server and initiate the set
start mongo --port 37017 --eval "config = { _id: 's0', members:[{ _id : 0, host : 'localhost:37017' },{ _id : 1, host : 'localhost:37018' },{ _id : 2, host : 'localhost:37019' }]};rs.initiate(config)"
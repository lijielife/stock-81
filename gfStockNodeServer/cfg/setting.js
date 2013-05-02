var fs = require('fs')

var setting={};

//log
setting.log={};
//setting.log.stream=fs.createWriteStream(__dirname + '/../log/app.log'); //process.stdout;
setting.log.stream=process.stdout; //process.stdout;
setting.log.level='debug';
setting.log.dateFormat="yyyy-mm-dd HH:MM:ss";

module.exports = setting;
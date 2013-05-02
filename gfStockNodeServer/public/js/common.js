function formatPencent(p){
	var p=p*100;
	return formatDouble(p)+"%";
}

function formatDouble(p){
	var str=p+"";
	var out=str;
	
	var dotIndex = str.indexOf(".");
	
	if(dotIndex==-1){
		return str+".00";
	}
	
	var shouldIndex=dotIndex+3;
	
	if(shouldIndex<=str.length){
		out=str.substr(0,shouldIndex);
	}else{
		var zeroNum=shouldIndex-str.length;
		for(var i=0;i<zeroNum;i++){
			out+="0";
		}
	}
	
	return out;
}

function formatMoney(v){
	if(v=="-") return "-";
	return formatDouble(v/100.0);
}

function parseUrlParam() {
  // This function is anonymous, is executed immediately and 
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    	// If first entry with this name
    if (typeof query_string[pair[0]] === "undefined") {
      query_string[pair[0]] = pair[1];
    	// If second entry with this name
    } else if (typeof query_string[pair[0]] === "string") {
      var arr = [ query_string[pair[0]], pair[1] ];
      query_string[pair[0]] = arr;
    	// If third or later entry with this name
    } else {
      query_string[pair[0]].push(pair[1]);
    }
  } 
   return query_string;
};
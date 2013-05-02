/**  直线图  **/

function Point(x,y,index){
	this.x=x;
	this.y=y;
	this.index=index;
}

function LineChart(){
	this.leftTime=0;
	this.rightTime=-1;
	this.yTime=60000;//minute,hour,day
	this.maxTime=3600000;//1hour 30 minute
	this.yUnitNum=10;
	
	this.data=[];
	
	this.yMax=0;
	
	//style
	this.xLineStyle="#6d6c6c";
	this.yLineStyle="#6d6c6c";
	this.lineStyle="#0000ff";
	this.xLabelStyle="#444"
	this.yLabelStyle="#444"
}
			
LineChart.prototype.addPoint=function(x,y,index){
	var p = new Point(x,y,index);
	if(this.rightTime<x){
		this.data.push(p);
	}else if(this.leftTime>x){
		this.data.splice(0,0,p);
	}else{
		//find position
		var i=0;
		for(i=0;i<this.data.length;i++){
			if(this.data[i].x==x){
				return;
			}else if(this.data[i].x>x){
				break;
			}
		}	
		this.data.splice(i-1,0,p);
	}

	if(this.rightTime<x){
		this.rightTime=x;
	}
	if(x<this.leftTime){
		this.leftTime=x;
	}
}


LineChart.prototype.draw=function(x0,y0,w,h,context){
	this.drawXY(x0,y0,w,h,context);
	this.drawXLabel(x0,y0,w,h,context);
	this.drawYLabel(x0,y0,w,h,context);
	this.drawChart(x0,y0,w,h,context);
	
	
}

LineChart.prototype.drawXLabel=function(x0,y0,w,h,context){
	context.beginPath();
	context.strokeStyle = "#ccc";
	
	//rightTime

	var leftTime =  this.rightTime-this.yTime;
	
	context.fillStyle=this.xLabelStyle;
	context.font = "3pt Calibri";
	if(this.yTime==60000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%5;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00:20";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-5000;
		for(var t=rightTime;t>minTime;t-=5000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%15000==0){
				var txt=$.format.date(new Date(t), "HH:mm:ss");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}else if(this.yTime==300000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%30;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00:00";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-30000;
		for(var t=rightTime;t>minTime;t-=30000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%60000==0){
				var txt=$.format.date(new Date(t), "HH:mm:ss");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}else if(this.yTime==600000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%60;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-60000;
		for(var t=rightTime;t>minTime;t-=60000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%60000==0){
				var txt=$.format.date(new Date(t), "HH:mm");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}else if(this.yTime==1200000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%60;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-60000;
		for(var t=rightTime;t>minTime;t-=60000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%120000==0){
				var txt=$.format.date(new Date(t), "HH:mm");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}else if(this.yTime==1800000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%120;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-60000;
		for(var t=rightTime;t>minTime;t-=60000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%120000==0){
				var txt=$.format.date(new Date(t), "HH:mm");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}else if(this.yTime==3600000){
		//minute
		var rightTime = Math.ceil(this.rightTime/1000);
		var restInt = rightTime%300;
		rightTime=(rightTime-restInt)*1000;
		
		var txt = "09:00";
		var len=context.measureText(txt).width/2;
		var minTime = leftTime-300000;
		for(var t=rightTime;t>minTime;t-=300000){
			var x=x0+(t-leftTime)*w/this.yTime;
			
			context.moveTo(x,y0+h);
			context.lineTo(x,y0+h-10);
			context.stroke();
			
			if(t%300000==0){
				var txt=$.format.date(new Date(t), "HH:mm");
				context.fillText(txt,x-len,y0+h+12);
			}
			
		}
	}
}

LineChart.prototype.drawYLabel=function(x0,y0,w,h,context){
	context.beginPath();
	context.strokeStyle = "#ccc";
	
	context.fillStyle=this.yLabelStyle;
	
	var txt = "20.20";
	var len=context.measureText(txt).width;
	var yMax=this.yMax;
	context.lineWidth = 1;
	
	var divH=yMax/10;
	var n=0;
	for(var i=divH;i<yMax;i+=divH){
		n++;
		var y = y0+h-i*h/yMax;
		context.moveTo(x0,y);
		context.lineTo(x0+w,y);
		context.stroke();
		
		//if(n%2==0)
			context.fillText(myformatDouble(i),x0-len-5,y+5);
	}
	
}

LineChart.prototype.drawXY=function(x0,y0,w,h,context){
	context.beginPath();
	context.strokeStyle = this.xLineStyle;
	context.moveTo(x0,y0+h);
	context.lineTo(x0+w,y0+h);
	context.stroke();
	
	/*
	context.beginPath();
	context.strokeStyle = this.xLineStyle;
	context.moveTo(x0,y0);
	context.lineTo(x0+w,y0);
	context.stroke();
	*/

	context.beginPath();
	context.strokeStyle = this.yLineStyle;
	context.moveTo(x0,y0+h);
	context.lineTo(x0,y0);
	context.stroke();
	
	/*
	context.beginPath();
	context.strokeStyle = this.yLineStyle;
	context.moveTo(x0+w,y0);
	context.lineTo(x0+w,y0+h);
	context.stroke();
	*/
}

LineChart.prototype.drawChart=function(x0,y0,w,h,context){
	//x
	if(this.data.length==0) return;
	
	var maxY=1;
	for(var i=0;i<this.data.length;i++){
		var p = this.data[i];
		if(p.y>maxY){
			maxY=p.y;
		}
	}
	
	if(this.yMax*0.6>maxY || this.yMax*0.9<maxY){
		//do again
		this.yMax=Math.ceil(maxY/0.7)+1;
	}
	
	if(this.yMax<0){
		this.yMax=1;
	}
	
	if(this.rightTime<=0){
		this.rightTime=this.data[this.data.length-1].x;
	}
	
	var leftTime = this.rightTime-this.yTime;
	
	var isFirst=true;
	context.beginPath();
	context.strokeStyle = this.lineStyle;
	
	var index=-1;
	for(var i=0;i<this.data.length;i++){
		var p = this.data[i];
		if(p.x<leftTime) continue;
		var x = (p.x-leftTime)*w/this.yTime+x0;
		var y = y0+h-(p.y-0)*h/this.yMax
		
		if(index+1!=p.index){
			context.moveTo(x,y);
			index=p.index;
		}else{
			context.lineTo(x,y);
		}
		index=p.index;
	}
	context.stroke();
}

function clearContext(x,y,w,h,context){
	context.beginPath();
	
	context.clearRect(x, y, w, y);
}

function clearCanvas(canvas){
	var context = canvas.getContext("2d");
	context.beginPath();
	
	context.save();
	context.setTransform(1, 0, 0, 1, 0, 0);
	context.clearRect(0, 0, canvas.width, canvas.height);
	context.restore();
}

function myformatDouble(p){
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
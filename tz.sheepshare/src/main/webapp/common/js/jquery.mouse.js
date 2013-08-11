$.extend( $.support, {
	eventCapture: ( "addEventListener" in document )
} );
/*
* jQuery Mobile Framework : "mouse" plugin
* Copyright (c) jQuery Project
* Dual licensed under the MIT or GPL Version 2 licenses.
* http://jquery.org/license
*/

(function($,window,document,undefined){var dataPropertyName="virtualMouseBindings",touchTargetPropertyName="virtualTouchID",virtualEventNames="vmouseover vmousedown vmousemove vmouseup vclick vmouseout vmousecancel".split(" "),touchEventProps="clientX clientY pageX pageY screenX screenY".split(" "),activeDocHandlers={},resetTimerID=0,startX=0,startY=0,didScroll=false,clickBlockList=[],blockMouseTriggers=false,blockTouchTriggers=false,eventCaptureSupported=$.support.eventCapture,$document=$(document),
nextTouchID=1,lastTouchID=0;$.vmouse={moveDistanceThreshold:10,clickDistanceThreshold:10,resetTimerDuration:1500};function getNativeEvent(event){while(event&&typeof event.originalEvent!=="undefined")event=event.originalEvent;return event}function createVirtualEvent(event,eventType){var t=event.type;event=$.Event(event);event.type=eventType;var oe=event.originalEvent;var props=$.event.props;if(oe)for(var i=props.length,prop;i;){prop=props[--i];event[prop]=oe[prop]}if(t.search(/^touch/)!==-1){var ne=
getNativeEvent(oe),t=ne.touches,ct=ne.changedTouches,touch=t&&t.length?t[0]:ct&&ct.length?ct[0]:undefined;if(touch)for(var i=0,len=touchEventProps.length;i<len;i++){var prop=touchEventProps[i];event[prop]=touch[prop]}}return event}function getVirtualBindingFlags(element){var flags={};while(element){var b=$.data(element,dataPropertyName);for(var k in b)if(b[k])flags[k]=flags.hasVirtualBinding=true;element=element.parentNode}return flags}function getClosestElementWithVirtualBinding(element,eventType){while(element){var b=
$.data(element,dataPropertyName);if(b&&(!eventType||b[eventType]))return element;element=element.parentNode}return null}function enableTouchBindings(){blockTouchTriggers=false}function disableTouchBindings(){blockTouchTriggers=true}function enableMouseBindings(){lastTouchID=0;clickBlockList.length=0;blockMouseTriggers=false;disableTouchBindings()}function disableMouseBindings(){enableTouchBindings()}function startResetTimer(){clearResetTimer();resetTimerID=setTimeout(function(){resetTimerID=0;enableMouseBindings()},
$.vmouse.resetTimerDuration)}function clearResetTimer(){if(resetTimerID){clearTimeout(resetTimerID);resetTimerID=0}}function triggerVirtualEvent(eventType,event,flags){var defaultPrevented=false;if(flags&&flags[eventType]||!flags&&getClosestElementWithVirtualBinding(event.target,eventType)){var ve=createVirtualEvent(event,eventType);$(event.target).trigger(ve);defaultPrevented=ve.isDefaultPrevented()}return defaultPrevented}function mouseEventCallback(event){var touchID=$.data(event.target,touchTargetPropertyName);
if(!blockMouseTriggers&&(!lastTouchID||lastTouchID!==touchID))triggerVirtualEvent("v"+event.type,event)}function handleTouchStart(event){var touches=getNativeEvent(event).touches;if(touches&&touches.length===1){var target=event.target,flags=getVirtualBindingFlags(target);if(flags.hasVirtualBinding){lastTouchID=nextTouchID++;$.data(target,touchTargetPropertyName,lastTouchID);clearResetTimer();disableMouseBindings();didScroll=false;var t=getNativeEvent(event).touches[0];startX=t.pageX;startY=t.pageY;
triggerVirtualEvent("vmouseover",event,flags);triggerVirtualEvent("vmousedown",event,flags)}}}function handleScroll(event){if(blockTouchTriggers)return;if(!didScroll)triggerVirtualEvent("vmousecancel",event,getVirtualBindingFlags(event.target));didScroll=true;startResetTimer()}function handleTouchMove(event){if(blockTouchTriggers)return;var t=getNativeEvent(event).touches[0];var didCancel=didScroll,moveThreshold=$.vmouse.moveDistanceThreshold;didScroll=didScroll||Math.abs(t.pageX-startX)>moveThreshold||
Math.abs(t.pageY-startY)>moveThreshold;var flags=getVirtualBindingFlags(event.target);if(didScroll&&!didCancel)triggerVirtualEvent("vmousecancel",event,flags);triggerVirtualEvent("vmousemove",event,flags);startResetTimer()}function handleTouchEnd(event){if(blockTouchTriggers)return;disableTouchBindings();var flags=getVirtualBindingFlags(event.target);triggerVirtualEvent("vmouseup",event,flags);if(!didScroll)if(triggerVirtualEvent("vclick",event,flags)){var t=getNativeEvent(event).changedTouches[0];
clickBlockList.push({touchID:lastTouchID,x:t.clientX,y:t.clientY});blockMouseTriggers=true}triggerVirtualEvent("vmouseout",event,flags);didScroll=false;startResetTimer()}function hasVirtualBindings(ele){var bindings=$.data(ele,dataPropertyName),k;if(bindings)for(k in bindings)if(bindings[k])return true;return false}function dummyMouseHandler(){}function getSpecialEventObject(eventType){var realType=eventType.substr(1);return{setup:function(data,namespace){if(!hasVirtualBindings(this))$.data(this,
dataPropertyName,{});var bindings=$.data(this,dataPropertyName);bindings[eventType]=true;activeDocHandlers[eventType]=(activeDocHandlers[eventType]||0)+1;if(activeDocHandlers[eventType]===1)$document.bind(realType,mouseEventCallback);$(this).bind(realType,dummyMouseHandler);if(eventCaptureSupported){activeDocHandlers["touchstart"]=(activeDocHandlers["touchstart"]||0)+1;if(activeDocHandlers["touchstart"]===1)$document.bind("touchstart",handleTouchStart).bind("touchend",handleTouchEnd).bind("touchmove",
handleTouchMove).bind("scroll",handleScroll)}},teardown:function(data,namespace){--activeDocHandlers[eventType];if(!activeDocHandlers[eventType])$document.unbind(realType,mouseEventCallback);if(eventCaptureSupported){--activeDocHandlers["touchstart"];if(!activeDocHandlers["touchstart"])$document.unbind("touchstart",handleTouchStart).unbind("touchmove",handleTouchMove).unbind("touchend",handleTouchEnd).unbind("scroll",handleScroll)}var $this=$(this),bindings=$.data(this,dataPropertyName);if(bindings)bindings[eventType]=
false;$this.unbind(realType,dummyMouseHandler);if(!hasVirtualBindings(this))$this.removeData(dataPropertyName)}}}for(var i=0;i<virtualEventNames.length;i++)$.event.special[virtualEventNames[i]]=getSpecialEventObject(virtualEventNames[i]);if(eventCaptureSupported)document.addEventListener("click",function(e){var cnt=clickBlockList.length;var target=e.target;if(cnt){var x=e.clientX,y=e.clientY,threshold=$.vmouse.clickDistanceThreshold;var ele=target;while(ele){for(var i=0;i<cnt;i++){var o=clickBlockList[i],
touchID=0;if(ele===target&&Math.abs(o.x-x)<threshold&&Math.abs(o.y-y)<threshold||$.data(ele,touchTargetPropertyName)===o.touchID){e.preventDefault();e.stopPropagation();return}}ele=ele.parentNode}}},true)})(jQuery,window,document);


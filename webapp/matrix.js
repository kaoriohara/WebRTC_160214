"use strict";

function current_map_pos(result) {
  var currentPos = new google.maps.LatLng(parseFloat(result[1]), parseFloat(result[2])); /*現在地*/
  var myOptions = {
    zoom: 17, /*拡大比率*/
    center: currentPos, /*表示枠内の中心点*/
    mapTypeControlOptions: { mapTypeIds: ['sample', google.maps.MapTypeId.ROADMAP] }/*表示タイプの指定*/
  };
  var map = new google.maps.Map(document.getElementById('map_canvas'), myOptions);
  set_map_options(map, currentPos);
}

function matrix_call() {
  var url = "https://20151121ubuntu.cloudapp.net:8448/_matrix/client/api/v1/events?access_token=MDAyOWxvY2F0aW9uIDIwMTUxMTIxdWJ1bnR1LmNsb3VkYXBwLm5ldAowMDEzaWRlbnRpZmllciBrZXkKMDAxMGNpZCBnZW4gPSAxCjAwM2ZjaWQgdXNlcl9pZCA9IEB0YWRoYWNrMjAxNnRlc3Q6MjAxNTExMjF1YnVudHUuY2xvdWRhcHAubmV0CjAwMTZjaWQgdHlwZSA9IGFjY2VzcwowMDFkY2lkIHRpbWUgPCAxNDU1NDI2NDI4NjY3CjAwMmZzaWduYXR1cmUgxr3YWKsR6zD0M_JqHz5FIyb_0O24r5HRmr0krYH3wHQK";
  $.getJSON(url, function(json){
    // alert("JSON Data: " + json.end);
    matrix_call_text(json.end);
  });
}

function matrix_call_chunk(end) {
  var test = "s53_117_10_2_1"
  var url = "https://20151121ubuntu.cloudapp.net:8448/_matrix/client/api/v1/events?access_token=MDAyOWxvY2F0aW9uIDIwMTUxMTIxdWJ1bnR1LmNsb3VkYXBwLm5ldAowMDEzaWRlbnRpZmllciBrZXkKMDAxMGNpZCBnZW4gPSAxCjAwM2ZjaWQgdXNlcl9pZCA9IEB0YWRoYWNrMjAxNnRlc3Q6MjAxNTExMjF1YnVudHUuY2xvdWRhcHAubmV0CjAwMTZjaWQgdHlwZSA9IGFjY2VzcwowMDFkY2lkIHRpbWUgPCAxNDU1NDI2NDI4NjY3CjAwMmZzaWduYXR1cmUgxr3YWKsR6zD0M_JqHz5FIyb_0O24r5HRmr0krYH3wHQK";
  $.getJSON(url+"&from="+test, function(json){
    // alert("JSON Data: " + json.chunk[0].content.body);
    console.log(json.chunk);
    console.log(json.chunk[2].content.body);
    var body = json.chunk[2].content.body;
    var result = body.match(/\((.+),(.+)\)/);
    console.log(result);
    current_map_pos(result);
  });

}

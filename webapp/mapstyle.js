function initialize() {
  var currentPos = new google.maps.LatLng(35.681735, 139.762651); /*現在地*/
  var myOptions = {
    zoom: 17, /*拡大比率*/
    center: currentPos, /*表示枠内の中心点*/
    mapTypeControlOptions: { mapTypeIds: ['sample', google.maps.MapTypeId.ROADMAP] }/*表示タイプの指定*/
  };
  var map = new google.maps.Map(document.getElementById('map_canvas'), myOptions);

  set_map_options(map, currentPos);
  setTimeout(matrix_call_chunk(),3000);
}

function set_map_options(map, currentPos){
  /*アイコン設定(今ココ)*/
  var iconImakoko = new google.maps.MarkerImage('img/ico.png',
    new google.maps.Size(100,100), /*アイコンサイズ設定*/
    new google.maps.Point(0,0) /*アイコン位置設定*/
    );

  /*今ココ表示設定*/
    var markerOptions = {
    position: currentPos,
    map: map,
    icon: iconImakoko,
    title: ' 現在地'
  };

  var markerImakoko = new google.maps.Marker(markerOptions);

  /*取得スタイルの貼り付け*/
  var styleOptions = [
  {
    "stylers": [
    { "saturation": -100 },
    { "visibility": "simplified" },
    { "lightness": 22 }
    ]
  }
  ];
  var styledMapOptions = { name: ' ' }
  var sampleType = new google.maps.StyledMapType(styleOptions, styledMapOptions);
  map.mapTypes.set('sample', sampleType);
  map.setMapTypeId('sample');
}
//google.maps.event.addDomListener(window, 'load', initialize);

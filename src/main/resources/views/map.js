
/** Javascript for map.html
 * Created by Haoming on 19/04/17.
 */

var map;
var markers = [];

function initMap() {
    var uniPos = {lat:-43.5235375, lng:172.5839233};
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 11,
        center:uniPos
    });
}

function drawMarkers(addresses) {
    clearMarkers();

    var geoCoder = new google.maps.Geocoder();
    for (var i = 0; i < addresses.length; i++) {
        geoCoder.geocode({'address': addresses[i]}, geocodeCallback(addresses[i]));
    }
}

function geocodeCallback(address) {
    return function (results, status) {
        if (status === 'OK') {
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location
            });
            markers.push(marker);

            var infoWindow = new google.maps.InfoWindow();
            infoWindow.setContent(address);

            google.maps.event.addListener(marker, 'mouseover', function () {
                infoWindow.open(map, marker);
            });

            google.maps.event.addListener(marker, 'mouseout', function () {
                infoWindow.close();
            });
        }
    }
}

function clearMarkers() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}



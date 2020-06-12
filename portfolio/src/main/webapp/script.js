// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Redirects page from http:// to https:// to ensure secure origin
if (location.protocol !== 'https:') {
    location.replace(`https:${location.href.substring(location.protocol.length)}`);
}

// When the user scrolls down 80px from the top of the document, resize the navbar's padding and the logo's font size
window.onscroll = function() {scrollFunction()};

function scrollFunction() {
  if (document.body.scrollTop > 80 || document.documentElement.scrollTop > 80) {
    document.getElementById("navbar").style.padding = "15px 10px";
    document.getElementById("logo").style.fontSize = "20px";
    document.getElementById("myBtn").style.display = "block";
  } else {
    document.getElementById("navbar").style.padding = "65px 10px";
    document.getElementById("logo").style.fontSize = "22px";
    document.getElementById("myBtn").style.display = "none";
  }
}

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
  document.body.scrollTop = 0; // For Safari
  document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
}

// Handles both default and input values
function getDisplayContentFromMaxValue(maxValue) {
  fetch(`/data?comment-number=${maxValue}`).then(response => response.json()).then((list) => {
    // list is an arraylist containing strings, so we have to
    // reference its elements to create HTML content
    
    const commentListElement = document.getElementById('name-container');
    commentListElement.innerHTML = '';

    var i;
    for (i = 0; (i < list.length && i < maxValue); i++) {
      commentListElement.appendChild(
      createListElement(list[i]));
    }
  });
}

// Display number of comments with input value
function getDisplayContentFromInput(event) {
  event.preventDefault();
  var maxValue = +document.getElementById("num-value").value;
  console.log(typeof maxValue);

  getDisplayContentFromMaxValue(maxValue);
}

// Displays default number of comments on page load
function loadDisplayContent() {  
  getDisplayContentFromMaxValue(3);
}

// Deletes the existing comments from webpage
function deleteCommentContent() {
  fetch('/delete-data').then(() => {
    loadDisplayContent();
  });
  
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

// Holds all points on the map
var mapPoints = []; 

// Initialize and add the map
function initMap() {

  // Locations shown when page is loaded
  var locations = [
      ['Chicago, U.S.', 41.8781, -87.6298],
      ['Springfield, U.S.', 39.799999, -89.650002],
      ['Lagos, Nigeria', 6.465422, 3.406448],
      ['Accra, Ghana', 5.550000, -0.020000],
      ['Doha, Qatar', 25.286106, 51.534817]
  ];

  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 10,
    mapTypeId: google.maps.MapTypeId.ROADMAP
  });

  var bounds = new google.maps.LatLngBounds();

  var infoWindow = new google.maps.InfoWindow();

  var marker, i;

  for (i = 0; i < locations.length; i++) {  
    marker = new google.maps.Marker({
      position: new google.maps.LatLng(locations[i][1], locations[i][2]),
      map: map,
      animation: google.maps.Animation.DROP
    });

    // Extends the map to include all points
    mapPoints.push(marker);
    bounds.extend(marker.position);

    
    // Opens the info window for each hard-coded marker
    google.maps.event.addListener(marker, 'click', (function(marker, i) {
      return function() {
        toggleBounce(marker);
        infoWindow.setContent(locations[i][0]);
        infoWindow.open(map, marker);
        setTimeout(toggleBounce(marker), 1000);
      }
    })(marker, i));
  }

  var userMarker;
  // Implementation of HTML5 geolocation for each user if given permission.
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      // Create user marker
      userMarker = new google.maps.Marker({
        position: pos,
        map: map,
        animation: google.maps.Animation.DROP
      });

      infoWindow.setPosition(pos);
      infoWindow.setContent('Location found. <br>Lat: '+pos.lat+'<br>Long: '+pos.lng);
      infoWindow.open(map, userMarker);
      map.setCenter(pos);

    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });
  } else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, map.getCenter());
  }

  map.addListener('center_changed', function() {
    // 3 seconds after the center of the map has changed, pan back to the
    // marker.
    window.setTimeout(function() {
    // Auto centers map to fit all markers
    map.fitBounds(bounds);
    }, 15000);
  });
  

  // Enable bouncing of markers
  function toggleBounce(marker) {
    if (marker.getAnimation() !== null) {
      marker.setAnimation(null);
    } else {
      marker.setAnimation(google.maps.Animation.BOUNCE);
    }
  }

}

// Checks for errors with Geolocation
function handleLocationError(browserHasGeolocation, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(browserHasGeolocation ? 'Error: The Geolocation service failed.' : 'Error: Your browser doesn\'t support geolocation.');
  infoWindow.open(map);
}


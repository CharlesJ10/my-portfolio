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
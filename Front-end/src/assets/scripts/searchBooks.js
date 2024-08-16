document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("form");

  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const barcodeNumber = document.getElementById("barcodeNumber").value;
    const isbn = document.getElementById("isbn").value;
    const author = document.getElementById("author").value;
    const title = document.getElementById("title").value;

    const url = `http://localhost:8080/api/books/search?barcodeNumber=${encodeURIComponent(
      barcodeNumber
    )}&isbn=${encodeURIComponent(isbn)}&author=${encodeURIComponent(
      author
    )}&title=${encodeURIComponent(title)}`;

    fetch(url)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        console.log("Search result: ", data);
        displayResults(data);
      })
      .catch((error) => {
        console.error("An error occured: ", error);
      });
  });
});

function displayResults(data) {
  let html =
    "<table class='results-table'><tr><th>Title</th><th>Author</th><th>Available copies</th><th></th></tr>";

  data.forEach((book) => {
    html += `<tr>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.noOfAvailableCopies}</td>
                <td><a class="action-btn" href="book-info.html?id=${book.id}">View Details</a></td>
            </tr>`;
  });

  html += "</table>";

  const resultContainer = document.getElementById("resultsContainer");
  resultContainer.innerHTML = html;
}

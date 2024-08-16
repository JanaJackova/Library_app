document.addEventListener("DOMContentLoaded", () => {
  const bookId = new URLSearchParams(window.location.search).get("id");
  if (!bookId) {
    console.error("No book ID specified in the URL");
    requestAnimationFrame;
  }

  document.getElementById("bookId").textContent = bookId;

  prefillForm(bookId);
});

function prefillForm(bookId) {
  fetch(`http://localhost:8080/api/books/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch book data.");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Book info: ", data);
      document.getElementById("title").value = data.title;
      document.getElementById("author").value = data.author;
      document.getElementById("isbn").value = data.isbn;
      document.getElementById("publisher").value = data.publisher;
      document.getElementById("yearPublished").value = data.yearOfPublication;
      document.getElementById("placePublished").value = data.placeOfPublication;
      document.getElementById("noOfAvailableCopies").value =
        data.noOfAvailableCopies;
      document.getElementById("barcodeNumber").value = data.barcodeNumber;
    })
    .catch((error) => {
      console.error("An excpected error: ", error);
    });
}

document.querySelector("form").addEventListener("submit", function (event) {
  event.preventDefault();

  const bookId = new URLSearchParams(window.location.search).get("id");

  const formData = {
    title: document.getElementById("title").value,
    author: document.getElementById("author").value,
    isbn: document.getElementById("isbn").value,
    publisher: document.getElementById("publisher").value,
    yearOfPublication: document.getElementById("yearPublished").value,
    placeOfPublication: document.getElementById("placePublished").value,
    noOfAvailableCopies: document.getElementById("noOfAvailableCopies").value,
    barcodeNumber: document.getElementById("barcodeNumber").value,
  };

  console.log("Payload DTO: ", formData);

  updateBookData(bookId, formData);
});

function updateBookData(id, data) {
  fetch(`http://localhost:8080/api/books/updateBook/${id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => {
      if (!response.ok) {
        console.log("Failed to update book data: ", response);
        throw new Error("Failed to update book data.");
      }
      return response.json();
    })
    .catch((error) => {
      console.error("An unexpected error ocurred: ", error);
    });
}

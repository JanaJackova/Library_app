document.addEventListener("DOMContentLoaded", function () {
  const memberBarcodeParam = getQueryParam("memberBarcode");
  if (memberBarcodeParam) {
    document.getElementById("memberBarcode").value = memberBarcodeParam;
  }

  const bookBarcodeParam = getQueryParam("bookBarcode");
  if (bookBarcodeParam) {
    document.getElementById("bookBarcode").value = bookBarcodeParam;
  }

  const form = document.querySelector("form");
  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const memberBarcode = document.getElementById("memberBarcode").value;
    const bookBarcode = document.getElementById("bookBarcode").value;

    console.log("memberBarcode: ", memberBarcode);
    console.log("bookBarcode: ", bookBarcode);

    Promise.all([
      fetchMemberIdbyBarcode(memberBarcode),
      fetchBookIdByBarcode(bookBarcode),
    ])
      .then(([memberId, bookId]) => {
        if (memberId && bookId) {
          console.log("memberId: ", memberId);
          console.log("bookId: ", bookId);
          createCheckoutRegister(memberId, bookId);
        } else {
          alert("Invalid member or book barcode");
        }
      })
      .catch((error) => {
        console.error("Unexpected error: ", error);
      });
  });
});

function getQueryParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
}

function fetchMemberIdbyBarcode(barcode) {
  return fetch(
    `http://localhost:8080/api/members/search?barcodeNumber=${barcode}`
  )
    .then((response) => {
      if (!response.ok) {
        console.log("Error fetching member by barcode");
        throw new Error("Error fetching member by barcode");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Member data: ", data);
      if (data.length > 0) {
        return data[0].id;
      } else {
        return null;
      }
    });
}

function fetchBookIdByBarcode(barcode) {
  return fetch(
    `http://localhost:8080/api/books/search?barcodeNumber=${barcode}`
  )
    .then((response) => {
      if (!response.ok) {
        console.log("Error fetching book by barcode");
        throw new Error("Error fetching book by barcode");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Book data: ", data);
      if (data.length > 0) {
        return data[0].id;
      } else {
        return null;
      }
    });
}

function createCheckoutRegister(memberId, bookId) {
  fetch(`http://localhost:8080/api/books/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not OK");
      }
      return response.json();
    })
    .then((book) => {
      if (!book || book.noOfAvailableCopies <= 0) {
        alert("Checkout failed: no copies available or book not found.");
        throw new Error(
          "Checkout failed: no copies available or book not found."
        );
      }
      const payload = {
        memberId: memberId,
        bookId: bookId,
        checkoutDate: new Date().toISOString().split("T")[0],
      };
      console.log("Checkout register payload: ", payload);
      return fetch(`http://localhost:8080/api/registers/createRegister`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error creating a register");
          }
          return response.json();
        })
        .then((register) => {
          return decreaseBookCopies(bookId);
        })
        .then(() => {
          alert("Book checked-out successfully and copies are updated.");
          location.reload();
        })
        .catch((error) => {
          console.error("An error occurred with checkout process: ", error);
        });
    });
}

function decreaseBookCopies(id) {
  return fetch(`http://localhost:8080/api/books/${id}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error fetching a book");
      }
      return response.json();
    })
    .then((data) => {
      const updateCopies = data.noOfAvailableCopies - 1;
      return fetch(`http://localhost:8080/api/books/updateBook/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ noOfAvailableCopies: updateCopies }),
      });
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Error while updating book copies");
      }
      return response.json();
    })
    .catch((error) => {
      console.error("An error occurred while updating book copies : ", error);
    });
}

let bookId = null;

document.addEventListener("DOMContentLoaded", () => {
  bookId = getQueryParam("id");
  if (!bookId) {
    console.error("No book ID specified in the URL");
    return;
  }

  fetch(`http://localhost:8080/api/books/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        console.error("Network response was not ok");
        throw new Error("Network response was not ok.");
      }
      return response.json();
    })
    .then((data) => {
      displayBookDetails(data);
      updateLinkVisibility(data.noOfAvailableCopies);
      const checkoutLink = document.getElementById("checkoutLink");
      checkoutLink.href = `book-checkout.html?bookBarcode=${data.barcodeNumber}`;
    })
    .catch((error) => {
      console.error("Unexpected error: ", error);
    });

  const currentHoldersLink = document.getElementById("currentHoldersLink");
  currentHoldersLink.addEventListener("click", function (event) {
    event.preventDefault();
    fetchCurrentHolders(bookId);
  });

  const removeCopyLink = document.getElementById("removeCopyLink");
  removeCopyLink.addEventListener("click", function () {
    confirmAndRemoveCopy();
  });

  const addCopyLink = document.getElementById("addCopyLink");
  addCopyLink.addEventListener("click", function () {
    addBookCopy();
  });

  const editBookLink = document.getElementById("editBookLink");
  editBookLink.href = `edit-book.html?id=${bookId}`;

  const checkoutHistoryLink = document.getElementById("checkoutHistoryLink");
  checkoutHistoryLink.addEventListener("click", function (event) {
    event.preventDefault();
    fetchCheckoutHistory(bookId);
  });
});

function getQueryParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
}

function displayBookDetails(book) {
  console.log("Book: ", book);

  document.getElementById(
    "bookTitleAndAuthor"
  ).textContent = `${book.title}, ${book.author}`;

  document.getElementById("isbn").textContent = book.isbn;
  document.getElementById("publisher").textContent = book.publisher;
  document.getElementById("yearOfPublication").textContent =
    book.yearOfPublication;
  document.getElementById("placeOfPublication").textContent =
    book.placeOfPublication;
  document.getElementById("availableCopies").textContent =
    book.noOfAvailableCopies;

  const removeCopyLink = document.getElementById("removeCopyLink");
  if (book.noOfAvailableCopies === 0) {
    removeCopyLink.style.display = "none";
  }
}

function updateLinkVisibility(availableCopies) {
  const checkoutLink = document.getElementById("checkoutLink");
  const copyLink = document.getElementById("removeCopyLink");

  if (availableCopies === 0) {
    checkoutLink.style.display = "none";
    copyLink.style.display = "none";
  }
}

function fetchCurrentHolders(bookId) {
  console.log("Fetching current holders for bookId: ", bookId);

  fetch(`http://localhost:8080/api/registers/book/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        console.error("Failed to fetch current holders.");
        throw new Error("Failed to fetch current holders");
      }
      return response.json();
    })
    .then((registers) => {
      console.log("registers: ", registers);
      const currentHolders = registers.filter(
        (register) => register.returnDate == null
      );

      const memberDetailsPromises = currentHolders.map((register) =>
        fetch(`http://localhost:8080/api/members/${register.memberId}`)
          .then((response) => {
            if (!response.ok) {
              console.error("Failet to fetch member details");
              throw new Error("Failed to fetch member details");
            }
            return response.json();
          })
          .then((member) => ({
            ...member,
            checkoutDate: register.checkoutDate,
            dueDate: register.dueDate,
          }))
      );

      return Promise.all(memberDetailsPromises);
    })
    .then((membersWithDetails) => {
      displayCurrentHolders(membersWithDetails);
    })
    .catch((error) => {
      console.error("Unexpected error while fetching current holders: ", error);
    });
}

function displayCurrentHolders(members) {
  console.log("Displaying members: ", members);

  let html = "<p>No current holders</p>";

  if (members.length > 0) {
    html = `
          <h2>Current Holders</h2>
          <table class="results-table">
          <tr><th>First Name</th><th>Last Name</th><th>Card #</th><th>Checkout Date</th><th>Due Date</th></tr>
        `;
  }

  members.forEach((member) => {
    const checkoutDate = new Date(member.checkoutDate).toLocaleDateString();
    const dueDate = new Date(member.dueDate).toLocaleDateString();

    html += `
        <tr>
          <td>${member.firstName}</td>
          <td>${member.lastName}</td>
          <td>${member.id}</td>
          <td>${member.checkoutDate}</td>
          <td>${member.dueDate}</td>
        </tr>
      `;
  });

  if (members.length > 0) {
    html += `</tbody></table>`;
  }

  const currentHoldersContainer = document.getElementById(
    "currentHoldersContainer"
  );
  currentHoldersContainer.innerHTML = html;
}

function removeBookCopy() {
  updateBookCopies(-1);
}

function addBookCopy() {
  updateBookCopies(1);
}

function confirmAndRemoveCopy() {
  if (confirm("Are you sure you want to remove a copy?")) {
    removeBookCopy();
  }
}

function updateBookCopies(incr) {
  fetch(`http://localhost:8080/api/books/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        console.error("Failed to fetch a book");
        throw new Error("Failed to fetch a book");
      }
      return response.json();
    })
    .then((data) => {
      const updatedCopies = data.noOfAvailableCopies + incr;

      return fetch(`http://localhost:8080/api/books/updateBook/${bookId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ noOfAvailableCopies: updatedCopies }),
      });
    })
    .then((response) => {
      if (!response.ok) {
        console.error("Failed to update book copies");
        throw new Error("Failed to update book copies");
      }
      return response.json();
    })
    .then((updatedBook) => {
      if (incr === 1) alert("A copy has been successfully added.");
      if (incr === -1) alert("A copy was successfully removed.");
      location.reload();
    })
    .catch((error) => {
      console.error(
        "Unexpected error while updating available copies: ",
        error
      );
    });
}

function fetchCheckoutHistory(bookId) {
  fetch(`http://localhost:8080/api/registers/book/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        console.log("Failed to fetch registers: ", response);
        throw new Error("Failed to fetch registers");
      }
      return response.json();
    })
    .then((registers) => {
      console.log("All registers: ", registers);
      const returnedRegisters = registers.filter(
        (register) => register.returnDate != null
      );
      console.log("Filtered registers: ", returnedRegisters);
      const memberDetailsPromises = returnedRegisters.map((register) =>
        fetch(`http://localhost:8080/api/members/${register.memberId}`)
          .then((response) => {
            if (!response.ok) {
              console.log("Failed to fetch member details: ", response);
              throw new Error("Failed to fetch member details");
            }
            return response.json();
          })
          .then((member) => ({
            ...member,
            checkoutDate: register.checkoutDate,
            dueDate: register.dueDate,
            returnDate: register.returnDate,
            overdueFine: register.overdueFine,
          }))
      );
      return Promise.all(memberDetailsPromises);
    })
    .then((membersWithDetails) => {
      console.log("Members with additional info: ", membersWithDetails);
      displayCheckoutHistory(membersWithDetails);
    })
    .catch((error) => {
      console.error("An unexpected error: ", error);
    });
}

function displayCheckoutHistory(members) {
  let html = "";

  const checkoutHistoryContainer = document.getElementById(
    "checkoutHistoryContainer"
  );

  if (members.length === 0) {
    checkoutHistoryContainer.innerHTML = `<p>No checkout history</p>`;
    return;
  }

  html = `
         <h2>Checkout History</h2>
         <table class="results-table">
         <tr><th>First Name</th><th>Last Name</th><th>Card No</th><th>Checkout Date</th><th>Due Date</th><th>Return Date</th><th>Overdue Fine</th></tr>
     `;

  members.forEach((member) => {
    const checkoutDate = new Date(member.checkoutDate).toLocaleDateString();
    const dueDate = new Date(member.dueDate).toLocaleDateString();
    const returnDate = new Date(member.returnDate).toLocaleDateString();
    const overdueFine = member.overdueFine
      ? `${parseFloat(member.overdueFine).toFixed(2)} CZK`
      : "";

    html += `
          <tr>
            <td>${member.firstName}</td>
            <td>${member.lastName}</td>
            <td>${member.id}</td>
            <td>${checkoutDate}</td>
            <td>${dueDate}</td>
            <td>${returnDate}</td>
            <td>${overdueFine}</td>
            </tr>
            `;
  });

  html += `</table>`;

  checkoutHistoryContainer.innerHTML = html;
}

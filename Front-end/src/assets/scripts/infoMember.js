let memberId = null;

document.addEventListener("DOMContentLoaded", () => {
  memberId = getQueryParam("id");
  if (!memberId) {
    console.error("No member ID specified in the URL");
    return;
  }

  const url = `http://localhost:8080/api/members/${memberId}`;

  fetch(url)
    .then((response) => {
      if (!response.ok) {
        console.error("Network error: ", response);
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Fetched member: ", data);
      displayMemberDetails(data);
      fetchCheckoutBook(data.id);
    })
    .catch((error) => {
      console.error("An error occured: ", error);
    });

  const memberHistoryLink = document.querySelector(
    'a[href="member-history.html"]'
  );
  if (memberHistoryLink)
    memberHistoryLink.href = `member-history.html?id=${memberId}`;

  const editMemberLink = document.querySelector('a[href="edit-member.html"]');
  if (editMemberLink) editMemberLink.href = `edit-member.html?id=${memberId}`;
});

function getQueryParam(param) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(param);
}

function displayMemberDetails(member) {
  document.getElementById(
    "memberFullName"
  ).textContent = `${member.firstName} ${member.lastName}`;

  document.getElementById("cardNumber").textContent = member.id;

  let memberInfo = "";
  let address = "N/A";
  if (member.address) {
    if (memberInfo) memberInfo = `(member.address.memberInfo)`;
    address = `${member.address.streetName} ${member.address.streetNumber}, ${member.address.zipCode} ${member.address.placeName}, ${member.address.country} ${memberInfo}`;
  }

  document.getElementById("address").textContent = address;
  document.getElementById("phone").textContent = member.phone;
  document.getElementById("email").textContent = member.email;

  let dob = new Date(member.dateOfBirth).toLocaleDateString("cs-CZ");
  document.getElementById("dob").textContent = dob;

  let membershipStarted = new Date(member.membershipStarted).toLocaleDateString(
    "cs-CZ"
  );
  document.getElementById("membershipStarted").textContent = membershipStarted;

  let membershipEnded = member.membershipEnded
    ? new Date(member.membershipEnded).toLocaleDateString("cs-CZ")
    : "-";
  document.getElementById("membershipEnded").textContent = membershipEnded;

  document.getElementById("membershipStatus").textContent = member.isActive
    ? "Active"
    : "Terminated";

  // dynamically set the "Terminate Membership" or "Activate Membership" link
  const membershipActionLink = document.getElementById("membershipActionLink");
  if (member.isActive) {
    membershipActionLink.textContent = "Terminate Membership";
    membershipActionLink.href = "javascript:terminateMembership()";
  } else {
    membershipActionLink.textContent = "Activate Membership";
    membershipActionLink.href = "javascript:activateMembership()";
  }

  // control the visibility of the checkout link based on member's active status
  const checkoutLink = document.getElementById("checkoutLink");
  if (member.isActive) {
    checkoutLink.style.display = "inline";
    checkoutLink.href = `book-checkout.html?memberBarcode=${member.barcodeNumber}`;
  } else {
    checkoutLink.style.display = "none";
  }
}

function fetchCheckoutBook(memberId) {
  // fetch data from Checkout_register
  fetch(`http://localhost:8080/api/registers/member/${memberId}`)
    .then((response) => {
      if (!response.ok) {
        console.error("Network error: ", response);
        throw new Error("Network response was not OK");
      }
      return response.json();
    })
    .then((data) => {
      if (data.length === 0) {
        displayCheckedOutBooks([]);
        return;
      }

      // filter out books to display
      const bookDetailsPromise = data
        .filter((data) => data.returnDate === null)
        .map((data) =>
          fetch(`http://localhost:8080/api/books/${data.bookId}`)
            .then((response) => {
              if (!response.ok) {
                console.error("Network error: ", response);
                throw new Error("Network response was not OK");
              }
              return response.json();
            })
            .then((bookDetails) => ({
              ...bookDetails,
              dueDate: data.dueDate,
              registerId: data.id,
            }))
            .catch((error) => {
              console.error("An error occurred with fetching book: ", error);
            })
        );

      Promise.all(bookDetailsPromise).then((bookDetails) => {
        displayCheckedOutBooks(bookDetails);
      });
    })
    .catch((error) => {
      console.error("An error occurred with fetching register: ", error);
    });
}

function displayCheckedOutBooks(books) {
  console.log("Books to display: ", books);

  const bookHeading = document.getElementById("checkedOutBooksHeading");
  const booksTable = document.getElementById("checkedOutBooks");

  booksTable.innerHTML = "";

  if (books.lenght === 0 || !books) {
    bookHeading.style.display = "none";
    return;
  }

  let tbody = document.createElement("tbody");
  booksTable.appendChild(tbody);

  books.forEach((book, index) => {
    let row = tbody.insertRow();
    let detaiCell = row.insertCell(0);
    let actionCell = row.insertCell(1);

    detaiCell.innerHTML = `${index + 1} ${book.title}, ${
      book.author
    } (Due date: ${book.dueDate})`;

    let space = document.createTextNode("\u00A0\u00A0\u00A0\u00A0");
    actionCell.appendChild(space);

    let returnLink = document.createElement("a");
    returnLink.href = "javascript:void(0)";
    returnLink.className = "action-btn"
    returnLink.textContent = "Return this book";
    returnLink.onclick = function () {
      returnBook(book.registerId, book.title, book.author);
    };

    actionCell.appendChild(returnLink);
  });
}

function returnBook(registerId, bookTitle, bookAuthor) {
  console.log("Inside returnBook() method...");

  const payload = {
    returnDate: new Date().toISOString().split("T")[0],
  };

  console.log("returnBook payload: ", payload);

  fetch(`http://localhost:8080/api/registers/updateRegister/${registerId}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  })
    .then((response) => {
      if (!response.ok) {
        console.error("Failed to return a book");
        throw new Error("Failed to return a book");
      }
      return response.json();
    })
    .then(async () => {
      const response = await fetch(
        `http://localhost:8080/api/registers/${registerId}`
      );
      if (!response.ok) {
        console.error("Failed to fetch updated register details.");
        throw new Error("Failed to fetch updated register details.");
      }
      return response.json();
    })
    .then((data) => {
      increaseBookCopies(data.bookId);
      return data;
    })
    .then((data) => {
      let alertMessage = `Book "${bookTitle}, ${bookAuthor}" successfully returned.`;
      if (data.overdueFine != null) {
        let formattedFine = parseFloat(data.overdueFine).toFixed(2);
        alertMessage += `\n\nOverdue Fine: ${formattedFine} CZK.`;
      }
      alert(alertMessage);
      fetchCheckoutBook(memberId);
    })
    .catch((error) => {
      console.error("An error occured: ", error);
    });
}

function increaseBookCopies(bookId) {
  console.log("Inside increaseBookCopies() method...");

  fetch(`http://localhost:8080/api/books/${bookId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch book details");
      }
      return response.json();
    })
    .then((data) => {
      const updatedCopise = data.noOfAvailableCopies + 1;
      console.log(
        "Number of available copies before returning: ",
        data.noOfAvailableCopies
      );

      return fetch(`http://localhost:8080/api/books/updateBook/${bookId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ noOfAvailableCopies: updatedCopise }),
      });
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to update book copies.");
      }
      return response.json();
    })
    .catch((error) => {
      console.error("An error occured");
    });
}

function terminateMembership() {
  updateMembershipStatus(false);
}

function activateMembership() {
  updateMembershipStatus(true);
}

function updateMembershipStatus(isActive) {
  const today = new Date().toISOString().split("T")[0];
  const payload = isActive
    ? { membershipEnded: "" }
    : { membershipEnded: today };
  console.log("updateMembershipStatus payload: ", payload);

  fetch(`http://localhost:8080/api/members/updateMember/${memberId}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  })
    .then((response) => {
      if (!response.ok) {
        console.error("Failed to update membership status");
        throw new Error("Failed to update membership status");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Membership status updated successfully: ", data);
      location.reload();
    })
    .catch((error) => {
      console.error(
        "An error occured while trying to update membership status."
      );
    });
}

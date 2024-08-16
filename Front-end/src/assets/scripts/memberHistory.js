let memberId = null;

document.addEventListener("DOMContentLoaded", () => {
  memberId = new URLSearchParams(window.location.search).get("id");
  if (!memberId) {
    console.error("No member ID specified in the URL");
    return;
  }

  // fetch the member to get full name
  fetch(`http://localhost:8080/api/members/${memberId}`)
    .then((response) => {
      if (!response.ok) {
        console.log("Failed to get a member by id: ", response);
        throw new Error("Failed to get a member by id.");
      }
      return response.json();
    })
    .then((member) => {
      console.log("Member info: ", member);
      document.getElementById(
        "memberFullName"
      ).textContent = `${member.firstName} ${member.lastName}`;
    })
    .catch((error) => {
      console.error("An unexpected error: ", error);
    });

  fetchMemberHistory(memberId);
});

function fetchMemberHistory(memberId) {
  fetch(`http://localhost:8080/api/registers/member/${memberId}`)
    .then((response) => {
      if (!response.ok) {
        console.log("Failed to fetch member history: ", response);
        throw new Error("Failed to fetch member history.");
      }
      return response.json();
    })
    .then((registers) => {
      console.log("Fetched register: ", registers);
      const historyRegisters = registers.filter(
        (register) => register.returnDate != null
      );
      console.log("Filtered regiters: ", historyRegisters);
      const returnedBooks = historyRegisters.map((register) => {
        return fetch(`http://localhost:8080/api/books/${register.bookId}`)
          .then((response) => {
            if (!response.ok) {
              throw new Error("Failedd to fetch book details");
            }
            return response.json();
          })
          .then((book) => ({
            ...register,
            bookTitle: book.title,
            bookAuthor: book.author,
          }));
      });
      return Promise.all(returnedBooks);
    })
    .then((returnedBooks) => {
      console.log("Returned books: ", returnedBooks);
      displayMemberHistory(returnedBooks);
    })
    .catch((error) => {
      alert("An unexpected error ocurred.");
      console.error("Error: ", error);
    });
}

function displayMemberHistory(returnedBooks) {
  const historyTable = document.getElementById("historyTable");
  historyTable.innerHTML = "";

  if (returnedBooks.length === 0) {
    historyTable.innerHTML = "<p>No history found for this member.</p>";
    return;
  }

  let html = `
        <table class="results-table">
            <tr><th>Book</th><th>Checked-out</th><th>Returned</th><Th>Ovedue Fine</th></tr>

    `;

  returnedBooks.forEach((register) => {
    const checkoutDate = new Date(register.checkoutDate).toLocaleDateString();
    const returnDate = new Date(register.returnDate).toLocaleDateString();
    const overdueFine =
      register.overdueFine !== null
        ? `${parseFloat(register.overdueFine).toFixed(2)} USD`
        : "";

    html += `
            <tr>
                <td><a href="book-info.html?id=${register.bookId}"> ${register.bookTitle}, ${register.bookAuthor}</td>
                <td>${checkoutDate}</td>
                <td>${returnDate}</td>
                <td>${overdueFine}</td>
        `;
  });

  html += `</table>`;
  historyTable.innerHTML = html;
}

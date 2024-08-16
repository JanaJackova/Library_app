document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("form");

  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const today = new Date().toISOString().split("T")[0];
    console.log("Today: ", today);

    const formData = {
      firstName: document.getElementById("firstName").value,
      lastName: document.getElementById("lastName").value,
      dateOfBirth: document.getElementById("dob").value,
      address: {
        streetName: document.getElementById("streetName").value,
        streetNumber: document.getElementById("streetNo").value,
        zipCode: document.getElementById("zipCode").value,
        placeName: document.getElementById("placeName").value,
        country: document.getElementById("country").value,
        additionalInfo: document.getElementById("addInfo").value,
      },
      email: document.getElementById("email").value,
      phone: document.getElementById("phone").value,
      barcodeNumber: document.getElementById("barcode").value,
      membershipStarted: today,
      isActive: true,
    };

    addNewMember(formData, form);
  });
});

function addNewMember(data, form) {
  console.log("Attempting to add a member: ", data);

  fetch("http://localhost:8080/api/members/addMember", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to add new memeber");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Member added succesfully: ", data);
      form.reset(); // Clear the form
    })
    .catch((error) => {
      console.error("Error: ", error);
    });
}

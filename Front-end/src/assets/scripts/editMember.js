document.addEventListener("DOMContentLoaded", () => {
  const memberId = new URLSearchParams(window.location.search).get("id");
  if (!memberId) {
    console.error("No member ID specified in the URL");
    requestAnimationFrame;
  }

  document.getElementById("memberId").textContent = memberId;

  prefillForm(memberId);
});

function prefillForm(memberId) {
  fetch(`http://localhost:8080/api/members/${memberId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch member data.");
      }
      return response.json();
    })
    .then((data) => {
      console.log("Member info: ", data);
      document.getElementById("firstName").value = data.firstName;
      document.getElementById("lastName").value = data.lastName;
      document.getElementById("dob").value = data.dateOfBirth;
      document.getElementById("streetName").value = data.address.streetName;
      document.getElementById("streetNo").value = data.address.streetNumber;
      document.getElementById("zipCode").value = data.address.zipCode;
      document.getElementById("placeName").value = data.address.placeName;
      document.getElementById("country").value = data.address.country;
      document.getElementById("addInfo").value = data.address.additionalInfo;
      document.getElementById("email").value = data.email;
      document.getElementById("phone").value = data.phone;
      document.getElementById("barcode").value = data.barcodeNumber;
    })
    .catch((error) => {
      console.error("An excpected error: ", error);
    });
}

document.querySelector("form").addEventListener("submit", function (event) {
  event.preventDefault();

  const memberId = new URLSearchParams(window.location.search).get("id");

  const formData = {
    firstName: document.getElementById("firstName").value,
    lastName: document.getElementById("lastName").value,
    dateOfBirth: document.getElementById("dob").value,
    address: {
      streetName: document.getElementById("streetName").value,
      streetNumber: document.getElementById("streetNo").value,
      zipCode: document.getElementById("zipCode").value,
      palceName: document.getElementById("placeName").value,
      country: document.getElementById("country").value,
      additionalInfo: document.getElementById("addInfo").value,
    },
    email: document.getElementById("email").value,
    phone: document.getElementById("phone").value,
    barcodeNumber: document.getElementById("barcode").value,
  };

  console.log("Payload DTO: ", formData);

  updateMemberData(memberId, formData);
});

function updateMemberData(id, data) {
  fetch(`http://localhost:8080/api/members/updateMember/${id}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => {
      if (!response.ok) {
        console.log("Failed to update member data: ", response);
        throw new Error("Failed to update member data.");
      }
      return response.json();
    })
    .catch((error) => {
      console.error("An unexpected error ocurred: ", error);
    });
}

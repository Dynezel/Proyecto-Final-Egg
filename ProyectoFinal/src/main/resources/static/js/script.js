document.addEventListener("DOMContentLoaded", function () {
  const rangoHorarioContainer = document.getElementById(
    "rangoHorarioContainer"
  );
  const agregarRangoHorarioBtn = document.getElementById("agregarRangoHorario");

  agregarRangoHorarioBtn.addEventListener("click", function () {
    console.log("Agregando nuevo RangoHorario");
    const nuevoRangoHorario = createRangoHorario();
    rangoHorarioContainer.appendChild(nuevoRangoHorario);
  });

  function createRangoHorario() {
    const nuevoRangoHorario = document.createElement("div");
    nuevoRangoHorario.classList.add("rango-horario"); // Añadir una clase para el estilo si es necesario
    nuevoRangoHorario.innerHTML = `
            <div class="form-group">
                <label>Fecha:</label>
                <input type="date" name="fecha" class="fecha controls" required />
            </div>
          <label>Día de la semana:</label>
          <select name="diaSemana" class="diaSemana controls">
              <option value="Lunes">Lunes</option>
              <option value="Martes">Martes</option>
              <option value="Miércoles">Miércoles</option>
              <option value="Jueves">Jueves</option>
              <option value="Viernes">Viernes</option>
              <option value="Sabado">Sabado</option>
              <option value="Domingo">Domingo</option>
          </select>
      </div>
      <div class="form-group">
          <label>Hora de inicio:</label>
          <select class="controls horaInicio" name="horaInicio"></select>
      </div>
      <div class="form-group">
          <label>Hora de fin:</label>
          <select class="controls horaFin" name="horaFin"></select>
      </div>
    `;

    // Poblamos las opciones de tiempo para los selects de este nuevo rango horario
    populateDateOptions(nuevoRangoHorario.querySelector(".fecha"));
    populateTimeOptions(nuevoRangoHorario.querySelector(".horaInicio"));
    populateTimeOptions(nuevoRangoHorario.querySelector(".horaFin"));

    return nuevoRangoHorario;
  }

  // Esta función se llamará cada vez que se agregue un nuevo rango horario
  function addRangoHorario() {
    const rangoHorarioContainer = document.getElementById(
      "rangoHorarioContainer"
    );
    rangoHorarioContainer.appendChild(createRangoHorario());
  }

  var startTime = document.getElementById("horaInicio");
  var endTime = document.getElementById("horaFin");

    function populateDateOptions(input) {
        // Puedes personalizar las opciones de fecha según tus necesidades
        var today = new Date();
        var options = "";
        for (var i = 0; i < 7; i++) {
          var date = new Date(today);
          date.setDate(today.getDate() + i);
          var formattedDate = date.toISOString().split("T")[0];
          options += '<option value="' + formattedDate + '">' + formattedDate + "</option>";
        }
        input.innerHTML = options;
      }

  function populateTimeOptions(input) {
    var options = "";
    for (var i = 0; i < 24; i++) {
      for (var j = 0; j < 2; j++) {
        var hour = i.toString().padStart(2, "0");
        var minute = j === 0 ? "00" : "30";
        options +=
          '<option value="' +
          hour +
          ":" +
          minute +
          '">' +
          hour +
          ":" +
          minute +
          "</option>";
      }
    }
    input.innerHTML = options;
  }

  populateTimeOptions(startTime);
  populateTimeOptions(endTime);
});

// Javali: mejoras opcionales. Todo funciona sin JavaScript,
// salvo reordenar líneas, que usa los botones Subir y Bajar.
(function () {
  "use strict";

  // Después de corregir, lleva el foco al resultado para lectores de pantalla y teclado.
  var resultado = document.getElementById("resultado");
  if (resultado) {
    resultado.focus();
  }

  // Ejercicios de ordenar: botones Subir / Bajar accesibles con teclado.
  var lista = document.getElementById("lista-orden");
  if (!lista) {
    return;
  }
  var anuncio = document.getElementById("anuncio-orden");

  function items() {
    return Array.prototype.slice.call(lista.querySelectorAll("li"));
  }

  function actualizar() {
    var elementos = items();
    elementos.forEach(function (li, posicion) {
      li.querySelector(".numero").textContent = (posicion + 1) + ".";
      li.querySelector("[data-accion='subir']").disabled = posicion === 0;
      li.querySelector("[data-accion='bajar']").disabled =
        posicion === elementos.length - 1;
    });
  }

  function mover(li, direccion) {
    var vecino = direccion < 0 ? li.previousElementSibling : li.nextElementSibling;
    if (!vecino) {
      return;
    }
    if (direccion < 0) {
      lista.insertBefore(li, vecino);
    } else {
      lista.insertBefore(vecino, li);
    }
    actualizar();
    var posicion = items().indexOf(li) + 1;
    if (anuncio) {
      anuncio.textContent =
        "Línea movida a la posición " + posicion + " de " + items().length + ".";
    }
    var boton = li.querySelector(
      "[data-accion='" + (direccion < 0 ? "subir" : "bajar") + "']"
    );
    if (boton && !boton.disabled) {
      boton.focus();
    } else {
      li.querySelector("[data-accion='" + (direccion < 0 ? "bajar" : "subir") + "']").focus();
    }
  }

  lista.addEventListener("click", function (evento) {
    var boton = evento.target.closest("button[data-accion]");
    if (!boton) {
      return;
    }
    var li = boton.closest("li");
    mover(li, boton.getAttribute("data-accion") === "subir" ? -1 : 1);
  });

  // Atajo: Alt + flecha arriba/abajo sobre una línea enfocada.
  lista.addEventListener("keydown", function (evento) {
    if (!evento.altKey) {
      return;
    }
    var li = evento.target.closest("li");
    if (!li) {
      return;
    }
    if (evento.key === "ArrowUp") {
      evento.preventDefault();
      mover(li, -1);
    } else if (evento.key === "ArrowDown") {
      evento.preventDefault();
      mover(li, 1);
    }
  });

  // Se muestran los controles solo cuando hay JavaScript.
  lista.classList.add("con-js");
  actualizar();
})();

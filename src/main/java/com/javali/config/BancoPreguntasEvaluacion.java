package com.javali.config;

import com.javali.dtos.PreguntaRequest;

import java.util.List;

// Preguntas de la evaluación inicial, ordenadas de más básicas a más avanzadas.
// Dificultad: 1 = básica, 2 = intermedia, 3 = avanzada (cuanto más difícil, más pesa).
public final class BancoPreguntasEvaluacion {

    private BancoPreguntasEvaluacion() {}

    public static List<PreguntaRequest> preguntas() {
        return List.of(
                // ---------- Básicas ----------
                new PreguntaRequest(
                        "¿Cuál es una declaración correcta de una variable entera?",
                        null, 1,
                        List.of("int edad = 20;", "edad int = 20;",
                                "integer edad = 20;", "int edad == 20;"),
                        0, 1),
                new PreguntaRequest(
                        "¿Qué tipo conviene usar para guardar el precio 19.99?",
                        null, 2,
                        List.of("int", "boolean", "double", "char"),
                        2, 1),
                new PreguntaRequest(
                        "¿Qué operador compara si dos números son iguales?",
                        null, 3,
                        List.of("=", "=>", "!=", "=="),
                        3, 1),
                new PreguntaRequest(
                        "¿Cuántas veces se ejecuta el cuerpo del ciclo?",
                        """
                        for (int i = 0; i < 3; i++) {
                            System.out.println(i);
                        }
                        """,
                        4,
                        List.of("2", "3", "4", "Infinitas"),
                        1, 1),

                // ---------- Intermedias ----------
                new PreguntaRequest(
                        "¿Qué muestra este programa?",
                        """
                        int edad = 17;

                        if (edad >= 18) {
                            System.out.println("Mayor");
                        }
                        System.out.println("Fin");
                        """,
                        5,
                        List.of("Mayor", "Fin", "Mayor y luego Fin", "No compila"),
                        1, 2),
                new PreguntaRequest(
                        "¿Qué valor se muestra?",
                        """
                        int resultado = 7 / 2;
                        System.out.println(resultado);
                        """,
                        6,
                        List.of("3.5", "4", "3", "No compila"),
                        2, 2),
                new PreguntaRequest(
                        "¿Cómo se compara el contenido de dos Strings?",
                        """
                        String a = new String("java");
                        String b = new String("java");
                        """,
                        7,
                        List.of("a == b", "a = b", "a.equals(b)", "a.compare(b)"),
                        2, 2),
                new PreguntaRequest(
                        "¿Qué muestra este código?",
                        """
                        int[] numeros = {4, 8, 15};
                        System.out.println(numeros[numeros.length - 1]);
                        """,
                        8,
                        List.of("15", "8", "4", "Lanza una excepción"),
                        0, 2),
                new PreguntaRequest(
                        "El compilador informa: incompatible types: int cannot be "
                                + "converted to String. ¿Cuál es la causa?",
                        """
                        int total = 10;
                        String mensaje = total;
                        """,
                        9,
                        List.of("Falta un punto y coma",
                                "Se asignó un int a una variable de tipo String",
                                "La variable total no fue declarada",
                                "String no existe en Java"),
                        1, 2),

                // ---------- Avanzadas ----------
                new PreguntaRequest(
                        "¿Qué muestra este programa?",
                        """
                        static int duplicar(int n) {
                            n = n * 2;
                            return n;
                        }

                        public static void main(String[] args) {
                            int x = 5;
                            duplicar(x);
                            System.out.println(x);
                        }
                        """,
                        10,
                        List.of("10", "5", "0", "No compila"),
                        1, 3),
                new PreguntaRequest(
                        "¿Qué ocurre con la última línea?",
                        """
                        class Persona {
                            String nombre;

                            Persona(String nombre) {
                                this.nombre = nombre;
                            }
                        }

                        Persona p = new Persona();
                        """,
                        11,
                        List.of("Crea una persona con nombre null",
                                "Crea una persona con nombre vacío",
                                "Lanza NullPointerException al ejecutar",
                                "No compila: el constructor exige un argumento"),
                        3, 3),
                new PreguntaRequest(
                        "¿Qué valor se muestra?",
                        """
                        int suma = 0;

                        for (int i = 1; i <= 4; i++) {
                            if (i % 2 == 0) {
                                continue;
                            }
                            suma += i;
                        }
                        System.out.println(suma);
                        """,
                        12,
                        List.of("10", "6", "4", "3"),
                        2, 3)
        );
    }
}

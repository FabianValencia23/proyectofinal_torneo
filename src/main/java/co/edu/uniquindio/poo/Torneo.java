package co.edu.uniquindio.poo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

public class Torneo{

        private final String nombre;
        private LocalDate fechaInicio; 
        private LocalDate fechaInicioInscripciones;
        private LocalDate fechaCierreInscripciones;
        private final byte numeroParticipantes;
        private final byte limiteEdad;
        private final int valorInscripcion;
        private final TipoTorneo tipoTorneo;
        private final Collection<Equipo> equipos;
        private final GeneroTorneo generoTorneo;
        private final Collection<Juez> jueces;

        public Torneo(String nombre, LocalDate fechaInicio, LocalDate fechaInicioInscripciones,
                        LocalDate fechaCierreInscripciones, byte numeroParticipantes, byte limiteEdad,
                        int valorInscripcion, TipoTorneo tipoTorneo, GeneroTorneo generoTorneo) {

                assert nombre != null;
                setFechaInicio(fechaInicio);
                setFechaInicioInscripciones(fechaInicioInscripciones);
                assert fechaCierreInscripciones != null;
                assert numeroParticipantes >= (byte) 0;
                assert limiteEdad >= (byte)0;
                assert valorInscripcion >= 0;
                assert fechaInicio.isAfter(fechaInicioInscripciones) && fechaInicio.isAfter(fechaCierreInscripciones);
                assert fechaCierreInscripciones.isAfter(fechaInicioInscripciones);
                

                this.nombre = nombre;
                this.fechaInicio = fechaInicio;
                this.fechaInicioInscripciones = fechaInicioInscripciones;
                this.fechaCierreInscripciones = fechaCierreInscripciones;
                this.numeroParticipantes = numeroParticipantes;
                this.limiteEdad = limiteEdad;
                this.valorInscripcion = valorInscripcion;
                this.tipoTorneo = tipoTorneo;
                this.equipos = new LinkedList<>();
                this.generoTorneo = generoTorneo;
                this.jueces = new LinkedList<>();
        }
        
        public String getNombre() {
                return nombre;
        }

        public LocalDate getFechaInicio() {
                return fechaInicio;
        }

        public LocalDate getFechaInicioInscripciones() {
                return fechaInicioInscripciones;
        }

        public LocalDate getFechaCierreInscripciones() {
                return fechaCierreInscripciones;
        }

        public byte getNumeroParticipantes() {
                return numeroParticipantes;
        }

        public byte getLimiteEdad() {
                return limiteEdad;
        }

        public int getValorInscripcion() {
                return valorInscripcion;
        }

        public void setFechaInicio(LocalDate fechaInicio) {
                assert fechaInicio != null;
                assert (fechaInicioInscripciones == null || fechaInicio.isAfter(fechaInicioInscripciones))&&
                (fechaCierreInscripciones == null || fechaInicio.isAfter(fechaCierreInscripciones));
                
                this.fechaInicio = fechaInicio;
        }

        public void setFechaInicioInscripciones(LocalDate fechaInicioInscripciones) {
                assert fechaInicioInscripciones != null;

                this.fechaInicioInscripciones = fechaInicioInscripciones;
        }

        public void setFechaCierreInscripciones(LocalDate fechaCierreInscripciones) {
                this.fechaCierreInscripciones = fechaCierreInscripciones;
        }

        public TipoTorneo getTipoTorneo() {
                return tipoTorneo;
        }

        public Collection<Equipo> getEquipos() {
                return Collections.unmodifiableCollection(equipos);
        }

        public Collection<Juez> getJueces() {
                return Collections.unmodifiableCollection(jueces);
        }

        public void registrarEquipo(Equipo equipo){
                validarEquipoExistente(equipo);
                validarInscripcionesAbiertas();
                equipos.add(equipo);
        }

        private void validarInscripcionesAbiertas() {
                boolean inscripcionesAbiertas = fechaInicioInscripciones.isBefore(LocalDate.now())
                                                &&fechaCierreInscripciones.isAfter(LocalDate.now());

                assert inscripcionesAbiertas : "Las inscripciones no estan abiertas";
        }

        private void validarEquipoExistente(Equipo equipo) {
                boolean existeEquipo = buscarEquipoPorNombre(equipo.nombreEquipo()).isPresent();
                assert !existeEquipo : "El equipo ya esta registrado";

        }

        public Optional<Equipo> buscarEquipoPorNombre(String nombreEquipo) {
                Predicate<Equipo> condicion = equipo -> equipo.nombreEquipo().equals(nombreEquipo);
                return equipos.stream().filter(condicion).findAny();
        }

        public void registrarJugador(String nombreEquipo, Jugador jugador) {
                var equipo = buscarEquipoPorNombre(nombreEquipo);
                equipo.ifPresent((e) -> registrarJugador(e,jugador));

        }

        public void registrarJugador(Equipo equipo, Jugador jugador) {
                assert !LocalDate.now().isAfter(fechaCierreInscripciones) : "No se puede registrar jugadores despues de la fecha de cierre";
                validarLimiteEdadJugador(jugador);
                validarJugadorExiste(jugador);
                equipo.registrarJugador(jugador);
        }

        private void validarJugadorExiste(Jugador jugador) {
                boolean existeJugador = buscarJugador(jugador).isPresent();
                assert !existeJugador : "El jugador ya esta registrado";
        }

        private Optional<Jugador> buscarJugador(Jugador jugador) {
                return equipos.stream()
                .map(equipo -> equipo.buscarJugador(jugador))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
        }

        private void validarLimiteEdadJugador(Jugador jugador) {
                var edadAInicioTorneo = jugador.calcularEdad(fechaInicio);
                assert limiteEdad == 0 || limiteEdad >= edadAInicioTorneo : "No se pueden regitrar jugadores que excedan el limite de edad del torneo";
        }

        public void registrarJuez(Juez juez){
                assert !LocalDate.now().isAfter(fechaCierreInscripciones) : "No se puede registrar jueces despues de la fecha de cierre";
                validarJuezExistente(juez);
                jueces.add(juez);
        }

        private void validarJuezExistente(Juez juez) {
                boolean existeJuez = buscarJuezPorLicencia(juez.getLicencia()).isPresent();
                assert !existeJuez : "El juez ya esta registrado";
        }

        public Optional<Juez> buscarJuezPorLicencia(String licencia) {
                Predicate<Juez> condicion = juez -> juez.getLicencia().equals(licencia);
                return jueces.stream().filter(condicion).findAny();
        }



        
}

package com.proyecto.musicgofx.modelo.servicios;

import com.proyecto.musicgofx.excepciones.UsuarioNoEncontradoException;
import com.proyecto.musicgofx.excepciones.UsuarioYaExisteException;
import com.proyecto.musicgofx.modelo.entidades.Usuario;
import com.proyecto.musicgofx.modelo.persistencia.RepositorioDatos;
import com.proyecto.musicgofx.excepciones.GmailInvalidoException;
import com.proyecto.musicgofx.excepciones.RecargaSaldoException;
import com.proyecto.musicgofx.excepciones.EdadValidaException;

import com.proyecto.musicgofx.util.Validadores;

import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {

    private final List<Usuario> usuarios;
    private final RepositorioDatos repositorio;
    private Validadores validarGmail;

    public GestorUsuarios(RepositorioDatos repositorio) {
        this.repositorio = repositorio;
        List<Usuario> cargados = repositorio.cargarUsuarios();
        this.usuarios = (cargados != null) ? cargados : new ArrayList<>();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public Usuario buscarPorIdOAlias(String criterio) {
        if (criterio == null || criterio.isBlank()) return null;
        for (Usuario u : usuarios) {
            if (u.getId().equalsIgnoreCase(criterio) || u.getNombre().equalsIgnoreCase(criterio)) {
                return u;
            }
        }
        return null;
    }

    public Usuario iniciarSesion(String alias, String correo) throws UsuarioNoEncontradoException, GmailInvalidoException {
        Usuario usuario = buscarPorIdOAlias(alias);

        if (usuario == null) {
            throw new UsuarioNoEncontradoException("El usuario no existe.");
        }
        if (!usuario.getCorreo().equalsIgnoreCase(correo)) {
            throw new GmailInvalidoException("El usuario y el Gmail no coinciden.");
        }
        return usuario;
    }

    public void registrar(String alias, String correo, int edad)
            throws UsuarioYaExisteException, GmailInvalidoException, EdadValidaException {

        if (buscarPorIdOAlias(alias) != null) {
            throw new UsuarioYaExisteException("El alias " + alias + " ya esta en uso.");
        }

        if(!validarGmail.esGmailValido(correo)){
            throw new GmailInvalidoException("El Gmail ingresado "+ correo + " no se puede utilizar");
        }

        boolean correoexiste = usuarios.stream().anyMatch(usuario ->
                usuario.getCorreo() != null && usuario.getCorreo().equalsIgnoreCase(correo));

        if(correoexiste){
            throw new UsuarioYaExisteException("El correo "+ correo + " ya se encuentra registrado");
        }

        if (edad <= 0 || edad > 120){
            throw new EdadValidaException("Ingrese una edad Valida.");
        }

        Usuario nuevo = new Usuario(alias, correo, edad);
        usuarios.add(nuevo);
        guardarCambios();
    }

    public void modificar(String aliasActual, String nuevoAlias, String nuevoCorreo) throws UsuarioNoEncontradoException, UsuarioYaExisteException {
        Usuario u = buscarPorIdOAlias(aliasActual);
        if (u == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado.");
        }

        if (nuevoAlias != null && !nuevoAlias.isBlank() && !nuevoAlias.equalsIgnoreCase(aliasActual)) {
            if (buscarPorIdOAlias(nuevoAlias) != null) {
                throw new UsuarioYaExisteException("El nuevo alias ya esta en uso.");
            }
            u.setNombre(nuevoAlias);
        }

        if (nuevoCorreo != null && !nuevoCorreo.isBlank() && !nuevoCorreo.equalsIgnoreCase(u.getCorreo())) {
            u.setCorreo(nuevoCorreo);
        }

        guardarCambios();
    }

    public void eliminar(String alias) throws UsuarioNoEncontradoException {
        Usuario u = buscarPorIdOAlias(alias);
        if (u == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado.");
        }
        usuarios.remove(u);
        guardarCambios();
    }

    /**
     * Cambia el estado del control parental del usuario y guarda los cambios en disco.
     * * @param usuarioActivo El usuario que ha iniciado sesión.
     * @param activar true para encender el control parental, false para apagarlo.
     */
    public void configurarControlParental(Usuario usuarioActivo, boolean activar) {
        if (usuarioActivo != null) {
            usuarioActivo.setControlParental(activar);
            guardarCambios();;
        }
    }
    public void gestionarRecargaSaldo(Usuario usuarioActivo , double recarga){
        if(recarga<=0){
            throw  new RecargaSaldoException("Saldo invalido. Debe ser mayor a 0 ");
        }
        usuarioActivo.recargarSaldo( recarga);
        guardarCambios();
    }

    public void guardarCambios() {
        repositorio.guardarUsuarios(this.usuarios);
    }

}
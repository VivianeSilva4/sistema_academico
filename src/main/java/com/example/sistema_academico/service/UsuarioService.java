package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.UsuarioResponseDto;
import com.example.sistema_academico.dto.update.UpdateUsuarioDto;
import com.example.sistema_academico.dto.form.UsuarioRequestDto;
import com.example.sistema_academico.mapear.MapearUsuario;
import com.example.sistema_academico.model.Usuario;
import com.example.sistema_academico.model.role.Role;
import com.example.sistema_academico.repository.ICursoRepository;
import com.example.sistema_academico.repository.IUsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuarioRepository iUsuarioRepository;
    private final ICursoRepository cursoRepository;

    @Transactional
    public UsuarioResponseDto save(UsuarioRequestDto usuarioDto){
        if(usuarioDto.tipoUsuario() != Role.ATLETA){
            throw new SecurityException("Você não possui" +
                    " permissão para se cadastra como usuario");
        }
        var curso = cursoRepository.findById(usuarioDto.curso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não existe"));
        var userEntity = MapearUsuario.toEntity(usuarioDto, curso);

        var salvarUsuario = iUsuarioRepository.save(userEntity);
        return  MapearUsuario.toDto(salvarUsuario);
    }

    @Transactional
    public void cadastrarTecnico( Integer idTecnico, Integer idCoodernador){

        Usuario coodernador = iUsuarioRepository.findById(idCoodernador)
                .orElseThrow(()-> new EntityNotFoundException("coodernador não foi encontrado"));

        if(!coodernador.getTipoUsuario().equals(Role.COORDENADOR)){
            throw  new SecurityException("Somente coodernador por cadastrar tecnico");
        }

        Usuario tecnico = iUsuarioRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("atleta não encontrado "));

        if(!tecnico.getTipoUsuario().equals(Role.ATLETA)){
            throw new SecurityException("usuario não é valido para tecnico");
        }

        tecnico.setTipoUsuario(Role.TECNICO);
        iUsuarioRepository.save(tecnico);
    }
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuario(Integer id){
        return iUsuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios(){
        return iUsuarioRepository.findAll();
    }

    @Transactional
    public void deletarUsuario(Integer id){
        if(iUsuarioRepository.existsById(id)){
            iUsuarioRepository.deleteById(id);
        }
    }
    @Transactional
    public void atualizarDados(Integer id, UpdateUsuarioDto userDto){
        var userEntity = iUsuarioRepository.findById(id);

        if(userEntity.isPresent()){
            var user = userEntity.get();

            if(userDto.nomeCompleto() != null){
                user.setNomeCompleto(userDto.nomeCompleto());
            }
            if(userDto.apelido() != null){
                user.setApelido(userDto.apelido());
            }
            if(userDto.password() != null){
                user.setSenha(userDto.password());
            }
            iUsuarioRepository.save(user);
        }
    }
}

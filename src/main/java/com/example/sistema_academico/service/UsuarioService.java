package com.example.sistema_academico.service;

import com.example.sistema_academico.dto.Response.UsuarioResponseDto;
import com.example.sistema_academico.dto.update.UpdateUsuarioDto;
import com.example.sistema_academico.dto.form.UsuarioRequestDto;
import com.example.sistema_academico.mapear.MapearUsuario;
import com.example.sistema_academico.model.Usuario;
import com.example.sistema_academico.domain.Role;
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

    private final IUsuarioRepository usuarioRepository;
    private final ICursoRepository cursoRepository;



    @Transactional
    public UsuarioResponseDto save(UsuarioRequestDto usuarioDto){
        // somente atletas podem se cadastrar como usuario, pois o coordenador
        // e o arbitro eu assumir que já estão cadastrado no sistema
        if(usuarioDto.tipoUsuario() != Role.ATLETA){
            throw new SecurityException("Você não possui" +
                    " permissão para se cadastra como usuario");
        }
        // verifico se o id do curso existe no banco
        var curso = cursoRepository.findById(usuarioDto.curso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não existe"));
        // converto o dto em uma entidade
        var userEntity = MapearUsuario.toEntity(usuarioDto, curso);

        var salvarUsuario = usuarioRepository.save(userEntity);
        return  MapearUsuario.toDto(salvarUsuario);
    }

    @Transactional
    public void cadastrarTecnico( Integer idTecnico, Integer idCoodernador){
        // busco no banco se o coodernador é um id valido
        Usuario coodernador = usuarioRepository.findById(idCoodernador)
                .orElseThrow(()-> new EntityNotFoundException("coodernador não foi encontrado"));
        // verifico se o usuario ele tem permissão do coordenador
        if(!coodernador.getTipoUsuario().equals(Role.COORDENADOR)){
            throw  new SecurityException("Somente coodernador por cadastrar tecnico");
        }
        // verifico se o id do usuario é valido
        Usuario tecnico = usuarioRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("atleta não encontrado "));
        //verifico se o usuario é do tipo atleta
        if(!tecnico.getTipoUsuario().equals(Role.ATLETA)){
            throw new SecurityException("usuario não é valido para tecnico");
        }
        // se for valido, o o tipo_usuario passa a ser do tipo Tecnico
        tecnico.setTipoUsuario(Role.TECNICO);
        usuarioRepository.save(tecnico);
    }
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuario(Integer id){
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public void deletarUsuario(Integer id){
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
        }
    }
    @Transactional
    public void atualizarDados(Integer id, UpdateUsuarioDto userDto){
        var userEntity = usuarioRepository.findById(id);

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
            usuarioRepository.save(user);
        }
    }
}

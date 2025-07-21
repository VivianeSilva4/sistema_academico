package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.UsuarioResponseDto;
import com.example.sistema_academico.mapear.MapearUsuario;
import com.example.sistema_academico.dto.update.UpdateUsuarioDto;
import com.example.sistema_academico.dto.form.UsuarioRequestDto;
import com.example.sistema_academico.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/usuarios")
    public ResponseEntity<?> createUser(@Valid @RequestBody UsuarioRequestDto usuarioDto){
        try {
            usuarioService.save(usuarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @PatchMapping("/{idTecnico}/{idCoodernador}")
    public ResponseEntity<?> cadastrarTecnico (@PathVariable("idTecnico") Integer idTecnico,
                                               @PathVariable("idCoodernador") Integer idCoodernador){
        try {
            usuarioService.cadastrarTecnico(idTecnico,idCoodernador);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar técnico: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getById(@PathVariable("userId") Integer id){
        try {
            var user = usuarioService.buscarUsuario(id);
            if(user.isPresent()){
                UsuarioResponseDto responseDto = MapearUsuario.toDto(user.get());
                return ResponseEntity.ok(responseDto);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<?> ListaUsers(){
        try {
            var usuarios = usuarioService.listarUsuarios();
            List<UsuarioResponseDto> dtos = usuarios.stream()
                    .map(MapearUsuario::toDto).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar usuários: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> apagarUsuario(@PathVariable("userId") Integer userId){
        try {
            usuarioService.deletarUsuario(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao apagar usuário: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable("userId") Integer id,
                                              @RequestBody UpdateUsuarioDto userDto){
        try {
            usuarioService.atualizarDados(id, userDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

}


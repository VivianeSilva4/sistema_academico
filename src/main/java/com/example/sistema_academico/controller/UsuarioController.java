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
   public ResponseEntity<Void> createUser(@Valid @RequestBody UsuarioRequestDto usuarioDto){
         usuarioService.save(usuarioDto);
         return ResponseEntity.status(HttpStatus.CREATED).build();
   }

   @PatchMapping("/{idTecnico}/{idCoodernador}")
   public ResponseEntity<Void> cadastrarTecnico (@PathVariable("idTecnico") Integer idTecnico,
                                                 @PathVariable("idCoodernador") Integer idCoodernador){
       usuarioService.cadastrarTecnico(idTecnico,idCoodernador);
       return ResponseEntity.ok().build();
   }
    @GetMapping("/{userId}")
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable("userId") Integer id){
       var user = usuarioService.buscarUsuario(id);
       if(user.isPresent()){
           UsuarioResponseDto responseDto = MapearUsuario.toDto(user.get());
           return ResponseEntity.ok(responseDto);
       }
       return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<UsuarioResponseDto>> ListaUsers(){
       var usuarios = usuarioService.listarUsuarios();

        List<UsuarioResponseDto> dtos = usuarios.stream()
                .map(MapearUsuario::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> apagarUsuario(@PathVariable("userId") Integer userId){
       usuarioService.deletarUsuario(userId);
       return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> atualizarUsuario(@PathVariable("userId") Integer id,
                                                 @RequestBody UpdateUsuarioDto userDto){
        usuarioService.atualizarDados(id, userDto);
        return ResponseEntity.noContent().build();
    }

}

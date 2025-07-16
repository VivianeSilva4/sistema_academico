package com.example.sistema_academico.controller;

import com.example.sistema_academico.dto.Response.CampusResponseDto;
import com.example.sistema_academico.dto.form.CampusRequestDto;
import com.example.sistema_academico.mapear.MapearCampus;
import com.example.sistema_academico.dto.update.UpdateCampusDto;
import com.example.sistema_academico.service.CampusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api")
public class CampusController {

    private final CampusService campusService;

    @PostMapping("/campus")
    public ResponseEntity<Void> salvarCampus(@Valid @RequestBody CampusRequestDto campus){
        campusService.salvar(campus);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{campusId}")
    public ResponseEntity<CampusResponseDto> getById(@PathVariable("campusId") Integer id){
        var campus = campusService.buscarCampus(id);

        if(campus.isPresent()){
            var dto = MapearCampus.toDto(campus.get());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public ResponseEntity<List<CampusResponseDto>> ListaCampus(){
        var campus = campusService.listarCampus();
        List<CampusResponseDto> dto = campus.stream().map(MapearCampus::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{campusId}")
    public ResponseEntity<Void> apagarCampus(@PathVariable("campusId") Integer userId){
        campusService.deletarCampus(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{campusId}")
    public ResponseEntity<Void> atualizarCampus(@PathVariable("campusId") Integer id,
                                                 @RequestBody UpdateCampusDto campusDto){
        campusService.atualizarDados(id, campusDto);
        return ResponseEntity.noContent().build();
    }
}

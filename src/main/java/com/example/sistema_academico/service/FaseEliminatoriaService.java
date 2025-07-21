package com.example.sistema_academico.service;

import com.example.sistema_academico.domain.Fase;
import com.example.sistema_academico.domain.Role;
import com.example.sistema_academico.dto.form.TimeClassificacaoDto;
import com.example.sistema_academico.model.*;
import com.example.sistema_academico.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaseEliminatoriaService {

    private final IJogoRepository jogoRepository;
    private final IGrupoRepository grupoRepository;
    private final IEventosRepository eventoRepository;
    private final IClassificacaoGrupoRepository classificacaoGrupoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IEquipeRepository equipeRepository; // Injeção do repositório de Equipes

    public void gerarFaseEliminatoria(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        List<Grupo> grupos = grupoRepository.findByEvento(evento);
        if (grupos.isEmpty()) {
            throw new IllegalStateException("Evento não possui grupos.");
        }

        for (Grupo grupo : grupos) {
            List<Jogo> jogos = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);
            boolean todosFinalizadosNoGrupo = jogos.stream()
                    .allMatch(Jogo::isFinalizado);

            if (!todosFinalizadosNoGrupo) {
                throw new IllegalStateException("Nem todos os jogos do grupo "
                        + grupo.getNome() + " foram finalizados.");
            }
        }

        List<TimeClassificacaoDto> classificados = new ArrayList<>();
        for (Grupo grupo : grupos) {
            List<ClassificacaoGrupo> top2 = classificacaoGrupoRepository
                    .buscarTop2PorGrupo(grupo.getId());

            if (top2.size() < 2) {
                throw new IllegalStateException("Grupo " + grupo.getNome()
                        + " não possui 2 classificados suficientes.");
            }

            // Correção: top2.get(0).getEquipe().getId() para obter o ID da equipe
            classificados.add(new TimeClassificacaoDto(top2.get(0).getEquipe().getId(),
                    grupo.getId(), true));

            classificados.add(new TimeClassificacaoDto(top2.get(1).getEquipe().getId(),
                    grupo.getId(), false));
        }

        gerarChaveamento(classificados, evento);
    }

    private void gerarChaveamento(List<TimeClassificacaoDto> classificados, Eventos evento) {
        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);
        if (arbitros.isEmpty()) {
            throw new IllegalStateException("Nenhum árbitro disponível.");
        }

        Random random = new Random();

        if (classificados.size() == 8) {
            gerarQuartasDeFinalChaveCheia(classificados, evento, arbitros, random);
        } else if (classificados.size() == 6) {
            gerarQuartasDeFinalChaveNaoCheia(classificados, evento, arbitros, random);
        } else {
            throw new IllegalStateException("Quantidade de classificados inválida: "
                    + classificados.size() + ". Esperado 6 ou 8.");
        }
    }

    private void gerarQuartasDeFinalChaveCheia(List<TimeClassificacaoDto> classificados,
                                               Eventos evento, List<Usuario> arbitros,
                                               Random random) {

        List<TimeClassificacaoDto> primeiros = classificados.stream()
                .filter(TimeClassificacaoDto::primeiroDoGrupo)
                .collect(Collectors.toList());

        List<TimeClassificacaoDto> segundos = classificados.stream()
                .filter(c -> !c.primeiroDoGrupo())
                .collect(Collectors.toList());

        Collections.shuffle(primeiros, random);
        Collections.shuffle(segundos, random);

        List<Jogo> jogos = new ArrayList<>();

        for (TimeClassificacaoDto time1Dto : primeiros) {
            TimeClassificacaoDto time2Dto = null;
            List<TimeClassificacaoDto> tempSegundosPool = new ArrayList<>(segundos);
            Collections.shuffle(tempSegundosPool, random);

            for (TimeClassificacaoDto potentialOpponent : tempSegundosPool) {
                if (!Objects.equals(time1Dto.grupo(), potentialOpponent.grupo())) {
                    time2Dto = potentialOpponent;
                    break;
                }
            }

            if (time2Dto == null) {
                throw new IllegalStateException("Não foi possível formar confrontos válidos nas" +
                        " quartas de final sem repetir grupo.");
            }
            segundos.remove(time2Dto);


            Equipes equipeA = equipeRepository.findById(time1Dto.equipe())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe A não encontrada: "));

            Equipes equipeB = equipeRepository.findById(time2Dto.equipe())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe B não encontrada: "));

            Jogo jogo = criarJogo(Fase.QUARTAS, evento, equipeA, equipeB, arbitros, random);
            jogos.add(jogo);
        }

        jogoRepository.saveAll(jogos);
    }

    private void gerarQuartasDeFinalChaveNaoCheia(List<TimeClassificacaoDto> classificados, Eventos evento,
                                                  List<Usuario> arbitros, Random random) {

        List<ClassificacaoGrupo> classificacoesGlobais = new ArrayList<>();
        for (TimeClassificacaoDto dto : classificados) {
            // Correção: Passando equipeId, grupoId na ordem correta para ClassificacaoGrupoId
            classificacaoGrupoRepository.findById(new ClassificacaoGrupoId(dto.equipe(),dto.grupo()))
                    .ifPresent(classificacoesGlobais::add);
        }

        classificacoesGlobais.sort(Comparator
                .comparing(ClassificacaoGrupo::getPontos).reversed()
                .thenComparing(ClassificacaoGrupo::getSaldoGols).reversed()
                .thenComparing(ClassificacaoGrupo::getVitorias).reversed()
        );

        List<Equipes> semiDiretoByes = classificacoesGlobais.subList(0, 2)
                .stream()
                .map(ClassificacaoGrupo::getEquipe)
                .collect(Collectors.toList());

        Set<Integer> equipesSemiDiretoIds = semiDiretoByes.stream()
                .map(Equipes::getId)
                .collect(Collectors.toSet());

        List<TimeClassificacaoDto> quartasDisputa = classificados.stream()
                .filter(dto -> !equipesSemiDiretoIds.contains(dto.equipe()))
                .collect(Collectors.toList());

        List<Jogo> jogosQuartas = new ArrayList<>();
        Collections.shuffle(quartasDisputa, random);

        List<TimeClassificacaoDto> poolQuartas = new ArrayList<>(quartasDisputa);

        while (poolQuartas.size() >= 2) {
            TimeClassificacaoDto equipeA_dto = poolQuartas.remove(0);
            Integer grupoA_id = equipeA_dto.grupo();

            TimeClassificacaoDto equipeB_dto = null;
            boolean foundOpponent = false;

            for (int i = 0; i < poolQuartas.size(); i++) {
                TimeClassificacaoDto temp_equipeB_dto = poolQuartas.get(i);
                if (!Objects.equals(grupoA_id, temp_equipeB_dto.grupo())) {
                    equipeB_dto = poolQuartas.remove(i);
                    foundOpponent = true;
                    break;
                }
            }

            if (!foundOpponent) {
                throw new IllegalStateException("Não foi possível montar confrontos de quartas sem " +
                        "repetir grupos. Sobraram equipes do mesmo grupo.");
            }


            Equipes equipeA = equipeRepository.findById(equipeA_dto.equipe())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe A não encontrada" ));
            Equipes equipeB = equipeRepository.findById(equipeB_dto.equipe())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe B não encontrada"));

            Jogo jogo = criarJogo(Fase.QUARTAS, evento, equipeA, equipeB, arbitros, random);
            jogosQuartas.add(jogo);
        }

        jogoRepository.saveAll(jogosQuartas);
    }

    public void gerarSemifinalChaveCheia(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        List<Jogo> jogosQuartas = jogoRepository.findByEventoAndFase(evento, Fase.QUARTAS);

        boolean todosFinalizados = jogosQuartas.stream().allMatch(Jogo::isFinalizado);
        if (!todosFinalizados) {
            throw new IllegalStateException("Nem todos os jogos das quartas de final foram finalizados.");
        }

        List<Equipes> vencedoresQuartas = new ArrayList<>();
        for (Jogo jogo : jogosQuartas) {
            vencedoresQuartas.add(determinarVencedor(jogo));
        }

        if (vencedoresQuartas.size() != 4) {
            throw new IllegalStateException("Número incorreto de vencedores das quartas (esperado: 4," +
                    " encontrado: " + vencedoresQuartas.size() + ").");
        }

        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);
        List<Jogo> jogosSemis = new ArrayList<>();
        Random random = new Random();

        Collections.shuffle(vencedoresQuartas, random);

        Equipes semi1TimeA = vencedoresQuartas.get(0);
        Equipes semi1TimeB = vencedoresQuartas.get(1);
        Equipes semi2TimeA = vencedoresQuartas.get(2);
        Equipes semi2TimeB = vencedoresQuartas.get(3);

        Grupo grupoSemi1TimeA = buscarGrupoDaEquipe(semi1TimeA, evento);
        Grupo grupoSemi1TimeB = buscarGrupoDaEquipe(semi1TimeB, evento);
        Grupo grupoSemi2TimeA = buscarGrupoDaEquipe(semi2TimeA, evento);
        Grupo grupoSemi2TimeB = buscarGrupoDaEquipe(semi2TimeB, evento);


        Jogo semi1 = criarJogo(Fase.SEMISFINAL, evento, semi1TimeA, semi1TimeB, arbitros, random);
        jogosSemis.add(semi1);

        Jogo semi2 = criarJogo(Fase.SEMISFINAL, evento, semi2TimeA, semi2TimeB, arbitros, random);
        jogosSemis.add(semi2);

        jogoRepository.saveAll(jogosSemis);
    }

    public void gerarSemifinalChaveNaoCheia(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        List<Jogo> jogosQuartas = jogoRepository.findByEventoAndFase(evento, Fase.QUARTAS);

        boolean todosFinalizados = jogosQuartas.stream().allMatch(Jogo::isFinalizado);
        if (!todosFinalizados) {
            throw new IllegalStateException("Nem todos os jogos das quartas foram finalizados.");
        }

        List<Equipes> vencedoresDasQuartas = new ArrayList<>();
        for (Jogo jogo : jogosQuartas) {
            vencedoresDasQuartas.add(determinarVencedor(jogo));
        }

        List<ClassificacaoGrupo> classificacoesGlobais = new ArrayList<>();
        List<Grupo> gruposDoEvento = grupoRepository.findByEvento(evento);
        for (Grupo grupo : gruposDoEvento) {
            classificacoesGlobais.addAll(classificacaoGrupoRepository.buscarTop2PorGrupo(grupo.getId()));
        }

        classificacoesGlobais.sort(Comparator
                .comparing(ClassificacaoGrupo::getPontos).reversed()
                .thenComparing(ClassificacaoGrupo::getSaldoGols).reversed()
                .thenComparing(ClassificacaoGrupo::getVitorias).reversed()
        );

        List<Equipes> byes = classificacoesGlobais.subList(0, 2)
                .stream()
                .map(ClassificacaoGrupo::getEquipe)
                .collect(Collectors.toList());

        byes.removeAll(vencedoresDasQuartas);

        if (byes.size() != 2 || vencedoresDasQuartas.size() != 2) {
            throw new IllegalStateException("Erro ao identificar semifinalistas: BYEs (" + byes.size() + ") ou Vencedores das Quartas (" + vencedoresDasQuartas.size() + ") inválidos.");
        }

        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);
        List<Jogo> jogosSemis = new ArrayList<>();
        Random random = new Random();

        // Embaralha as listas para um pareamento aleatório
        Collections.shuffle(byes, random);
        Collections.shuffle(vencedoresDasQuartas, random);

        Equipes semi1TimeA, semi1TimeB, semi2TimeA, semi2TimeB;

        // A lógica de verificação de grupo repetido foi removida.
        // As equipes serão pareadas diretamente após o embaralhamento.
        semi1TimeA = byes.get(0);
        semi1TimeB = vencedoresDasQuartas.get(0);
        semi2TimeA = byes.get(1);
        semi2TimeB = vencedoresDasQuartas.get(1);

        Jogo semi1 = criarJogo(Fase.SEMISFINAL, evento, semi1TimeA, semi1TimeB, arbitros, random);
        jogosSemis.add(semi1);

        Jogo semi2 = criarJogo(Fase.SEMISFINAL, evento, semi2TimeA, semi2TimeB, arbitros, random);
        jogosSemis.add(semi2);

        jogoRepository.saveAll(jogosSemis);
    }

    public void gerarFinal(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        List<Jogo> jogosSemis = jogoRepository.findByEventoAndFase(evento, Fase.SEMISFINAL);

        boolean todosFinalizados = jogosSemis.stream().allMatch(Jogo::isFinalizado);
        if (!todosFinalizados) {
            throw new IllegalStateException("Nem todos os jogos da semifinal foram finalizados.");
        }

        List<Equipes> finalistas = new ArrayList<>();
        for (Jogo jogo : jogosSemis) {
            finalistas.add(determinarVencedor(jogo));
        }

        if (finalistas.size() != 2) {
            throw new IllegalStateException("Número incorreto de finalistas (esperado: 2, encontrado: " + finalistas.size() + ").");
        }

        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);
        Random random = new Random();

        Jogo finalJogo = criarJogo(Fase.FINAL, evento, finalistas.get(0), finalistas.get(1), arbitros, random);
        jogoRepository.save(finalJogo);
    }

    private Equipes determinarVencedor(Jogo jogo) {
        if (Boolean.TRUE.equals(jogo.getWoA())) {
            return jogo.getEquipeB();
        } else if (Boolean.TRUE.equals(jogo.getWoB())) {
            return jogo.getEquipeA();
        } else if (jogo.getPlacaA() != null && jogo.getPlacaB() != null) {
            if (jogo.getPlacaA() > jogo.getPlacaB()) {
                return jogo.getEquipeA();
            } else if (jogo.getPlacaB() > jogo.getPlacaA()) {
                return jogo.getEquipeB();
            } else {
                throw new IllegalStateException("Jogo " + jogo.getId() + " empatado sem critério de desempate definido.");
            }
        } else {
            throw new IllegalStateException("Jogo " + jogo.getId() + " não finalizado ou com placares incompletos.");
        }
    }

    private Jogo criarJogo(Fase fase, Eventos evento, Equipes equipeA, Equipes equipeB, List<Usuario> arbitros, Random random) {
        Jogo jogo = new Jogo();
        jogo.setFase(fase);
        jogo.setEvento(evento);
        jogo.setEquipeA(equipeA);
        jogo.setEquipeB(equipeB);
        jogo.setDataHora(LocalDateTime.now());
        jogo.setWoA(false);
        jogo.setWoB(false);
        jogo.setFinalizado(false);
        jogo.setPlacaA(null);
        jogo.setPlacaB(null);
        jogo.setArbitro(arbitros.get(random.nextInt(arbitros.size())));
        return jogo;
    }

    private Grupo buscarGrupoDaEquipe(Equipes equipe, Eventos evento) {
        return grupoRepository.findByEvento(evento).stream()
                .filter(grupo -> grupo.getEquipe().contains(equipe))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Grupo não encontrado para equipe "
                        + equipe.getNome() + " no evento " + evento.getNome() + "."));
    }

    public Equipes obterVencedorDoTorneio(Integer eventoId) {
        Eventos evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));

        // Busca o jogo da final para o evento
        Optional<Jogo> jogoFinalOpt = jogoRepository.findByEventoAndFase(evento, Fase.FINAL)
                .stream()
                .findFirst();

        if (jogoFinalOpt.isEmpty()) {
            throw new IllegalStateException("O jogo da final para o evento " + evento.getNome()
                    + " ainda não foi gerado.");
        }

        Jogo jogoFinal = jogoFinalOpt.get();

        if (!jogoFinal.isFinalizado()) {
            throw new IllegalStateException("O jogo da final ainda não foi finalizado. Impossível determinar" +
                    " o vencedor do torneio.");
        }


        return determinarVencedor(jogoFinal);
    }
}
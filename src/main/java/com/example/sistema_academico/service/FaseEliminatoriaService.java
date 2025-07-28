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
    private final IEquipeRepository equipeRepository;

    public void gerarFaseEliminatoria(Integer eventoId) {
        Eventos evento = getEvento(eventoId);
        List<Grupo> grupos = getGruposDoEvento(evento);

        validarConclusaoFaseDeGrupos(grupos);

        List<TimeClassificacaoDto> classificados = getClassificadosPorGrupo(grupos);

        gerarChaveamento(classificados, evento);
    }

    private void gerarChaveamento(List<TimeClassificacaoDto> classificados, Eventos evento) {
        List<Usuario> arbitros = getArbitrosDisponiveis();
        Random random = new Random();

        if (classificados.size() > 8) {
            classificados = classificados.stream().limit(8).toList();
        }

        if (classificados.size() == 8) {
            gerarQuartasDeFinalChaveCheia(classificados, evento, arbitros, random);

        } else if (classificados.size() == 6 ) {
            gerarQuartasDeFinalChaveNaoCheia(classificados, evento, arbitros, random);

        } else if(classificados.size() == 4){
            List<Equipes> semifinalistasDiretos = classificados.stream()
                    .map(dto -> getEquipe(dto.equipe(), Fase.GRUPOS.getDescricao()))
                    .collect(Collectors.toList());

            criarESalvarJogosSemifinaisDireto(evento, semifinalistasDiretos, arbitros, random);
        }else if(classificados.size() == 2){
            List<Equipes> finalDireto = classificados.stream()
                    .map(dto -> getEquipe(dto.equipe(), Fase.GRUPOS.getDescricao()))
                    .collect(Collectors.toList());

            criarESalvarJogoFinalDireto(evento, finalDireto, arbitros, random);
        }
        else {
            throw new IllegalStateException("Quantidade de classificados inválida: "
                    + classificados.size() + ". Esperado 2, 4, 6 ou 8.");
        }
    }

    public void gerarSemifinalChaveCheia(Integer eventoId) {
        Eventos evento = getEvento(eventoId);

        List <Jogo> jogosQuartas = getJogosPorFase(evento, Fase.QUARTAS);

        validarTodosJogosFinalizados(jogosQuartas, Fase.QUARTAS.getDescricao());

        List<Equipes> vencedoresQuartas = getVencedoresDosJogos(jogosQuartas);
        validarNumeroDeVencedores(vencedoresQuartas, 4, "quartas de final");

        List<Usuario> arbitros = getArbitrosDisponiveis();
        Random random = new Random();

        criarESalvarJogosSemifinais(evento, vencedoresQuartas, arbitros, random);
    }

    public void gerarSemifinalChaveNaoCheia(Integer eventoId) {
        Eventos evento = getEvento(eventoId);
        List<Jogo> jogosQuartas = getJogosPorFase(evento, Fase.QUARTAS);

        validarTodosJogosFinalizados(jogosQuartas, "quartas");

        List<Equipes> vencedoresDasQuartas = getVencedoresDosJogos(jogosQuartas);
        List<Equipes> byes = getByesParaSemifinal(evento, vencedoresDasQuartas);

        validarSemifinalistas(byes, vencedoresDasQuartas);

        List<Usuario> arbitros = getArbitrosDisponiveis();
        Random random = new Random();

        criarESalvarJogosSemifinaisComByes(evento, byes, vencedoresDasQuartas, arbitros, random);
    }

    public void gerarFinal(Integer eventoId) {
        Eventos evento = getEvento(eventoId);
        List<Jogo> jogosSemis = getJogosPorFase(evento, Fase.SEMISFINAL);

        validarTodosJogosFinalizados(jogosSemis, "semifinal");

        List<Equipes> finalistas = getVencedoresDosJogos(jogosSemis);
        validarNumeroDeFinalistas(finalistas);

        List<Usuario> arbitros = getArbitrosDisponiveis();
        Random random = new Random();

        Jogo finalJogo = criarJogo(Fase.FINAL, evento, finalistas.get(0), finalistas.get(1), arbitros, random);
        jogoRepository.save(finalJogo);
    }

    public Equipes obterVencedorDoTorneio(Integer eventoId) {
        Eventos evento = getEvento(eventoId);
        Optional<Jogo> jogoFinalOpt = jogoRepository.findByEventoAndFase(evento, Fase.FINAL)
                .stream()
                .findFirst();

        if (jogoFinalOpt.isEmpty()) {
            throw new IllegalStateException("O jogo da final para o evento "
                    + evento.getNome() + " ainda não foi gerado.");
        }

        Jogo jogoFinal = jogoFinalOpt.get();

        if (!jogoFinal.isFinalizado()) {
            throw new IllegalStateException("O jogo da final ainda não foi finalizado." +
                    " Impossível determinar o vencedor do torneio.");
        }

        return determinarVencedor(jogoFinal);
    }

    private Eventos getEvento(Integer eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado."));
    }

    private List<Grupo> getGruposDoEvento(Eventos evento) {
        List<Grupo> grupos = grupoRepository.findByEvento(evento);
        if (grupos.isEmpty()) {
            throw new IllegalStateException("Evento não possui grupos.");
        }
        return grupos;
    }

    private void validarConclusaoFaseDeGrupos(List<Grupo> grupos) {
        for (Grupo grupo : grupos) {
            List<Jogo> jogos = jogoRepository.findByGrupoAndFase(grupo, Fase.GRUPOS);
            boolean todosFinalizadosNoGrupo = jogos.stream().allMatch(Jogo::isFinalizado);
            if (!todosFinalizadosNoGrupo) {
                throw new IllegalStateException("Nem todos os jogos do grupo "
                        + grupo.getNome() + " foram finalizados.");
            }
        }
    }

    private List<TimeClassificacaoDto> getClassificadosPorGrupo(List<Grupo> grupos) {
        List<TimeClassificacaoDto> classificados = new ArrayList<>();
        for (Grupo grupo : grupos) {
            List<ClassificacaoGrupo> top2 = classificacaoGrupoRepository.buscarTop2PorGrupo(grupo.getId());
            if (top2.size() < 2) {
                throw new IllegalStateException("Grupo " + grupo.getNome()
                        + " não possui 2 classificados suficientes.");
            }
            classificados.add(new TimeClassificacaoDto(top2.get(0).getEquipe().getId(),
                    grupo.getId(), true));
            classificados.add(new TimeClassificacaoDto(top2.get(1).getEquipe().getId(),
                    grupo.getId(), false));
        }
        return classificados;
    }
    private void criarESalvarJogoFinalDireto(Eventos evento, List<Equipes> finalistas,
                                             List<Usuario> arbitros, Random random) {
        if (finalistas.size() != 2) {
            throw new IllegalStateException("Para a final direta, são necessárias exatamente 2 equipes.");
        }

        // Embaralha para adicionar aleatoriedade (opcional)
        Collections.shuffle(finalistas, random);

        Equipes timeA = finalistas.get(0);
        Equipes timeB = finalistas.get(1);

        Jogo finalJogo = criarJogo(Fase.FINAL, evento, timeA, timeB, arbitros, random);

        jogoRepository.save(finalJogo);
    }

    private void criarESalvarJogosSemifinaisDireto(Eventos evento, List<Equipes> semifinalistas,
                                                   List<Usuario> arbitros, Random random) {
        List<Jogo> jogosSemis = new ArrayList<>();

        // Agrupa os times por seus grupos iniciais
        Map<Grupo, List<Equipes>> equipesPorGrupo = new HashMap<>();
        for (Equipes equipe : semifinalistas) {
            Grupo grupo = buscarGrupoDaEquipe(equipe, evento);
            equipesPorGrupo.computeIfAbsent(grupo, k -> new ArrayList<>()).add(equipe);
        }

        // Tenta formar confrontos entre times de grupos diferentes
        boolean emparehamentoFeito = false;
        outer:
        for (int i = 0; i < semifinalistas.size(); i++) {
            for (int j = i + 1; j < semifinalistas.size(); j++) {
                Equipes a1 = semifinalistas.get(i);
                Equipes a2 = semifinalistas.get(j);
                Grupo g1 = buscarGrupoDaEquipe(a1, evento);
                Grupo g2 = buscarGrupoDaEquipe(a2, evento);

                // Verifica se são de grupos diferentes
                if (!g1.equals(g2)) {
                    // Encontra os outros dois times
                    List<Equipes> outros = new ArrayList<>(semifinalistas);
                    outros.remove(a1);
                    outros.remove(a2);

                    Equipes b1 = outros.get(0);
                    Equipes b2 = outros.get(1);
                    Grupo g3 = buscarGrupoDaEquipe(b1, evento);
                    Grupo g4 = buscarGrupoDaEquipe(b2, evento);

                    // Verifica se também são de grupos diferentes ou permite mesmo grupo se necessário
                    if (!b1.equals(b2)) {
                        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, a1, a2, arbitros, random));
                        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, b1, b2, arbitros, random));
                        emparehamentoFeito = true;
                        break outer;
                    }
                }
            }
        }

        // Se não foi possível evitar confrontos do mesmo grupo, embaralha e cria confrontos aleatórios
        if (!emparehamentoFeito) {
            Collections.shuffle(semifinalistas, random);
            jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, semifinalistas.get(0), semifinalistas.get(1), arbitros, random));
            jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, semifinalistas.get(2), semifinalistas.get(3), arbitros, random));
        }

        jogoRepository.saveAll(jogosSemis);
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
            TimeClassificacaoDto time2Dto = encontrarOponenteParaQuartas(time1Dto, segundos, random);
            segundos.remove(time2Dto);

            Equipes equipeA = getEquipe(time1Dto.equipe(), "A");
            Equipes equipeB = getEquipe(time2Dto.equipe(), "B");

            jogos.add(criarJogo(Fase.QUARTAS, evento, equipeA, equipeB, arbitros, random));
        }
        jogoRepository.saveAll(jogos);
    }

    private TimeClassificacaoDto encontrarOponenteParaQuartas(TimeClassificacaoDto currentTeam,
                                                              List<TimeClassificacaoDto> opponents,
                                                              Random random) {
        List<TimeClassificacaoDto> tempOpponentPool = new ArrayList<>(opponents);
        Collections.shuffle(tempOpponentPool, random);

        for (TimeClassificacaoDto potentialOpponent : tempOpponentPool) {
            if (!Objects.equals(currentTeam.grupo(), potentialOpponent.grupo())) {
                return potentialOpponent;
            }
        }
        throw new IllegalStateException("Não foi possível formar confrontos válidos nas " +
                "quartas de final sem repetir grupo.");
    }

    private void gerarQuartasDeFinalChaveNaoCheia(List<TimeClassificacaoDto> classificados,
                                                  Eventos evento,
                                                  List<Usuario> arbitros, Random random) {

        List<ClassificacaoGrupo> classificacoesGlobais = getClassificacoesGlobais(classificados);
        List<Equipes> semiDiretoByes = getByes(classificacoesGlobais);


        Set<Integer> equipesSemiDiretoIds = semiDiretoByes.stream()
                .map(Equipes::getId)
                .collect(Collectors.toSet());

        List<TimeClassificacaoDto> quartasDisputa = classificados.stream()
                .filter(dto -> !equipesSemiDiretoIds.contains(dto.equipe()))
                .collect(Collectors.toList());

        List<Jogo> jogosQuartas = criarJogosQuartas(quartasDisputa, evento, arbitros, random);
        jogoRepository.saveAll(jogosQuartas);
    }

    private List<ClassificacaoGrupo> getClassificacoesGlobais(List<TimeClassificacaoDto>
                                                                      classificados) {
        List<ClassificacaoGrupo> classificacoesGlobais = new ArrayList<>();
        for (TimeClassificacaoDto dto : classificados) {
            classificacaoGrupoRepository.findById(new ClassificacaoGrupoId(dto.equipe()
                            , dto.grupo())).ifPresent(classificacoesGlobais::add);
        }
        classificacoesGlobais.sort(Comparator
                .comparing(ClassificacaoGrupo::getPontos).reversed()
                .thenComparing(ClassificacaoGrupo::getSaldoGols).reversed()
                .thenComparing(ClassificacaoGrupo::getVitorias).reversed()
        );
        return classificacoesGlobais;
    }

    private List<Equipes> getByes(List<ClassificacaoGrupo> classificacoesGlobais) {
        if(classificacoesGlobais.size() == 6){
            return classificacoesGlobais.subList(0, 2)
                    .stream()
                    .map(ClassificacaoGrupo::getEquipe)
                    .collect(Collectors.toList());
        }
        return classificacoesGlobais.subList(0, 4)
                .stream()
                .map(ClassificacaoGrupo::getEquipe)
                .collect(Collectors.toList());
    }

    private List<Jogo> criarJogosQuartas(List<TimeClassificacaoDto> quartasDisputa,
                                         Eventos evento,
                                         List<Usuario> arbitros, Random random) {
        List<Jogo> jogosQuartas = new ArrayList<>();
        Collections.shuffle(quartasDisputa, random);
        List<TimeClassificacaoDto> quartas = new ArrayList<>(quartasDisputa);

        while (quartas.size() >= 2) {
            TimeClassificacaoDto equipeA_dto = quartas.remove(0);
            Integer grupoA_id = equipeA_dto.grupo();
            TimeClassificacaoDto equipeB_dto = null;
            boolean oponenteEncontrado = false;

            for (int i = 0; i < quartas.size(); i++) {
                TimeClassificacaoDto temp_equipeB_dto = quartas.get(i);
                if (!Objects.equals(grupoA_id, temp_equipeB_dto.grupo())) {
                    equipeB_dto = quartas.remove(i);
                    oponenteEncontrado = true;
                    break;
                }
            }

            if (!oponenteEncontrado) {
                throw new IllegalStateException("Não foi possível montar confrontos de quartas " +
                        "sem repetir grupos. Sobraram equipes do mesmo grupo.");
            }

            Equipes equipeA = getEquipe(equipeA_dto.equipe(), "A");
            Equipes equipeB = getEquipe(equipeB_dto.equipe(), "B");

            jogosQuartas.add(criarJogo(Fase.QUARTAS, evento, equipeA, equipeB, arbitros, random));
        }
        return jogosQuartas;
    }

    private List<Jogo> getJogosPorFase(Eventos evento, Fase fase) {
        return jogoRepository.findByEventoAndFase(evento, fase);
    }

    private void validarTodosJogosFinalizados(List<Jogo> jogos, String fase) {
        boolean todosFinalizados = jogos.stream().allMatch(Jogo::isFinalizado);
        if (!todosFinalizados) {
            throw new IllegalStateException("Nem todos os jogos das " + fase + " foram finalizados.");
        }
    }

    private List<Equipes> getVencedoresDosJogos(List<Jogo> jogos) {
        List<Equipes> vencedores = new ArrayList<>();
        for (Jogo jogo : jogos) {
            vencedores.add(determinarVencedor(jogo));
        }
        return vencedores;
    }

    private void validarNumeroDeVencedores(List<Equipes> vencedores, int expected, String fase) {
        if (vencedores.size() != expected) {
            throw new IllegalStateException("Número incorreto de vencedores das " + fase + " (esperado: "
                    + expected + ", encontrado: " + vencedores.size() + ").");
        }
    }

    private void criarESalvarJogosSemifinais(Eventos evento, List<Equipes> vencedoresQuartas,
                                             List<Usuario> arbitros, Random random) {
        List<Jogo> jogosSemis = new ArrayList<>();
        Collections.shuffle(vencedoresQuartas, random);

        Equipes semi1TimeA = vencedoresQuartas.get(0);
        Equipes semi1TimeB = vencedoresQuartas.get(1);
        Equipes semi2TimeA = vencedoresQuartas.get(2);
        Equipes semi2TimeB = vencedoresQuartas.get(3);

        validarGruposSemifinais(semi1TimeA, semi1TimeB, evento);
        validarGruposSemifinais(semi2TimeA, semi2TimeB, evento);

        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, semi1TimeA, semi1TimeB, arbitros, random));
        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento, semi2TimeA, semi2TimeB, arbitros, random));

        jogoRepository.saveAll(jogosSemis);
    }

    private void validarGruposSemifinais(Equipes equipeA, Equipes equipeB, Eventos evento) {
        Grupo grupoSemiTimeA = buscarGrupoDaEquipe(equipeA, evento);
        Grupo grupoSemiTimeB = buscarGrupoDaEquipe(equipeB, evento);
        if (grupoSemiTimeA.equals(grupoSemiTimeB)) {
            throw new IllegalStateException("Não foi possível montar semifinais sem repetir grupos.");
        }
    }

    private List<Equipes> getByesParaSemifinal(Eventos evento, List<Equipes> vencedoresDasQuartas) {
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
        return byes;
    }

    private void validarSemifinalistas(List<Equipes> byes, List<Equipes> vencedoresDasQuartas) {
        if (byes.size() != 2 || vencedoresDasQuartas.size() != 2) {
            throw new IllegalStateException("Erro ao identificar semifinalistas: BYEs (" + byes.size()
                    + ") ou Vencedores das Quartas (" + vencedoresDasQuartas.size() + ") inválidos.");
        }
    }

    private void criarESalvarJogosSemifinaisComByes(Eventos evento, List<Equipes> byes,
                                                    List<Equipes> vencedoresDasQuartas,
                                                    List<Usuario> arbitros, Random random) {
        List<Jogo> jogosSemis = new ArrayList<>();
        Collections.shuffle(byes, random);
        Collections.shuffle(vencedoresDasQuartas, random);

        Equipes semi1TimeA = byes.get(0);
        Equipes semi1TimeB = vencedoresDasQuartas.get(0);
        Equipes semi2TimeA = byes.get(1);
        Equipes semi2TimeB = vencedoresDasQuartas.get(1);

        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento,
                semi1TimeA, semi1TimeB, arbitros, random));
        jogosSemis.add(criarJogo(Fase.SEMISFINAL, evento,
                semi2TimeA, semi2TimeB, arbitros, random));

        jogoRepository.saveAll(jogosSemis);
    }

    private void validarNumeroDeFinalistas(List<Equipes> finalistas) {
        if (finalistas.size() != 2) {
            throw new IllegalStateException("Número incorreto de finalistas (esperado: 2," +
                    " encontrado: "
                    + finalistas.size() + ").");
        }
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
                throw new IllegalStateException("Jogo " + jogo.getId()
                        + " empatado sem critério de desempate definido.");
            }
        } else {
            throw new IllegalStateException("Jogo " + jogo.getId()
                    + " não finalizado ou com placares incompletos.");
        }
    }

    private Jogo criarJogo(Fase fase, Eventos evento, Equipes equipeA, Equipes equipeB,
                           List<Usuario> arbitros, Random random) {
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

    private List<Usuario> getArbitrosDisponiveis() {
        List<Usuario> arbitros = usuarioRepository.findAllByTipoUsuario(Role.ARBITRO);
        if (arbitros.isEmpty()) {
            throw new IllegalStateException("Nenhum árbitro disponível.");
        }
        return arbitros;
    }

    private Equipes getEquipe(Integer equipeId, String label) {
        return equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe " + label + " não encontrada: " + equipeId));
    }

    private Grupo buscarGrupoDaEquipe(Equipes equipe, Eventos evento) {
        return grupoRepository.findByEvento(evento).stream()
                .filter(grupo -> grupo.getEquipe().contains(equipe))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Grupo não encontrado para equipe "
                        + equipe.getNome() + " no evento " + evento.getNome() + "."));
    }
}

package br.fiap.floodshield.service;

import br.fiap.floodshield.domain.Ocorrencia;
import br.fiap.floodshield.domain.OcorrenciaStatus;
import br.fiap.floodshield.domain.Usuario;
import br.fiap.floodshield.repository.OcorrenciaRepository;
import br.fiap.floodshield.repository.UsuarioRepository;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.CreateRequest;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.Response;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.UpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository, UsuarioRepository usuarioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Response> list(Authentication auth, Pageable pageable) {
        Usuario usuario = currentUser(auth);
        boolean gestor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        Page<Ocorrencia> page = gestor
                ? ocorrenciaRepository.findAll(pageable)
                : ocorrenciaRepository.findByUsuarioId(usuario.getId(), pageable);
        return page.map(this::toResponse);
    }

    public Response get(Long id, Authentication auth) {
        return toResponse(findAllowed(id, auth));
    }

    @Transactional
    public Response create(CreateRequest request, Authentication auth) {
        Usuario usuario = currentUser(auth);
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTitulo(request.title().trim());
        ocorrencia.setDescricao(request.description().trim());
        ocorrencia.setBairro(request.neighborhood().trim());
        ocorrencia.setNivelCm(request.waterLevelCm());
        ocorrencia.setLatitude(request.latitude());
        ocorrencia.setLongitude(request.longitude());
        ocorrencia.setStatus(OcorrenciaStatus.ABERTA);
        ocorrencia.setUsuario(usuario);
        return toResponse(ocorrenciaRepository.save(ocorrencia));
    }

    @Transactional
    public Response update(Long id, UpdateRequest request, Authentication auth) {
        Ocorrencia ocorrencia = findAllowed(id, auth);
        if (request.title() != null) {
            ocorrencia.setTitulo(request.title().trim());
        }
        if (request.description() != null) {
            ocorrencia.setDescricao(request.description().trim());
        }
        if (request.neighborhood() != null) {
            ocorrencia.setBairro(request.neighborhood().trim());
        }
        if (request.waterLevelCm() != null) {
            ocorrencia.setNivelCm(request.waterLevelCm());
        }
        if (request.latitude() != null) {
            ocorrencia.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            ocorrencia.setLongitude(request.longitude());
        }
        if (request.status() != null) {
            ocorrencia.setStatus(request.status());
        }
        return toResponse(ocorrenciaRepository.save(ocorrencia));
    }

    @Transactional
    public void delete(Long id, Authentication auth) {
        Ocorrencia ocorrencia = findAllowed(id, auth);
        ocorrenciaRepository.delete(ocorrencia);
    }

    private Ocorrencia findAllowed(Long id, Authentication auth) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada"));
        boolean gestor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        Usuario usuario = currentUser(auth);
        if (!gestor && !ocorrencia.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Sem permissão para esta ocorrência");
        }
        return ocorrencia;
    }

    private Usuario currentUser(Authentication auth) {
        return usuarioRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado"));
    }

    private Response toResponse(Ocorrencia o) {
        return new Response(
                o.getId(),
                o.getTitulo(),
                o.getDescricao(),
                o.getBairro(),
                o.getNivelCm(),
                o.getLatitude(),
                o.getLongitude(),
                o.getStatus(),
                o.getUsuario().getId(),
                o.getCriadoEm());
    }
}

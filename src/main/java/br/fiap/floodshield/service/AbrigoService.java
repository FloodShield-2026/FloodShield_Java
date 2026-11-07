package br.fiap.floodshield.service;

import br.fiap.floodshield.domain.Abrigo;
import br.fiap.floodshield.repository.AbrigoRepository;
import br.fiap.floodshield.web.dto.DomainDtos.AbrigoResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AbrigoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final AbrigoRepository abrigoRepository;

    public AbrigoService(AbrigoRepository abrigoRepository) {
        this.abrigoRepository = abrigoRepository;
    }

    public List<AbrigoResponse> list(String bairro, Double latitude, Double longitude) {
        List<Abrigo> all = abrigoRepository.findAll();
        String query = normalize(bairro);

        Double refLat = latitude;
        Double refLng = longitude;

        if (query != null) {
            List<Abrigo> matches = all.stream()
                    .filter(a -> containsIgnoreCase(a.getBairro(), query)
                            || containsIgnoreCase(a.getEndereco(), query)
                            || containsIgnoreCase(a.getNome(), query))
                    .toList();

            if (!matches.isEmpty()) {
                Abrigo anchor = matches.stream()
                        .filter(a -> a.getLatitude() != null && a.getLongitude() != null)
                        .findFirst()
                        .orElse(matches.get(0));
                if (refLat == null) {
                    refLat = anchor.getLatitude();
                }
                if (refLng == null) {
                    refLng = anchor.getLongitude();
                }

                Double finalLat = refLat;
                Double finalLng = refLng;
                return all.stream()
                        .sorted(Comparator
                                .comparing((Abrigo a) -> !isMatch(a, query))
                                .thenComparing(a -> distanceOrMax(a, finalLat, finalLng))
                                .thenComparing(Abrigo::getNome, String.CASE_INSENSITIVE_ORDER))
                        .map(a -> toResponse(a, finalLat, finalLng, isMatch(a, query)))
                        .toList();
            }
        }

        if (refLat != null && refLng != null) {
            Double finalLat = refLat;
            Double finalLng = refLng;
            return all.stream()
                    .sorted(Comparator
                            .comparing((Abrigo a) -> distanceOrMax(a, finalLat, finalLng))
                            .thenComparing(Abrigo::getNome, String.CASE_INSENSITIVE_ORDER))
                    .map(a -> toResponse(a, finalLat, finalLng, false))
                    .toList();
        }

        return all.stream()
                .sorted(Comparator.comparing(Abrigo::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(a -> toResponse(a, null, null, false))
                .toList();
    }

    public Optional<Abrigo> sugerirMaisProximo(String bairro, Double latitude, Double longitude) {
        return list(bairro, latitude, longitude).stream()
                .filter(AbrigoResponse::open)
                .findFirst()
                .flatMap(r -> abrigoRepository.findById(r.id()));
    }

    private boolean isMatch(Abrigo abrigo, String query) {
        return containsIgnoreCase(abrigo.getBairro(), query)
                || containsIgnoreCase(abrigo.getEndereco(), query)
                || containsIgnoreCase(abrigo.getNome(), query);
    }

    private AbrigoResponse toResponse(Abrigo abrigo, Double refLat, Double refLng, boolean matchedNeighborhood) {
        Double distance = null;
        if (refLat != null && refLng != null && abrigo.getLatitude() != null && abrigo.getLongitude() != null) {
            distance = round1(haversineKm(refLat, refLng, abrigo.getLatitude(), abrigo.getLongitude()));
        }
        return new AbrigoResponse(
                abrigo.getId(),
                abrigo.getNome(),
                abrigo.getEndereco(),
                abrigo.getBairro(),
                abrigo.getLatitude(),
                abrigo.getLongitude(),
                abrigo.getVagasDisponiveis(),
                abrigo.isAberto(),
                distance,
                matchedNeighborhood);
    }

    private static double distanceOrMax(Abrigo abrigo, Double refLat, Double refLng) {
        if (refLat == null || refLng == null || abrigo.getLatitude() == null || abrigo.getLongitude() == null) {
            return Double.MAX_VALUE;
        }
        return haversineKm(refLat, refLng, abrigo.getLatitude(), abrigo.getLongitude());
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean containsIgnoreCase(String source, String query) {
        return source != null && query != null && source.toLowerCase(Locale.ROOT).contains(query);
    }
}
